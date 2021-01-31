package me.bristermitten.pdmexample

import me.bristermitten.pdm.PluginDependencyManager
import org.bukkit.plugin.java.JavaPlugin

class PDMExampleKotlin : JavaPlugin()
{
	override fun onEnable()
	{
		/*
		Note that these methods must be chained in Kotlin to avoid Intrinsics generation, which will cause
		ClassNotFoundExceptions if the Kotlin stdlib is not present on the classpath.

		Alternatively, make a Java bootstrap class that calls PDM, or use any of Kotlin's null safety operators
		which should also avoid this issue
		 */
		PluginDependencyManager.of(this) //Create a new PluginDependencyManager with data from the JavaPlugin
			.loadAllDependencies() //Reads dependencies.json and starts downloading / loading all dependencies async
			.join() //Block until all dependencies are downloaded - in production a callback is advised to avoid lag
	}
}
