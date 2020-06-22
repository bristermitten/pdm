package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdm.repository.MavenCentralRepository
import me.bristermitten.pdm.repository.artifact.ReleaseArtifact
import me.bristermitten.pdm.repository.artifact.SnapshotArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.jvm.tasks.Jar
import java.io.File

class PDM : Plugin<Project>
{
    companion object
    {
        private val IGNORED_REPOS = setOf("MavenLocal")
        private val REMAPPED_REPOS = mapOf("MavenRepo" to MavenCentralRepository.MAVEN_CENTRAL_ALIAS)
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun apply(project: Project)
    {
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
                (REMAPPED_REPOS[it.name] ?: it.name) to it.url.toString()
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
                    DependenciesConfiguration(repositories.filterKeys {
                        it !in REMAPPED_REPOS.values
                    }, dependencies.toSet())
            )
            val outputDir = File("${project.buildDir}/resources/main/")

            outputDir.mkdirs()
            outputDir.resolve("dependencies.json").writeText(json)
        }
    }
}
