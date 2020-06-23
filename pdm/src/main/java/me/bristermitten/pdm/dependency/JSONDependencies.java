package me.bristermitten.pdm.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class JSONDependencies
{

    private final Map<String, String> repositories;
    private final Set<DependencyDAO> dependencies;

    @Nullable
    private final String dependenciesDirectory;

    public JSONDependencies(Map<String, String> repositories, Set<DependencyDAO> dependencies, @Nullable String dependenciesDirectory)
    {
        this.repositories = repositories;
        this.dependencies = dependencies;
        this.dependenciesDirectory = dependenciesDirectory;
    }

    public Map<String, String> getRepositories()
    {
        return repositories;
    }

    public Set<DependencyDAO> getDependencies()
    {
        return dependencies;
    }

    @Nullable
    public String getDependenciesDirectory()
    {
        return dependenciesDirectory;
    }
}
