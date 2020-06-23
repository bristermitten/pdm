package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdm.http.HTTPService
import me.bristermitten.pdm.repository.artifact.ReleaseArtifact
import me.bristermitten.pdm.repository.artifact.SnapshotArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.jvm.tasks.Jar
import org.slf4j.LoggerFactory
import java.io.File

class PDM : Plugin<Project>
{
    companion object
    {
        private val IGNORED_REPOS = setOf("MavenLocal")
        private val LOGGER = LoggerFactory.getLogger(PDM::class.java)
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun apply(project: Project)
    {
        val httpService = HTTPService(project.name)
        val extension = project.extensions.create("pdm", PDMExtension::class.java)
        val configurations = project.configurations
        val pdmConfiguration = configurations.create("pdm")
        val implementation = configurations.findByName("compileClasspath")

        implementation?.extendsFrom(pdmConfiguration)


        val pdmInternal = project.configurations.create("pdminternal")

        val pdmDependency = project.dependencies.add(pdmInternal.name, "me.bristermitten:pdm:${extension.version}")
        implementation?.dependencies?.add(pdmDependency)
        pdmInternal.dependencies.add(pdmDependency)


        project.task("pdm").doLast {
            val task = (project.tasks.getByName("jar") ?: project.task("jar"))
            (task as Jar).from(*pdmInternal.map { project.zipTree(it) }.toTypedArray())


            val mavenRepositories = project.repositories
                    .filterIsInstance<MavenArtifactRepository>().filter {
                        it.name !in IGNORED_REPOS
                    }

            val repositories = mavenRepositories.map {
                val url = if (it.name == "MavenRepo")
                {
                    extension.centralMirror
                } else
                {
                    it.url.toString()
                }
                it.name to url
            }.toMap()

            val dependencies = pdmConfiguration.allDependencies.mapNotNull { dependency ->
                val groupId = dependency.group ?: return@mapNotNull null
                val version = dependency.version ?: return@mapNotNull null


                val repoAlias = if (!extension.searchRepositories)
                {
                    null
                } else
                {
                    val artifact = if (version.endsWith("-SNAPSHOT"))
                    {
                        SnapshotArtifact(groupId, dependency.name, version)
                    } else
                    {
                        ReleaseArtifact(groupId, dependency.name, version)
                    }
                    val repoAlias = repositories.entries.firstOrNull { (_, repoURL) ->
                        val pomContent = httpService.downloadPom(repoURL, artifact)
                        pomContent.isNotEmpty()
                    }?.key
                    if (repoAlias == null)
                    {
                        LOGGER.error("No repository found for dependency {}", artifact)
                    }
                    repoAlias
                }

                PDMDependency(groupId, dependency.name, version, repoAlias)
            }

            val json = gson.toJson(
                    DependenciesConfiguration(
                            repositories,
                            dependencies.toSet(),
                            extension.outputDirectory
                    )
            )
            val outputDir = File("${project.buildDir}/resources/main/")

            outputDir.mkdirs()
            outputDir.resolve("dependencies.json").writeText(json)
        }
    }
}
