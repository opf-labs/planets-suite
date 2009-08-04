/**
 * 
 */
package eu.planets_project.services.migration.dia.impl.genericwrapper.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The <code>DocumentLocator</code> provides methods for locating, loading and
 * instantiating <code>Document</code> instances.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class DocumentLocator extends ResourceLocator {

    /**
     * Initialise this <code>DocumentLocator</code> with a path/name of the
     * resource containing the document data to be located.
     * <code>documentResourcePath</code> must be either an absolute file path on
     * a mounted file system or a relative file path / file name on the
     * classpath.
     * 
     * @param documentResourcePath
     *            file name, relative or absolute path to the document resource
     *            to locate.
     */
    public DocumentLocator(String documentResourcePath) {
        super(documentResourcePath);
    }

    /**
     * Get a <code>Document</code> instance containing the contents of the
     * located document resource. This method will first attempt finding the
     * resource by using the {@link #getResourceStream} method.
     * 
     * @return <code>Document</code> containing the resource data.
     * @throws FileNotFoundException
     *             if the document could not be located.
     * @throws IOException
     *             if the document could not be initialised with the resource
     *             even though it was found.
     * @throws SAXException
     *             if it was not possible to build an XML document from the
     *             contents of the document resource.
     */
    public Document getDocument() throws FileNotFoundException, IOException,
            SAXException {

        try {
            final DocumentBuilderFactory docBuilderFacctory = DocumentBuilderFactory
                    .newInstance();

            final DocumentBuilder documentBuilder = docBuilderFacctory
                    .newDocumentBuilder();

            Document document = documentBuilder.parse(getResourceStream());

            return document;
        } catch (ParserConfigurationException pce) {
            // This will never happen, unless the default configuration of the
            // parser is broken.
            throw new SAXException("Parser configuration error.", pce);
        }
    }

}
