package me.bristermitten.pdm

data class PDMConfig(
        private val repositories: Map<String, String>,
        private val dependencies: Set<PDMDependency>
)

data class PDMDependency(
        val groupId: String,
        val artifactId: String,
        val version: String,
        val repository: String?
)
