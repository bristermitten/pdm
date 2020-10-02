package me.bristermitten.pdm;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLClassLoader;
import java.util.function.Function;
import java.util.logging.Logger;

public class PDMSettings
{

    private final File rootDirectory;
    private final Function<String, Logger> loggerSupplier;
    private final URLClassLoader classLoader;

    public PDMSettings(@NotNull final File rootDirectory, @NotNull final Function<String, Logger> loggerSupplier,
                       @NotNull final URLClassLoader classLoader)
    {
        this.rootDirectory = rootDirectory;
        this.loggerSupplier = loggerSupplier;
        this.classLoader = classLoader;
    }

    @NotNull
    public URLClassLoader getClassLoader()
    {
        return classLoader;
    }

    @NotNull
    public File getRootDirectory()
    {
        return rootDirectory;
    }

    @NotNull
    public Function<String, Logger> getLoggerSupplier()
    {
        return loggerSupplier;
    }

}
