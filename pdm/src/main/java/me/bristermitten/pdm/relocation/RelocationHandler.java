package me.bristermitten.pdm.relocation;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class RelocationHandler {
    private static final String CLASS_NAME = "me.lucko.jarrelocator.JarRelocator";
    private static final String RUN_METHOD = "run";

    private final Constructor<?> constructor;
    private final Method run;

    public RelocationHandler(@NotNull final ClassLoader loader) {
        try {
            final Class<?> clazz = loader.loadClass(CLASS_NAME);

            this.constructor = clazz.getDeclaredConstructor(File.class, File.class, Map.class);
            this.constructor.setAccessible(true);

            this.run = clazz.getDeclaredMethod(RUN_METHOD);
            this.run.setAccessible(true);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void relocate(@NotNull final Path input, @NotNull final Path output,
                         @NotNull final Map<String, String> relocations) {
        try {
            final Object relocator = constructor.newInstance(input.toFile(), output.toFile(), relocations);
            run.invoke(relocator);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
