/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.utils.PDURI;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Implementation of the DigitalObjectManager interface based upon a file system
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class DigitalObjectManagerImpl implements DigitalObjectManager {
	/** The logger instance */
    private static Log _log = LogFactory.getLog(DigitalObjectManagerImpl.class);
    
    /** The extension used for storing digital object metadata */
    private final static String DO_EXTENSION = ".planets.do";

    /** The name of this data registry instance */
    private String _name = null;
	/** This is the root directory of this particular Data Registry */
	private File _root = null;

	/**
	 * @param name
	 * 		The name of the data registry
	 * @param root
	 * 		A directory that is the root for this data registry
	 * @return
	 * 		A new DigitalObjectManagerImpl instance based upon a root directory
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(String name, File root) throws IllegalArgumentException {
		return new DigitalObjectManagerImpl(name, root);
	}

	/**
	 * A convenience instantiator that create the File object for the user
	 * @param name
	 * 		The name of the data registry
	 * @param rootPath
	 * 		The string path to a directory that is the root for this data registry
	 * @return
	 * 		A new DigitalObjectManagerImpl instance based upon a root directory
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(String name, String rootPath) throws IllegalArgumentException {
		return new DigitalObjectManagerImpl(name, new File(rootPath));
	}

	/**
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	public URI[] list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		URI[] retVal = null;

		// First lets look at the passed URI, if it's null then we need to return the root 
		DigitalObjectManagerImpl._log.debug("Testing for null URI");
		if (pdURI == null)
		{
			DigitalObjectManagerImpl._log.debug("URI is empty so return root URI only");
			retVal = new URI[1];
			try {
				retVal[0] = PDURI.formDataRegistryRootURI("localhost", "8080", this._name);
			} catch (URISyntaxException e) {
				DigitalObjectManagerImpl._log.error("URI Syntax exception");
				DigitalObjectManagerImpl._log.error(e.getMessage());
				DigitalObjectManagerImpl._log.error(e.getStackTrace());
				return null;
			} 
			return retVal; 
		}
		DigitalObjectManagerImpl._log.debug("URI is NOT NULL");
		PDURI realPdURI;
		String fullPath = null;
		try {
			realPdURI = new PDURI(pdURI);
			fullPath = this._root.getCanonicalPath() + File.separator + realPdURI.getDataRegistryPath();
			File searchRoot = new File(fullPath);
			if (searchRoot.exists() && searchRoot.isDirectory()) {
				// Create a filter to avoid do metadata files
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File f, String name) {
						return !name.endsWith(DigitalObjectManagerImpl.DO_EXTENSION);
					}
				};
				String[] contents = searchRoot.list(filter);
				ArrayList<URI> provContents = new ArrayList<URI>();
				for (String s : contents) {
					realPdURI.replaceDecodedPath(s);
					provContents.add(realPdURI.getURI());
				}
				retVal = new URI[provContents.size()];
				provContents.toArray(retVal);
			}
		} catch (URISyntaxException e) {
			DigitalObjectManagerImpl._log.error("URI Syntax exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			return null;
		} catch (UnsupportedEncodingException e) {
			DigitalObjectManagerImpl._log.error("Unsupported encoding exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			return null;
		} catch (IOException e) {
			DigitalObjectManagerImpl._log.error("IO exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			return null;
		}

		return retVal;
	}

	/**
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		DigitalObject retObj = null;
	
		try {
			PDURI parsedURI = new PDURI(pdURI);
			parsedURI.replaceDecodedPath(parsedURI.getDataRegistryPath() + DigitalObjectManagerImpl.DO_EXTENSION);
			String fullPath = this._root.getCanonicalPath() + File.separator + parsedURI.getDataRegistryPath();
			StringBuilder fileData = new StringBuilder(1024);
			BufferedReader reader = new BufferedReader(new FileReader(fullPath));
			char[] buf = new char[1024];
			int numRead = 0;
			while((numRead=reader.read(buf)) != -1) {
				fileData.append(buf, 0, numRead);
			}
			reader.close();
			retObj = new DigitalObject.Builder(fileData.toString()).build();
		} catch (UnsupportedEncodingException e) {
			DigitalObjectManagerImpl._log.error("Unsupported encoding exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to unupported encoding error", e);
		} catch (URISyntaxException e) {
			DigitalObjectManagerImpl._log.error("URI Syntax exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to URI Syntax error", e);
		} catch (FileNotFoundException e) {
			DigitalObjectManagerImpl._log.error("File Not Found exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("The DigitalObject was not found", e);
		} catch (IOException e) {
			DigitalObjectManagerImpl._log.error("IO exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to IO problem", e);
		}
		
		return retObj;
	}

	/**
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		try {
			// get the path from the URI to store at
			PDURI _parsedURI = new PDURI(pdURI);
			String path = _parsedURI.getDataRegistryPath();

			// We need to append the path to the root dir of this registry for the data
			File doBinary = new File(this._root.getCanonicalPath() + 
					File.separator + path);
			File doMetadata = new File(this._root.getCanonicalPath() + 
					File.separator + path + DigitalObjectManagerImpl.DO_EXTENSION);
			
			// Persist the object to a file
			InputStream inStream = digitalObject.getContent().read();
			OutputStream binStream = new FileOutputStream(doBinary);
			byte[] buf = new byte[1024];
			int len;
			while ((len = inStream.read(buf)) > 0) {
				binStream.write(buf, 0, len);
			}
			inStream.close();
			binStream.close();
			
			// Now write the Digital Object metadata to the file
			// First we need to update the purl and the content object reference
			URL purl = doBinary.toURI().toURL();
	        /* Create the content: */
	        Content c1 = Content.byReference(purl);
	        /* Given these, we can instantiate our object: */
	        DigitalObject object = new DigitalObject.Builder(digitalObject).permanentUrl(purl).content(c1).build();
			OutputStream outStream = new FileOutputStream(doMetadata);
			outStream.write(object.toXml().getBytes());
			outStream.close();
		} catch (UnsupportedEncodingException e) {
			DigitalObjectManagerImpl._log.error("Unsupported encodeing exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to unupported encoding error", e);
		} catch (URISyntaxException e) {
			DigitalObjectManagerImpl._log.error("URI Syntax exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to URI Syntax error", e);
		} catch (IOException e) {
			DigitalObjectManagerImpl._log.error("IO exception");
			DigitalObjectManagerImpl._log.error(e.getMessage());
			DigitalObjectManagerImpl._log.error(e.getStackTrace());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to IO problem", e);
		}
	}
	
	//=============================================================================================
	// PRIVATE METHODS
	//=============================================================================================
	/**
	 * We don't want the no arg constructor used as every file based registry should have a
	 * root directory
	 */
	private DigitalObjectManagerImpl() {
	}

	/**
	 * Constructor that instantiates the data registry on a root directory
	 * @param name
	 * 		A non null, none empty java.lang.String that provides a name for this data registry
	 * @param root
	 * 		A directory that is the root for this data registry, this should be a none null
	 * 		java.io.File that is initialised to point to an existing directory
	 * @throws IllegalArgumentException
	 * 		When one of the passed parameters doesn't satisfy the criteria documented above
	 */
	private DigitalObjectManagerImpl(String name, File root) throws IllegalArgumentException {
		// Check the passed arguments, this will throw the IllegalArgumentException if not OK
		this.checkConstructorArguments(name, root);
		
		// OK we have a root directory so assign it and assign the name
		this._root = root;
		this._name = name;
	}
	
	private void checkConstructorArguments(String name, File root) throws IllegalArgumentException {
		// Ensure root is not null
		if (root == null) {
			String message = "Supplied root dir is null";
			DigitalObjectManagerImpl._log.error(message);
			throw new IllegalArgumentException(message);
		// first make sure it exists and is a directory
		} else if (root.exists()) {
			// OK root exists but it MUST be a directory
			if (!root.isDirectory()) {
				String message = root.getPath() + " is not a directory";
				DigitalObjectManagerImpl._log.error(message);
				throw new IllegalArgumentException(message);
			}
		// It doesn't exist so lets create the directory
		} else {
			String message = "Directory " + root.getPath() + " doesn't exist";
			DigitalObjectManagerImpl._log.error(message);
			throw new IllegalArgumentException(message);
		}

		// Name should not be null or empty
		if (name == null) {
			String message = "The supplied name should not be null";
			DigitalObjectManagerImpl._log.error(message);
			throw new IllegalArgumentException(message);
		} else if (name.length() < 1) {
			String message = "The supplied name should not be empty";
			DigitalObjectManagerImpl._log.error(message);
			throw new IllegalArgumentException(message);
		}
	}
}
