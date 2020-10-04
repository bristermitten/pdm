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

    private static final DocumentBuilderFactory DB_FACTORY;

    static
    {
        DB_FACTORY = DocumentBuilderFactory.newInstance();
        DB_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        DB_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
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
            final DocumentBuilder dBuilder = DB_FACTORY.newDocumentBuilder();
            final Document doc = dBuilder.parse(pomContent);

            doc.normalizeDocument();

            return doc;
        }
        catch (ParserConfigurationException | SAXException | IOException exception)
        {
            throw new IllegalArgumentException(exception);
        }
    }
}
