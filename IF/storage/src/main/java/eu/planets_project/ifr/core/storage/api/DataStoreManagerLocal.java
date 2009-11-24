/*
 * FileSystemManagerLocal.java
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
 * 29/02/2008:	Provisional check in of a local interface for the file system management
 * 				functionality of the Data Registry.
 */
package eu.planets_project.ifr.core.storage.api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

import org.w3c.dom.Document;

public interface DataStoreManagerLocal {
	
	/*
	 * TODO This is a local method and should return a PDM instance, this will be
	 * 		amended when the PDM objects and API are released.  For now an XML document is
	 * 		returned.
	 * Reads and returns the XIP for a given PD-URI.
	 */
	Document read(URI pdURI);
	
	/*
	 * Return a list of contained PD-URIs for a directory, and NULL if the PD-URI
	 * passed is for a file
	 */
	URI[] list(URI pdURI);

	/*
	 * Open a file stream for a given PD-URI, from Java.
	 */
	FileInputStream openFileStream(URI pdURI);

	/*
	 * Open a file stream onto the store, from Java.
	 */
	FileOutputStream openFileOutputStream(URI pdURI);

	/*
	 * Stores the XIP at the specified PD-URI.
	 */
	void store(URI pdURI, Document xipDoc);
}

