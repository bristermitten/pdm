package me.bristermitten.pdmlibs.pom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

/**
 * @author AlexL
 */
public interface ParseStage<T>
{

    @Nullable
    T parse(@NotNull final Document document);
}
