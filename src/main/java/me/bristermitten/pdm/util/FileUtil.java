package me.bristermitten.pdm.util;

import java.io.File;

public final class FileUtil
{

    private FileUtil()
    {
    }

    public static void createDirectoryIfNotPresent(File file)
    {
        if (!file.exists())
        {
            file.mkdirs();
        }
    }
}
