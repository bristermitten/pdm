package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.pom.snapshot.GetLatestSnapshotVersionParseProcess;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Set;

public class SnapshotArtifact extends Artifact
{

    public SnapshotArtifact(@NotNull final String groupId, @NotNull final String artifactId,
                            @NotNull final String version)
    {
        super(groupId, artifactId, version, null, null);
    }

    public SnapshotArtifact(@NotNull final String groupId, @NotNull final String artifactId,
                            @NotNull final String version, @Nullable final String repoBaseURL,
                            @Nullable final Set<Artifact> transitive)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL);
    }

    @Nullable
    @Override
    public URL getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);

        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".jar");
    }

    @Override
    public @Nullable URL getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);

        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".pom");
    }

    @Nullable
    private String getLatestVersion(@NotNull String baseURL, @NotNull HTTPService httpService)
    {
        final URL metadataURL = URLs.parseURL(createBaseURL(baseURL) + "maven-metadata.xml");

        if (metadataURL == null)
        {
            return null;
        }

        if (!httpService.ping(metadataURL))
        {
            return null; //Don't even attempt to parse if the request will fail
        }

        final PomParser pomParser = new PomParser();

        try
        {
            return pomParser.parse(new GetLatestSnapshotVersionParseProcess(), httpService.readFrom(metadataURL));
        }
        catch (final @NotNull Exception exception)
        {
            throw new IllegalArgumentException("Error while parsing POM from " + metadataURL, exception);
        }
    }
}
