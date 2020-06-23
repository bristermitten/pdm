package me.bristermitten.pdm.http;

import me.bristermitten.pdm.repository.artifact.Artifact;
import me.bristermitten.pdm.util.URLUtil;
import org.jetbrains.annotations.NotNull;

public class HTTPService
{

    private static final String USER_AGENT_FORMAT = "Java-PDM; Plugin:%s";

    private final String userAgent;

    public HTTPService(@NotNull final String managing)
    {
        this.userAgent = String.format(USER_AGENT_FORMAT, managing);
    }

    @NotNull
    public byte[] downloadFrom(@NotNull final String url)
    {
        return URLUtil.getBytes(url, userAgent);
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
