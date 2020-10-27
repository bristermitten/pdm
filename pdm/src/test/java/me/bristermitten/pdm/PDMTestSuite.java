package me.bristermitten.pdm;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.*;

/**
 * @author AlexL
 */
public class PDMTestSuite
{

    @NotNull
    protected final PluginDependencyManager pdm;
    @NotNull
    protected final URLClassLoader classLoader;
    @NotNull
    protected final File libraryDirectory;

    @NotNull
    private final Logger logger = Logger.getLogger(getClass().getName());

    public PDMTestSuite() throws IOException
    {

        classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());
        libraryDirectory = Files.createTempDirectory("PDM").toFile();
        //        libraryDirectory.deleteOnExit();
        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);
        logger.addHandler(handlerObj);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        pdm = PluginDependencyManager.builder()
                .loggerFactory($ -> logger)
                .rootDirectory(libraryDirectory)
                .classLoader(classLoader)
                .applicationName("PDM-Test-Suite")
                .applicationVersion("N/A")
                .build();

        pdm.addRepository(
                "maven-central",
                "https://repo.bristermitten.me/repository/maven-central/"
        );
    }
}
