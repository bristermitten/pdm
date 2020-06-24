package me.bristermitten.pdm

import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class PDM : Plugin<Project>
{
    private val artifactFactory = ArtifactFactory()


    private fun Project.createPDMConfiguration(): Configuration
    {
        val pdmConfig = configurations.create("pdm")
        val compileOnly = configurations.findByName("compileOnly")
        compileOnly?.extendsFrom(pdmConfig)
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
        val dependency = project.dependencies.add(pdmInternal.name, "me.bristermitten:pdm:${extension.version}")
        implementation?.dependencies?.add(dependency)

        pdmInternal.dependencies.add(dependency)
        return pdmInternal
    }

    override fun apply(project: Project)
    {
        val extension = project.extensions.create("pdm", PDMExtension::class.java)
        val pdmConfiguration = project.createPDMConfiguration()

        val pdmDependency = project.addPDMDependency(extension)
        val dependenciesTask = PDMGenDependenciesTask(artifactFactory, pdmConfiguration, extension)
        val pdmTask = PDMTask(extension, pdmDependency, artifactFactory, dependenciesTask)

        project.task("pdm").doLast {
            pdmTask.invoke(project, it)
        }

        project.task("pdmGenDependencies").doLast {
            dependenciesTask.invoke(project)
        }
    }

}
