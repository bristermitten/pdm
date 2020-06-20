package me.bristermitten.pdm.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dependency
{

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;

    public Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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


    @Override
    public String toString()
    {
        return groupId + ":" + artifactId + ":" + version;
        //        return "Dependency{" +
        //                "groupId='" + groupId + '\'' +
        //                ", artifactId='" + artifactId + '\'' +
        //                ", version='" + version + '\'' +
        //                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency that = (Dependency) o;
        return getGroupId().equals(that.getGroupId()) &&
                getArtifactId().equals(that.getArtifactId()) &&
                getVersion().equals(that.getVersion());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion());
    }

}
