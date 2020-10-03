package me.bristermitten.pdm;

import me.bristermitten.pdmlibs.config.CacheConfiguration;
import me.bristermitten.pdmlibs.util.Reflection;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author AlexL
 */
@SuppressWarnings("UnusedReturnValue")
public final class PDMBuilder
{

    public static final String DEPENDENCIES_RESOURCE_NAME = "dependencies.json";
    public static final String PLUGIN_CLASS_LOADER_NAME = "org.bukkit.plugin.java.PluginClassLoader";
    private Function<String, Logger> loggerFactory = Logger::getLogger;
    @Nullable private InputStream dependenciesResource = null;
    @Nullable private File rootDirectory = null;
    @Nullable private URLClassLoader classLoader = null;
    @Nullable private String applicationName = null;
    @Nullable private String applicationVersion = null;
    private CacheConfiguration cacheConfiguration = CacheConfiguration.builder().build();

    /**
     * @deprecated Use one of the static factory methods; the direct replacement for this method is {@link #builder(Plugin)}.
     * @param plugin Plugin implementation instance
     */
    @Deprecated
    public PDMBuilder(@NotNull final Plugin plugin)
    {
        loggerFactory(clazz -> plugin.getLogger());
        dependenciesResource(plugin.getResource(DEPENDENCIES_RESOURCE_NAME));
        rootDirectory(plugin.getDataFolder().getParentFile());
        classLoader((URLClassLoader) plugin.getClass().getClassLoader());
        applicationName(plugin.getName());
        applicationVersion(plugin.getDescription().getVersion());
    }

    /**
     * @deprecated Use one of the static factory methods; the direct replacement for this method is {@link #builder(Class)}
     * @param plugin Plugin implementation class
     */
    @Deprecated
    public PDMBuilder(@NotNull final Class<? extends Plugin> plugin)
    {
        Validate.isTrue(PLUGIN_CLASS_LOADER_NAME.equals(plugin.getClassLoader().getClass().getName()), "Plugin must be loaded with a PluginClassLoader");
        classLoader((URLClassLoader) plugin.getClassLoader());
        PluginDescriptionFile description = Reflection.getFieldValue(classLoader, "description");
        dependenciesResource(classLoader.getResourceAsStream(DEPENDENCIES_RESOURCE_NAME));
        rootDirectory(new File("./plugins"));
        applicationName(description.getName());
        applicationVersion(description.getVersion());
        loggerFactory(clazz -> Logger.getLogger(description.getName()));
    }

    /**
     * @deprecated Do not construct an instance of this class manually.
     */
    @Deprecated
    public PDMBuilder()
    {

    }

    @NotNull
    public static PDMBuilder builder()
    {
        return new PDMBuilder();
    }

    @NotNull
    public static PDMBuilder builder(@NotNull final Plugin plugin)
    {
        return new PDMBuilder()
                .loggerFactory(clazz -> plugin.getLogger())
                .dependenciesResource(plugin.getResource(DEPENDENCIES_RESOURCE_NAME))
                .rootDirectory(plugin.getDataFolder().getParentFile())
                .classLoader((URLClassLoader) plugin.getClass().getClassLoader())
                .applicationName(plugin.getName())
                .applicationVersion(plugin.getDescription().getVersion());
    }

    @NotNull
    public static PDMBuilder builder(@NotNull final Class<? extends Plugin> plugin)
    {
        Validate.isTrue(PLUGIN_CLASS_LOADER_NAME.equals(plugin.getClassLoader().getClass().getName()), "Plugin must be loaded with a PluginClassLoader");

        final URLClassLoader classLoader = (URLClassLoader) plugin.getClassLoader();
        final PluginDescriptionFile description = Reflection.getFieldValue(classLoader, "description");

        return builder()
                .classLoader((URLClassLoader) plugin.getClassLoader())
                .dependenciesResource(classLoader.getResourceAsStream(DEPENDENCIES_RESOURCE_NAME))
                .rootDirectory(new File("./plugins"))
                .applicationName(description.getName())
                .applicationVersion(description.getVersion())
                .loggerFactory(clazz -> Logger.getLogger(description.getName()));
    }

    @NotNull
    public static PluginDependencyManager of(@NotNull final Plugin plugin)
    {
        return builder(plugin)
                .build();
    }

    @NotNull
    public static PluginDependencyManager of(@NotNull final Class<? extends Plugin> plugin)
    {
        return builder(plugin)
                .build();
    }

    @NotNull
    public PDMBuilder loggerFactory(@NotNull final Function<String, Logger> loggerFactory)
    {
        this.loggerFactory = loggerFactory;
        return this;
    }

    @NotNull
    public PDMBuilder dependenciesResource(@NotNull final InputStream dependenciesResource)
    {
        this.dependenciesResource = dependenciesResource;
        return this;
    }

    @NotNull
    public PDMBuilder rootDirectory(@NotNull final File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
        return this;
    }

    @NotNull
    public PDMBuilder classLoader(@NotNull URLClassLoader classLoader)
    {
        this.classLoader = classLoader;
        return this;
    }

    @NotNull
    public PDMBuilder applicationName(@NotNull String applicationName)
    {
        this.applicationName = applicationName;
        return this;
    }

    @NotNull
    public PDMBuilder applicationVersion(@NotNull String applicationVersion)
    {
        this.applicationVersion = applicationVersion;
        return this;
    }

    @NotNull
    public PDMBuilder caching(@NotNull Consumer<CacheConfiguration.Builder> configuration)
    {
        final CacheConfiguration.Builder builder = CacheConfiguration.builder();
        configuration.accept(builder);
        this.cacheConfiguration = builder.build();
        return this;
    }

    @NotNull
    public PluginDependencyManager build()
    {
        Objects.requireNonNull(loggerFactory, "loggerFactory cannot be null");
        Objects.requireNonNull(rootDirectory, "rootDirectory cannot be null");
        Objects.requireNonNull(classLoader, "classLoader cannot be null");
        Objects.requireNonNull(applicationName, "applicationName cannot be null");
        Objects.requireNonNull(applicationVersion, "applicationVersion cannot be null");
        Objects.requireNonNull(cacheConfiguration, "cacheConfiguration cannot be null");

        return new PluginDependencyManager(loggerFactory, dependenciesResource, rootDirectory, classLoader, applicationName, applicationVersion, cacheConfiguration);
    }
}
