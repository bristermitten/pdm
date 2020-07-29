package me.bristermitten.pdmlibs.pom;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AlexL
 */
public class MavenPlaceholderReplacer
{

    private static final String PATTERN_FORMAT = "\\$\\{%s\\}";

    @NotNull
    private final Map<Matcher, String> placeholders = new LinkedHashMap<>();

    public MavenPlaceholderReplacer(@NotNull final Map<String, String> placeholders)
    {
        placeholders.forEach((placeholder, replacement) -> {
            String format = String.format(PATTERN_FORMAT, placeholder)
                    .replace(".", "\\."); //sanitize for regex

            this.placeholders.put(Pattern.compile(format).matcher(""), replacement);
        });
    }

    @NotNull
    public String replace(@NotNull final String value)
    {
        String temp = value;
        for (Map.Entry<Matcher, String> entry : placeholders.entrySet())
        {
            Matcher matcher = entry.getKey();
            matcher.reset(temp);
            String replacement = entry.getValue();
            if (replacement == null)
            {
                continue;
            }
            temp = matcher.replaceAll(replacement);
        }

        return temp;
    }
}
