package me.bristermitten.pdm

import me.bristermitten.pdmlibs.config.CacheConfiguration
import me.bristermitten.pdmlibs.repository.MavenCentral


open class PDMExtension
{
	/**
	 * Which version of PDM should be shaded. If none is provided, the version matching the plugin version is used.
	 */
	var version: String = javaClass.classLoader.getResource("version")?.readText()?.trim() ?: "error"
		protected set

	/**
	 * The directory name that dependencies should be downloaded to. If null, PDM resolves this as `PluginLibraries`.
	 * Typically this option should be left alone, to create a shared directory for all plugins to use, but it can be changed if necessary.
	 *
	 */
	var outputDirectory: String? = null
		protected set

	/**
	 * Which Maven Central mirror to use to download dependencies.
	 * Maven Central prohibits use in a production environment, so downloading must be done from a different mirror at runtime.
	 * By default this is set to `https://repo.bristermitten.me`, but could be changed if necessary.
	 *
	 * It is strongly advised **against** setting this to the actual Maven Central URL, as there is some ambiguity in whether this is prohibited or not.
	 */
	var centralMirror: String = MavenCentral.DEFAULT_CENTRAL_MIRROR
		protected set

	/**
	 * If the plugin should search the configured Maven Repositories to find which is used for a dependency at build time.
	 * Disabling this can speed up build time, but will potentially slow down server startup, as all repositories will have to be searched.
	 *
	 */
	var searchRepositories: Boolean = true
		protected set

	/**
	 * Disable the searching of repositories at build type.
	 *
	 * @see [searchRepositories]
	 */
	protected fun disableRepositorySearching()
	{
		searchRepositories = false
	}

	/**
	 * If this project should be treated as a Spigot project.
	 * If it is, no repository searching will be done for any Spigot dependencies, as they are assumed to be provided at runtime.
	 *
	 * @see [isSpigotArtifact]
	 */
	var spigot: Boolean = true
		private set

	/**
	 * Disable Spigot handling for this project.
	 *
	 * @see [spigot]
	 */
	protected fun disableSpigot()
	{
		this.spigot = false
	}

	/**
	 * If the PDM runtime should be bundled into the built jar.
	 * These classes are required for functionality, and are automatically set to include via the `jar` task.
	 *
	 * However, this can be disabled if necessary, if the classes will be provided via alternative means.
	 */
	var bundlePDMRuntime: Boolean = true
		private set

	/**
	 * Disable the bundling of the PDM runtime
	 * @see [bundlePDMRuntime]
	 */
	protected fun disableRuntimeBundling()
	{
		this.bundlePDMRuntime = false
	}


	/**
	 * If the PDM Repository should be added by the plugin. This is the repository for the runtime, and is located at:
	 * [https://repo.bristermitten.me/repository/maven-public/]
	 *
	 * This can be disabled if another repository which provides it is added.
	 */
	var addPDMRepository: Boolean = true
		protected set

	var caching: CacheConfiguration = CacheConfiguration.builder().build()
		private set

	protected fun caching(config: CacheConfiguration.Builder.() -> Unit)
	{
		this.caching = CacheConfiguration.builder().apply(config).build()
	}
}
