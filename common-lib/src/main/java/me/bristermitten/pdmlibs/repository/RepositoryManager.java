package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.dependency.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RepositoryManager
{

    private final Map<String, Repository> byAlias = new HashMap<>();
    private final Map<String, Repository> byURL = new HashMap<>();

    @NotNull
    private final Logger logger;

    public RepositoryManager(@NotNull final Logger logger)
    {
        this.logger = logger;
    }

    @Nullable
    public synchronized Repository getByAlias(@NotNull final String name)
    {
        return byAlias.get(name);
    }

    @Nullable
    public synchronized Repository getByURL(@NotNull final String url)
    {
        return byURL.get(url);
    }

    public synchronized void addRepository(@NotNull final String alias, @NotNull final Repository repository)
    {
        if (getByAlias(alias) != null)
        {
            throw new IllegalArgumentException("Will not redefine repository with alias " + alias);
        }
        byAlias.put(alias, repository);
        byURL.put(repository.getURL(), repository);
    }

    @NotNull
    public Collection<Repository> getRepositories()
    {
        return Collections.unmodifiableCollection(byAlias.values());
    }

    @Nullable
    public Repository firstContaining(@NotNull final Dependency dependency)
    {
        if (dependency.getRepoAlias() != null)
        {
            final Repository configuredRepo = getByAlias(dependency.getRepoAlias());

            if (configuredRepo != null)
            {
                if (configuredRepo.contains(dependency))
                {
                    return configuredRepo;
                }

                logger.warning(() -> "Despite being the configured repository, repo " + configuredRepo + " did not contain artifact " + dependency);
            }
            if (configuredRepo == null)
            {
                logger.warning(() -> "There was no configured repository with the alias " + dependency.getRepoAlias());
            }
        }

        return byURL.values().stream()
                .filter(repo -> repo.contains(dependency))
                .findFirst().orElse(null);
    }
}
