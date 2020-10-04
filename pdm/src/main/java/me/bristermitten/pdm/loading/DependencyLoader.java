package me.bristermitten.pdm.loading;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class DependencyLoader {
    private final Set<File> loaded = new HashSet<>();

    private final Logger logger;

    protected DependencyLoader(@NotNull final Function<String, Logger> logger) {
        this.logger = logger.apply(getClass().getName());
    }

    protected abstract void load(@NotNull final File @NotNull ... files) throws MalformedURLException;

    public void loadDependency(@Nullable final File @Nullable ... files) {
        if (files == null || Arrays.stream(files).anyMatch(Objects::isNull) || Arrays.stream(files).noneMatch(loaded::contains)) {
            return;
        }

        try {
            //noinspection NullableProblems
            load(files);
        } catch (MalformedURLException exception) {
            logger.log(Level.SEVERE, exception, () -> "Could not load dependenc(y/ies) from files: " + Arrays.toString(files));
        }

        loaded.addAll(Arrays.asList(files));
    }
}
