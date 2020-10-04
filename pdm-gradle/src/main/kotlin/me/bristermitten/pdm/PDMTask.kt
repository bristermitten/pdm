package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdm.json.RepositoryTypeAdapter
import me.bristermitten.pdmlibs.dependency.DependencyFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.DefaultParseProcess
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository
import me.bristermitten.pdmlibs.repository.RepositoryManager
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.jvm.tasks.Jar
import java.io.File

class PDMTask(
		private val config: PDMExtension,
		private val pdmDependency: Configuration,
		private val dependencyFactory: DependencyFactory,
		private val repositoryManager: RepositoryManager,
		private val dependenciesTask: PDMGenDependenciesTask
) : (Project, Task) -> Unit
{

	override fun invoke(project: Project, task: Task)
	{
		if (config.bundlePDMRuntime)
		{
			setupBundling(project)
		}

		val httpService = HTTPService(project.name, config.version, config.caching)
		val repositoryFactory = MavenRepositoryFactory(httpService, DefaultParseProcess(dependencyFactory, repositoryManager, httpService))
		val gson = GsonBuilder().registerTypeAdapter(Repository::class.java, RepositoryTypeAdapter(repositoryFactory)).create()

		val state = dependenciesTask.generateProjectState(project, repositoryFactory)

		val cacheFile = project.buildDir.resolve("tmp").resolve("pdm").resolve("cache")
		if (cacheFile.exists())
		{
			val cached = gson.fromJson(cacheFile.reader(), ProjectState::class.java)
			if (cached == state)
			{
				val depsFile = File("${project.buildDir}/resources/main/dependencies.json")
				if (depsFile.exists().not())
				{
					cacheFile.copyTo(depsFile, false) //just in case it doesn't exist, load the cached one
				}
				return //If nothing has changed, then nothing needs querying.
			}
		}

		cacheFile.parentFile.mkdirs()
		cacheFile.writeText(gson.toJson(state))

		dependenciesTask.process(state, project)
	}

	@Suppress("UnstableApiUsage")
	private fun setupBundling(project: Project)
	{
		val jarTask = (project.tasks.getByName("jar") ?: project.task("jar")) as Jar

		jarTask.from(pdmDependency.map {
			@Suppress("IMPLICIT_CAST_TO_ANY")
			if (it.isDirectory) it else project.zipTree(it)
		})
	}


}
