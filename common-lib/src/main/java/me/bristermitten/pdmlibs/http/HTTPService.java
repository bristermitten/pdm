package me.bristermitten.pdmlibs.http;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.util.URLUtil;
import org.jetbrains.annotations.NotNull;

public class HTTPService
{

    private static final String USER_AGENT_FORMAT = "PDM/%s; Plugin:%s";

    private final String userAgent;
    private final String version;

    public HTTPService(@NotNull final String managing, @NotNull final String version)
    {
        this.userAgent = String.format(USER_AGENT_FORMAT, version, managing);
        this.version = version;
    }

    @NotNull
    public byte[] downloadFrom(@NotNull final String url)
    {
        return URLUtil.getBytes(url, userAgent);
    }

    @NotNull
    public boolean ping(@NotNull final String url)
    {
        return URLUtil.prepareConnection(url, userAgent) != null;
    }

    @NotNull
    public byte[] download(@NotNull final String url, @NotNull final Artifact artifact)
    {
        String jarURL = artifact.getJarURL(url, this);
        if (jarURL == null)
        {
            return new byte[0];
        }
        return downloadFrom(jarURL);
    }

    @NotNull
    public byte[] downloadPom(@NotNull final String url, @NotNull final Artifact artifact)
    {
        String pomURL = artifact.getPomURL(url, this);
        if (pomURL == null)
        {
            return new byte[0];
        }
        return downloadFrom(pomURL);
    }
}
