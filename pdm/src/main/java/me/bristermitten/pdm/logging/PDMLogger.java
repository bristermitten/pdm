package me.bristermitten.pdm.logging;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author AlexL
 */
public interface PDMLogger
{

    void info(@NotNull Supplier<String> message);

    void warning(@NotNull Supplier<String> message);

    void debug(@NotNull Supplier<String> message);
}
