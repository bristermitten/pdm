package me.bristermitten.pdmlibs.pom;

import me.bristermitten.pdmlibs.dependency.Dependency;
import me.bristermitten.pdmlibs.dependency.DependencyDTO;
import me.bristermitten.pdmlibs.dependency.DependencyFactory;
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

    private final DependencyFactory dependencyFactory;
    private final RepositoryManager repositoryManager;
    private final HTTPService httpService;

    public ExtractParentsParseStage(@NotNull final DependencyFactory dependencyFactory, @NotNull final RepositoryManager repositoryManager,
                                    @NotNull final HTTPService httpService)
    {
        this.dependencyFactory = dependencyFactory;
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

        final DependencyDTO parentDTO = DependencyNotationExtractor.extractFrom((Element) parent);
        final Dependency dependency = dependencyFactory.toArtifact(parentDTO);
        final Repository containingRepo = repositoryManager.firstContaining(dependency);

        if (containingRepo == null)
        {
            //TODO log a warning?
            return null;
        }

        try (final InputStream inputStream = httpService.readPom(containingRepo.getURL(), dependency))
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
