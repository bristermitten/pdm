package me.bristermitten.pdm;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.MavenCentralRepository;
import me.bristermitten.pdm.repository.RepositoryManager;
import me.bristermitten.pdm.repository.SpigotRepository;
import me.bristermitten.pdm.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyManager
{

    public static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull
    private final PDMSettings settings;

    private final RepositoryManager repositoryManager;

    private final DependencyLoader loader;
    private final Map<Dependency, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();
    private final Logger logger;
    private File pdmDirectory;

    public DependencyManager(@NotNull final PDMSettings settings)
    {
        this(settings, PDM_DIRECTORY_NAME);
    }

    public DependencyManager(@NotNull final PDMSettings settings, String outputDirectoryName)
    {
        this.settings = settings;
        this.logger = settings.getLoggerSupplier().get();
        this.loader = new DependencyLoader(settings.getClassLoader(), settings.getLoggerSupplier().get());

        repositoryManager = new RepositoryManager();
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
                MavenCentralRepository.MAVEN_CENTRAL_ALIAS, new MavenCentralRepository()
        );
        repositoryManager.addRepository(
                SpigotRepository.SPIGOT_ALIAS, new SpigotRepository()
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
                        logger.info(() -> "Repository " + repo + " did not contain dependency " + dependency + " despite it being the configured repo!");
                    }
                    if (checked.size() == reposToCheck.size())
                    {
                        logger.warning(() -> "No repository found for " + dependency + ", it cannot be downloaded. Other plugins may not function properly.");
                        return file;
                    }
                    continue;
                }

                logger.info(() -> "Loading Transitive Dependencies for " + dependency + "...");
                //Load all transitive dependencies before loading the actual jar
                repo.getTransitiveDependencies(dependency)
                        .thenAccept(transitiveDependencies -> transitiveDependencies.forEach(transitive -> downloadAndLoad(transitive).join()))
                        .thenRun(() -> {
                            if (!file.exists())
                            {
                                downloadToFile(repo, dependency, file);
                            }
                        }).join();
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
        FileUtil.createDirectoryIfNotPresent(pdmDirectory);
        if (file.exists())
        {
            return;
        }
        logger.info(() -> "Downloading Dependency " + dependency + "...");
        repo.downloadDependency(dependency)
                .exceptionally(throwable -> {
                    logger.log(Level.SEVERE, throwable, () -> "Exception thrown while downloading " + dependency);
                    return new byte[0];
                })
                .thenAccept(bytes -> {
                    if (bytes.length == 0)
                    {
                        return;
                    }
                    try (final ByteArrayInputStream input = new ByteArrayInputStream(bytes))
                    {
                        Files.copy(input, file.toPath());
                    }
                    catch (IOException e)
                    {
                        logger.log(Level.SEVERE, e, () -> "Could not copy file for " + dependency + ", threw ");
                        downloadsInProgress.remove(dependency);
                    }
                    logger.info(() -> "Downloaded Dependency " + dependency + "!");
                });
    }
}
