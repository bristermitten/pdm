package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.ParseProcess;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MavenRepositoryFactory
{

    @NotNull
    private final HTTPService httpService;

    @NotNull
    private final ParseProcess<Set<Artifact>> parseProcess;


    public MavenRepositoryFactory(@NotNull HTTPService httpService, @NotNull final ParseProcess<Set<Artifact>> parseProcess)
    {
        this.httpService = httpService;
        this.parseProcess = parseProcess;
    }

    public Repository create(@NotNull final String baseURL)
    {
        return new MavenRepository(baseURL, httpService, parseProcess);
    }
}
