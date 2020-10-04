package me.bristermitten.pdm.loading.types;

import me.bristermitten.pdm.loading.DependencyLoader;
import me.bristermitten.pdm.loading.loaders.IsolatedClassLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class IsolatedDependencyLoader extends DependencyLoader {
    private final Map<File, ClassLoader> loaders = new HashMap<>();

    public IsolatedDependencyLoader(@NotNull final Function<String, Logger> logger) {
        super(logger);
    }

    @Override
    protected void load(@NotNull final File @NotNull ... files) throws MalformedURLException {
        final Set<URL> urls = new HashSet<>();

        for (final File file : files) {
            urls.add(file.toURI().toURL());
        }

        final ClassLoader loader = new IsolatedClassLoader(urls.toArray(new URL[0]));
        Arrays.stream(files).forEach(file -> loaders.put(file, loader));
    }

    @NotNull
    public ClassLoader getClassLoader(@NotNull final File file) {
        return loaders.get(file);
    }
}
