package me.bristermitten.pdm;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * @author AlexL
 */
public class PDMTestSuite
{

    protected final PluginDependencyManager pdm;
    protected final URLClassLoader classLoader;
    protected final File libraryDirectory;
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public PDMTestSuite() throws IOException
    {

        classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());
        libraryDirectory = Files.createTempDirectory("PDM").toFile();
        //        libraryDirectory.deleteOnExit();

        pdm = new PDMBuilder()
                .rootDirectory(libraryDirectory)
                .classLoader(classLoader)
                .applicationName("PDM-Test-Suite")
                .applicationVersion("N/A")
                .loggerFactory($ -> logger)
                .build();

        pdm.addRepository(
                "maven-central",
                "https://repo.bristermitten.me/repository/maven-central/"
        );
    }
}
