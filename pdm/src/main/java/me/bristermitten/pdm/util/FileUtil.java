package me.bristermitten.pdm.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class FileUtil
{

    private FileUtil()
    {
    }

    /**
     * Create a given directory if it does not exist.
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
}
