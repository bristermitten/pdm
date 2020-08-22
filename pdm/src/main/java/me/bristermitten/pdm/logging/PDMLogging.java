package me.bristermitten.pdm.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author AlexL
 */
public class PDMLogging
{

    private static final String PDM_PREFIX = "PDM - ";
    private final Map<String, PDMLogger> loggers = new ConcurrentHashMap<>();

    private final PDMLoggerFactory loggerFactory;

    public PDMLogging(PDMLoggerFactory loggerFactory)
    {
        this.loggerFactory = loggerFactory;
    }

    public static Supplier<String> applyPrefix(Supplier<String> message)
    {
        return () -> PDM_PREFIX + message.get();
    }


    public PDMLogger getLogger(String name)
    {
        return loggers.computeIfAbsent(name, key -> loggerFactory.apply(name));
    }
}
