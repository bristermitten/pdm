package me.bristermitten.pdm.util;

import com.google.common.io.ByteStreams;
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
    public static URLConnection prepareConnection(@NotNull final String urlText)
    {
        try
        {
            URL url = new URL(urlText);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Java-PDM");
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

    @Nullable
    public static byte[] getBytes(@NotNull final String url)
    {
        URLConnection connection = URLUtil.prepareConnection(url);
        if (connection == null)
        {
            return null;
        }
        try
        {
            return ByteStreams.toByteArray(connection.getInputStream());
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
