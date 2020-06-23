package me.bristermitten.pdm

data class DependenciesConfiguration(
        private val repositories: Map<String, String>,
        private val dependencies: Set<PDMDependency>,
        private val dependenciesDirectory: String?
)

data class PDMDependency(
        val groupId: String,
        val artifactId: String,
        val version: String,
        val repository: String?
)
