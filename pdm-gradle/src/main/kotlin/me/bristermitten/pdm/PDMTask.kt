package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdm.json.RepositoryTypeAdapter
import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.PomParser
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.jvm.tasks.Jar

class PDMTask(
        private val config: PDMExtension,
        private val pdmDependency: Configuration,
        private val artifactFactory: ArtifactFactory,
        private val dependenciesTask: PDMGenDependenciesTask
) : (Project, Task) -> Unit
{

    override fun invoke(project: Project, task: Task)
    {
        @Suppress("UnstableApiUsage")
        if (config.bundlePDMRuntime)
        {
            setupBundling(project)
        }

        val httpService = HTTPService(project.name, config.version)
        val pomParser = PomParser(artifactFactory)
        val repositoryFactory = MavenRepositoryFactory(httpService, pomParser)
        val gson = GsonBuilder().registerTypeAdapter(Repository::class.java, RepositoryTypeAdapter(repositoryFactory)).create()

        val state = dependenciesTask.generateProjectState(project, repositoryFactory)

        val cacheFile = project.buildDir.resolve("tmp").resolve("pdm").resolve("cache")
        if (cacheFile.exists())
        {
            val cached = gson.fromJson(cacheFile.reader(), ProjectState::class.java)
            if (cached == state)
            {
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
