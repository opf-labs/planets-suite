/*
 * FileSystemManagerRemote.java
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
 * 29/02/2008:	Provisional check in of a remote interface for the file system management
 * 				functionality of the Data Registry.
 */
package eu.planets_project.ifr.core.storage.api;

import java.net.URI;
import java.net.URL;

import org.w3c.dom.Document;

public interface DataStoreManagerRemote {
	
	/*
	 * Reads and returns the XIP for a given PD-URI.
	 */
	Document read(URI pdURI);
	
	/*
	 * Reads and returns the XIP for a given PD-URI. Binary data (files) are
	 * base 64 encoded within the document.
	 */
	Document readEmbedded(URI pdURI);

	/*
	 * Return a list of contained PD-URIs for a directory, and NULL if the PD-URI
	 * passed is for a file
	 */
	URI[] list(URI pdURI);

	/*
	 * Returns an array of download URLs for a given PD-URI
	 */
	URL[] listDownladURLs(URI pdURI);
	
	/*
	 * Get a list of the supported import protocols, e.g. `planets', `https', `ftp', `file'. 
	 */
	String[] listSupportedImportProtocols(URI pdURI);

	/*
	 * Stores the XIP at the specified PD-URI.
	 */
	void store(URI pdURI, Document xipDoc);
}
