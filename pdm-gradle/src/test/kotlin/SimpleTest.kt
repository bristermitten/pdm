import me.bristermitten.pdmlibs.artifact.ArtifactFactory
import me.bristermitten.pdmlibs.artifact.ReleaseArtifact
import me.bristermitten.pdmlibs.config.CacheConfiguration
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.pom.DefaultParseProcess
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.RepositoryManager
import org.junit.jupiter.api.Test
import java.util.logging.Logger

/**
 * @author AlexL
 */
class SimpleTest
{
	@Test
	fun test()
	{
		val artifactFactory = ArtifactFactory()
		val repositoryManager = RepositoryManager(Logger.getLogger(javaClass.name))
		val httpService = HTTPService("PDM-Test-Suite", "N/A", CacheConfiguration.builder().build())
		val repositoryFactory = MavenRepositoryFactory(httpService, DefaultParseProcess(artifactFactory, repositoryManager, httpService))

		val repo = repositoryFactory.create("https://jcenter.bintray.com/")
		val transitiveDependencies = repo.getTransitiveDependencies(ReleaseArtifact(
				"net.dv8tion",
				"JDA",
				"4.2.0_189"
		))

		println(transitiveDependencies)
	}
}
