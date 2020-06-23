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

    private final ArtifactFactory artifactFactory;

    public PomParser(ArtifactFactory artifactFactory)
    {

        this.artifactFactory = artifactFactory;
    }

    public Set<Artifact> extractDependenciesFromPom(@NotNull final String pomContent)
    {
        Set<Artifact> dependencySet = new HashSet<>();
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            ByteArrayInputStream is = new ByteArrayInputStream(pomContent.getBytes());

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
                Node nNode = dependencies.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Artifact parsed = getDependencyFromXML((Element) nNode);
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
        String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
        String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
        String version = dependencyElement.getElementsByTagName("version").item(0).getTextContent();
        String scope = dependencyElement.getElementsByTagName("scope").item(0).getTextContent();
        if (SCOPES_TO_DROP.contains(scope))
        {
            return null;
        }
        return artifactFactory.toArtifact(groupId, artifactId, version, null, null);
    }
}
