package me.bristermitten.pdm;

import com.google.common.io.CharStreams;
import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.dependency.JSONDependencies;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.MavenRepository;
import me.bristermitten.pdm.util.Constants;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PluginDependencyManager
{

    private final Plugin managing;
    private final DependencyManager manager;

    private final Set<Dependency> requiredDependencies = new HashSet<>();

    public PluginDependencyManager(Plugin managing)
    {
        this.managing = managing;
        manager = new DependencyManager(managing);

        loadDependenciesFromFile();
    }

    public void addRequiredDependency(Dependency dependency)
    {
        requiredDependencies.add(dependency);
    }

    private void loadDependenciesFromFile()
    {
        InputStream dependenciesResource = managing.getResource("dependencies.json");
        if (dependenciesResource == null)
        {
            return;
        }
        String json = null;
        try
        {
            //noinspection UnstableApiUsage
            json = CharStreams.toString(new InputStreamReader(dependenciesResource));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (json == null)
        {
            return;
        }
        JSONDependencies jsonDependencies = Constants.GSON.fromJson(json, JSONDependencies.class);
        if (jsonDependencies == null)
        {
            managing.getLogger().warning("jsonDependencies was null - Invalid JSON?");
            return;
        }
        Map<String, String> repositories = jsonDependencies.getRepositories();
        if (repositories != null)
        {
            repositories.forEach((alias, repo) -> {
                JarRepository existing = manager.getRepositoryManager().getByName(alias);
                if (existing != null)
                {
                    managing.getLogger().fine(() -> "Will not redefine repository " + alias);
                    return;
                }
                MavenRepository mavenRepository = new MavenRepository(repo, manager.getManager(), manager);
                manager.getRepositoryManager().addRepository(alias, mavenRepository);

                managing.getLogger().fine(() -> "Made new repository named " + alias);
            });
        }


        jsonDependencies.getDependencies().forEach(dao -> {
            Dependency dependency = dao.toDependency(manager.getRepositoryManager());
            addRequiredDependency(dependency);
        });
    }

    public CompletableFuture<Void> loadAllDependencies()
    {
        return CompletableFuture.allOf(requiredDependencies.stream()
                .map(manager::downloadAndLoad)
                .toArray(CompletableFuture[]::new));
    }
}
