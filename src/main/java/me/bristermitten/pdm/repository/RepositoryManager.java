package me.bristermitten.pdm.repository;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepositoryManager
{

    private final Map<String, JarRepository> repositories = new HashMap<>();

    @Nullable
    public synchronized JarRepository getByName(String name)
    {
        return repositories.get(name);
    }

    public synchronized void addRepository(String alias, JarRepository repository)
    {
        if (getByName(alias) != null)
        {
            throw new IllegalArgumentException("Will not redefine repository with alias " + alias);
        }
        repositories.put(alias, repository);
    }

    public Collection<JarRepository> getRepositories()
    {
        return Collections.unmodifiableCollection(repositories.values());
    }

}
