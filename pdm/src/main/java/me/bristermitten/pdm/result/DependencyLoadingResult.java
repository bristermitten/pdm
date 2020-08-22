package me.bristermitten.pdm.result;

import me.bristermitten.pdmlibs.artifact.Artifact;

import java.util.Set;

/**
 * @author AlexL
 */
public class DependencyLoadingResult
{

    private final Artifact artifact;
    private final int transitiveDependenciesCount;
    private final Set<DependencyLoadingResult> transitiveDependencies;
    private final long downloadTime;

    public DependencyLoadingResult(Artifact artifact, int transitiveDependenciesCount, Set<DependencyLoadingResult> transitiveDependencies, long downloadTime)
    {
        this.artifact = artifact;
        this.transitiveDependenciesCount = transitiveDependenciesCount;
        this.transitiveDependencies = transitiveDependencies;
        this.downloadTime = downloadTime;
    }
}
