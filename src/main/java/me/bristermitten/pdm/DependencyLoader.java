package me.bristermitten.pdm;

import me.bristermitten.pdm.util.ClassLoaderReflection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class DependencyLoader
{

    private final Plugin plugin;
    private final Set<File> loaded = new HashSet<>();

    public DependencyLoader(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public void loadDependency(File file)
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
            URLClassLoader classLoader = (URLClassLoader) plugin.getClass().getClassLoader();
            ClassLoaderReflection.addURL(classLoader, file.toURI().toURL());
            loaded.add(file);
        }
        catch (MalformedURLException e)
        {
            plugin.getLogger().log(Level.SEVERE, e, () -> "Could not load dependency from file " + file);
        }
    }
}
