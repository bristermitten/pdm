package me.bristermitten.pdmlibs.http;

import me.bristermitten.pdmlibs.config.CacheConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum URLType
{
    JAR(CacheConfiguration::cacheJars),
    POM(CacheConfiguration::cachePoms),
    OTHER(CacheConfiguration::cacheOtherData);

    private final Predicate<CacheConfiguration> allowedCheck;

    URLType(Predicate<CacheConfiguration> allowedCheck)
    {
        this.allowedCheck = allowedCheck;
    }

    public boolean canBeCached(@NotNull final CacheConfiguration configuration)
    {
        return allowedCheck.test(configuration);
    }

}
