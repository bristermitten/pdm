package me.bristermitten.pdm.http;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class HTTPManager
{

    private final Logger logger;

    public HTTPManager(Logger logger)
    {
        this.logger = logger;
    }

    public CompletableFuture<byte[]> downloadRawContentFromURL(String url)
    {
        return CompletableFuture.supplyAsync(new HTTPDownloadSupplier(url, logger));
    }

    public CompletableFuture<Boolean> getURLStatus(String url)
    {
        return CompletableFuture.supplyAsync(new HTTPStatusSupplier(url, logger));
    }
}
