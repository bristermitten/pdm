package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Set;

/**
 * Encapsulates a repository, usually an external Maven repository.
 * <p>
 * All methods may perform blocking web requests, but implementations may cache data where appropriate.
 */
public interface Repository
{

    @NotNull
    String getURL();

    /**
     * Download the Jar content of a given Artifact in bytes.
     * <p>
     * Downloading may fail, usually if the artifact is not present in this repository. In this case an empty array will be returned.
     * However, callers should usually check {@link Repository#contains(Artifact)} before calling this method.
     * <p>
     * This method will likely use {@link Repository#fetchJarContent(Artifact)} in its implementation (although this is not guaranteed),
     * so the same semantics usually apply.
     *
     * @param artifact the Artifact to download
     * @return an array containing the bytes of the downloaded jar, or an empty array if downloading failed.
     */
    @NotNull
    byte @NotNull [] download(@NotNull final Artifact artifact);

    /**
     * Get the Jar content of a given Artifact.
     * <p>
     * The returned stream should provide access to all of the bytes of the Jar's content,
     * and will be empty if the repository does not contain the given Artifact.
     *
     * @param artifact the Artifact to download
     * @return an {@link InputStream} containing the bytes of the Jar, or empty if downloading failed
     * @see Repository#download(Artifact)
     */
    @NotNull
    InputStream fetchJarContent(@NotNull final Artifact artifact);

    @NotNull
    Set<Artifact> getTransitiveDependencies(@NotNull final Artifact artifact);

    boolean contains(@NotNull final Artifact artifact);
}
