package me.bristermitten.pdm.dependency;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.pdm.repository.JarRepository;
import me.bristermitten.pdm.repository.RepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DependencyDTO
{

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;

    @NotNull
    @SerializedName("repository")
    private final String repository;

    public DependencyDTO(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String sourceRepository)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = sourceRepository;
    }

    public Dependency toDependency(RepositoryManager repositoryManager)
    {
        JarRepository byName = repositoryManager.getByName(repository);
        return new Dependency(groupId, artifactId, version, byName);
    }

    @Override
    public String toString()
    {
        return "DependencyDAO{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", sourceRepository='" + repository + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DependencyDTO)) return false;
        DependencyDTO that = (DependencyDTO) o;
        return getGroupId().equals(that.getGroupId()) &&
                getArtifactId().equals(that.getArtifactId()) &&
                getVersion().equals(that.getVersion()) &&
                getRepository().equals(that.getRepository());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getGroupId(), getArtifactId(), getVersion(), getRepository());
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

    public String getRepository()
    {
        return repository;
    }
}
