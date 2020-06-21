package me.bristermitten.pdm.http;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPStatusSupplier implements BooleanSupplier
{

    private final String downloadingFrom;
    private final Logger logger;

    public HTTPStatusSupplier(String downloadingFrom, Logger logger)
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
    public boolean getAsBoolean()
    {
        URL url = getURL(downloadingFrom);
        if (url == null)
        {
            return false;
        }
        try
        {
            HttpURLConnection input = (HttpURLConnection) url.openConnection();
            input.connect();
            int responseCode = input.getResponseCode();
            return responseCode == 200;
        }
        catch (IOException e)
        {
            return false;
        }
    }
}
