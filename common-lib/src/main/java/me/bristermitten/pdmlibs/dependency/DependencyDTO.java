package me.bristermitten.pdmlibs.dependency;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class DependencyDTO
{

    @NotNull
    private final String groupId;
    @NotNull
    private final String artifactId;
    @NotNull
    private final String version;

    @Nullable
    @SerializedName("repository")
    private final String repositoryAlias;

    @Nullable
    private final Set<DependencyDTO> transitive;

    @Nullable
    private final Map<String, String> relocations;

    public DependencyDTO(@NotNull final String groupId, @NotNull final String artifactId,
                         @NotNull final String version, @Nullable final String sourceRepository,
                         @Nullable final Set<DependencyDTO> transitive, @Nullable final Map<String, String> relocations)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repositoryAlias = sourceRepository;
        this.transitive = transitive;
        this.relocations = relocations;
    }

    @NotNull
    public String getGroupId()
    {
        return groupId;
    }

    @NotNull
    public String getArtifactId()
    {
        return artifactId;
    }

    @NotNull
    public String getVersion()
    {
        return version;
    }

    @Nullable
    public String getRepositoryAlias()
    {
        return repositoryAlias;
    }

    @Nullable
    public Set<DependencyDTO> getTransitive()
    {
        return transitive;
    }

    @Nullable
    public Map<String, String> getRelocations() {
        return relocations;
    }
}
