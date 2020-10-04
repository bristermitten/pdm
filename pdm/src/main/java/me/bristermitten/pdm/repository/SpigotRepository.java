package me.bristermitten.pdm.repository;

import com.google.common.collect.ImmutableSet;
import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.ParseProcess;
import me.bristermitten.pdmlibs.repository.MavenRepository;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public final class SpigotRepository extends MavenRepository
{

    public static final String SPIGOT_ALIAS = "spigot-repo";

    @Unmodifiable private static final Set<String> SPIGOT_DEPENDENCY_GROUPS = new ImmutableSet.Builder<String>()
            .add(
                    "net.minecraft",
                    "org.spigotmc",
                    "org.bukkit",
                    "com.destroystokyo.paper"
            ).build();
    @Unmodifiable private static final Set<String> SPIGOT_DEPENDENCY_ARTIFACTS = new ImmutableSet.Builder<String>()
            .add(
                    "server",
                    "spigot",
                    "spigot-api",
                    "bukkit",
                    "craftbukkit",
                    "paper-api"
            ).build();
    private static final Logger LOGGER = Logger.getLogger("SpigotRepository");

    public SpigotRepository(@NotNull final HTTPService httpService, @NotNull final ParseProcess<Set<Dependency>> parseProcess)
    {
        super(SPIGOT_ALIAS, httpService, parseProcess);
    }


    @NotNull
    @Override
    public Set<Dependency> getTransitiveDependencies(@NotNull final Dependency dependency)
    {
        return Collections.emptySet();
    }

    @Override
    public boolean contains(@NotNull final Dependency dependency)
    {
        return isSpigotDependency(dependency);
    }

    @NotNull
    @Override
    public byte @NotNull [] download(@NotNull final Dependency dependency)
    {
        final String version = Bukkit.getVersion();

        if (!dependency.getVersion().contains(version))
        {
            LOGGER.warning(() -> "Dependency on " + dependency + " does not match server version of " + version + ". This could cause version problems.");
        }

        return new byte[0];
    }

    private boolean isSpigotDependency(@NotNull final Dependency dependency)
    {
        return SPIGOT_DEPENDENCY_GROUPS.contains(dependency.getGroupId().toLowerCase()) &&
                SPIGOT_DEPENDENCY_ARTIFACTS.contains(dependency.getArtifactId().toLowerCase());
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SpigotRepository)) return false;
        if (!super.equals(o)) return false;
        SpigotRepository that = (SpigotRepository) o;
        //todo: what the fuck is this?
        return LOGGER.equals(that.LOGGER);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), LOGGER);
    }
}
