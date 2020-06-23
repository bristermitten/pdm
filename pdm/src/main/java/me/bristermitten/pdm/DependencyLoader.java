package me.bristermitten.pdm;

import me.bristermitten.pdm.util.ClassLoaderReflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyLoader
{

    @NotNull
    private final URLClassLoader classLoader;
    @NotNull
    private final Logger logger;

    @NotNull
    private final Set<File> loaded = new HashSet<>();

    public DependencyLoader(@NotNull final URLClassLoader classLoader, @NotNull final Logger logger)
    {
        this.classLoader = classLoader;
        this.logger = logger;
    }

    public void loadDependency(@Nullable final File file)
    {
        if (file == null)
        {
            return;
        }
        if (loaded.contains(file))
        {
            return;
        }
        try
        {
            ClassLoaderReflection.addURL(classLoader, file.toURI().toURL());
            loaded.add(file);
        }
        catch (MalformedURLException e)
        {
            logger.log(Level.SEVERE, e, () -> "Could not load dependency from file " + file);
        }
    }
}
