package me.bristermitten.pdmexample;

import me.bristermitten.pdm.PDMBuilder;
import me.bristermitten.pdm.PluginDependencyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PDMExample extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        PluginDependencyManager dependencyManager = PluginDependencyManager.of(this);
        dependencyManager.loadAllDependencies().thenRun(
                () -> getLogger().info("All Loaded Asynchronously!"));
    }
}
