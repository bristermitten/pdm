package me.bristermitten.pdm.repository.pom;

import me.bristermitten.pdm.dependency.Dependency;
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

    private PomParser()
    {

    }

    public static Set<Dependency> extractDependenciesFromPom(byte[] pomContent)
    {
        Set<Dependency> dependencySet = new HashSet<>();
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            ByteArrayInputStream is = new ByteArrayInputStream(pomContent);

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
                    Dependency parsed = getDependencyFromXML((Element) nNode);
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

    private static Dependency getDependencyFromXML(Element dependencyElement)
    {
        String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
        String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
        String version = dependencyElement.getElementsByTagName("version").item(0).getTextContent();
        String scope = dependencyElement.getElementsByTagName("scope").item(0).getTextContent();
        if (SCOPES_TO_DROP.contains(scope))
        {
            return null;
        }

        return new Dependency(groupId, artifactId, version);
    }
}
