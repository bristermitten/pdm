package me.bristermitten.pdmlibs.config;

public final class CacheConfiguration
{

    private final boolean cachePoms;
    private final boolean cacheJars;
    private final boolean cacheParsedPoms;
    private final boolean cacheOtherData;

    public CacheConfiguration(boolean cachePoms, boolean cacheJars, boolean cacheParsedPoms, boolean cacheOtherData)
    {
        this.cachePoms = cachePoms;
        this.cacheJars = cacheJars;
        this.cacheParsedPoms = cacheParsedPoms;
        this.cacheOtherData = cacheOtherData;
    }

    public static Builder builder()
    {
        return new Builder();
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

        public Builder disablePomCaching()
        {
            cachePoms = false;
            return this;
        }

        public Builder disableJarCaching()
        {
            cacheJars = false;
            return this;
        }

        public Builder disableParsedPomCaching()
        {
            cacheParsedPoms = false;
            return this;
        }

        public Builder disableOtherDataCaching()
        {
            cacheOtherData = false;
            return this;
        }

        public CacheConfiguration build()
        {
            return new CacheConfiguration(cachePoms, cacheJars, cacheParsedPoms, cacheOtherData);
        }
    }
}
