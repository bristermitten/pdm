package me.bristermitten.pdm.dependency;

import me.bristermitten.pdm.repository.RepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DependencyDAO
{

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;

    @NotNull
    private final String sourceRepository;

    public DependencyDAO(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String sourceRepository)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.sourceRepository = sourceRepository;
    }

    public Dependency toDependency(RepositoryManager repositoryManager)
    {
        return new Dependency(groupId, artifactId, version, repositoryManager.getByName(sourceRepository));
    }

    @Override
    public String toString()
    {
        return "DependencyDAO{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", sourceRepository='" + sourceRepository + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DependencyDAO)) return false;
        DependencyDAO that = (DependencyDAO) o;
        return getGroupId().equals(that.getGroupId()) &&
                getArtifactId().equals(that.getArtifactId()) &&
                getVersion().equals(that.getVersion()) &&
                getSourceRepository().equals(that.getSourceRepository());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion(), getSourceRepository());
    }

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

    public String getSourceRepository()
    {
        return sourceRepository;
    }
}
