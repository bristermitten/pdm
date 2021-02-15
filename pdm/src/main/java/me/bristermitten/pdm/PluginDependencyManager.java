package me.bristermitten.pdm;

import com.google.gson.JsonParseException;
import me.bristermitten.pdm.dependency.JSONDependencies;
import me.bristermitten.pdm.util.Constants;
import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.config.CacheConfiguration;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.repository.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("unused") // some API methods in here we don't want to be told off for
public final class PluginDependencyManager
{

    @NotNull
    private final DependencyManager manager;

    @NotNull
    private final Set<Artifact> requiredDependencies = new HashSet<>();

    @NotNull
    private final Logger logger;

    PluginDependencyManager(@NotNull final Function<String, Logger> loggerFactory, @Nullable final InputStream dependenciesResource,
                            @NotNull final File rootDirectory, @NotNull final URLClassLoader classLoader,
                            @NotNull final String applicationName, @NotNull final String applicationVersion,
                            @NotNull final CacheConfiguration cacheConfiguration)
    {
        this.logger = loggerFactory.apply(getClass().getName());

        final HTTPService httpService = new HTTPService(applicationName, applicationVersion, cacheConfiguration);
        final PDMSettings settings = new PDMSettings(rootDirectory, loggerFactory, classLoader);

        this.manager = new DependencyManager(settings, httpService);

        if (dependenciesResource != null)
        {
            loadDependenciesFromFile(dependenciesResource);
        }
    }

    /**
     * Create a new empty builder for {@link PluginDependencyManager} with no
     * configuration
     *
     * @return empty builder
     */
    @NotNull
    public static Builder builder()
    {
        return new Builder();
    }

    public void addRequiredDependency(@NotNull final Artifact dependency)
    {
        requiredDependencies.add(dependency);
    }

    public void addRepository(@NotNull final String alias, @NotNull final String repositoryUrl)
    {
        manager.getRepositoryManager().addRepository(alias, manager.getRepositoryFactory().create(repositoryUrl));
    }

    private void loadDependenciesFromFile(@NotNull final InputStream dependenciesResource)
    {
        final JSONDependencies jsonDependencies;

        try (final Reader reader = new InputStreamReader(dependenciesResource))
        {
            jsonDependencies = Constants.GSON.fromJson(reader, JSONDependencies.class);
        }
        catch (IOException | JsonParseException exception)
        {
            logger.log(Level.WARNING, "Could not read dependencies.json", exception);
            exception.printStackTrace();
            return;
        }

        if (jsonDependencies == null)
        {
            logger.log(Level.WARNING, "jsonDependencies was null - Invalid JSON?");
            return;
        }

        final Map<String, String> repositories = jsonDependencies.getRepositories();

        repositories.forEach((alias, repo) -> {
            final Repository existing = manager.getRepositoryManager().getByAlias(alias);

            if (existing != null)
            {
                logger.fine(() -> "Will not redefine repository " + alias);
                return;
            }

            final Repository repository = manager.getRepositoryFactory().create(repo);
            manager.getRepositoryManager().addRepository(alias, repository);

            logger.fine(() -> "Made new repository named " + alias);
        });

        jsonDependencies.getDependencies().forEach(dto -> {
            final Artifact dependency = manager.getArtifactFactory().toArtifact(dto);
            addRequiredDependency(dependency);
        });

        if (jsonDependencies.getDependenciesDirectory() != null)
        {
            manager.setOutputDirectoryName(jsonDependencies.getDependenciesDirectory());
        }
    }

    /**
     * Download (if applicable) and load all required dependencies
     * as configured by {@link PluginDependencyManager#addRequiredDependency(Artifact)} and {@link PluginDependencyManager#loadDependenciesFromFile(InputStream)}
     * <p>
     * This method is <b>non blocking</b>, and returns a {@link CompletableFuture}
     * which is completed once all dependencies have been downloaded (if applicable), loaded into the classpath, or failed.
     * <p>
     * Because of the non blocking nature, important parts of initialization (that require classes from dependencies) should
     * typically either block, or
     *
     * @return a {@link CompletableFuture} that is completed when dependency loading finishes.
     * @since 0.0.1
     */
    @NotNull
    public CompletableFuture<Void> loadAllDependencies()
    {
        if (requiredDependencies.isEmpty())
        {
            logger.warning("There were no dependencies to load! This might be intentional, but if not, check your dependencies configuration!");
        }

        return CompletableFuture.allOf(requiredDependencies.stream()
                .map(manager::downloadAndLoad)
                .toArray(CompletableFuture[]::new));
    }

    /**
     * Download (if applicable) all required dependencies
     * as configured by {@link PluginDependencyManager#addRequiredDependency(Artifact)} and {@link PluginDependencyManager#loadDependenciesFromFile(InputStream)}
     * <p>
     * This method is <b>non blocking</b>, and returns a {@link CompletableFuture}
     * which is completed once all dependencies have been downloaded (if applicable), or failed.
     *
     * @return a {@link CompletableFuture} that is completed when dependency downloading finishes.
     * @since 0.0.22
     */
    @NotNull
    public CompletableFuture<List<File>> downloadAllDependencies()
    {
        if (requiredDependencies.isEmpty())
        {
            logger.warning("There were no dependencies to download! This might be intentional, but if not, check your dependencies configuration!");
        }

        return CompletableFuture.supplyAsync(
                () -> requiredDependencies.stream()
                        .map(manager::download)
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
    }

    /**
     * @author AlexL
     */
    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder
    {

        public static final String DEPENDENCIES_RESOURCE_NAME = "dependencies.json";

        private Function<String, Logger> loggerFactory = Logger::getLogger;
        @Nullable
        private InputStream dependenciesResource = null;
        @Nullable
        private File rootDirectory = null;
        @Nullable
        private URLClassLoader classLoader = null;
        @Nullable
        private String applicationName = null;
        @Nullable
        private String applicationVersion = null;
        private CacheConfiguration cacheConfiguration = CacheConfiguration.builder().build();

        Builder()
        {

        }

        @NotNull
        public Builder loggerFactory(@NotNull final Function<String, Logger> loggerFactory)
        {
            this.loggerFactory = loggerFactory;
            return this;
        }

        @NotNull
        public Builder dependenciesResource(@NotNull final InputStream dependenciesResource)
        {
            this.dependenciesResource = dependenciesResource;
            return this;
        }

        @NotNull
        public Builder rootDirectory(@NotNull final File rootDirectory)
        {
            this.rootDirectory = rootDirectory;
            return this;
        }

        @NotNull
        public Builder classLoader(@NotNull URLClassLoader classLoader)
        {
            this.classLoader = classLoader;
            return this;
        }

        @NotNull
        public Builder applicationName(@NotNull String applicationName)
        {
            this.applicationName = applicationName;
            return this;
        }

        @NotNull
        public Builder applicationVersion(@NotNull String applicationVersion)
        {
            this.applicationVersion = applicationVersion;
            return this;
        }

        @NotNull
        public Builder caching(@NotNull Consumer<CacheConfiguration.Builder> configuration)
        {
            final CacheConfiguration.Builder builder = CacheConfiguration.builder();
            configuration.accept(builder);
            this.cacheConfiguration = builder.build();
            return this;
        }

        @NotNull
        public PluginDependencyManager build()
        {
            Objects.requireNonNull(loggerFactory, "loggerFactory cannot be null");
            Objects.requireNonNull(rootDirectory, "rootDirectory cannot be null");
            Objects.requireNonNull(classLoader, "classLoader cannot be null");
            Objects.requireNonNull(applicationName, "applicationName cannot be null");
            Objects.requireNonNull(applicationVersion, "applicationVersion cannot be null");
            Objects.requireNonNull(cacheConfiguration, "cacheConfiguration cannot be null");

            return new PluginDependencyManager(loggerFactory, dependenciesResource, rootDirectory, classLoader, applicationName, applicationVersion, cacheConfiguration);
        }
    }
}
