package eu.planets_project.ifr.core.storage.api;

import java.io.IOException;
import java.net.URI;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

/**
 * The DocumentManager interface provides methods to store, query and retrieve XML documents from a PLANETS IF Data Registry.
 * 
 * @author CFwilson
 *
 */
public interface DocumentManager {
	/**
	 * Stores the passed XML document in the Data Registry document store with the name supplied.
	 * The document is stored not as a binary but as a JCR tree representation of the document which means that
	 * it is searchable using XQuery.
	 *
	 * @param	name
	 *			A string name for the document to be stored.
	 * @param	doc
	 *			The XML document to be parsed and stored
	 * @return	The PDURI for the stored document
	 * @throws	IOException
	 * @throws	LoginException
	 * @throws	RepositoryException
	 * @throws	TransformerConfigurationException
	 * @throws	TransformerException
	 */
	URI storeDocument(String name, Document doc) throws IOException, LoginException, RepositoryException, TransformerConfigurationException, TransformerException;

	/**
	 * Retrieves the XML document with the name passed as a parameter
	 *
	 * @param	name
	 *			The data registry name for the document supplied when storing it.
	 * @return	The stored document as an XML document.
	 * @throws	IOException
	 * @throws	ParserConfigurationException
	 * @throws	TransformerConfigurationException
	 * @throws	TransformerException
	 */
	Document getDocument(String name) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException;
}
