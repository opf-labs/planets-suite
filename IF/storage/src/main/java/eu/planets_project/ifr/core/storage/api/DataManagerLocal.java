/*
 * DataManagerLocal.java
 *
 * Created on 04 July 2007, 13:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.api;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * The DataManagerLocal interface provides methods to store and retrieve binary data to and from a PLANETS IF Data Registry.
 *
 * @author CFwilson, BL
 */
public interface DataManagerLocal extends DataManagerRemote {
	/**
	 * Retrieves the byte sequence identified by the passed PDURI in an InputStream.
	 * 
	 * @deprecated
	 * @param	pdURI
	 *		The PDURI for the item to be returned
	 * @return	An InputStream containing the binary identified by the passed PDURI.
	 * @throws	PathNotFoundException
	 * @throws	URISyntaxException
	 */
	@Deprecated
	public InputStream retrieve(URI pdURI) throws PathNotFoundException, URISyntaxException;

	/**
	 * Stores the binary data from the passed InputStream in the Data Registry at the location
	 * specified by the PDURI passed as a parameter.<p/>
	 * Implemented as write once, will not overwrite an existing node.
	 * 
	 * @deprecated
	 * @param	pdURI
	 *		Identifies the location at which to store the binary
	 * @param	stream
	 *		An InputStream containing the binary to be added to the Data Registry.
	 * @throws	LoginException
	 * @throws	RepositoryException
	 * @throws	URISyntaxException
	 */
	@Deprecated
	public void store(URI pdURI, InputStream stream) throws LoginException, RepositoryException, URISyntaxException;

	/**
	 * Create a temporary directory and pass back the file:/// handle.
	 * This location is currently identified by properties file contents.
	 * 
	 * @return	A URI for a temporary sandbox (currently as a file:/ URI)
	 * @throws	URISyntaxException
	 */
	public URI createLocalSandbox() throws URISyntaxException;

	/**
	 * Stores a byte sequence in the PLANETS Data Registry at the location specified
	 * by the supplied PDURI.<p/>
	 * This method is implemented as write once and will not overwrite existing content.
	 * 
	 * @param	pdURI
	 *			A PDURI to identify the stored binary.
	 * @param	binary
	 *          The binary to store supplied as a <code>byte[]</code>.
	 * @throws	LoginException
	 * @throws	RepositoryException
	 * @throws	URISyntaxException 
	 */
	void storeBinary(URI pdURI, byte[] binary) throws LoginException, RepositoryException, URISyntaxException;
}
