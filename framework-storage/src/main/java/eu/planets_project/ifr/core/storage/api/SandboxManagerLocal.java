/*
 * SandboxManagerLocal.java
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

import java.net.URI;

public interface SandboxManagerLocal {
	/*
	 * Create a temporary directory and pass back the file:/// handle. 
	 */
	URI createLocalSandbox();
}

