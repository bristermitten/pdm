package me.bristermitten.pdm.repository.artifact;

import me.bristermitten.pdm.http.HTTPService;
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
    public abstract String getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service);

    @Nullable
    public abstract String getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service);

    @NotNull
    public String getGroupId()
    {
        return groupId;
    }

    @NotNull
    public String getArtifactId()
    {
        return artifactId;
    }

    @NotNull
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

    @NotNull
    protected final String createBaseURL(@NotNull final String repoURL)
    {
        return addSlashIfNecessary(repoURL) + this.toArtifactURL() + "/";
    }

    @NotNull
    private String addSlashIfNecessary(@NotNull final String concatTo)
    {
        if (concatTo.endsWith("/"))
        {
            return concatTo;
        }
        return concatTo + "/";
    }

    @NotNull
    public final String toArtifactURL()
    {
        return String.format("%s/%s/%s",
                groupId.replace('.', '/'),
                artifactId,
                version
        );
    }
}
