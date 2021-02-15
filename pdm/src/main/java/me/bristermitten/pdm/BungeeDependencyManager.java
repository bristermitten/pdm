package me.bristermitten.pdm;

import org.jetbrains.annotations.NotNull;

import java.net.URLClassLoader;

public class BungeeDependencyManager
{
    /**
     * Creates a new instance of the {@link PluginDependencyManager} from the given
     * BungeeCord plugin class.
     *
     * Example usage:
     * <code>
     *     PluginDependencyManager dependencyManager = PluginDependencyManager.of(this);
     *     dependencyManager.loadAllDependencies();
     * </code>
     *
     * @param plugin the plugin instance
     * @return instance of {@link PluginDependencyManager} from the plugin class
     */
    @NotNull
    public static PluginDependencyManager of(@NotNull final net.md_5.bungee.api.plugin.Plugin plugin)
    {
        return builder(plugin)
                .build();
    }

    /**
     * Create a new builder for {@link PluginDependencyManager} with configuration
     * from the given BungeeCord plugin instance
     *
     * @param plugin the plugin instance
     * @return a builder with configuration from the plugin
     */
    @NotNull
    public static PluginDependencyManager.Builder builder(@NotNull final net.md_5.bungee.api.plugin.Plugin plugin)
    {
        return new PluginDependencyManager.Builder()
                .applicationName(plugin.getDescription().getName())
                .applicationVersion(plugin.getDescription().getVersion())
                .classLoader((URLClassLoader) plugin.getClass().getClassLoader())
                .rootDirectory(plugin.getDataFolder().getParentFile())
                .dependenciesResource(plugin.getResourceAsStream(PluginDependencyManager.Builder.DEPENDENCIES_RESOURCE_NAME))
                .loggerFactory(clazz -> plugin.getLogger());
    }
}
