package me.bristermitten.pdmlibs.http;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.bristermitten.pdmlibs.util.URLs;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

import static java.util.Objects.requireNonNull;

public class HTTPCache
{

    private final String userAgent;
    private final @NotNull LoadingCache<URL, byte[]> urlCache;

    public HTTPCache(String userAgent)
    {
        this.userAgent = userAgent;
        urlCache = CacheBuilder.newBuilder().weakValues()
                .build(CacheLoader.from(input -> URLs.getBytes(requireNonNull(input), this.userAgent)));
    }


    @NotNull
    public byte[] fetch(@NotNull final URL url)
    {
        return urlCache.getUnchecked(url);
    }

    public boolean contains(@NotNull final URL url)
    {
        return urlCache.getIfPresent(url) != null;
    }
}
