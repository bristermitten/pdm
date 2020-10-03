package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.ParseProcess;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.util.Streams;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    private final ParseProcess<Set<Artifact>> parseProcess;

    @NotNull
    private final PomParser pomParser = new PomParser();

    public MavenRepository(@NotNull final String baseURL, @NotNull final HTTPService httpService,
                           @NotNull final ParseProcess<Set<Artifact>> parseProcess)
    {
        this.baseURL = baseURL;
        this.httpService = httpService;
        this.parseProcess = parseProcess;
    }

    @NotNull
    @Override
    public String getURL()
    {
        return baseURL;
    }

    @Override
    public boolean contains(@NotNull final Artifact artifact)
    {
        if (containingArtifacts.contains(artifact))
        {
            return true;
        }

        final URL pomURL = artifact.getPomURL(baseURL, httpService);

        if (pomURL == null)
        {
            return false;
        }

        final boolean contains = httpService.ping(pomURL);

        if (contains)
        {
            containingArtifacts.add(artifact);
        }

        return contains;
    }

    @NotNull
    @Override
    public byte @NotNull [] download(@NotNull final Artifact artifact)
    {
        return Streams.toByteArray(fetchJarContent(artifact));
    }

    @NotNull
    @Override
    public InputStream fetchJarContent(@NotNull final Artifact artifact)
    {
        return httpService.readJar(baseURL, artifact);
    }

    @NotNull
    @Override
    public Set<Artifact> getTransitiveDependencies(@NotNull final Artifact artifact)
    {
        try (final InputStream pom = httpService.readPom(baseURL, artifact))
        {
            if (pom.available() == 0)
            {
                return Collections.emptySet();
            }
            return parse(artifact, pom);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            return Collections.emptySet();
        }
    }

    @NotNull
    private Set<Artifact> parse(@NotNull final Artifact artifact, @NotNull final InputStream pom)
    {
        try
        {
            return pomParser.parse(parseProcess, pom);
        }
        catch (Exception exception)
        {
            throw new IllegalArgumentException("Could not parse pom for " + artifact + " at " + artifact.getPomURL(baseURL, httpService), exception);
        }
    }

    @Override
    public boolean equals(final Object o)
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

    @NotNull
    @Override
    public String toString()
    {
        return "MavenRepository{" +
                "baseURL='" + baseURL + '\'' +
                '}';
    }
}
