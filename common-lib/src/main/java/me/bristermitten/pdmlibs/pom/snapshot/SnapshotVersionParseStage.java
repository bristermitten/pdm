package me.bristermitten.pdmlibs.pom.snapshot;

import me.bristermitten.pdmlibs.pom.ParseStage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author AlexL
 */
public class SnapshotVersionParseStage implements ParseStage<@Nullable String>
{

    @Override
    @Nullable
    public String parse(@NotNull Document document)
    {
        Element versioning = (Element) document.getElementsByTagName("versioning").item(0);

        NodeList versions = versioning.getElementsByTagName("snapshotVersions");
        Element snapshotVersions = (Element) versions.item(0);
        if (snapshotVersions == null)
        {
            return null;
        }
        NodeList snapshotVersion = snapshotVersions.getElementsByTagName("snapshotVersion");
        if (snapshotVersion == null)
        {
            return null;
        }

        for (int j = 0; j < snapshotVersion.getLength(); j++)
        {
            Element snapshotItem = (Element) snapshotVersion.item(j);
            String extension = snapshotItem.getElementsByTagName("extension").item(0).getTextContent();
            Node classifier = snapshotItem.getElementsByTagName("classifier").item(0);
            if (extension.equals("jar") && classifier == null)
            {
                return snapshotItem.getElementsByTagName("value").item(0).getTextContent();
            }
        }
        return null;
    }
}
