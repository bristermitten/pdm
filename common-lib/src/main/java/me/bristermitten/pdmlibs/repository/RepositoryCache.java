package me.bristermitten.pdmlibs.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.bristermitten.pdmlibs.artifact.Artifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class RepositoryCache {
    private final Cache<Artifact, Set<Artifact>> transitiveDependencyCache = CacheBuilder.newBuilder()
            .weakKeys()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Nullable
    public Set<Artifact> getTransitives(@NotNull final Artifact artifact) {
        return transitiveDependencyCache.getIfPresent(artifact);
    }

    public void add(Artifact artifact, @NotNull  Set<Artifact> transitives) {
        transitiveDependencyCache.put(artifact, transitives);
    }
}
