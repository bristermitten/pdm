package me.bristermitten.pdmlibs.artifact

import me.bristermitten.pdmlibs.http.HTTPService
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author AlexL
 */
class ReleaseArtifactTests
{

    @Test
    fun `Test Processing of Sonatype Nexus Release Artifact`()
    {
        val artifactFactory = ArtifactFactory()
        val artifact = artifactFactory.toArtifact(
                "me.bristermitten",
                "common-lib",
                "0.0.2",
                null,
                null
        )
        val httpService = HTTPService("PDM-Test-Suite", "N/A")

        val jarUrl = artifact.getJarURL("https://repo.bristermitten.me/repository/maven-releases/", httpService)

        assertNotNull(jarUrl)
        assertTrue(httpService.ping(jarUrl!!))
    }
}
