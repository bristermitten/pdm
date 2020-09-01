package me.bristermitten.pdm

import me.bristermitten.pdm.json.ArtifactDTO
import me.bristermitten.pdmlibs.repository.Repository

data class ProjectState(
        val repos: Map<String, Repository>,
        val dependencies: Set<ArtifactDTO>
)

operator fun ProjectState.plus(projectState: ProjectState): ProjectState
{
    return ProjectState(repos + projectState.repos, dependencies + projectState.dependencies)
}
