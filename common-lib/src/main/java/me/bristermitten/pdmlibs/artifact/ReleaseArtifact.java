package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ReleaseArtifact extends Artifact
{


    public ReleaseArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version, null, null);
    }

    public ReleaseArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable String repoBaseURL, @Nullable Set<Artifact> transitive)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL);
    }

    @Override
    @NotNull
    public String getJarURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".jar";
    }

    @Override
    @NotNull
    public String getPomURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".pom";
    }

}
