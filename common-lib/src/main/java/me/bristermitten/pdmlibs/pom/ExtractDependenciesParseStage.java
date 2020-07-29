package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * @author AlexL
 */
public class ExtractDependenciesParseStage implements ParseStage<Set<Artifact>>
{

    private static final Set<String> DEFAULT_SCOPES_TO_DROP = new HashSet<>(Arrays.asList(
            "test",
            "provided",
            "system"
    ));

    @NotNull
    private final ArtifactFactory artifactFactory;

    @NotNull
    private final Set<String> ignoredScopes;

    @NotNull
    private final MavenPlaceholderReplacer placeholderReplacer;

    public ExtractDependenciesParseStage(@NotNull final ArtifactFactory artifactFactory, @NotNull Map<String, String> placeholders)
    {
        this(artifactFactory, DEFAULT_SCOPES_TO_DROP, placeholders);
    }

    public ExtractDependenciesParseStage(@NotNull final ArtifactFactory artifactFactory, @NotNull final Set<String> ignoredScopes, @NotNull Map<String, String> placeholders)
    {
        this.artifactFactory = artifactFactory;
        this.ignoredScopes = ignoredScopes;
        this.placeholderReplacer = new MavenPlaceholderReplacer(placeholders);
    }

    @NotNull
    @Override
    public Set<Artifact> parse(@NotNull Document document)
    {
        final Set<Artifact> dependencySet = new LinkedHashSet<>();

        NodeList dependenciesNodeList = document.getElementsByTagName("dependencies");
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

        return dependencySet;
    }

    private Artifact getDependencyFromXML(Element dependencyElement)
    {
        final String groupId = placeholderReplacer.replace(dependencyElement.getElementsByTagName("groupId").item(0).getTextContent());
        final String artifactId = placeholderReplacer.replace(dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent());
        final NodeList versionNodeList = dependencyElement.getElementsByTagName("version");
        if (versionNodeList == null || versionNodeList.getLength() == 0)
        {
            return null;
        }

        final String version = placeholderReplacer.replace(versionNodeList.item(0).getTextContent());
        final NodeList scopeList = dependencyElement.getElementsByTagName("scope");
        if (scopeList != null && scopeList.getLength() > 0)
        {
            String scope = scopeList.item(0).getTextContent();
            if (ignoredScopes.contains(scope))
            {
                return null;
            }
        }

        return artifactFactory.toArtifact(groupId, artifactId, version, null, null);
    }
}
