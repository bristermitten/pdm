package me.bristermitten.pdm

import org.bukkit.plugin.java.JavaPlugin

class Test : JavaPlugin()
{
    override fun onEnable()
    {
        val dependencyManager = PluginDependencyManager(this)
        val dependencies = dependencyManager.loadAllDependencies()
        dependencies.thenAccept {
            repeat(10) {
                println("Kotlin works!")
            }
        }
    }
}
