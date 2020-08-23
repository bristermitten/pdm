package me.bristermitten.pdm;

import me.bristermitten.pdm.repository.SpigotRepository;
import me.bristermitten.pdm.util.FileUtil;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/*
 * This is definitely a god object, needs cleaning up.
 */
public class DependencyManager
{

    public static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull
    private final PDMSettings settings;

    private final RepositoryManager repositoryManager;
    private final MavenRepositoryFactory repositoryFactory;
    private final DependencyLoader loader;
    private final ArtifactFactory artifactFactory = new ArtifactFactory();
    private final HTTPService httpService;

    /**
     * A Map that caches download tasks for artifacts.
     * <p>
     * This ensures that artifacts are only downloaded once, rather than a potential race condition that involves multiple
     * tasks writing to the same file.
     */
    private final Map<Artifact, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();
    private final Logger logger;
    private final DefaultParseProcess parseProcess;
    private File pdmDirectory;

    public DependencyManager(@NotNull final PDMSettings settings, HTTPService httpService)
    {
        this(settings, PDM_DIRECTORY_NAME, httpService);
    }

    public DependencyManager(@NotNull final PDMSettings settings, String outputDirectoryName, HTTPService httpService)
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
        this.pdmDirectory = new File(settings.getRootDirectory(), outputDirectoryName);
        FileUtil.createDirectoryIfNotPresent(pdmDirectory);
    }

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

    public ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    public MavenRepositoryFactory getRepositoryFactory()
    {
        return repositoryFactory;
    }

    public CompletableFuture<Void> downloadAndLoad(Artifact dependency)
    {
        CompletableFuture<File> downloaded = download(dependency);

        return downloaded.thenAccept(loader::loadDependency);
    }

    private CompletableFuture<File> download(Artifact dependency)
    {
        CompletableFuture<File> inProgress = downloadsInProgress.get(dependency);
        if (inProgress != null)
        {
            return inProgress;
        }

        File file = new File(pdmDirectory, dependency.getJarName());

        Collection<Repository> repositoriesToSearch = getRepositoriesToSearchFor(dependency);


        CompletableFuture<File> downloadingFuture = CompletableFuture.supplyAsync(() -> {

            @Nullable final Repository containingRepo = repositoryManager.firstContaining(dependency);
            if (containingRepo == null)
            {
                logger.warning(() -> "No repository found for " + dependency + ", it cannot be downloaded. Other plugins may not function properly. " +
                        "Repositories Searched: " + repositoriesToSearch.stream().map(Repository::getURL).collect(toList()));
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
        }).exceptionally(t -> {
            logger.log(Level.SEVERE, t, () -> "Could not download " + dependency);
            downloadsInProgress.remove(dependency);
            return file;
        });

        downloadingFuture.thenRun(() -> downloadsInProgress.remove(dependency)); //remove from the cache once the download is actually done

        downloadsInProgress.put(dependency, downloadingFuture);
        return downloadingFuture;
    }

    private Set<CompletableFuture<Void>> downloadTransitiveDependencies(@NotNull final Repository repository, @NotNull final Artifact artifact)
    {
        logger.fine(() -> "Downloading Transitive Dependencies for " + artifact);
        Set<Artifact> transitiveDependencies = artifact.getTransitiveDependencies();
        if (transitiveDependencies == null)
        {
            transitiveDependencies = repository.getTransitiveDependencies(artifact);
        }

        return transitiveDependencies.stream().map(this::downloadAndLoad).collect(Collectors.toSet());
    }

    private Collection<Repository> getRepositoriesToSearchFor(Artifact dependency)
    {
        if (dependency.getRepoAlias() != null)
        {
            Repository byURL = repositoryManager.getByAlias(dependency.getRepoAlias());
            if (byURL == null)
            {
                logger.warning(() -> "No repository configured for " + dependency.getRepoAlias());
                return Collections.emptySet();
            }
            return Collections.singleton(byURL);
        } else
        {
            return repositoryManager.getRepositories();
        }
    }

    private void writeToFile(@NotNull final InputStream data, @NotNull final File file)
    {
        FileUtil.createDirectoryIfNotPresent(pdmDirectory);
        try
        {
            FileUtil.writeFrom(file, data);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, e, () -> "Could not copy file for " + file);
        }
    }
}
