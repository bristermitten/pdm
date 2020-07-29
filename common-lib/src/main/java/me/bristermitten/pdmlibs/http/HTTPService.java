package me.bristermitten.pdmlibs.http;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.util.Streams;
import me.bristermitten.pdmlibs.util.URLUtil;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HTTPService
{

    private static final String USER_AGENT_FORMAT = "PDM/%s; Plugin:%s";

    private final String userAgent;

    public HTTPService(@NotNull final String managing, @NotNull final String version)
    {
        this.userAgent = String.format(USER_AGENT_FORMAT, version, managing);
    }

    @NotNull
    public InputStream readFrom(@NotNull final String url)
    {
        return URLUtil.read(url, userAgent);
    }

    public boolean ping(@NotNull final String url)
    {
        return URLUtil.prepareConnection(url, userAgent) != null;
    }

    @NotNull
    public InputStream read(@NotNull final String url, @NotNull final Artifact artifact)
    {
        String jarURL = artifact.getJarURL(url, this);
        if (jarURL == null)
        {
            return new ByteArrayInputStream(new byte[0]);
        }
        return readFrom(jarURL);
    }

    @NotNull
    public InputStream readPom(@NotNull final String url, @NotNull final Artifact artifact)
    {
        String pomURL = artifact.getPomURL(url, this);
        if (pomURL == null)
        {
            return Streams.createEmptyStream();
        }
        return readFrom(pomURL);
    }
}
