package me.bristermitten.pdm

import me.bristermitten.pdm.dependency.Dependency
import org.bukkit.plugin.java.JavaPlugin

class Test : JavaPlugin()
{
    override fun onEnable()
    {
        val dependencyManager = DependencyManager(this)
        dependencyManager.downloadAndLoad(
                Dependency(
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib-jdk8",
                        "1.3.72"
                )
        ).thenAccept {
            repeat(10) {
                println("Kotlin works!")
            }
        }
    }
}
