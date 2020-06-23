package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

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
     * Download the Jar content of a given artifact in bytes.
     *
     * Downloading may fail, usually if the artifact is not present in this repository. In this case an empty array will be returned.
     * @param artifact the artifact to download
     * @return an array containing the bytes of the downloaded jar, or an empty array if downloading failed.
     */
    @NotNull
    byte[] download(@NotNull final Artifact artifact);

    @NotNull
    Set<Artifact> getTransitiveDependencies(@NotNull final Artifact artifact);

    boolean contains(@NotNull final Artifact artifact);
}
