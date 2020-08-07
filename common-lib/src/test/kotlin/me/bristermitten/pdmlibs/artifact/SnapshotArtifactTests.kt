package me.bristermitten.pdmlibs.artifact

import me.bristermitten.pdmlibs.http.HTTPService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author AlexL
 */
class SnapshotArtifactTests
{
    @Test
    fun `Test Processing of Jitpack Snapshot Artifact`()
    {
        val artifactFactory = ArtifactFactory()
        val artifact = artifactFactory.toArtifact(
                "com.github.JohnnyJayJay",
                "compatre",
                "master-SNAPSHOT",
                null,
                null
        )
        val httpService = HTTPService("PDM-Test-Suite", "N/A")

        val jarUrl = artifact.getJarURL("https://jitpack.io/", httpService)
        assertNotNull(jarUrl)
        assertTrue(httpService.ping(jarUrl!!))
    }

    @Test
    fun `Test Processing of Sonatype Nexus Snapshot Artifact`()
    {
        val artifactFactory = ArtifactFactory()
        val artifact = artifactFactory.toArtifact(
                "me.bristermitten",
                "fluency",
                "1.1-SNAPSHOT",
                null,
                null
        )
        val httpService = HTTPService("PDM-Test-Suite", "N/A")

        val jarUrl = artifact.getJarURL("https://repo.bristermitten.me/repository/maven-snapshots/", httpService)

        assertNotNull(jarUrl)
        assertTrue(httpService.ping(jarUrl!!))
    }
}
