package me.bristermitten.pdm.result;

import java.util.Collections;
import java.util.Set;

/**
 * @author AlexL
 */
public class LoadingResult
{

    public static final LoadingResult EMPTY = new LoadingResult(0, 0, Collections.emptySet());

    private final int downloadedDependencies;
    private final int totalLoadTime;
    private final Set<DependencyLoadingResult> dependencyLoadingResults;

    public LoadingResult(int downloadedDependencies, int totalLoadTime, Set<DependencyLoadingResult> dependencyLoadingResults)
    {
        this.downloadedDependencies = downloadedDependencies;
        this.totalLoadTime = totalLoadTime;
        this.dependencyLoadingResults = dependencyLoadingResults;
    }
}
