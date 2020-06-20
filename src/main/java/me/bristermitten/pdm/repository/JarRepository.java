package me.bristermitten.pdm.repository;

import me.bristermitten.pdm.dependency.Dependency;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface JarRepository
{

    CompletableFuture<byte[]> downloadDependency(Dependency dependency);
    CompletableFuture<Set<Dependency>> getTransitiveDependencies(Dependency dependency);
    CompletableFuture<Boolean> contains(Dependency dependency);
}
