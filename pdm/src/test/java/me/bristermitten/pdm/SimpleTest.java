package me.bristermitten.pdm;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class SimpleTest
{

    private static final Logger LOGGER = Logger.getLogger("SimpleTest");

    private static final String DEPENDENCIES_JSON = "{\n" +
            "  \"repositories\": {\n" +
            "    \"maven\": \"https://hub.spigotmc.org/nexus/content/repositories/snapshots/\"\n" +
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
    public void simplePDMTest() throws ClassNotFoundException
    {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader());
        new PluginDependencyManager(
                () -> LOGGER,
                new ByteArrayInputStream(DEPENDENCIES_JSON.getBytes()),
                new File("tests"),
                classLoader
        ).loadAllDependencies().join();
        System.out.println("Kotlin? " + classLoader.loadClass("kotlin.Unit"));
    }
}
