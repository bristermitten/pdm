package me.bristermitten.pdm;

import me.bristermitten.pdm.repository.MavenCentral;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.Logger;

public class SimpleTest
{

    private static final Logger LOGGER = Logger.getLogger("SimpleTest");

    private static final String DEPENDENCIES_JSON = "{\n" +
            "  \"repositories\": {\n" +
            "    \"maven\": \"" + MavenCentral.DEFAULT_CENTRAL_MIRROR + "\"\n" +
            "  },\n" +
            "  \"dependencies\": [\n" +
            "    {\n" +
            "      \"groupId\": \"org.jetbrains.kotlin\",\n" +
            "      \"artifactId\": \"kotlin-stdlib-jdk8\",\n" +
            "      \"version\": \"1.3.72\",\n" +
            "      \"repository\": \"maven-central\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"dependenciesDirectory\": \"Hello\"\n" +
            "}";

    @Test
    public void simplePDMTest() throws IOException
    {
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()))
        {
            new PluginDependencyManager(
                    () -> LOGGER,
                    new ByteArrayInputStream(DEPENDENCIES_JSON.getBytes()),
                    Files.createTempDirectory("tests").toFile(),
                    classLoader
            ).loadAllDependencies().join();

            LOGGER.info(() -> {
                try
                {
                    return "Kotlin? " + classLoader.loadClass("kotlin.Unit");
                }
                catch (ClassNotFoundException e)
                {
                    throw new IllegalArgumentException(e);
                }
            });
        }

    }
}
