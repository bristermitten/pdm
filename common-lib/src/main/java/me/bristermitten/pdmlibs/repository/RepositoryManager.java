package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
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

    private final Logger logger;

    public RepositoryManager(Logger logger)
    {
        this.logger = logger;
    }

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

    public synchronized void addRepository(String alias, @NotNull Repository repository)
    {
        if (getByAlias(alias) != null)
        {
            throw new IllegalArgumentException("Will not redefine repository with alias " + alias);
        }
        byAlias.put(alias, repository);
        byURL.put(repository.getURL(), repository);
    }

    public @NotNull Collection<Repository> getRepositories()
    {
        return Collections.unmodifiableCollection(byAlias.values());
    }

    @Nullable
    public Repository firstContaining(@NotNull final Artifact artifact)
    {
        if (artifact.getRepoAlias() != null)
        {
            final Repository configuredRepo = getByAlias(artifact.getRepoAlias());
            if (configuredRepo != null)
            {
                if (configuredRepo.contains(artifact))
                {
                    return configuredRepo;
                }
                logger.warning(() -> "Despite being the configured repository, repo " + configuredRepo + " did not contain artifact " + artifact);
            }
            if (configuredRepo == null)
            {
                logger.warning(() -> "There was no configured repository with the alias " + artifact.getRepoAlias());
            }
        }

        return byURL.values().stream()
                .filter(repo -> repo.contains(artifact))
                .findFirst().orElse(null);
    }
}
