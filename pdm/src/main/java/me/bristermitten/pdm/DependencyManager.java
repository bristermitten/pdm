package me.bristermitten.pdm;

import me.bristermitten.pdm.util.FileUtil;
import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory;
import me.bristermitten.pdmlibs.repository.Repository;
import me.bristermitten.pdmlibs.repository.RepositoryManager;
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

/**
 * TODO this is definitely a god object, needs cleaning up.
 */
public class DependencyManager
{

    public static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    @NotNull
    private final PDMSettings settings;

    private final RepositoryManager repositoryManager;
    private final MavenRepositoryFactory repositoryFactory;
    private final DependencyLoader loader;
    private final ArtifactFactory artifactFactory = new ArtifactFactory();
    private final PomParser pomParser = new PomParser(artifactFactory);

    private final Map<Artifact, CompletableFuture<File>> downloadsInProgress = new ConcurrentHashMap<>();
    private final Logger logger;
    private File pdmDirectory;

    public DependencyManager(@NotNull final PDMSettings settings, HTTPService httpService)
    {
        this(settings, PDM_DIRECTORY_NAME, httpService);
    }

    public DependencyManager(@NotNull final PDMSettings settings, String outputDirectoryName, HTTPService httpService)
    {
        this.settings = settings;
        this.logger = settings.getLoggerSupplier().get();
        this.loader = new DependencyLoader(settings.getClassLoader(), settings.getLoggerSupplier().get());

        this.repositoryManager = new RepositoryManager();

        this.repositoryFactory = new MavenRepositoryFactory(httpService, pomParser);

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
        //        repositoryManager.addRepository(
        //                SpigotRepository.SPIGOT_ALIAS, new SpigotRepository(httpService)
        //        );
    }

    public ArtifactFactory getArtifactFactory()
    {
        return artifactFactory;
    }

    public MavenRepositoryFactory getRepositoryFactory()
    {
        return repositoryFactory;
    }

    public CompletableFuture<Void> downloadAndLoad(Artifact dependency)
    {

        CompletableFuture<File> downloaded = download(dependency);
        return downloaded.thenAccept(loader::loadDependency);
    }

    private CompletableFuture<File> download(Artifact dependency)
    {
        CompletableFuture<File> inProgress = downloadsInProgress.get(dependency);
        if (inProgress != null)
        {
            return inProgress;
        }

        File file = new File(pdmDirectory, dependency.getJarName());

        Collection<Repository> reposToCheck = getRepositoriesToCheckFor(dependency);
        Set<Repository> checked = ConcurrentHashMap.newKeySet();

        CompletableFuture<File> downloadingFuture = CompletableFuture.supplyAsync(() -> {
            for (Repository repository : reposToCheck)
            {
                if (!file.exists() && !repository.contains(dependency))
                {
                    if (Objects.equals(dependency.getRepoAlias(), repository.getURL()))
                    {
                        logger.warning(() -> "Repository " + repository + " did not contain " + dependency + " despite it being the configured repository.");
                    }
                    if (checked.size() == reposToCheck.size())
                    {
                        logger.warning(() -> "No repository found for " + dependency + ", it cannot be downloaded. Other plugins may not function properly.");
                        return file;
                    }
                    continue;
                }
                Set<Artifact> transitiveDependencies = dependency.getTransitiveDependencies();
                if (transitiveDependencies == null)
                {
                    transitiveDependencies = repository.getTransitiveDependencies(dependency);
                }

                for (Artifact transitiveDependency : transitiveDependencies)
                {
                    downloadAndLoad(transitiveDependency).join();
                }

                if (!file.exists())
                {
                    final byte[] jarContent = repository.download(dependency);
                    writeToFile(jarContent, file);
                }
                return file;
            }
            return file;
        });

        downloadsInProgress.put(dependency, downloadingFuture);
        return downloadingFuture;
    }

    private Collection<Repository> getRepositoriesToCheckFor(Artifact dependency)
    {
        if (dependency.getRepoAlias() != null)
        {
            Repository byURL = repositoryManager.getByAlias(dependency.getRepoAlias());
            if (byURL == null)
            {
                logger.warning(() -> "No repository configured for " + dependency.getRepoAlias());
            }
            return Collections.singleton(byURL);
        } else
        {
            return repositoryManager.getRepositories();
        }
    }

    private void writeToFile(@NotNull byte[] bytes, File file)
    {
        FileUtil.createDirectoryIfNotPresent(pdmDirectory);
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
            logger.log(Level.SEVERE, e, () -> "Could not copy file for " + file + ", threw ");
        }
    }
}
