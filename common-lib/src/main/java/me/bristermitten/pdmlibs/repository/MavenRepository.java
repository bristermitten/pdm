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

    public MavenRepository(@NotNull final String baseURL, @NotNull HTTPService httpService, @NotNull ParseProcess<Set<Artifact>> parseProcess)
    {
        this.baseURL = baseURL;
        this.httpService = httpService;
        this.parseProcess = parseProcess;
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
        final URL pomURL = artifact.getPomURL(baseURL, httpService);
        if (pomURL == null)
        {
            return false;
        }
        boolean contains = httpService.ping(pomURL);
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
        return httpService.readJar(baseURL, artifact);
    }

    @Override
    @NotNull
    public Set<Artifact> getTransitiveDependencies(@NotNull Artifact artifact)
    {
        try (@NotNull InputStream pom = httpService.readPom(baseURL, artifact))
        {
            if (pom.available() == 0)
            {
                return Collections.emptySet();
            }
            try
            {
                return new PomParser().parse(parseProcess, pom);
            }
            catch (final Exception e)
            {
                throw new IllegalArgumentException("Could not parse pom for " + artifact + " at " + artifact.getPomURL(baseURL, httpService), e);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return Collections.emptySet();
        }
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
