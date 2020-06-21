package me.bristermitten.pdm.repository;

import me.bristermitten.pdm.DependencyManager;
import me.bristermitten.pdm.dependency.Dependency;
import me.bristermitten.pdm.http.HTTPManager;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class SpigotRepository extends MavenRepository
{

    public static final String SPIGOT_ALIAS = "spigot";
    private static final Set<String> SPIGOT_DEPENDENCY_GROUPS = Collections.unmodifiableSet(
            new HashSet<>(
                    Arrays.asList(
                            "net.minecraft",
                            "org.spigotmc",
                            "org.bukkit",
                            "com.destroystokyo.paper"
                    )
            )
    );
    private static final Set<String> SPIGOT_DEPENDENCY_ARTIFACTS = Collections.unmodifiableSet(
            new HashSet<>(
                    Arrays.asList(
                            "server",
                            "spigot",
                            "spigot-api",
                            "bukkit",
                            "craftbukkit",
                            "paper-api"
                    )
            )
    );
    private final Logger logger = Logger.getLogger("SpigotRepository");

    public SpigotRepository(HTTPManager httpManager, DependencyManager manager)
    {
        super("", httpManager, manager);
    }

    @Override
    public CompletableFuture<Boolean> contains(Dependency dependency)
    {
        return CompletableFuture.completedFuture(isSpigotDependency(dependency));
    }

    @Override
    public CompletableFuture<Set<Dependency>> getTransitiveDependencies(Dependency dependency)
    {
        return CompletableFuture.completedFuture(Collections.emptySet());
    }

    @Override
    public CompletableFuture<byte[]> downloadDependency(Dependency dependency)
    {
        if (dependency.getVersion().contains(Bukkit.getVersion()))
        {
            logger.warning(() -> "Dependency on " + dependency + " does not match server version of " + Bukkit.getVersion() + ". This could cause version problems.");
        }
        return CompletableFuture.completedFuture(null);
    }

    private boolean isSpigotDependency(Dependency dependency)
    {
        return SPIGOT_DEPENDENCY_GROUPS.contains(dependency.getGroupId().toLowerCase()) && SPIGOT_DEPENDENCY_ARTIFACTS.contains(dependency.getArtifactId().toLowerCase());
    }
}
