package me.bristermitten.pdm.loading.types;

import me.bristermitten.pdm.loading.DependencyLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Function;
import java.util.logging.Logger;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class ClasspathAddendumDependencyLoader extends DependencyLoader {
    private static final Method ADD_URL;

    static {
        try {
            ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL.setAccessible(true);
        } catch (NoSuchMethodException exception) {
            throw new AssertionError(exception);
        }
    }

    private final URLClassLoader loader;

    public ClasspathAddendumDependencyLoader(@NotNull final Function<String, Logger> logger, @NotNull final URLClassLoader loader) {
        super(logger);
        this.loader = loader;
    }

    @Override
    protected void load(@NotNull final File @NotNull ... files) throws MalformedURLException {
        for (final File file : files) {
            try {
                ADD_URL.invoke(loader, file.toURI().toURL());
            } catch (IllegalAccessException | InvocationTargetException exception) {
                throw new AssertionError(exception);
            }
        }
    }
}
