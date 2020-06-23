package me.bristermitten.pdm.repository;

import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.http.HTTPService;
import me.bristermitten.pdm.repository.artifact.Artifact;
import me.bristermitten.pdm.repository.artifact.ReleaseArtifact;
import me.bristermitten.pdm.repository.artifact.SnapshotArtifact;
import me.bristermitten.pdm.repository.pom.PomParser;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MavenRepository implements JarRepository
{

    private final String baseURL;
    private final Map<Dependency, byte[]> downloaded = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger("MavenRepository");
    private final HTTPService httpService;

    public MavenRepository(String baseURL, HTTPService httpService)
    {
        this.baseURL = baseURL;
        this.httpService = httpService;
    }

    @Override
    public CompletableFuture<Boolean> contains(Dependency dependency)
    {
        return downloadDependency(dependency)
                .thenApply(bytes -> bytes.length != 0);
    }

    @Override
    public CompletableFuture<byte[]> downloadDependency(Dependency dependency)
    {
        byte[] existing = downloaded.get(dependency);
        if (existing != null)
        {
            return CompletableFuture.completedFuture(existing);
            //Simple caching
        }
        Artifact artifact = dependency.getVersion().endsWith("-SNAPSHOT") ?
                new SnapshotArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()) :
                new ReleaseArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());

        return CompletableFuture.supplyAsync(() -> httpService.download(baseURL, artifact))
                .handle((bytes, throwable) -> {
                    if (bytes != null && throwable == null && downloaded.get(dependency) == null)
                    {
                        downloaded.put(dependency, bytes);
                        return bytes;
                    } else if (throwable != null)
                    {
                        logger.log(Level.SEVERE, throwable, () -> "Could not download " + dependency);
                    }
                    return new byte[0];
                });
    }

    @Override
    public CompletableFuture<Set<Dependency>> getTransitiveDependencies(Dependency dependency)
    {

        Artifact artifact = dependency.getVersion().endsWith("-SNAPSHOT") ?
                new SnapshotArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()) :
                new ReleaseArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());

        return CompletableFuture.supplyAsync(() -> httpService.downloadPom(baseURL, artifact))
                .thenApply(PomParser::extractDependenciesFromPom)
                .handle((pomContent, throwable) -> {
                    if (throwable != null)
                    {
                        logger.log(Level.SEVERE, throwable, () -> "Could not download " + dependency + " from " + baseURL);
                        return Collections.emptySet();
                    }
                    if (pomContent != null)
                    {
                        return pomContent;
                    }
                    return Collections.emptySet();
                });
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MavenRepository)) return false;
        MavenRepository that = (MavenRepository) o;
        return Objects.equals(baseURL, that.baseURL);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(baseURL);
    }

    @Override
    public String toString()
    {
        return "MavenRepository{" +
                "baseURL='" + baseURL + "'}";
    }
}
