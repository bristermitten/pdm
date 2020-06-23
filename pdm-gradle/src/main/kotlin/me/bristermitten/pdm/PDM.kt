package me.bristermitten.pdm

import com.google.gson.GsonBuilder
import me.bristermitten.pdmlibs.artifact.Artifact
import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.PomParser
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository
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
    private val artifactFactory = ArtifactFactory()


    override fun apply(project: Project)
    {
        val httpService = HTTPService(project.name)
        val pomParser = PomParser(artifactFactory)
        val repositoryFactory = MavenRepositoryFactory(httpService, pomParser)

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
                it.name to repositoryFactory.create(url)
            }.toMap()

            val dependencies = pdmConfiguration.allDependencies.mapNotNull { dependency ->
                val groupId = dependency.group ?: return@mapNotNull null
                val version = dependency.version ?: return@mapNotNull null

                artifactFactory.toArtifact(groupId, dependency.name, version, null, null).resolvePDMDependency(
                        extension.searchRepositories,
                        repositories
                )
            }

            val json = gson.toJson(
                    DependenciesConfiguration(
                            repositories.mapValues { it.value.url },
                            dependencies.toSet(),
                            extension.outputDirectory
                    )
            )
            val outputDir = File("${project.buildDir}/resources/main/")

            outputDir.mkdirs()
            outputDir.resolve("dependencies.json").writeText(json)
        }
    }

    private fun Artifact.resolvePDMDependency(searchRepositories: Boolean, repositories: Map<String, Repository>): PDMDependency
    {
        if (!searchRepositories)
        {
            return PDMDependency(groupId, artifactId, version, null, null)
        }

        val containingRepo = if (repoAlias != null)
        {
            repositories[repoAlias!!]
        } else
        {
            repositories.values.firstOrNull { repo ->
                repo.contains(this)
            }
        }

        if (containingRepo == null)
        {
            LOGGER.error("No repository found for dependency {}", this)
            return PDMDependency(groupId, artifactId, version, null, null)
        }

        val dependencies = containingRepo.getTransitiveDependencies(this)
                .map { it.resolvePDMDependency(searchRepositories, repositories) }
                .toSet()

        return PDMDependency(groupId, artifactId, version, repoAlias, dependencies)
    }
}
