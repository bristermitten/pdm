package me.bristermitten.pdm.repository;

import me.bristermitten.pdm.DependencyManager;
import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.http.HTTPManager;
import me.bristermitten.pdm.repository.pom.PomParser;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MavenRepository implements JarRepository
{

    private final String baseURL;
    private final HTTPManager httpManager;
    private final DependencyManager manager;
    private final Set<Dependency> containing = ConcurrentHashMap.newKeySet();

    public MavenRepository(String baseURL, HTTPManager httpManager, DependencyManager manager)
    {
        this.baseURL = baseURL;
        this.httpManager = httpManager;
        this.manager = manager;
    }

    @Override
    public CompletableFuture<Boolean> contains(Dependency dependency)
    {
        if (containing.contains(dependency))
        {
            return CompletableFuture.completedFuture(true);
            //Simple caching
        }
        String jarUrl = prepareMavenRepoJarURL(dependency);
        return httpManager.downloadRawContentFromURL(jarUrl)
                .exceptionally(e -> null)
                .thenApply(Objects::nonNull)
                .whenComplete((b, t) -> {
                    if (b != null && b)
                    {
                        containing.add(dependency);
                    }
                });
    }

    @Override
    public CompletableFuture<byte[]> downloadDependency(Dependency dependency)
    {
        return httpManager.downloadRawContentFromURL(prepareMavenRepoJarURL(dependency));
    }

    @Override
    public CompletableFuture<Set<Dependency>> getTransitiveDependencies(Dependency dependency)
    {
        String pomURL = prepareMavenRepoPomURL(dependency);
        return httpManager.downloadRawContentFromURL(pomURL)
                .thenApply(PomParser::extractDependenciesFromPom);
    }

    private String prepareMavenRepoJarURL(Dependency dependency)
    {
        return MessageFormat.format("{0}/{1}/{2}/{3}/{4}",
                baseURL,
                dependency.getGroupId().replace('.', '/'),
                dependency.getArtifactId(),
                dependency.getVersion(),
                MessageFormat.format("{0}-{1}.jar", dependency.getArtifactId(), dependency.getVersion()));
    }

    private String prepareMavenRepoPomURL(Dependency dependency)
    {
        return MessageFormat.format("{0}/{1}/{2}/{3}/{4}",
                baseURL,
                dependency.getGroupId().replace('.', '/'),
                dependency.getArtifactId(),
                dependency.getVersion(),
                MessageFormat.format("{0}-{1}.pom", dependency.getArtifactId(), dependency.getVersion()));
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
                "baseURL='" + baseURL + '\'' +
                ", httpManager=" + httpManager +
                ", manager=" + manager +
                ", containing=" + containing +
                '}';
    }
}
