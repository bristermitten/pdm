package me.bristermitten.pdm.repository.artifact;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Artifact
{

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;

    protected Artifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }


    @Nullable
    public abstract byte[] download(@NotNull final String baseRepoURL);

    @Nullable
    public abstract byte[] downloadPom(@NotNull final String baseRepoURL);

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "Artifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    protected final String createBaseURL(final String repoURL)
    {
        return addSlashIfNecessary(repoURL) + this.toArtifactURL() + "/";
    }

    private String addSlashIfNecessary(final String concatTo)
    {
        if (concatTo.endsWith("/"))
        {
            return concatTo;
        }
        return concatTo + "/";
    }

    public final String toArtifactURL()
    {
        return String.format("%s/%s/%s",
                groupId.replace('.', '/'),
                artifactId,
                version
        );
    }
}
