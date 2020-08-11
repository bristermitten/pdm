package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
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
public class DefaultParseProcess implements ParseProcess<Set<Artifact>>
{

    @NotNull
    private final ArtifactFactory artifactFactory;

    private final ExtractParentsParseStage extractParentsParseStage;

    public DefaultParseProcess(@NotNull final ArtifactFactory artifactFactory,
                               @NotNull final RepositoryManager repositoryManager,
                               @NotNull final HTTPService httpService)
    {
        this.artifactFactory = artifactFactory;
        extractParentsParseStage = new ExtractParentsParseStage(artifactFactory, repositoryManager, httpService);
    }

    @NotNull
    @Override
    public Set<Artifact> parse(@NotNull Document document)
    {
        final List<Document> parents = extractParentsParseStage.parse(document);
        MavenPlaceholderReplacer placeholderReplacer = new MavenPlaceholderReplacer(Collections.emptyMap());
        final ExtractPropertiesParseStage propertiesParseStage = new ExtractPropertiesParseStage(placeholderReplacer);

        Collections.reverse(parents);
        for (Document parent : parents)
        {
            placeholderReplacer.addAllFrom(propertiesParseStage.parse(parent));
        }

        final MavenPlaceholderReplacer placeholders = propertiesParseStage.parse(document);

        final ParseStage<Set<Artifact>> dependenciesParseStage = new ExtractDependenciesParseStage(this.artifactFactory, placeholders);

        return dependenciesParseStage.parse(document);
    }

}
