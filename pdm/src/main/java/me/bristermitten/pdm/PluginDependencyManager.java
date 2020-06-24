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
import java.util.function.Supplier;
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

    private final HTTPService httpService;

    public PluginDependencyManager(@NotNull final Plugin managing)
    {
        this(
                managing::getLogger,
                managing.getResource("dependencies.json"),
                managing.getDataFolder().getParentFile(),
                (URLClassLoader) managing.getClass().getClassLoader(),
                managing.getName(),
                managing.getDescription().getVersion());
    }

    public PluginDependencyManager(@NotNull final Supplier<Logger> loggerSupplier,
                                   @Nullable final InputStream dependenciesResource,
                                   @NotNull final File rootDirectory,
                                   @NotNull final URLClassLoader classLoader,
                                   @NotNull final String applicationName,
                                   @NotNull final String applicationVersion)
    {
        this.logger = loggerSupplier.get();
        this.httpService = new HTTPService(applicationName, applicationVersion);

        final PDMSettings settings = new PDMSettings(
                rootDirectory,
                loggerSupplier,
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
