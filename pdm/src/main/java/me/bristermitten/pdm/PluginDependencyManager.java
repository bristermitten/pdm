package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.JSONDependencies;
import me.bristermitten.pdm.util.Constants;
import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.repository.Repository;
import me.bristermitten.pdmlibs.util.Streams;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PluginDependencyManager
{

    @NotNull
    private final DependencyManager manager;

    @NotNull
    private final Set<Artifact> requiredDependencies = new HashSet<>();

    @NotNull
    private final Logger logger;

    @NotNull
    private final HTTPService httpService;

    /**
     * Create a new PluginDependencyManager for a given Plugin.
     *
     * @param managing the Plugin whose Logger, dependencies.json, and other data should be used.
     */
    public PluginDependencyManager(@NotNull final Plugin managing)
    {
        this(
                name -> managing.getLogger(),
                managing.getResource("dependencies.json"),
                managing.getDataFolder().getParentFile(),
                (URLClassLoader) managing.getClass().getClassLoader(),
                managing.getName(),
                managing.getDescription().getVersion());
    }

    public PluginDependencyManager(@NotNull final Function<String, Logger> loggerFactory,
                                   @Nullable final InputStream dependenciesResource,
                                   @NotNull final File rootDirectory,
                                   @NotNull final URLClassLoader classLoader,
                                   @NotNull final String applicationName,
                                   @NotNull final String applicationVersion)
    {
        this.logger = loggerFactory.apply(getClass().getName());
        this.httpService = new HTTPService(applicationName, applicationVersion);

        final PDMSettings settings = new PDMSettings(
                rootDirectory,
                loggerFactory,
                classLoader);

        this.manager = new DependencyManager(settings, httpService);

        if (dependenciesResource != null)
        {
            loadDependenciesFromFile(dependenciesResource);
        }
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
        final String json = Streams.toString(dependenciesResource);
        if (json == null)
        {
            logger.log(Level.WARNING, "Could not read dependencies.json");
            return;
        }

        final JSONDependencies jsonDependencies = Constants.GSON.fromJson(json, JSONDependencies.class);
        if (jsonDependencies == null)
        {
            logger.warning("jsonDependencies was null - Invalid JSON?");
            return;
        }
        final Map<String, String> repositories = jsonDependencies.getRepositories();
        if (repositories != null)
        {
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
        }

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
     * This method is <b>non blocking</b>, and returns a {@link CompletableFuture<Void>}
     * which is completed once all dependencies have been downloaded (if applicable), loaded into the classpath, or failed.
     * <p>
     * Because of the non blocking nature, important parts of initialization (that require classes from dependencies) should
     * typically either block, or
     *
     * @return a {@link CompletableFuture<Void>} that is completed when dependency loading finishes.
     * @since 0.0.1
     */
    @NotNull
    public CompletableFuture<Void> loadAllDependencies()
    {
        logger.info("Loading Dependencies, please wait...");

        return CompletableFuture.allOf(requiredDependencies.stream()
                .map(manager::downloadAndLoad)
                .toArray(CompletableFuture[]::new))
                .thenRun(() -> logger.info("Done!"));
    }
}
