package me.bristermitten.pdmlibs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class URLUtil
{

    private URLUtil()
    {

    }

    @Nullable
    public static URLConnection prepareConnection(@NotNull final String urlText, @NotNull final String userAgent)
    {
        try
        {
            URL url = new URL(urlText);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }
            return connection;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @NotNull
    public static byte[] getBytes(@NotNull final String url, @NotNull final String userAgent)
    {
        URLConnection connection = URLUtil.prepareConnection(url, userAgent);
        if (connection == null)
        {
            return new byte[0];
        }
        try
        {
            return Streams.toByteArray(connection.getInputStream());
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }
}
