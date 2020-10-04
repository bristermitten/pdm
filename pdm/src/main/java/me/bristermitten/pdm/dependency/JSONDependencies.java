package me.bristermitten.pdm.dependency;

import me.bristermitten.pdmlibs.dependency.DependencyDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class JSONDependencies
{

    @NotNull
    private final Map<String, String> repositories;
    @NotNull
    private final Set<DependencyDTO> dependencies;

    @Nullable
    private final String dependenciesDirectory;

    public JSONDependencies(@NotNull final Map<String, String> repositories, @NotNull final Set<DependencyDTO> dependencies,
                            @Nullable final String dependenciesDirectory)
    {
        this.repositories = repositories;
        this.dependencies = dependencies;
        this.dependenciesDirectory = dependenciesDirectory;
    }

    @NotNull
    public Map<String, String> getRepositories()
    {
        return repositories;
    }

    @NotNull
    public Set<DependencyDTO> getDependencies()
    {
        return dependencies;
    }

    @Nullable
    public String getDependenciesDirectory()
    {
        return dependenciesDirectory;
    }
}
