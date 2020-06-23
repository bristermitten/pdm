package me.bristermitten.pdmlibs.artifact;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

public class ArtifactFactory
{

    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
    private static final Pattern DESCRIPTOR_SEPARATOR = Pattern.compile(":");

    @NotNull
    public Artifact toArtifact(@NotNull final String artifactDescriptor)
    {
        final String[] parts = DESCRIPTOR_SEPARATOR.split(artifactDescriptor);
        final String group = parts[0];
        final String artifact = parts[1];
        final String version = parts[2];

        return toArtifact(group, artifact, version, null, null);
    }

    @NotNull
    public Artifact toArtifact(@NotNull final ArtifactDTO dto)
    {
        Set<Artifact> transitive = null;
        if (dto.getTransitive() != null)
        {
            transitive = dto.getTransitive().stream().map(this::toArtifact).collect(toSet());
        }

        return toArtifact(dto.getGroupId(), dto.getArtifactId(), dto.getVersion(), dto.getRepositoryAlias(), transitive);
    }

    @NotNull
    public Artifact toArtifact(@NotNull final String group,
                               @NotNull final String artifact,
                               @NotNull final String version,
                               @Nullable final String repoAlias,
                               @Nullable final Set<Artifact> transitive)
    {

        if (version.endsWith(SNAPSHOT_SUFFIX))
        {
            return new SnapshotArtifact(group, artifact, version, repoAlias, transitive);
        }

        return new ReleaseArtifact(group, artifact, version, repoAlias, transitive);
    }
}
