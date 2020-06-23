package me.bristermitten.pdm.repository.artifact;

import me.bristermitten.pdm.http.HTTPService;
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
    public String getJarURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".jar";
    }

    @Override
    @Nullable
    public String getPomURL(@NotNull final String baseRepoURL, @NotNull final HTTPService httpService)
    {
        final String latestSnapshotVersion = getLatestVersion(baseRepoURL, httpService);
        if (latestSnapshotVersion == null)
        {
            return null;
        }

        return createBaseURL(baseRepoURL) + getArtifactId() + "-" + latestSnapshotVersion + ".pom";
    }


    private String getLatestVersion(String baseURL, HTTPService httpService)
    {
        String metadataURL = createBaseURL(baseURL) + "/maven-metadata.xml";
        byte[] bytes = httpService.downloadFrom(metadataURL);

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
