package me.bristermitten.pdm.repository.artifact;

import com.google.common.io.ByteStreams;
import me.bristermitten.pdm.util.URLUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

@SuppressWarnings("UnstableApiUsage")
public class SnapshotArtifact extends Artifact
{

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY;

    static
    {
        DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_BUILDER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        DOCUMENT_BUILDER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    }

    public SnapshotArtifact(@NotNull String groupId, @NotNull String artifactId, @NotNull String version)
    {
        super(groupId, artifactId, version);
    }

    @Override
    @Nullable
    public byte[] download(@NotNull final String baseRepoURL)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        final String url = createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".jar";

        return URLUtil.getBytes(url);
    }

    @Override
    @Nullable
    public byte[] downloadPom(@NotNull final String baseRepoURL)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        final String url = createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".pom";

        return URLUtil.getBytes(url);
    }


    private String getLatestVersion(String baseURL)
    {
        String metadataURL = createBaseURL(baseURL) + "/maven-metadata.xml";

        URLConnection connection = URLUtil.prepareConnection(metadataURL);
        if (connection == null)
        {
            return null;
        }
        byte[] bytes;
        try
        {
            bytes = ByteStreams.toByteArray(connection.getInputStream());
        }
        catch (IOException e)
        {
            return null;
        }

        Document doc;
        try
        {
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(bytes));
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            return null;
        }

        doc.getDocumentElement().normalize();
        Element versioning = (Element) doc.getElementsByTagName("versioning").item(0);
        NodeList snapshotVersions = versioning.getElementsByTagName("snapshotVersions");
        for (int i = 0; i < snapshotVersions.getLength(); i++)
        {
            NodeList snapshotVersion = ((Element) snapshotVersions.item(i)).getElementsByTagName("snapshotVersion");
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
        }
        return null;
    }
}
