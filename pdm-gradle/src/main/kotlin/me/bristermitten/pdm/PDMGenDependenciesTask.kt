package me.bristermitten.pdm

import com.google.gson.Gson
import me.bristermitten.pdm.json.ArtifactDTO
import me.bristermitten.pdm.json.DependenciesConfiguration
import me.bristermitten.pdm.json.PDMDependency
import me.bristermitten.pdmlibs.artifact.Artifact
import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.PomParser
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.slf4j.LoggerFactory
import java.io.File

class PDMGenDependenciesTask(
        private val artifactFactory: ArtifactFactory,
        private val pdmDependency: Configuration,
        private val config: PDMExtension,
        private val gson: Gson = Gson()
) : (Project) -> Unit
{
    companion object
    {
        private val LOGGER = LoggerFactory.getLogger(PDMGenDependenciesTask::class.java)
    }

    private fun generateProjectState(project: Project): ProjectState
    {
        val httpService = HTTPService(project.name, config.version)
        val pomParser = PomParser(artifactFactory)
        val repositoryFactory = MavenRepositoryFactory(httpService, pomParser)
        return generateProjectState(project, repositoryFactory)
    }

    fun generateProjectState(project: Project, repositoryFactory: MavenRepositoryFactory): ProjectState
    {
        val repositories = project.repositories.asSequence()
                .filterIsInstance<MavenArtifactRepository>()
                .filter {
                    it.name !in IGNORED_REPOS
                }
                .map {
                    val url = REPOSITORY_URL_MAPPINGS[it.name]?.invoke(config) ?: it.url.toString()
                    it.name to repositoryFactory.create(url)
                }.toMap()

        val dependencies = pdmDependency.allDependencies.mapNotNull {
            val group = it.group ?: return@mapNotNull null
            val version = it.version ?: return@mapNotNull null
            ArtifactDTO(group, it.name, version)
        }.toSet()

        return ProjectState(repositories, dependencies)
    }

    private fun process(state: ProjectState, outputDirectory: File)
    {
        val artifacts = state.dependencies.map {

            val artifact = artifactFactory.toArtifact(it.group, it.artifact, it.version, null, null)

            artifact.resolvePDMDependency(
                    config.spigot,
                    config.searchRepositories,
                    state.repos
            )
        }.toSet()

        val json = gson.toJson(
                DependenciesConfiguration(
                        state.repos.mapValues { it.value.url },
                        artifacts,
                        config.outputDirectory
                )
        )

        outputDirectory.resolve("dependencies.json").writeText(json)
    }

    fun process(state: ProjectState, project: Project)
    {

        val outputDir = File("${project.buildDir}/resources/main/")
        outputDir.mkdirs()
        process(state, outputDir)
    }

    override fun invoke(project: Project)
    {
        val state = generateProjectState(project)
        process(state, project)
    }

    private fun Artifact.resolvePDMDependency(spigot: Boolean, searchRepositories: Boolean, repositories: Map<String, Repository>): PDMDependency
    {
        if (spigot && isSpigotArtifact() || !searchRepositories)
        {
            return PDMDependency(groupId, artifactId, version, null, null)
        }

        val containingRepo = if (repoAlias != null)
        {
            repoAlias to repositories[repoAlias!!]
        } else
        {
            repositories.entries.firstOrNull { repo ->
                repo.value.contains(this)
            }?.toPair()
        }

        if (containingRepo == null)
        {
            LOGGER.error("No repository found for dependency {}", this)
            return PDMDependency(groupId, artifactId, version, null, null)
        }

        val dependencies = containingRepo.second?.getTransitiveDependencies(this)
                ?.map { it.resolvePDMDependency(spigot, searchRepositories, repositories) }
                ?.toSet()

        return PDMDependency(groupId, artifactId, version, containingRepo.first, dependencies)
    }
}
