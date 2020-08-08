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

    private final MavenPlaceholderReplacer replacer = new MavenPlaceholderReplacer(Collections.emptyMap());

    @NotNull
    @Override
    public MavenPlaceholderReplacer parse(@NotNull Document document)
    {
        //Default Placeholders
        final String groupId = document.getElementsByTagName("groupId").item(0).getNodeValue();
        if (groupId != null)
        {
            replacer.addPlaceholder("project.groupId", groupId);
        } else
        {
            throw new IllegalArgumentException("No group Id");
        }

        final String artifactId = document.getElementsByTagName("artifactId").item(0).getNodeValue();
        if (artifactId != null)
        {
            replacer.addPlaceholder("project.artifactId", artifactId);
        }

        final String version = document.getElementsByTagName("version").item(0).getNodeValue();
        if (version != null)
        {
            replacer.addPlaceholder("project.version", version);
        }

        NodeList propertiesElement = document.getElementsByTagName("properties");
        if (propertiesElement == null)
        {
            return replacer;
        }

        Node firstProperties = propertiesElement.item(0);
        if (firstProperties == null)
        {
            return replacer;
        }

        NodeList propertiesList = firstProperties.getChildNodes();

        for (int i = 0; i < propertiesList.getLength(); i++)
        {
            Node item = propertiesList.item(i);
            if (item instanceof Element)
            {
                Node child = item.getFirstChild();
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
