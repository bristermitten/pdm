package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AlexL
 */
public class MavenPlaceholderReplacer
{

    private static final String PATTERN_FORMAT = "$\\{%s\\}";

    private static final Logger LOGGER = Logger.getLogger(MavenPlaceholderReplacer.class.getName());

    @NotNull
    private final Map<Matcher, String> placeholders = new LinkedHashMap<>();

    public MavenPlaceholderReplacer(@NotNull final Map<String, String> placeholders)
    {
        placeholders.forEach(this::addPlaceholder);
    }

    public void addPlaceholder(@NotNull final String placeholder, @NotNull String replacement)
    {
        String format = Strings.escapeRegex(String.format(PATTERN_FORMAT, placeholder));

        String replace = replace(replacement);
        if (replace.contains("$"))
        {
            LOGGER.fine(() -> replace + " is an invalid placeholder, it will be discarded.");
            return;
        }
        this.placeholders.put(Pattern.compile(format).matcher(""), replace);
    }

    public void addAllFrom(@NotNull final MavenPlaceholderReplacer other)
    {
        other.placeholders.forEach(placeholders::put);
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
