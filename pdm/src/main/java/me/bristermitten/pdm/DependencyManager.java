package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.http.HTTPManager;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.MavenCentralRepository;
import me.bristermitten.pdm.repository.RepositoryManager;
import me.bristermitten.pdm.repository.SpigotRepository;
import me.bristermitten.pdm.util.FileUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DependencyManager
{

    private static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull
    private final Plugin managing;

    private final RepositoryManager repositoryManager;

    private final HTTPManager manager;

    private final DependencyLoader loader;
    private final File pdmDirectory;
    private final Map<Dependency, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();

    public DependencyManager(@NotNull final Plugin managing)
    {
        this.managing = managing;
        this.manager = new HTTPManager(managing.getLogger());
        this.loader = new DependencyLoader(managing);
        pdmDirectory = new File(managing.getDataFolder().getParentFile(), PDM_DIRECTORY_NAME);

        repositoryManager = new RepositoryManager();
        loadRepositories();

        FileUtil.createDirectoryIfNotPresent(pdmDirectory);
    }

    public RepositoryManager getRepositoryManager()
    {
        return repositoryManager;
    }

    public HTTPManager getManager()
    {
        return manager;
    }

    private void loadRepositories()
    {
        repositoryManager.addRepository(
                MavenCentralRepository.MAVEN_CENTRAL_ALIAS, new MavenCentralRepository(manager, this)
        );
        repositoryManager.addRepository(
                SpigotRepository.SPIGOT_ALIAS, new SpigotRepository(manager, this)
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

        File file = new File(pdmDirectory, dependency.getJarName());

        Collection<JarRepository> reposToCheck = getRepositoriesToCheckFor(dependency);
        Set<JarRepository> checked = ConcurrentHashMap.newKeySet();

        CompletableFuture<File> downloadingFuture = CompletableFuture.supplyAsync(() -> {
            for (JarRepository repo : reposToCheck)
            {
                Boolean contains = repo.contains(dependency).join();
                if (contains == null || !contains)
                {
                    if (dependency.getSourceRepository() != null)
                    {
                        managing.getLogger().info(() -> "Repository " + repo + " did not contain dependency " + dependency + " despite it being the configured repo!");
                    }
                    if (checked.size() == reposToCheck.size())
                    {
                        managing.getLogger().warning(() -> "No repository found for " + dependency + ", it cannot be downloaded. Other plugins may not function properly.");
                        return file;
                    }
                    continue;
                }

                managing.getLogger().info(() -> "Loading Transitive Dependencies for " + dependency + "...");
                //Load all transitive dependencies before loading the actual jar
                repo.getTransitiveDependencies(dependency)
                        .thenAccept(transitiveDependencies -> transitiveDependencies.forEach(transitive -> downloadAndLoad(transitive).join()))
                        .thenRun(() -> downloadToFile(repo, dependency, file)).join();
                return file;
            }
            throw new NoSuchElementException(dependency.toString());
        });
        downloadsInProgress.put(dependency, downloadingFuture);
        return downloadingFuture;
    }

    private Collection<JarRepository> getRepositoriesToCheckFor(Dependency dependency)
    {
        if (dependency.getSourceRepository() != null)
        {
            return Collections.singleton(dependency.getSourceRepository());
        } else
        {
            return repositoryManager.getRepositories();
        }
    }

    private synchronized void downloadToFile(JarRepository repo, Dependency dependency, File file)
    {
        if (file.exists())
        {
            return;
        }
        managing.getLogger().info(() -> "Downloading Dependency " + dependency + "...");
        repo.downloadDependency(dependency)
                .exceptionally(throwable -> {
                    managing.getLogger().log(Level.SEVERE, throwable, () -> "Exception thrown while downloading " + dependency);
                    return null;
                })
                .thenAccept(bytes -> {
                    if (bytes == null)
                    {
                        return;
                    }
                    try
                    {
                        Files.copy(new ByteArrayInputStream(bytes), file.toPath());
                    }
                    catch (IOException e)
                    {
                        managing.getLogger().log(Level.SEVERE, e, () -> "Could not copy file for " + dependency + ", threw ");
                    }
                    finally
                    {
                        downloadsInProgress.remove(dependency);
                        managing.getLogger().info(() -> "Downloaded Dependency " + dependency + "!");
                    }
                });
    }
}
