package me.bristermitten.pdm

import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.repository.RepositoryManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.util.ConfigureUtil
import java.util.logging.Logger

class PDM : Plugin<Project>
{
	companion object {
		const val CONFIGURATION_NAME = "pdm"
	}
	private val artifactFactory = ArtifactFactory()

	private val repositoryManager = RepositoryManager(Logger.getLogger(javaClass.name))

	private fun Project.createPDMConfiguration(): Configuration
	{
		plugins.apply("java") //add the Java Plugin if it's not already present

		val pdmConfig = configurations.create(CONFIGURATION_NAME)
		val compileConfig = configurations.findByName("compileOnly")
		requireNotNull(compileConfig) {
			"No 'compileOnly' configuration defined. Is the Java plugin present?"
		}
		compileConfig.extendsFrom(pdmConfig)
		return pdmConfig
	}

	private fun Project.addPDMDependency(extension: PDMExtension): Configuration
	{
		val pdmInternal = project.configurations.create("PDMInternal")
		val implementation = configurations.findByName("implementation")
		if (extension.addPDMRepository)
		{
			repositories.maven {
				it.setUrl(PDM_REPO_URL)
			}
		}
		val dependency = project.dependencies.add(pdmInternal.name, "me.bristermitten:pdm:${extension.version}") as ExternalModuleDependency
		dependency.exclude(mapOf("module" to "kotlin-stdlib"))

		implementation?.dependencies?.add(dependency)

		pdmInternal.dependencies.add(dependency)
		return pdmInternal
	}

	override fun apply(project: Project)
	{
		val extension = project.extensions.create("pdm", PDMExtension::class.java)
		val pdmConfiguration = project.createPDMConfiguration()

		val pdmDependency = project.addPDMDependency(extension)
		val dependenciesTask = PDMGenDependenciesTask(artifactFactory, pdmConfiguration, extension, repositoryManager)
		val pdmTask = PDMTask(extension, pdmDependency, artifactFactory, repositoryManager, dependenciesTask)

		project.task("pdm").doLast {
			pdmTask.invoke(project, it)
		}

		project.task("pdmGenDependencies").doLast {
			dependenciesTask.invoke(project)
		}
	}
}
