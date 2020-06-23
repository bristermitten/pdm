package me.bristermitten.pdmlibs.repository;

import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.pom.PomParser;
import org.jetbrains.annotations.NotNull;

public class MavenRepositoryFactory
{

    @NotNull
    private final HTTPService httpService;
    @NotNull
    private final PomParser pomParser;

    public MavenRepositoryFactory(@NotNull HTTPService httpService, @NotNull PomParser pomParser)
    {
        this.httpService = httpService;
        this.pomParser = pomParser;
    }

    public Repository create(@NotNull final String baseURL)
    {
        return new MavenRepository(baseURL, httpService, pomParser);
    }
}
