package me.bristermitten.pdmexample

import me.bristermitten.pdm.PDMBuilder
import me.bristermitten.pdm.PluginDependencyManager
import org.bukkit.plugin.java.JavaPlugin


class PDMExampleKotlin : JavaPlugin()
{

	override fun onEnable()
	{
		val dependencyManager = PDMBuilder(this).build() ?: return //Necessary to bypass Intrinsics generation
		dependencyManager.loadAllDependencies().join()
	}
}
