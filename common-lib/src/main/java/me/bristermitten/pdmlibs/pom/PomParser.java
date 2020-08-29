package me.bristermitten.pdmlibs.pom;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PomParser
{

    private static final DocumentBuilderFactory dbFactory;

    static
    {
        dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    }

    public <T> @NotNull T parse(@NotNull final ParseProcess<T> parseProcess, @NotNull final InputStream pomContent)
    {

        final Document document = getDocument(pomContent);

        return Objects.requireNonNull(parseProcess.parse(document), "Parse Process " + parseProcess + " returned null!");
    }

    @NotNull
    public Document getDocument(@NotNull final InputStream pomContent)
    {
        try
        {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(pomContent);
            doc.normalizeDocument();

            return doc;
        }
        catch (@NotNull ParserConfigurationException | SAXException | IOException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
