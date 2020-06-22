package me.bristermitten.pdm.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPDownloadSupplier implements Supplier<byte[]>
{

    private final String downloadingFrom;
    private final Logger logger;

    public HTTPDownloadSupplier(String downloadingFrom, Logger logger)
    {
        this.downloadingFrom = downloadingFrom;
        this.logger = logger;
    }

    private URL getURL(String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException e)
        {
            logger.log(Level.SEVERE, e, () -> "Could not download content from URL" + downloadingFrom);
            return null;
        }
    }

    @Override
    public byte[] get()
    {
        URL url = getURL(downloadingFrom);
        if (url == null)
        {
            return new byte[0];
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Java-PPM");
            try (InputStream input = con.getInputStream())
            {
                int next;
                while ((next = input.read()) != -1)
                {
                    outputStream.write(next);
                }
            }
            return outputStream.toByteArray();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, e, () -> "Could not download content from URL" + downloadingFrom);
            return new byte[0];
        }
    }
}
