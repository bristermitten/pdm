package me.bristermitten.pdm;

import me.bristermitten.pdmlibs.util.Reflection;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author AlexL
 */
public final class PDMBuilder
{

    public static final String DEPENDENCIES_RESOURCE_NAME = "dependencies.json";
    private Function<String, Logger> loggerFactory = Logger::getLogger;
    private InputStream dependenciesResource = null;
    private File rootDirectory = null;
    private URLClassLoader classLoader = null;
    private String applicationName = null;
    private String applicationVersion = null;

    public PDMBuilder(@NotNull final Plugin plugin)
    {
        loggerFactory(clazz -> plugin.getLogger());
        dependenciesResource(plugin.getResource(DEPENDENCIES_RESOURCE_NAME));
        rootDirectory(plugin.getDataFolder().getParentFile());
        classLoader((URLClassLoader) plugin.getClass().getClassLoader());
        applicationName(plugin.getName());
        applicationVersion(plugin.getDescription().getVersion());
    }

    public PDMBuilder(@NotNull final Class<? extends Plugin> plugin)
    {
        Validate.isTrue("org.bukkit.plugin.java.PluginClassLoader".equals(plugin.getClassLoader().getClass().getName()),
            "Plugin must be loaded with a PluginClassLoader");
        classLoader((URLClassLoader) plugin.getClassLoader());
        PluginDescriptionFile description = Reflection.getFieldValue(classLoader, "description");
        dependenciesResource(classLoader.getResourceAsStream(DEPENDENCIES_RESOURCE_NAME));
        rootDirectory(new File("./plugins"));
        applicationName(description.getName());
        applicationVersion(description.getVersion());
        loggerFactory(clazz -> Logger.getLogger(description.getName()));
    }

    public PDMBuilder()
    {

    }

    public PDMBuilder loggerFactory(@NotNull Function<String, Logger> loggerFactory)
    {
        this.loggerFactory = loggerFactory;
        return this;
    }

    public PDMBuilder dependenciesResource(InputStream dependenciesResource)
    {
        this.dependenciesResource = dependenciesResource;
        return this;
    }

    public PDMBuilder rootDirectory(@NotNull File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public PDMBuilder classLoader(@NotNull URLClassLoader classLoader)
    {
        this.classLoader = classLoader;
        return this;
    }

    public PDMBuilder applicationName(@NotNull String applicationName)
    {
        this.applicationName = applicationName;
        return this;
    }

    public PDMBuilder applicationVersion(@NotNull String applicationVersion)
    {
        this.applicationVersion = applicationVersion;
        return this;
    }

    public PluginDependencyManager build()
    {
        Objects.requireNonNull(loggerFactory, "loggerFactory cannot be null");
        Objects.requireNonNull(rootDirectory, "rootDirectory cannot be null");
        Objects.requireNonNull(classLoader, "classLoader cannot be null");
        Objects.requireNonNull(applicationName, "applicationName cannot be null");
        Objects.requireNonNull(applicationVersion, "applicationVersion cannot be null");

        return new PluginDependencyManager(loggerFactory, dependenciesResource, rootDirectory, classLoader, applicationName, applicationVersion);
    }
}
