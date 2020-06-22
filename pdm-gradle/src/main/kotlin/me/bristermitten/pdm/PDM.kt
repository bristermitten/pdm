package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdm.repository.artifact.ReleaseArtifact
import me.bristermitten.pdm.repository.artifact.SnapshotArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.jvm.tasks.Jar
import java.io.File

class PDM : Plugin<Project>
{
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val existingRepoNames = setOf(
            "MavenRepo",
            "MavenLocal"
    )

    override fun apply(project: Project)
    {
        val configurations = project.configurations
        val pdmConfiguration = configurations.create("pdm")
        val implementation = configurations.findByName("compileClasspath")

        implementation?.extendsFrom(pdmConfiguration)


        val pdmInternal = project.configurations.create("pdminternal")

        val pdmDependency = project.dependencies.add(pdmInternal.name, "me.bristermitten:pdm:1.0-SNAPSHOT")
        implementation?.dependencies?.add(pdmDependency)
        pdmInternal.dependencies.add(pdmDependency)


        project.task("pdm").doLast {

            val task = (project.tasks.getByName("jar") ?: project.task("jar"))
            (task as Jar).from(*pdmInternal.map { project.zipTree(it) }.toTypedArray())


            val mavenRepositories = project.repositories
                    .filterIsInstance<MavenArtifactRepository>().filter {
                        it.name !in existingRepoNames
                    }

            val repositories = mavenRepositories.map {
                it.name to it.url.toString()
            }.toMap()

            val dependencies = pdmConfiguration.allDependencies.mapNotNull { dependency ->
                val groupId = dependency.group ?: return@mapNotNull null
                val version = dependency.version ?: return@mapNotNull null

                val artifact = if (version.endsWith("-SNAPSHOT"))
                {
                    SnapshotArtifact(groupId, dependency.name, version)
                } else
                {
                    ReleaseArtifact(groupId, dependency.name, version)
                }

                val repo = mavenRepositories.firstOrNull { repo ->
                    val downloadPom = artifact.downloadPom(repo.url.toString())
                    downloadPom != null
                }
                val repoAlias = repositories.entries.firstOrNull {
                    it.value == repo?.url?.toString()
                }?.key
                PDMDependency(groupId, dependency.name, version, repoAlias)
            }

            val json = gson.toJson(
                    PDMConfig(repositories, dependencies.toSet())
            )
            val outputDir = File("${project.buildDir}/resources/main/")

            outputDir.mkdirs()
            outputDir.resolve("dependencies.json").writeText(json)
        }
    }
}
