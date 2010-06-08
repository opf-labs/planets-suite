/*
 * DocumentStoreManagerLocal.java
 *
 * Authors:	Carl Wilson & Andrew Jackson
 * Organisation: The British Library
 * Date: 29 February 2008
 * Project: PLANETS
 * Sub-Project: The Interoperability Framework (IF)
 * Work Package: Registry Foundation (IF/4)
 */

/*
 * History
 * 
 * 29/02/2008:	Provisional check in of a local interface for the document store management
 * 				functionality of the Data Registry.
 */

package eu.planets_project.ifr.core.storage.api;

import java.net.URI;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.w3c.dom.Document;

public interface DocumentStoreManagerLocal {

	/*
	 * Create a document store called �name�, that can store any XML documents. 
	 */
	URI createDocumentStore(String name);

	/*
	 * Create a document store called �name�, where each document must validate against the supplied schema. 
	 */
	URI createDocumentStore(String name, URI schemaLoc);

	/*
	 * Add a document to the store. 
	 */
	URI storeDocument(String name, Document doc);

	/*
	 * Retrieve a document from the store
	 */
	Document getDocument(String name);

	/*
	 * Execute the supplied JCR/XPath query and return the resulting element set.
	 */
	QueryResult query(Query query);
}
