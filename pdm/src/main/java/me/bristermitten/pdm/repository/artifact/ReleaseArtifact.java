package me.bristermitten.pdm.repository.artifact;

import me.bristermitten.pdm.util.URLUtil;
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
    public byte[] download(@NotNull final String baseRepoURL)
    {
        final String url = createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".jar";

        return URLUtil.getBytes(url);
    }

    @Override
    @Nullable
    public byte[] downloadPom(@NotNull final String baseRepoURL)
    {
        final String url = createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".pom";
        return URLUtil.getBytes(url);
    }
}
