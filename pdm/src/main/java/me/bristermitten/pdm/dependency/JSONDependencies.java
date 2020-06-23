package me.bristermitten.pdm.dependency;

import me.bristermitten.pdmlibs.artifact.ArtifactDTO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class JSONDependencies
{

    private final Map<String, String> repositories;
    private final Set<ArtifactDTO> dependencies;

    @Nullable
    private final String dependenciesDirectory;

    public JSONDependencies(Map<String, String> repositories, Set<ArtifactDTO> dependencies, @Nullable String dependenciesDirectory)
    {
        this.repositories = repositories;
        this.dependencies = dependencies;
        this.dependenciesDirectory = dependenciesDirectory;
    }

    public Map<String, String> getRepositories()
    {
        return repositories;
    }

    public Set<ArtifactDTO> getDependencies()
    {
        return dependencies;
    }

    @Nullable
    public String getDependenciesDirectory()
    {
        return dependenciesDirectory;
    }
}
