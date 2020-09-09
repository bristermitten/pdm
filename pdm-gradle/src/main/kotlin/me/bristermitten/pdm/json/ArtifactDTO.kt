package me.bristermitten.pdm.json

import me.bristermitten.pdmlibs.artifact.Artifact

data class ArtifactDTO(
        val group: String,
        val artifact: String,
        val version: String,
        val isProject: Boolean,
        val exclusions: List<ExcludeRule>
)

data class ExcludeRule(val group: String?, val module: String?)
{
    fun match(artifact: Artifact): Boolean
    {
        return if(group != null && module != null) {
            artifact.groupId.equals(group, ignoreCase = true) && artifact.artifactId.equals(module, ignoreCase = true)
        } else if(group != null) {
            artifact.groupId.equals(group, ignoreCase = true)
        } else {
            artifact.artifactId.equals(module, ignoreCase = true)
        }
    }
}
