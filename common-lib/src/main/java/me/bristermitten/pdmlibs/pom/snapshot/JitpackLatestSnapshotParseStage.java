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
public class JitpackLatestSnapshotParseStage implements ParseStage<@Nullable String>
{

    private static final String JITPACK_JAR_NAME_FORMAT = "-%s-%s";

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
        final Node snapshot = versioningElement.getElementsByTagName("snapshot").item(0);
        if (!(snapshot instanceof Element))
        {
            return null;
        }

        final Element snapshotElement = (Element) snapshot;

        Node buildNumber = snapshotElement.getElementsByTagName("buildNumber").item(0);
        if (buildNumber == null)
        {
            return null;
        }

        Node timestamp = snapshotElement.getElementsByTagName("timestamp").item(0);
        if (timestamp == null)
        {
            return null;
        }


        return String.format(JITPACK_JAR_NAME_FORMAT, timestamp.getTextContent(), buildNumber.getTextContent());
    }
}
