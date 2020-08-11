package me.bristermitten.pdmlibs.artifact;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Set;

public class ReleaseArtifact extends Artifact
{


    public ReleaseArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version, null, null);
    }

    public ReleaseArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @Nullable String repoBaseURL, @Nullable Set<Artifact> transitive)
    {
        super(groupId, artifactId, version, transitive, repoBaseURL);
    }

    @Override
    @Nullable
    public URL getJarURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".jar");
    }

    @Override
    @Nullable
    public URL getPomURL(@NotNull String baseRepoURL, @NotNull HTTPService service)
    {
        return URLs.parseURL(createBaseURL(baseRepoURL) + getArtifactId() + "-" + getVersion() + ".pom");
    }

}
