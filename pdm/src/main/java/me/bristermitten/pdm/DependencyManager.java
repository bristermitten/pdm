package me.bristermitten.pdm;

import me.bristermitten.pdm.repository.SpigotRepository;
import me.bristermitten.pdm.util.FileUtils;
import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.DefaultParseProcess;
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory;
import me.bristermitten.pdmlibs.repository.Repository;
import me.bristermitten.pdmlibs.repository.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 * This is definitely a god object, needs cleaning up.
 */
public class DependencyManager
{

    public static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull private final PDMSettings settings;

    @NotNull private final RepositoryManager repositoryManager;
    @NotNull private final MavenRepositoryFactory repositoryFactory;
    @NotNull private final DependencyLoader loader;
    private final ArtifactFactory artifactFactory = new ArtifactFactory();
    @NotNull private final HTTPService httpService;

    /**
     * A Map that caches download tasks for artifacts.
     * <p>
     * This ensures that artifacts are only downloaded once, rather than a potential race condition that involves multiple
     * tasks writing to the same file.
     */
    private final Map<Artifact, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();
    private final Logger logger;
    @NotNull private final DefaultParseProcess parseProcess;
    private File pdmDirectory;

    public DependencyManager(@NotNull final PDMSettings settings, @NotNull final HTTPService httpService)
    {
        this(settings, PDM_DIRECTORY_NAME, httpService);
    }

    public DependencyManager(@NotNull final PDMSettings settings, @NotNull final String outputDirectoryName,
                             @NotNull final HTTPService httpService)
    {
        this.settings = settings;
        this.logger = settings.getLoggerSupplier().apply(getClass().getName());
        this.loader = new DependencyLoader(settings.getClassLoader(), settings.getLoggerSupplier());
        this.httpService = httpService;

        this.repositoryManager = new RepositoryManager(settings.getLoggerSupplier().apply(RepositoryManager.class.getName()));

        this.parseProcess = new DefaultParseProcess(artifactFactory, repositoryManager, httpService);
        this.repositoryFactory = new MavenRepositoryFactory(httpService, parseProcess);

        loadRepositories();

        setOutputDirectoryName(outputDirectoryName);
    }

    public void setOutputDirectoryName(@NotNull final String outputDirectoryName)
    {
        try
        {
            this.pdmDirectory = new File(settings.getRootDirectory().getCanonicalFile(), outputDirectoryName).getCanonicalFile();
            FileUtils.createDirectoryIfNotPresent(pdmDirectory);
        }
        catch (IOException exception)
        {
            throw new IllegalStateException(exception);
        }
    }

    @NotNull
    public RepositoryManager getRepositoryManager()
    {
        return repositoryManager;
    }

    private void loadRepositories()
    {
        repositoryManager.addRepository(
                SpigotRepository.SPIGOT_ALIAS, new SpigotRepository(httpService, parseProcess)
        );
    }

    @NotNull
    public ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    @NotNull
    public MavenRepositoryFactory getRepositoryFactory()
    {
        return repositoryFactory;
    }

    @NotNull
    public CompletableFuture<Void> downloadAndLoad(@NotNull final Artifact dependency)
    {
        return download(dependency).thenAccept(loader::loadDependency);
    }

    @NotNull
    public CompletableFuture<File> download(@NotNull final Artifact dependency)
    {
        final CompletableFuture<File> inProgress = downloadsInProgress.get(dependency);

        if (inProgress != null)
        {
            return inProgress;
        }

        final File file = new File(pdmDirectory, dependency.getJarName());
        final CompletableFuture<File> downloadingFuture = CompletableFuture.supplyAsync(() -> {
            @Nullable final Repository containingRepo = getRepositoryFor(dependency);

            if (containingRepo == null)
            {
                logger.warning(() -> "No repository found for " + dependency + ", it cannot be downloaded. Other plugins may not function properly.");
                return file;
            }

            downloadTransitiveDependencies(containingRepo, dependency)
                    .forEach(CompletableFuture::join);

            if (file.exists())
            {
                logger.fine(() -> dependency + " seems to already be present. We won't re-download it.");
                return file;
            }


            if (!file.exists())
            {
                final InputStream jarContent = containingRepo.fetchJarContent(dependency);
                writeToFile(jarContent, file);
            }

            return file;
        }).exceptionally(throwable -> {
            logger.log(Level.SEVERE, throwable, () -> "Could not download " + dependency);
            downloadsInProgress.remove(dependency);
            return file;
        });

        downloadingFuture.thenRun(() -> downloadsInProgress.remove(dependency)); //remove from the cache once the download is actually done

        downloadsInProgress.put(dependency, downloadingFuture);
        return downloadingFuture;
    }

    @NotNull
    private Set<CompletableFuture<Void>> downloadTransitiveDependencies(@NotNull final Repository repository, @NotNull final Artifact artifact)
    {
        logger.fine(() -> "Downloading Transitive Dependencies for " + artifact);

        Set<Artifact> transitiveDependencies = artifact.getTransitiveDependencies();

        if (transitiveDependencies == null)
        {
            transitiveDependencies = repository.getTransitiveDependencies(artifact);
            artifact.setTransitiveDependencies(transitiveDependencies); //To save potential repeated lookups
        }

        return transitiveDependencies.stream()
                .map(this::downloadAndLoad)
                .collect(Collectors.toSet());
    }

    @Nullable
    private Repository getRepositoryFor(@NotNull final Artifact artifact)
    {
        if (artifact.getRepoAlias() != null)
        {
            final Repository byURL = repositoryManager.getByAlias(artifact.getRepoAlias());

            if (byURL == null)
            {
                logger.warning(() -> "No repository configured for " + artifact.getRepoAlias());
                return null;
            }
            return byURL;
        } else
        {
            return repositoryManager.firstContaining(artifact);
        }
    }

    private void writeToFile(@NotNull final InputStream data, @NotNull final File file)
    {
        FileUtils.createDirectoryIfNotPresent(pdmDirectory);
        try
        {
            FileUtils.writeFrom(file, data);
        }
        catch (IOException exception)
        {
            logger.log(Level.SEVERE, exception, () -> "Could not copy file for " + file);
        }
    }
}
