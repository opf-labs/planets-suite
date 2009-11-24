/*
 * WorkflowStoreManagerRemote.java
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
 * 29/02/2008:	Provisional check in of a remote interface for the workflow store management
 * 				functionality of the Data Registry.
 */
package eu.planets_project.ifr.core.storage.api;

import java.net.URI;

import org.w3c.dom.Document;

public interface WorkflowStoreManagerRemote {

	/*
	 * Add a workflow t the store
	 */
	URI storeWorkflow(String name, Document doc);

	/*
	 * Retrieve a workflow from the store
	 */
	Document getWorkflow(String name);
}
