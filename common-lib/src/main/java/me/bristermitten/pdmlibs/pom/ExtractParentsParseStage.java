package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.artifact.Artifact;
import me.bristermitten.pdmlibs.artifact.ArtifactDTO;
import me.bristermitten.pdmlibs.artifact.ArtifactFactory;
import me.bristermitten.pdmlibs.http.HTTPService;
import me.bristermitten.pdmlibs.repository.Repository;
import me.bristermitten.pdmlibs.repository.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ExtractParentsParseStage implements ParseStage<@NotNull List<Document>>, ParseProcess<@NotNull List<Document>>
{

    private final ArtifactFactory artifactFactory;
    private final RepositoryManager repositoryManager;
    private final HTTPService httpService;

    public ExtractParentsParseStage(@NotNull final ArtifactFactory artifactFactory, @NotNull final RepositoryManager repositoryManager,
                                    @NotNull final HTTPService httpService)
    {
        this.artifactFactory = artifactFactory;
        this.repositoryManager = repositoryManager;
        this.httpService = httpService;
    }

    @NotNull
    @Override
    public List<Document> parse(@NotNull final Document document)
    {
        final List<Document> parentTree = new LinkedList<>();

        Document parent = loadParent(document);
        while (parent != null)
        {
            parentTree.add(parent);
            parent = loadParent(parent);
        }

        return parentTree;
    }

    @Nullable
    private Document loadParent(@NotNull final Document document)
    {

        final Node parent = document.getElementsByTagName("parent").item(0);

        if (!(parent instanceof Element))
        {
            return null;
        }

        final ArtifactDTO parentDTO = DependencyNotationExtractor.extractFrom((Element) parent);
        final Artifact artifact = artifactFactory.toArtifact(parentDTO);
        final Repository containingRepo = repositoryManager.firstContaining(artifact);

        if (containingRepo == null)
        {
            //TODO log a warning?
            return null;
        }

        try (final InputStream inputStream = httpService.readPom(containingRepo.getURL(), artifact))
        {
            return new PomParser().getDocument(inputStream);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            return null;
        }
    }
}
