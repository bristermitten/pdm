package me.bristermitten.pdm.repository.artifact;

import me.bristermitten.pdm.http.HTTPService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReleaseArtifact extends Artifact
{

    public ReleaseArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version);
    }

    @Override
    @Nullable
    public String getJarURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".jar";
    }

    @Override
    @Nullable
    public String getPomURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".pom";
    }

}
