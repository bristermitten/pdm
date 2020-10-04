package me.bristermitten.pdmlibs.config;

import org.jetbrains.annotations.NotNull;

public final class CacheConfiguration
{

    private final boolean cachePoms;
    private final boolean cacheJars;
    private final boolean cacheParsedPoms;
    private final boolean cacheOtherData;

    private CacheConfiguration(final boolean cachePoms, final boolean cacheJars,
                               final boolean cacheParsedPoms, final boolean cacheOtherData)
    {
        this.cachePoms = cachePoms;
        this.cacheJars = cacheJars;
        this.cacheParsedPoms = cacheParsedPoms;
        this.cacheOtherData = cacheOtherData;
    }

    @NotNull
    public static Builder builder()
    {
        return new Builder();
    }

    @NotNull
    public static CacheConfiguration of(final boolean cachePoms, final boolean cacheJars,
                                        final boolean cacheParsedPoms, final boolean cacheOtherData) {
        return new CacheConfiguration(cachePoms, cacheJars, cacheParsedPoms, cacheOtherData);
    }

    public boolean cachePoms()
    {
        return cachePoms;
    }

    public boolean cacheJars()
    {
        return cacheJars;
    }

    public boolean cacheParsedPoms()
    {
        return cacheParsedPoms;
    }

    public boolean cacheOtherData()
    {
        return cacheOtherData;
    }

    public static class Builder
    {

        private boolean cachePoms = true;
        private boolean cacheJars = true;
        private boolean cacheParsedPoms = true;
        private boolean cacheOtherData = true;

        @NotNull
        public Builder disablePomCaching()
        {
            cachePoms = false;
            return this;
        }

        @NotNull
        public Builder disableJarCaching()
        {
            cacheJars = false;
            return this;
        }

        @NotNull
        public Builder disableParsedPomCaching()
        {
            cacheParsedPoms = false;
            return this;
        }

        @NotNull
        public Builder disableOtherDataCaching()
        {
            cacheOtherData = false;
            return this;
        }

        @NotNull
        public CacheConfiguration build()
        {
            return new CacheConfiguration(cachePoms, cacheJars, cacheParsedPoms, cacheOtherData);
        }
    }
}
