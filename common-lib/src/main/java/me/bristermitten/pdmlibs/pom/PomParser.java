package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import org.jetbrains.annotations.NotNull;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PomParser
{

    private static final Set<String> SCOPES_TO_DROP = new HashSet<>(Arrays.asList(
            "test",
            "provided"
    ));
    private static final DocumentBuilderFactory dbFactory;

    static
    {
        dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    }

    private final ArtifactFactory artifactFactory;

    public PomParser(ArtifactFactory artifactFactory)
    {
        this.artifactFactory = artifactFactory;
    }

    public Set<Artifact> extractDependenciesFromPom(@NotNull final String pomContent)
    {
        Set<Artifact> dependencySet = new HashSet<>();
        try (ByteArrayInputStream is = new ByteArrayInputStream(pomContent.getBytes()))
        {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();


            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList dependenciesNodeList = doc.getElementsByTagName("dependencies");
            Element dependenciesElement = (Element) dependenciesNodeList.item(0);

            if (dependenciesElement == null)
            {
                return dependencySet;
            }
            NodeList dependencies = dependenciesElement.getElementsByTagName("dependency");
            if (dependencies == null)
            {
                return dependencySet;
            }
            for (int temp = 0; temp < dependencies.getLength(); temp++)
            {
                Node node = dependencies.item(temp);

                if (node instanceof Element)
                {
                    Artifact parsed = getDependencyFromXML((Element) node);
                    if (parsed != null)
                    {
                        dependencySet.add(parsed);
                    }
                }
            }
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new IllegalArgumentException(e);
        }

        return dependencySet;
    }

    private Artifact getDependencyFromXML(Element dependencyElement)
    {
        final String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
        final String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
        final NodeList versionNodeList = dependencyElement.getElementsByTagName("version");
        if (versionNodeList == null || versionNodeList.getLength() == 0)
        {
            return null;
        }

        final String version = versionNodeList.item(0).getTextContent();
        final NodeList scopeList = dependencyElement.getElementsByTagName("scope");
        if (scopeList != null && scopeList.getLength() > 0)
        {
            String scope = scopeList.item(0).getTextContent();
            if (SCOPES_TO_DROP.contains(scope))
            {
                return null;
            }
        }

        return artifactFactory.toArtifact(groupId, artifactId, version, null, null);
    }
}
