package me.bristermitten.pdm;

import me.bristermitten.pdmlibs.artifact.ReleaseArtifact;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SimpleTest extends PDMTestSuite
{


    public SimpleTest() throws IOException
    {
    }

    @Test
    void simplePDMTest()
    {
        pdm.addRequiredDependency(
                new ReleaseArtifact(
                        "org.jetbrains.kotlin",
                        "kotlin-stdlib-jdk8",
                        "1.3.72"
                )
        );
        pdm.loadAllDependencies().join();

        assertDoesNotThrow(() -> {
            classLoader.loadClass("kotlin.Unit");
        });

    }

    @Test
    void simplePDMTest2()
    {
        pdm.addRequiredDependency(
                new ReleaseArtifact(
                        "com.zaxxer",
                        "HikariCP",
                        "3.4.5"
                )
        );
        pdm.loadAllDependencies().join();

        assertDoesNotThrow(() -> {
            classLoader.loadClass("com.zaxxer.hikari.HikariConfig");
        });

    }
}
