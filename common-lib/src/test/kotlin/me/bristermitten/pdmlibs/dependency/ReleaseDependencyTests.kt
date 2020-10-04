package me.bristermitten.pdmlibs.dependency

import me.bristermitten.pdmlibs.config.CacheConfiguration
import me.bristermitten.pdmlibs.http.HTTPService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author AlexL
 */
class ReleaseDependencyTests
{

	@Test
	fun `Test Processing of Sonatype Nexus Release Artifact`()
	{
		val artifactFactory = DependencyFactory()
		val artifact = artifactFactory.toArtifact(
				"me.bristermitten",
				"common-lib",
				"0.0.2",
				null,
				null,
				mapOf("me.bristermitten.common-lib" to "me.bristermitten.pdmlibs.libs.common-lib")
		)

		val httpService = HTTPService("PDM-Test-Suite", "N/A", CacheConfiguration.builder().build())

		val jarUrl = artifact.getJarURL("https://repo.bristermitten.me/repository/maven-releases/", httpService)

		assertNotNull(jarUrl)
		assertTrue(httpService.ping(jarUrl!!))
	}
}
