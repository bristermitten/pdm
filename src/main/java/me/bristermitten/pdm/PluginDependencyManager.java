package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.dependency.JSONDependencies;
import me.bristermitten.pdm.repository.MavenRepository;
import me.bristermitten.pdm.util.Constants;
import me.bristermitten.pdm.util.InputStreamUtil;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.util.HashSet;
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
        String json = InputStreamUtil.readAll(dependenciesResource);
        if (json == null)
        {
            return;
        }
        JSONDependencies jsonDependencies = Constants.GSON.fromJson(json, JSONDependencies.class);
        jsonDependencies.getRepositories().forEach((alias, repo) -> {
            MavenRepository mavenRepository = new MavenRepository(repo, manager.getManager(), manager);
            manager.getRepositoryManager().addRepository(alias, mavenRepository);
        });


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
