package me.bristermitten.pdmlibs.repository;

public class DownloadResponse
{

    private final boolean success;
    private final byte[] content;

    public DownloadResponse(boolean success, byte[] content)
    {
        this.success = success;
        this.content = content;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public byte[] getContent()
    {
        return content;
    }
}
