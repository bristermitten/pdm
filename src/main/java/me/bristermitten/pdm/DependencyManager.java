package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.http.HTTPManager;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.MavenCentralRepository;
import me.bristermitten.pdm.util.FileUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DependencyManager
{

    private static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull
    private final Plugin managing;

    private final Map<String, JarRepository> repositories = new HashMap<>();

    private final HTTPManager manager;

    private final DependencyLoader loader;
    private final File pdmDirectory;
    private final Map<Dependency, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();

    public DependencyManager(@NotNull final Plugin managing)
    {
        this.managing = managing;
        this.manager = new HTTPManager(managing.getLogger());
        this.loader = new DependencyLoader(managing);
        loadRepositories();
        pdmDirectory = new File(managing.getDataFolder().getParentFile(), PDM_DIRECTORY_NAME);
    }

    private void loadRepositories()
    {
        repositories.put(
                "maven-central", new MavenCentralRepository(manager, this)
        );
    }

    public CompletableFuture<Void> downloadAndLoad(Dependency dependency)
    {
        CompletableFuture<File> downloaded = download(dependency);
        return downloaded.thenAccept(loader::loadDependency);
    }

    private CompletableFuture<File> download(Dependency dependency)
    {
        CompletableFuture<File> inProgress = downloadsInProgress.get(dependency);
        if (inProgress != null)
        {
            return inProgress;
        }
        FileUtil.createDirectoryIfNotPresent(pdmDirectory);

        File file = new File(pdmDirectory, dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar");

        CompletableFuture<File> fileFuture = new CompletableFuture<>();
        downloadsInProgress.put(dependency, fileFuture);
        for (JarRepository repo : repositories.values())
        {
            repo.contains(dependency).thenAccept(contains -> {
                if (contains == null || !contains)
                {
                    return;
                }

                //Load all transitive dependencies before loading the actual jar
                repo.getTransitiveDependencies(dependency)
                        .thenAccept(transitiveDependencies -> {
                            if (transitiveDependencies.isEmpty())
                            {
                                return;
                            }
                            transitiveDependencies.forEach(transitive -> downloadAndLoad(transitive).join());
                        })
                        .thenApply(v -> downloadToFile(repo, dependency, file))
                        .thenAccept(v2 -> fileFuture.complete(file));
            });
        }
        return fileFuture;
    }

    private synchronized CompletableFuture<Void> downloadToFile(JarRepository repo, Dependency dependency, File file)
    {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (file.exists())
        {
            future.complete(null);
            return future;
        }

        repo.downloadDependency(dependency).thenAccept(bytes -> {
            try
            {
                Files.copy(new ByteArrayInputStream(bytes), file.toPath());
            }
            catch (IOException e)
            {
                managing.getLogger().log(Level.SEVERE, "Could not copy file for {0} {1}", new Object[]{dependency, e});
            }
            future.complete(null);
        });
        return future;
    }
}
