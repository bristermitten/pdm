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

    @Nullable
    @Override
    public String parse(@NotNull final Document document)
    {
        final Element versioning = (Element) document.getElementsByTagName("versioning").item(0);
        final NodeList versions = versioning.getElementsByTagName("snapshotVersions");
        final Element snapshotVersions = (Element) versions.item(0);

        if (snapshotVersions == null)
        {
            return null;
        }

        final NodeList snapshotVersion = snapshotVersions.getElementsByTagName("snapshotVersion");

        if (snapshotVersion == null)
        {
            return null;
        }

        for (int j = 0; j < snapshotVersion.getLength(); j++)
        {
            final Element snapshotItem = (Element) snapshotVersion.item(j);
            final String extension = snapshotItem.getElementsByTagName("extension").item(0).getTextContent();
            final Node classifier = snapshotItem.getElementsByTagName("classifier").item(0);

            if (extension.equals("jar") && classifier == null)
            {
                return snapshotItem.getElementsByTagName("value").item(0).getTextContent();
            }
        }

        return null;
    }
}
