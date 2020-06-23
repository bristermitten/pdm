package me.bristermitten.pdmexample;

import me.bristermitten.pdm.PluginDependencyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PDMExample extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        PluginDependencyManager dependencyManager = new PluginDependencyManager(this, logger);
        dependencyManager.loadAllDependencies().thenRun(() -> {
            System.out.println("All loaded!");
        });
    }
}
