package me.bristermitten.pdm.dependency;

import me.bristermitten.pdm.dependency.DependencyDAO;

import java.util.Map;
import java.util.Set;

public class JSONDependencies
{

    private final Map<String, String> repositories;
    private final Set<DependencyDAO> dependencies;

    public JSONDependencies(Map<String, String> repositories, Set<DependencyDAO> dependencies)
    {
        this.repositories = repositories;
        this.dependencies = dependencies;
    }

    public Map<String, String> getRepositories()
    {
        return repositories;
    }

    public Set<DependencyDAO> getDependencies()
    {
        return dependencies;
    }

}
