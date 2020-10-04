package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.dependency.DependencyDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DependencyNotationExtractor
{

    private DependencyNotationExtractor()
    {

    }

    @Nullable
    public static DependencyDTO extractFrom(@NotNull final Element element)
    {

        final String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
        final String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
        final NodeList versionNodeList = element.getElementsByTagName("version");

        if (versionNodeList == null || versionNodeList.getLength() == 0)
        {
            return null;
        }

        final String version = versionNodeList.item(0).getTextContent();

        return new DependencyDTO(groupId, artifactId, version, null, null, null);
    }
}
