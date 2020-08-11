package me.bristermitten.pdmlibs.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class Strings
{

    private static final Pattern BLANK_PATTERN = Pattern.compile("\\s*");

    private Strings()
    {

    }

    public static boolean isBlank(@NotNull final String string)
    {
        return string.isEmpty() || BLANK_PATTERN.matcher(string).matches();
    }

    @NotNull
    public static String escapeRegex(@NotNull final String string)
    {
        return string.replace(".", "\\.")
                .replace("/", "\\/")
                .replace("$", "\\$");
    }
}
