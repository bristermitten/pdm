package me.bristermitten.pdmlibs.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

public class DependencyFactory
{

    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
    private static final Pattern DESCRIPTOR_SEPARATOR = Pattern.compile(":");

    @NotNull
    public Dependency toArtifact(@NotNull final String artifactDescriptor)
    {
        final String[] parts = DESCRIPTOR_SEPARATOR.split(artifactDescriptor);
        final String group = parts[0];
        final String artifact = parts[1];
        final String version = parts[2];

        return toArtifact(group, artifact, version, null, null, null);
    }

    @NotNull
    public Dependency toArtifact(@NotNull final DependencyDTO dto)
    {
        Set<Dependency> transitive = null;
        if (dto.getTransitive() != null)
        {
            transitive = dto.getTransitive().stream().map(this::toArtifact).collect(toSet());
        }

        return toArtifact(dto.getGroupId(), dto.getArtifactId(), dto.getVersion(), dto.getRepositoryAlias(), transitive, dto.getRelocations());
    }

    @NotNull
    public Dependency toArtifact(@NotNull final String group, @NotNull final String artifact,
                                 @NotNull final String version, @Nullable final String repoAlias,
                                 @Nullable final Set<Dependency> transitive, @Nullable final Map<String, String> relocations)
    {
        if (version.endsWith(SNAPSHOT_SUFFIX))
        {
            return new SnapshotDependency(group, artifact, version, repoAlias, transitive, relocations);
        }

        return new ReleaseDependency(group, artifact, version, repoAlias, transitive, relocations);
    }
}
