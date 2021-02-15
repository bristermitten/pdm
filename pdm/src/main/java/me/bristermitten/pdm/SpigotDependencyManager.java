package me.bristermitten.pdm;

import me.bristermitten.pdmlibs.util.Reflection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.logging.Logger;

public final class SpigotDependencyManager
{

    public static final String PLUGIN_CLASS_LOADER_NAME = "org.bukkit.plugin.java.PluginClassLoader";

    private SpigotDependencyManager()
    {

    }

    /**
     * Create a new builder for {@link PluginDependencyManager} with configuration
     * from the given Bukkit (Spigot) plugin instance
     *
     * @param plugin the plugin instance
     * @return a builder with configuration from the plugin
     */
    @NotNull
    public static PluginDependencyManager of(@NotNull final Plugin plugin)
    {
        return builder(plugin).build();
    }


    /**
     * Create a new builder for {@link PluginDependencyManager} with configuration
     * from the given Bukkit (Spigot) plugin class.
     * This method is provided in case callers wish to apply PDM before the plugin is initialized, and will use
     * {@link ClassLoader#getResource(String)} instead of {@link org.bukkit.plugin.java.JavaPlugin#getResource(String)}
     *
     * @param plugin the plugin instance
     * @return a builder with configuration from the plugin
     */
    @NotNull
    public static PluginDependencyManager of(@NotNull final Class<? extends Plugin> plugin)
    {
        return builder(plugin)
                .build();
    }

    @NotNull
    public static PluginDependencyManager.Builder builder(@NotNull final Plugin plugin)
    {
        final InputStream resource = plugin.getResource(PluginDependencyManager.Builder.DEPENDENCIES_RESOURCE_NAME);
        return new PluginDependencyManager.Builder()
                .loggerFactory(clazz -> plugin.getLogger())
                .dependenciesResource(Objects.requireNonNull(resource, "No dependencies.json file in jar"))
                .rootDirectory(plugin.getDataFolder().getParentFile())
                .classLoader((URLClassLoader) plugin.getClass().getClassLoader())
                .applicationName(plugin.getName())
                .applicationVersion(plugin.getDescription().getVersion());
    }


    @NotNull
    public static PluginDependencyManager.Builder builder(@NotNull final Class<? extends Plugin> plugin)
    {
        if (!PLUGIN_CLASS_LOADER_NAME.equals(plugin.getClassLoader().getClass().getName()))
        {
            throw new IllegalArgumentException("Plugin must be loaded with a PluginClassLoader");
        }

        final URLClassLoader classLoader = (URLClassLoader) plugin.getClassLoader();
        final PluginDescriptionFile description = Reflection.getFieldValue(classLoader, "description");

        final InputStream resource = classLoader.getResourceAsStream(PluginDependencyManager.Builder.DEPENDENCIES_RESOURCE_NAME);
        return PluginDependencyManager.builder()
                .classLoader((URLClassLoader) plugin.getClassLoader())
                .dependenciesResource(Objects.requireNonNull(resource, "No dependencies.json file on classpath"))
                .rootDirectory(new File("./plugins"))
                .applicationName(description.getName())
                .applicationVersion(description.getVersion())
                .loggerFactory(clazz -> Logger.getLogger(description.getName()));
    }

}
