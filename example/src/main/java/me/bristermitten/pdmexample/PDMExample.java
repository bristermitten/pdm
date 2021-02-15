package me.bristermitten.pdmexample;

import me.bristermitten.pdm.PluginDependencyManager;
import me.bristermitten.pdm.SpigotDependencyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PDMExample extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        //Create a new PluginDependencyManager with data from the JavaPlugin
        PluginDependencyManager dependencyManager = SpigotDependencyManager.of(this);

        dependencyManager
                .loadAllDependencies() //Reads dependencies.json and starts downloading / loading all dependencies async
                .thenRun(() -> getLogger().info("All Loaded Asynchronously!")); //The lambda will run once everything is loaded

        /*
        Note that you can also force the server to wait for the dependencies to be downloaded with join(),
        but this is not advised as it can cause lag. See PDMExampleKotlin.kt for an example of this
         */
    }
}
