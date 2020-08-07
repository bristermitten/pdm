import me.bristermitten.pdm.PDMBuilder
import org.bukkit.plugin.java.JavaPlugin

class PDMExampleKotlin : JavaPlugin()
{

	override fun onEnable()
	{
		val dependencyManager = PDMBuilder(this).build()
		dependencyManager.loadAllDependencies().join()
	}
}
