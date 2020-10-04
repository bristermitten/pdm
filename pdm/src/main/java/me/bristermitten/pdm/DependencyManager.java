package me.bristermitten.pdm;

import com.google.common.collect.ImmutableSet;
import me.bristermitten.pdm.loading.types.ClasspathAddendumDependencyLoader;
import me.bristermitten.pdm.loading.types.IsolatedDependencyLoader;
import me.bristermitten.pdm.relocation.RelocationHandler;
import me.bristermitten.pdm.repository.SpigotRepository;
import me.bristermitten.pdm.util.FileUtils;
import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.dependency.DependencyFactory;
import me.bristermitten.pdmlibs.dependency.ReleaseDependency;
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
import java.util.Optional;
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

    private static final Set<Dependency> PDM_REQUIREMENTS = ImmutableSet.<Dependency>builder()
            .add(new ReleaseDependency("me.lucko", "jar-relocator", "1.4"))
            .add(new ReleaseDependency("org.ow2.asm", "asm", "7.1"))
            .add(new ReleaseDependency("org.ow2.asm", "asm-commons", "7.1"))
            .build();

    @NotNull private final PDMSettings settings;

    @NotNull private final RepositoryManager repositoryManager;
    @NotNull private final MavenRepositoryFactory repositoryFactory;
    @NotNull private final IsolatedDependencyLoader isolatedDependencyLoader;
    @NotNull private final ClasspathAddendumDependencyLoader classpathAddendumDependencyLoader;
    private final DependencyFactory dependencyFactory = new DependencyFactory();
    @NotNull private final HTTPService httpService;

    private RelocationHandler relocationHandler;

    /**
     * A Map that caches download tasks for artifacts.
     * <p>
     * This ensures that artifacts are only downloaded once, rather than a potential race condition that involves multiple
     * tasks writing to the same file.
     */
    private final Map<Dependency, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();
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
        this.isolatedDependencyLoader = new IsolatedDependencyLoader(settings.getLoggerSupplier());
        this.classpathAddendumDependencyLoader = new ClasspathAddendumDependencyLoader(settings.getLoggerSupplier(), settings.getClassLoader());
        this.httpService = httpService;

        this.repositoryManager = new RepositoryManager(settings.getLoggerSupplier().apply(RepositoryManager.class.getName()));

        this.parseProcess = new DefaultParseProcess(dependencyFactory, repositoryManager, httpService);
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
    public DependencyFactory getArtifactFactory()
    {
        return dependencyFactory;
    }

    @NotNull
    public MavenRepositoryFactory getRepositoryFactory()
    {
        return repositoryFactory;
    }

    @NotNull
    public CompletableFuture<Void> downloadAndLoadPDMDependencies() {
        final Set<CompletableFuture<File>> futures = PDM_REQUIREMENTS.stream()
                .map(this::downloadAndRelocate)
                .collect(Collectors.toSet());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                .thenAccept(v -> {
                    final File[] files = futures.stream()
                            .map(CompletableFuture::join)
                            .toArray(File[]::new);

                    isolatedDependencyLoader.loadDependency(files);
                    this.relocationHandler = new RelocationHandler(isolatedDependencyLoader.getClassLoader(files[0]));
                });
    }

    @NotNull
    public CompletableFuture<Void> downloadAndRelocateAndLoad(@NotNull final Dependency dependency)
    {
        return downloadAndRelocate(dependency)
                .thenAccept(classpathAddendumDependencyLoader::loadDependency);
    }

    @NotNull
    public CompletableFuture<File> downloadAndRelocate(@NotNull final Dependency dependency)
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

                Optional.ofNullable(dependency.getRelocations())
                        .ifPresent(relocations -> {
                            final File relocated = new File(file.getPath() + "-remapped");
                            relocationHandler.relocate(file.toPath(), relocated.toPath(), relocations);

                            file.delete();
                            relocated.renameTo(file);
                        });
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
    private Set<CompletableFuture<Void>> downloadTransitiveDependencies(@NotNull final Repository repository, @NotNull final Dependency dependency)
    {
        logger.fine(() -> "Downloading Transitive Dependencies for " + dependency);

        Set<Dependency> transitiveDependencies = dependency.getTransitiveDependencies();

        if (transitiveDependencies == null)
        {
            transitiveDependencies = repository.getTransitiveDependencies(dependency);
            dependency.setTransitiveDependencies(transitiveDependencies); //To save potential repeated lookups
        }

        return transitiveDependencies.stream()
                .map(this::downloadAndRelocateAndLoad)
                .collect(Collectors.toSet());
    }

    @Nullable
    private Repository getRepositoryFor(@NotNull final Dependency dependency)
    {
        if (dependency.getRepoAlias() != null)
        {
            final Repository byURL = repositoryManager.getByAlias(dependency.getRepoAlias());

            if (byURL == null)
            {
                logger.warning(() -> "No repository configured for " + dependency.getRepoAlias());
                return null;
            }
            return byURL;
        } else
        {
            return repositoryManager.firstContaining(dependency);
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
