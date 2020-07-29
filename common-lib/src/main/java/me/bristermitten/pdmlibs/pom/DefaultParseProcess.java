package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.Set;

/**
 * @author AlexL
 */
public class DefaultParseProcess implements ParseProcess<Set<Artifact>>
{

    @NotNull
    private final ArtifactFactory artifactFactory;

    @NotNull
    private final ExtractPropertiesParseStage propertiesParseStage;

    public DefaultParseProcess(@NotNull final ArtifactFactory artifactFactory)
    {
        this.artifactFactory = artifactFactory;
        propertiesParseStage = new ExtractPropertiesParseStage();
    }

    @NotNull
    @Override
    public Set<Artifact> parse(@NotNull Document document)
    {
        final Map<String, String> placeholders = propertiesParseStage.parse(document);

        final ParseStage<Set<Artifact>> dependenciesParseStage = new ExtractDependenciesParseStage(this.artifactFactory, placeholders);

        return dependenciesParseStage.parse(document);
    }

}
