package me.bristermitten.pdm.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class FileUtil
{

    private FileUtil()
    {
    }

    /**
     * Create a given directory if it does not exist.
     *
     * @param file the directory to create.
     */
    public static void createDirectoryIfNotPresent(@NotNull final File file)
    {
        if (!file.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
    }

    public static void writeFrom(@NotNull final File file, @NotNull final InputStream inputStream) throws IOException
    {
        try (final ReadableByteChannel channel = Channels.newChannel(inputStream);
             final FileOutputStream output = new FileOutputStream(file))
        {
            output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        }
    }
}
