/*
 * DataRegistry.java
 *
 * Created on 02 July 2007, 08:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.api;

import java.net.URI;

import javax.xml.soap.SOAPException;

/**
 * 
 * @author CFwilson
 *
 */
public interface DataManagerRemote {

	/**
	 * Lists the PDURIs that are children of the URI passed.
	 * The passed PDURI should be that of a DataRegistry folder.<p/>
	 * If the user passes a <code>null</code> URI as the parameter the method returns the root PDURI for the Data Registry.<p/
	 * 
	 * @param	pdUri
	 *			The PDURI that the caller wishes to find the children of.
	 * @return	An array of PDURIs that are children of the parameter PDURI. An empty list is returned for an
	 * empty directory. <code>null</code> is returned if pdURI is not a folder.
	 * @throws 	SOAPException
	 */
	public URI[] list(URI pdUri) throws SOAPException; 

	/**
	 * Returns a download URI for the data object in the registry identified by the passed PDURI.
	 * For the current implementation this simply returns the webdav URL.  The WebDav JCR app
	 * is installed with the IF and the form of the root is known.
	 *
	 * @param	pdURI
	 * @return	A URI that is an http webdav URL that the file can be downloaded from
	 * @throws	SOAPException
	 */
	public URI listDownladURI(URI pdURI) throws SOAPException;

	/**
	 * Reads binary data at a PDURI and returns base64 encoded data in an
	 * XML document returned as a String.
	 * This method was developed while testing different approaches to passing binary objects
	 * and was not part of the original API specification
	 * 
	 * @deprecated
	 * @param	pdURI A PDURI that identifies the binary to be retrieved
	 * @return	An XML document containing the base 64 encoded binary as a String
	 * @throws	SOAPException
	 */
	@Deprecated
	public String read(URI pdURI) throws SOAPException;

	/**
	 * Stores a bitstream at the location specified by the passed PDURI.
	 * The bitstream is passed as a base64 encoded String parameter.<p/>
	 * This method was developed while testing different approaches to passing binary objects
	 * and was not part of the original API specification.<p/>
	 * Implemented as write once, will not overwrite an existing node.
	 * 
	 * @deprecated
	 * @param	pdURI
	 *		PDURI to store binary at
	 * @param	encodedFile
	 *		base64 encoded content string
	 * @throws	SOAPException
	 */
	@Deprecated
	void store(URI pdURI, String encodedFile) throws SOAPException;

	/**
	 * Returns the binary identified by the passed URI as a <code>byte[]</code>.
	 * 
	 * @param	pdURI
	 *          A PDURI identifying the binary to be retrieved
 	 * @return	A byte array containing the retrieved binary data
 	 * @throws	SOAPException
	 */
	byte[] retrieveBinary(URI pdURI) throws SOAPException;

	/**
	 * Performs a recursive search below the folder identified by the passed PDURI and returns a
	 * list of URIs identifying all files whose name contains the string passed.<p/>
	 * If passed the root data registry URI and the string "doc" this method would return
	 * an array of PDURIs, one for each file in the entire data registry with a name containing doc.
	 *
	 * @param	pdURI
	 *		A PDURI identifying the root location of the search
	 * @param	name
	 *		A search string used to filter the returned list of PDURIs.  Files will only be returned
	 *		if their name contains the value of the passed string.
	 * @return	A <code>URI[]</code> containing PDURIs for every file matching the search criteria
	 * @throws SOAPException
	 */
	public URI[] findFilesWithNameContaining(URI pdURI, String name) throws SOAPException;

	/**
	 * Performs a recursive search below the folder identified by the passed PDURI and returns a
	 * list of URIs identifying all files with an extension matching the string passed.<p/>
	 * If passed the root data registry URI and the string "doc" this method would return
	 * an array of PDURIs, one for each file in the entire data registry with a name ending <code>doc</code>.
	 *
	 * @param	pdURI
	 *			A PDURI identifying the root location of the search
	 * @param	ext
	 *		A search string used to filter the returned list of PDURIs.  Files will only be returned
	 *		if their name ends with the value of the passed string.
	 * @return	A <code>URI[]</code> containing PDURIs for every file matching the search criteria
	 * @throws SOAPException
	 */
	public URI[] findFilesWithExtension(URI pdURI, String ext) throws SOAPException;

}
