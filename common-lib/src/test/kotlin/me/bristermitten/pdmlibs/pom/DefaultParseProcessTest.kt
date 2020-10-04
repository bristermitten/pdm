package me.bristermitten.pdmlibs.pom

import me.bristermitten.pdmlibs.config.CacheConfiguration
import me.bristermitten.pdmlibs.dependency.DependencyFactory
import me.bristermitten.pdmlibs.http.HTTPService
import me.bristermitten.pdmlibs.repository.RepositoryManager
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.logging.Logger

/**
 * @author AlexL
 */
class DefaultParseProcessTest
{
	val logger = Logger.getLogger(javaClass.name)

	@Test
	fun `Test Parsing Correctly Functioning`()
	{
		val pom = SAMPLE_POM.replace("{slf4j.version}", "\${slf4j.version}").byteInputStream()

		val repositoryManager = RepositoryManager(logger)
		val httpService = HTTPService("PDM-Test-Suite", "N/A", CacheConfiguration.builder().build())

		val defaultParseProcess = DefaultParseProcess(DependencyFactory(), repositoryManager, httpService)

		val results = PomParser().parse(defaultParseProcess, pom)

		assertEquals(results.first().version, "1.7.25")
	}

	private companion object
	{
		@Language("xml")
		//Adjusted from https://repo1.maven.org/maven2/com/zaxxer/HikariCP/3.4.5/HikariCP-3.4.5.pom for simplicity
		private val SAMPLE_POM = """
            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <properties>
        <slf4j.version>1.7.25</slf4j.version>
    </properties>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>3.4.5</version>
    <packaging>bundle</packaging>
    <name>HikariCP</name>
    <description>Ultimate JDBC Connection Pool</description>
    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>{slf4j.version}</version>
        </dependency>
    </dependencies>
</project>"""
	}
}
