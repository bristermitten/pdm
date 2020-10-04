package me.bristermitten.pdm.json

import me.bristermitten.pdmlibs.dependency.Dependency

data class ArtifactDTO(
        val group: String,
        val artifact: String,
        val version: String,
        val isProject: Boolean,
        val exclusions: List<ExcludeRule>
)

data class ExcludeRule(val group: String?, val module: String?)
{
    fun match(dependency: Dependency): Boolean
    {
        return if(group != null && module != null) {
            dependency.groupId.equals(group, ignoreCase = true) && dependency.artifactId.equals(module, ignoreCase = true)
        } else if(group != null) {
            dependency.groupId.equals(group, ignoreCase = true)
        } else {
            dependency.artifactId.equals(module, ignoreCase = true)
        }
    }
}
