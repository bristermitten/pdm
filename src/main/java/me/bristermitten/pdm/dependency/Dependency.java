package me.bristermitten.pdm.dependency;

import me.bristermitten.pdm.repository.JarRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Dependency
{

    private static final String JAR_NAME_FORMAT = "%s-%s.jar";
    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;
    @Nullable
    private final JarRepository sourceRepository;

    public Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        this(groupId, artifactId, version, null);
    }

    public Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable JarRepository sourceRepository)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.sourceRepository = sourceRepository;
    }

    public @NotNull String getGroupId()
    {
        return groupId;
    }

    public @NotNull String getArtifactId()
    {
        return artifactId;
    }

    public @NotNull String getVersion()
    {
        return version;
    }

    public @Nullable JarRepository getSourceRepository()
    {
        return sourceRepository;
    }

    @Override
    public String toString()
    {
        String s = groupId + ":" + artifactId + ":" + version;
        if (sourceRepository != null)
        {
            s += "@" + sourceRepository;
        }
        return s;
    }

    public String getJarName()
    {
        return String.format(JAR_NAME_FORMAT, artifactId, version);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency that = (Dependency) o;
        return getGroupId().equals(that.getGroupId()) &&
                getArtifactId().equals(that.getArtifactId()) &&
                getVersion().equals(that.getVersion()) &&
                Objects.equals(getSourceRepository(), that.getSourceRepository());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion(), getSourceRepository());
    }
}
