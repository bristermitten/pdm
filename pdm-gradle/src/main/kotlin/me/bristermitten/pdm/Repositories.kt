package me.bristermitten.pdm

/**
 * Gradle Repository Names that should be ignored.
 */
val IGNORED_REPOS = setOf("MavenLocal")


val REPOSITORY_URL_MAPPINGS: Map<String, (PDMExtension) -> String> = mapOf(
		"MavenRepo" to { it.centralMirror }
)
