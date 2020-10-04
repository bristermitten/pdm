package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.dependency.DependencyFactory;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.repository.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author AlexL
 */
public class DefaultParseProcess implements ParseProcess<Set<Dependency>>
{

    @NotNull
    private final DependencyFactory dependencyFactory;

    @NotNull
    private final ExtractParentsParseStage extractParentsParseStage;

    public DefaultParseProcess(@NotNull final DependencyFactory dependencyFactory, @NotNull final RepositoryManager repositoryManager,
                               @NotNull final HTTPService httpService)
    {
        this.dependencyFactory = dependencyFactory;
        extractParentsParseStage = new ExtractParentsParseStage(dependencyFactory, repositoryManager, httpService);
    }

    @NotNull
    @Override
    public Set<Dependency> parse(@NotNull Document document)
    {
        final List<Document> parents = extractParentsParseStage.parse(document);
        final MavenPlaceholderReplacer placeholderReplacer = new MavenPlaceholderReplacer(Collections.emptyMap());
        final ExtractPropertiesParseStage propertiesParseStage = new ExtractPropertiesParseStage(placeholderReplacer);

        Collections.reverse(parents);

        for (final Document parent : parents)
        {
            placeholderReplacer.addAllFrom(propertiesParseStage.parse(parent));
        }

        final MavenPlaceholderReplacer placeholders = propertiesParseStage.parse(document);
        final ParseStage<Set<Dependency>> dependenciesParseStage = new ExtractDependenciesParseStage(this.dependencyFactory, placeholders);

        return dependenciesParseStage.parse(document);
    }
}
