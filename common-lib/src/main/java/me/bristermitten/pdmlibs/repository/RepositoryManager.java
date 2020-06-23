package me.bristermitten.pdmlibs.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepositoryManager
{

    private final Map<String, Repository> byAlias = new HashMap<>();
    private final Map<String, Repository> byURL = new HashMap<>();

    @Nullable
    public synchronized Repository getByAlias(String name)
    {
        return byAlias.get(name);
    }

    @Nullable
    public synchronized Repository getByURL(@NotNull final String url)
    {
        return byURL.get(url);
    }

    public synchronized void addRepository(String alias, Repository repository)
    {
        if (getByAlias(alias) != null)
        {
            throw new IllegalArgumentException("Will not redefine repository with alias " + alias);
        }
        byAlias.put(alias, repository);
        byURL.put(repository.getURL(), repository);
    }

    public Collection<Repository> getRepositories()
    {
        return Collections.unmodifiableCollection(byAlias.values());
    }


}
