package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Set;

public class ReleaseArtifact extends Artifact
{

    public ReleaseArtifact(@NotNull final String groupId, @NotNull final String artifactId,
                           @NotNull final String version)
    {
        super(groupId, artifactId, version, null, null);
    }

    public ReleaseArtifact(@NotNull final String groupId, @NotNull final String artifactId,
                           @NotNull final String version, @Nullable final String repoBaseURL,
                           @Nullable final Set<Artifact> transitive)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL);
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
