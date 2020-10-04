package me.bristermitten.pdmlibs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public final class URLs
{

    private URLs()
    {

    }


    @Nullable
    public static URLConnection prepareConnection(@NotNull final URL url, @NotNull final String userAgent)
    {
        try
        {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }

            return connection;
        }
        catch (IOException exception)
        {
            return null;
        }
    }

    @NotNull
    public static byte[] getBytes(@NotNull final URL url, @NotNull final String userAgent)
    {
        try (final InputStream inputStream = read(url, userAgent))
        {
            return Streams.toByteArray(inputStream);
        }
        catch (IOException exception)
        {
            return new byte[0];
        }
    }

    @NotNull
    public static InputStream read(@NotNull final URL url, @NotNull final String userAgent)
    {
        final URLConnection connection = URLs.prepareConnection(url, userAgent);

        if (connection == null)
        {
            return Streams.createEmptyStream();
        }
        try
        {
            return connection.getInputStream();
        }
        catch (IOException e)
        {
            return Streams.createEmptyStream();
        }
    }

    @Nullable
    public static URL parseURL(@NotNull final String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException exception)
        {
            return null;
        }
    }
}
