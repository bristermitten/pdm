package me.bristermitten.pdm

import com.google.gson.Gson
import me.bristermitten.pdm.json.ArtifactDTO
import me.bristermitten.pdm.json.DependenciesConfiguration
import me.bristermitten.pdm.json.ExcludeRule
import me.bristermitten.pdm.json.PDMDependency
import me.bristermitten.pdmlibs.artifact.Artifact
import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.DefaultParseProcess
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository
import me.bristermitten.pdmlibs.repository.RepositoryManager
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.slf4j.LoggerFactory
import java.io.File

class PDMGenDependenciesTask(
		private val artifactFactory: ArtifactFactory,
		private val pdmDependency: Configuration,
		private val config: PDMExtension,
        private val repositoryManager: RepositoryManager,
		private val gson: Gson = Gson()
) : (Project) -> Unit
{
	companion object
	{
		const val PROJECT_REPOSITORY_ALIAS = "project"
		private val LOGGER = LoggerFactory.getLogger(PDMGenDependenciesTask::class.java)
	}

	private fun generateProjectState(project: Project): ProjectState
	{
		val httpService = HTTPService(project.name, config.version, config.caching)
		val repositoryFactory = MavenRepositoryFactory(httpService, DefaultParseProcess(artifactFactory, repositoryManager, httpService))
		return generateProjectState(project, repositoryFactory)
	}

	fun generateProjectState(project: Project, repositoryFactory: MavenRepositoryFactory): ProjectState
	{
		return generateProjectState(project, pdmDependency, repositoryFactory)
	}

	private fun generateProjectState(project: Project, pdmConfiguration: Configuration, repositoryFactory: MavenRepositoryFactory): ProjectState
	{
		val repositories = project.repositories.asSequence()
				.filterIsInstance<MavenArtifactRepository>()
				.filter {
					it.name !in IGNORED_REPOS
				}
				.map {
					val url = REPOSITORY_URL_MAPPINGS[it.name]?.invoke(config) ?: it.url.toString()
					it.name to repositoryFactory.create(url)
				}.toMap().toMutableMap()

		val projectRepository = config.projectRepository
		if(projectRepository != null)
			repositories[PROJECT_REPOSITORY_ALIAS] = repositoryFactory.create(projectRepository)

		val dependencies = pdmConfiguration.allDependencies.mapNotNull {
			val group = it.group ?: return@mapNotNull null
			val version = it.version ?: return@mapNotNull null
			val exclusions = (it as? ModuleDependency)?.excludeRules?.map { ExcludeRule(it.group, it.module) } ?: emptyList()
			ArtifactDTO(group, it.name, version, it is ProjectDependency, exclusions)
		}.toSet()

		val submodulesProjectState = project.configurations
				.filter { it.name == "compile" || it.extendsFrom.any { it.name == "compile" } }
				.filterNot { it.name.contains("test", ignoreCase = true) }
				.flatMap { it.allDependencies.toList() }
				.mapNotNull { (it as? ProjectDependency)?.dependencyProject }
				.associateWith { it.configurations.findByName(PDM.CONFIGURATION_NAME) }
				.mapNotNull { (project, pdm) -> pdm?.let { generateProjectState(project, it, repositoryFactory) } }
				.toList()

		return submodulesProjectState.fold(ProjectState(repositories, dependencies)) { previous, new ->
			previous + new
		}
	}

	private fun process(state: ProjectState, outputDirectory: File)
	{
		val artifacts = state.dependencies.map {

			val artifact = artifactFactory.toArtifact(it.group, it.artifact, it.version, null, null)

			artifact.resolvePDMDependency(
					config.spigot,
					config.searchRepositories,
					state.repos,
					it.isProject,
					it.exclusions
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

	private fun Artifact.resolvePDMDependency(
			spigot: Boolean,
			searchRepositories: Boolean,
			repositories: Map<String, Repository>,
			isProject: Boolean,
			excludeRules: List<ExcludeRule>
	): PDMDependency
	{
		if(isProject && config.projectRepository != null)
		{
			return PDMDependency(groupId, artifactId, version, PROJECT_REPOSITORY_ALIAS, null)
		}

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
				?.filterNot { excludeRules.any { rule -> rule.match(it) } }
				?.map { it.resolvePDMDependency(spigot, searchRepositories, repositories, isProject, excludeRules) }
				?.toSet()

		return PDMDependency(groupId, artifactId, version, containingRepo.first, dependencies)
	}
}
