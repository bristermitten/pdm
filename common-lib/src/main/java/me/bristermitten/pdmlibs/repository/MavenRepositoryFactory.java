package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.ParseProcess;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MavenRepositoryFactory
{

    @NotNull
    private final HTTPService httpService;

    @NotNull
    private final ParseProcess<Set<Dependency>> parseProcess;


    public MavenRepositoryFactory(@NotNull final HTTPService httpService, @NotNull final ParseProcess<Set<Dependency>> parseProcess)
    {
        this.httpService = httpService;
        this.parseProcess = parseProcess;
    }

    @NotNull
    public Repository create(@NotNull final String baseURL)
    {
        return new MavenRepository(baseURL, httpService, parseProcess);
    }
}
