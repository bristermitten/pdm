package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

public abstract class Artifact
{

    private static final String JAR_NAME_FORMAT = "%s-%s.jar";

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;
    @Nullable
    private final String repoAlias;
    @Nullable
    private Set<Artifact> transitiveDependencies;

    protected Artifact(@NotNull final String groupId, @NotNull final String artifactId,
                       @NotNull final String version, @Nullable final Set<Artifact> transitiveDependencies,
                       @Nullable final String repoAlias)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.transitiveDependencies = transitiveDependencies;
        this.repoAlias = repoAlias;
    }

    @Nullable
    public String getRepoAlias()
    {
        return repoAlias;
    }

    @Nullable
    public abstract URL getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service);

    @Nullable
    public abstract URL getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service);

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

    /**
     * Get the transitive dependencies of this artifact.
     * <p>
     * There are semantics attached to the returned value:
     * {@code null} implies that the transitive dependencies have not been looked up, and so should be located by the runtime.
     * An empty set implies that the transitive dependencies <i>have</i> been looked up and are empty. That is, the artifact has no transitive dependencies.
     * A set with elements will have those elements downloaded, without querying the transitive dependencies again.
     *
     * @return the transitive dependencies of this artifact.
     */
    @Nullable
    public Set<Artifact> getTransitiveDependencies()
    {
        return transitiveDependencies;
    }

    public void setTransitiveDependencies(@Nullable final Set<Artifact> transitiveDependencies)
    {
        this.transitiveDependencies = transitiveDependencies;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "Artifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", repoBaseURL='" + repoAlias + '\'' +
                '}';
    }

    @NotNull
    protected final String createBaseURL(@NotNull final String repoURL)
    {
        return addSlashIfNecessary(repoURL) + this.toArtifactURL() + "/";
    }

    @NotNull
    private static String addSlashIfNecessary(@NotNull final String concatTo)
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

    @NotNull
    public String getJarName()
    {
        return String.format(JAR_NAME_FORMAT, artifactId, version);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Artifact)) return false;
        Artifact artifact = (Artifact) o;
        return getGroupId().equals(artifact.getGroupId()) && getArtifactId().equals(artifact.getArtifactId()) && getVersion().equals(artifact.getVersion()) && Objects.equals(getRepoAlias(), artifact.getRepoAlias()) && Objects.equals(getTransitiveDependencies(), artifact.getTransitiveDependencies());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion(), getRepoAlias(), getTransitiveDependencies());
    }
}
