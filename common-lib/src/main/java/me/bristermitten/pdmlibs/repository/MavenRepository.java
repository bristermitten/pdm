package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.util.Streams;
import me.bristermitten.pdmlibs.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MavenRepository implements Repository
{

    private final Set<Artifact> containingArtifacts = ConcurrentHashMap.newKeySet();
    @NotNull
    private final String baseURL;
    @NotNull
    private final HTTPService httpService;
    @NotNull
    private final PomParser pomParser;

    public MavenRepository(@NotNull final String baseURL, @NotNull HTTPService httpService, @NotNull PomParser pomParser)
    {
        this.baseURL = baseURL;
        this.httpService = httpService;
        this.pomParser = pomParser;
    }

    @Override
    @NotNull
    public String getURL()
    {
        return baseURL;
    }

    @Override
    public boolean contains(@NotNull Artifact artifact)
    {
        if (containingArtifacts.contains(artifact))
        {
            return true;
        }
        String jarURL = artifact.getJarURL(baseURL, httpService);
        if (jarURL == null)
        {
            return false;
        }
        boolean contains = httpService.ping(jarURL);
        if (contains)
        {
            containingArtifacts.add(artifact);
        }

        return contains;
    }

    @Override
    @NotNull
    public byte[] download(@NotNull Artifact artifact)
    {
        return Streams.toByteArray(fetchJarContent(artifact));
    }

    @Override
    @NotNull
    public InputStream fetchJarContent(@NotNull Artifact artifact)
    {
        return httpService.read(baseURL, artifact);
    }

    @Override
    @NotNull
    public Set<Artifact> getTransitiveDependencies(@NotNull Artifact artifact)
    {
        @NotNull byte[] pom = httpService.downloadPom(baseURL, artifact);
        final String pomContent = new String(pom).trim();
        if (Strings.isBlank(pomContent))
        {
            return Collections.emptySet();
        }
        return pomParser.extractDependenciesFromPom(pomContent);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MavenRepository)) return false;
        MavenRepository that = (MavenRepository) o;
        return baseURL.equals(that.baseURL);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(baseURL);
    }
}
