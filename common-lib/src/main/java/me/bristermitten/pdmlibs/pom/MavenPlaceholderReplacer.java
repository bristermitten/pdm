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
        placeholders.forEach(this::addPlaceholder);
    }

    public void addPlaceholder(@NotNull final String placeholder, @NotNull final String replacement)
    {

        String format = String.format(PATTERN_FORMAT, placeholder)
                .replace(".", "\\."); //sanitize for regex

        String replace = replace(replacement);
        this.placeholders.put(Pattern.compile(format).matcher(""), replace);
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
            temp = matcher.replaceFirst(replacement);
        }

        return temp;
    }
}
