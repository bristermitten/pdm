package me.bristermitten.pdm.reposcraper.repository;

import me.bristermitten.pdm.reposcraper.entity.Artifact;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface Repository {
    boolean contains(@NotNull final Artifact artifact);

    @NotNull InputStream download(@NotNull final Artifact artifact);
}
