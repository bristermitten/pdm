package me.bristermitten.pdm

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class PDMInvalidateCacheTask @Inject constructor(private val state: PDMProjectState) : DefaultTask()
{
	@TaskAction
	fun execute()
	{
		if (state.cacheFile.exists())
		{
			state.cacheFile.deleteRecursively()
			project.logger.info("Removed PDM Cache file.")
		} else
		{
			project.logger.info("PDM Cache file didn't exist, nothing to remove.")
		}
	}
}
