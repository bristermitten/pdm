package me.bristermitten.pdmlibs.pom.snapshot;

import me.bristermitten.pdmlibs.pom.ParseStage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author AlexL
 */
public class LatestElementParseStage implements ParseStage<@Nullable String>
{

    @Override
    @Nullable
    public String parse(@NotNull Document document)
    {
        final Node versioning = document.getElementsByTagName("versioning").item(0);
        if (!(versioning instanceof Element))
        {
            return null;
        }

        final Element versioningElement = (Element) versioning;
        final Node latestElement = versioningElement.getElementsByTagName("latest").item(0);
        if (!(latestElement instanceof Element))
        {
            return null;
        }

        return latestElement.getTextContent();
    }
}
