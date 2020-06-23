package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MavenRepository implements Repository
{

    private static final Set<Artifact> containingArtifacts = ConcurrentHashMap.newKeySet();
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
        return httpService.download(baseURL, artifact);
    }

    @Override
    public @NotNull Set<Artifact> getTransitiveDependencies(@NotNull Artifact artifact)
    {
        @NotNull byte[] pom = httpService.downloadPom(baseURL, artifact);
        final String pomContent = new String(pom);
        if (pomContent.isEmpty())
        {
            return Collections.emptySet();
        }

        return pomParser.extractDependenciesFromPom(pomContent);
    }
}
