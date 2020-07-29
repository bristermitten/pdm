package me.bristermitten.pdmlibs.pom;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AlexL
 */
public class ExtractPropertiesParseStage implements ParseStage<Map<String, String>>
{

    @NotNull
    @Override
    public Map<String, String> parse(@NotNull Document document)
    {
        final Map<String, String> properties = new LinkedHashMap<>();
        NodeList propertiesElement = document.getElementsByTagName("properties");
        if (propertiesElement == null)
        {
            return properties;
        }
        Node firstProperties = propertiesElement.item(0);
        if (firstProperties == null)
        {
            return properties;
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
                properties.put(((Element) item).getTagName(), child.getNodeValue());
            }

        }

        return properties;
    }
}
