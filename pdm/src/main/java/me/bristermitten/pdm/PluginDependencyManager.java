package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.dependency.JSONDependencies;
import me.bristermitten.pdm.http.HTTPService;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.MavenRepository;
import me.bristermitten.pdm.util.Constants;
import me.bristermitten.pdm.util.Streams;
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
    private final Set<Dependency> requiredDependencies = new HashSet<>();

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
                managing.getName());
    }

    public PluginDependencyManager(@NotNull final Supplier<Logger> loggerSupplier,
                                   @Nullable final InputStream dependenciesResource,
                                   @NotNull final File rootDirectory,
                                   @NotNull final URLClassLoader classLoader,
                                   @NotNull final String applicationName)
    {
        this.logger = loggerSupplier.get();
        this.httpService = new HTTPService(applicationName);

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

    public void addRequiredDependency(@NotNull final Dependency dependency)
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
                final JarRepository existing = manager.getRepositoryManager().getByName(alias);
                if (existing != null)
                {
                    logger.fine(() -> "Will not redefine repository " + alias);
                    return;
                }
                final MavenRepository mavenRepository = new MavenRepository(repo, httpService);
                manager.getRepositoryManager().addRepository(alias, mavenRepository);

                logger.fine(() -> "Made new repository named " + alias);
            });
        }

        jsonDependencies.getDependencies().forEach(dao -> {
            final Dependency dependency = dao.toDependency(manager.getRepositoryManager());
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
