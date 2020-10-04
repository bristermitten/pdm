package me.bristermitten.pdmlibs.pom;

import com.google.common.collect.Sets;
import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.dependency.DependencyDTO;
import me.bristermitten.pdmlibs.dependency.DependencyFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author AlexL
 */
public class ExtractDependenciesParseStage implements ParseStage<Set<Dependency>>
{

    private static final Set<String> DEFAULT_SCOPES_TO_DROP = Sets.newHashSet(
            "test",
            "provided",
            "system"
    );

    @NotNull
    private final DependencyFactory dependencyFactory;

    @NotNull
    private final Set<String> ignoredScopes;

    @NotNull
    private final MavenPlaceholderReplacer placeholderReplacer;

    public ExtractDependenciesParseStage(@NotNull final DependencyFactory dependencyFactory, @NotNull final MavenPlaceholderReplacer placeholders)
    {
        this(dependencyFactory, DEFAULT_SCOPES_TO_DROP, placeholders);
    }

    public ExtractDependenciesParseStage(@NotNull final DependencyFactory dependencyFactory, @NotNull final Set<String> ignoredScopes,
                                         @NotNull final MavenPlaceholderReplacer placeholders)
    {
        this.dependencyFactory = dependencyFactory;
        this.ignoredScopes = ignoredScopes;
        this.placeholderReplacer = placeholders;
    }

    @NotNull
    @Override
    public Set<Dependency> parse(@NotNull final Document document)
    {
        final Set<Dependency> dependencySet = new LinkedHashSet<>();
        final NodeList dependenciesNodeList = document.getElementsByTagName("dependencies");
        final Element dependenciesElement = (Element) dependenciesNodeList.item(0);

        if (dependenciesElement == null)
        {
            return dependencySet;
        }

        final NodeList dependencies = dependenciesElement.getElementsByTagName("dependency");

        if (dependencies == null)
        {
            return dependencySet;
        }

        for (int temp = 0; temp < dependencies.getLength(); temp++)
        {
            final Node node = dependencies.item(temp);

            if (node instanceof Element)
            {
                final Dependency parsed = getDependencyFromXML((Element) node);

                if (parsed != null)
                {
                    dependencySet.add(parsed);
                }
            }
        }

        return dependencySet;
    }

    @Nullable
    public Dependency getDependencyFromXML(@NotNull final Element dependencyElement)
    {
        final DependencyDTO dependencyDTO = DependencyNotationExtractor.extractFrom(dependencyElement);

        if (dependencyDTO == null)
        {
            return null;
        }

        final String groupId = placeholderReplacer.replace(dependencyDTO.getGroupId());
        final String artifactId = placeholderReplacer.replace(dependencyDTO.getArtifactId());
        final String version = placeholderReplacer.replace(dependencyDTO.getVersion());
        final NodeList scopeList = dependencyElement.getElementsByTagName("scope");

        if (scopeList != null && scopeList.getLength() > 0)
        {
            final String scope = scopeList.item(0).getTextContent();

            if (ignoredScopes.contains(scope))
            {
                return null;
            }
        }

        /*
         *  TODO currently we're just skipping optional dependencies.
         *  In the future we should look into having them loaded or not based on if they are actually needed
         */
        final Node optional = dependencyElement.getElementsByTagName("optional").item(0);

        if (optional instanceof Element)
        {
            final String isOptional = optional.getTextContent();

            if (isOptional.equals("true"))
            {
                return null;
            }
        }

        return dependencyFactory.toArtifact(groupId, artifactId, version, null, null, null);
    }
}
