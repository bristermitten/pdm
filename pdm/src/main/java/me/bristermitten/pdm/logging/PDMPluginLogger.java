package me.bristermitten.pdm.logging;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author AlexL
 */
public class PDMPluginLogger implements PDMLogger
{

    private final Logger pluginLogger;

    public PDMPluginLogger(Logger pluginLogger)
    {
        this.pluginLogger = pluginLogger;
    }


    @Override
    public void info(@NotNull Supplier<String> message)
    {
        pluginLogger.info(PDMLogging.applyPrefix(message));
    }

    @Override
    public void warning(@NotNull Supplier<String> message)
    {
        pluginLogger.warning(PDMLogging.applyPrefix(message));
    }

    @Override
    public void debug(@NotNull Supplier<String> message)
    {
        pluginLogger.fine(PDMLogging.applyPrefix(message));
    }
}
