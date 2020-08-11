package me.bristermitten.pdmlibs.http;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.config.CacheConfiguration;
import me.bristermitten.pdmlibs.util.Streams;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

public class HTTPService
{

    private static final String USER_AGENT_FORMAT = "PDM/%s; Plugin:%s";

    private final String userAgent;
    private final HTTPCache cache;
    private final CacheConfiguration cacheConfiguration;

    public HTTPService(@NotNull final String managing, @NotNull final String version, CacheConfiguration cacheConfiguration)
    {
        this.cacheConfiguration = cacheConfiguration;
        this.userAgent = String.format(USER_AGENT_FORMAT, version, managing);
        this.cache = new HTTPCache(userAgent);
    }

    @NotNull
    public InputStream readFrom(@NotNull final URL url)
    {
        return readFrom(url, URLType.OTHER);
    }

    @NotNull
    public InputStream readFrom(@NotNull final URL url, @NotNull final URLType urlType)
    {
        if (urlType.canBeCached(cacheConfiguration))
        {
            return new ByteArrayInputStream(cache.fetch(url));
        }
        return URLs.read(url, userAgent);
    }

    public boolean ping(@NotNull final URL url)
    {
        if (cache.contains(url))
        {
            return true;
        }
        return URLs.prepareConnection(url, userAgent) != null;
    }

    @NotNull
    public InputStream readJar(@NotNull final String repoURL, @NotNull final Artifact artifact)
    {
        URL jarURL = artifact.getJarURL(repoURL, this);
        if (jarURL == null)
        {
            return new ByteArrayInputStream(new byte[0]);
        }
        return readFrom(jarURL, URLType.JAR);
    }

    @NotNull
    public InputStream readPom(@NotNull final String repoURL, @NotNull final Artifact artifact)
    {
        URL pomURL = artifact.getPomURL(repoURL, this);
        if (pomURL == null)
        {
            return Streams.createEmptyStream();
        }
        return readFrom(pomURL, URLType.POM);
    }
}
