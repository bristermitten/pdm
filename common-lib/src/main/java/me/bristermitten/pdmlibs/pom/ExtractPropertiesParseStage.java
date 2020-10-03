package me.bristermitten.pdmlibs.pom;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;

/**
 * @author AlexL
 */
public class ExtractPropertiesParseStage implements ParseStage<MavenPlaceholderReplacer>
{

    @NotNull
    private final MavenPlaceholderReplacer replacer;

    public ExtractPropertiesParseStage()
    {
        replacer = new MavenPlaceholderReplacer(Collections.emptyMap());
    }

    public ExtractPropertiesParseStage(@NotNull final MavenPlaceholderReplacer replacer)
    {
        this();
        this.replacer.addAllFrom(replacer);
    }

    @NotNull
    @Override
    public MavenPlaceholderReplacer parse(@NotNull final Document document)
    {
        //Default Placeholders
        final String groupId = document.getElementsByTagName("groupId").item(0).getTextContent();

        if (groupId != null)
        {
            replacer.addPlaceholder("project.groupId", groupId);
        } else
        {
            throw new IllegalArgumentException("No group Id");
        }

        final String artifactId = document.getElementsByTagName("artifactId").item(0).getTextContent();

        if (artifactId != null)
        {
            replacer.addPlaceholder("project.artifactId", artifactId);
        } else
        {
            throw new IllegalArgumentException("No artifact Id");
        }

        final String version = document.getElementsByTagName("version").item(0).getTextContent();

        if (version != null)
        {
            replacer.addPlaceholder("project.version", version);
        } else
        {
            throw new IllegalArgumentException("No version");
        }

        final NodeList propertiesElement = document.getElementsByTagName("properties");

        if (propertiesElement == null)
        {
            return replacer;
        }

        final Node firstProperties = propertiesElement.item(0);

        if (firstProperties == null)
        {
            return replacer;
        }

        final NodeList propertiesList = firstProperties.getChildNodes();

        for (int i = 0; i < propertiesList.getLength(); i++)
        {
            final Node item = propertiesList.item(i);

            if (item instanceof Element)
            {
                final Node child = item.getFirstChild();

                if (child == null)
                {
                    continue;
                }
                replacer.addPlaceholder(((Element) item).getTagName(), child.getNodeValue());
            }
        }

        return replacer;
    }
}
