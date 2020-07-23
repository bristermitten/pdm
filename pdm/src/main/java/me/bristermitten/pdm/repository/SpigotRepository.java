package me.bristermitten.pdm.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import me.bristermitten.pdmlibs.repository.MavenRepository;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public final class SpigotRepository extends MavenRepository
{

    public static final String SPIGOT_ALIAS = "spigot-repo";
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

    @NotNull
    private final Logger logger = Logger.getLogger("SpigotRepository");

    public SpigotRepository(@NotNull HTTPService httpService, @NotNull PomParser pomParser)
    {
        super("", httpService, pomParser);
    }


    @Override
    public @NotNull Set<Artifact> getTransitiveDependencies(@NotNull Artifact dependency)
    {
        return Collections.emptySet();
    }

    @Override
    public boolean contains(@NotNull Artifact artifact)
    {
        return isSpigotDependency(artifact);
    }

    @Override
    public byte[] download(@NotNull Artifact dependency)
    {
        if (!dependency.getVersion().contains(Bukkit.getVersion()))
        {
            logger.warning(() -> "Dependency on " + dependency + " does not match server version of " + Bukkit.getVersion() + ". This could cause version problems.");
        }
        return new byte[0];
    }

    private boolean isSpigotDependency(Artifact dependency)
    {
        return SPIGOT_DEPENDENCY_GROUPS.contains(dependency.getGroupId().toLowerCase()) && SPIGOT_DEPENDENCY_ARTIFACTS.contains(dependency.getArtifactId().toLowerCase());
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SpigotRepository)) return false;
        if (!super.equals(o)) return false;
        SpigotRepository that = (SpigotRepository) o;
        return logger.equals(that.logger);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), logger);
    }
}
