package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.pom.snapshot.GetLatestSnapshotVersionParseProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SnapshotArtifact extends Artifact
{

    public SnapshotArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version, null, null);
    }

    public SnapshotArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable String repoBaseURL, @Nullable Set<Artifact> transitive)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL);
    }

    @Override
    @Nullable
    public String getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".jar";
    }

    @Override
    @Nullable
    public String getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".pom";
    }

    @Nullable
    private String getLatestVersion(String baseURL, HTTPService httpService)
    {
        String metadataURL = createBaseURL(baseURL) + "maven-metadata.xml";

        PomParser pomParser = new PomParser();
        return pomParser.parse(new GetLatestSnapshotVersionParseProcess(), httpService.readFrom(metadataURL));
    }
}
