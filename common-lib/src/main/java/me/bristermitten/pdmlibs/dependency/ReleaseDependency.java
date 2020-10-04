package me.bristermitten.pdmlibs.dependency;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public class ReleaseDependency extends Dependency
{

    public ReleaseDependency(@NotNull final String groupId, @NotNull final String artifactId,
                             @NotNull final String version)
    {
        super(groupId, artifactId, version, null, null, null);
    }

    public ReleaseDependency(@NotNull final String groupId, @NotNull final String artifactId,
                             @NotNull final String version, @Nullable final String repoBaseURL,
                             @Nullable final Set<Dependency> transitive, @Nullable final Map<String, String> relocations)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL, relocations);
    }

    @Nullable
    @Override
    public URL getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service)
    {
        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".jar");
    }

    @Nullable
    @Override
    public URL getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService service)
    {
        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".pom");
    }
}
