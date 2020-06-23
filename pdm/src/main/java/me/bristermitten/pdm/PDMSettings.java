package me.bristermitten.pdm;

import java.io.File;
import java.net.URLClassLoader;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class PDMSettings
{

    private final File rootDirectory;
    private final Supplier<Logger> loggerSupplier;
    private final URLClassLoader classLoader;

    public PDMSettings(File rootDirectory, Supplier<Logger> loggerSupplier, URLClassLoader classLoader)
    {
        this.rootDirectory = rootDirectory;
        this.loggerSupplier = loggerSupplier;
        this.classLoader = classLoader;
    }

    public URLClassLoader getClassLoader()
    {
        return classLoader;
    }

    public File getRootDirectory()
    {
        return rootDirectory;
    }

    public Supplier<Logger> getLoggerSupplier()
    {
        return loggerSupplier;
    }
}
