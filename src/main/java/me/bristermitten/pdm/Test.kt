package me.bristermitten.pdm

import org.bukkit.plugin.java.JavaPlugin

class Test : JavaPlugin()
{
    override fun onEnable()
    {
        val dependencyManager = PluginDependencyManager(this)
        dependencyManager.loadAllDependencies().thenAccept {
//            repeat(10) {
//                println("Kotlin works!")
//            }
        }
    }
}
