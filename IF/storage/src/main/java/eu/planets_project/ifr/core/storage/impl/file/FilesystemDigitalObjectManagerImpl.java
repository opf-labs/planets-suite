/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.file;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

import eu.planets_project.ifr.core.storage.api.PDURI;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Implementation of the DigitalObjectManager interface based upon a file system.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class FilesystemDigitalObjectManagerImpl implements DigitalObjectManager {
	/** The logger instance */
    private static Logger log = Logger.getLogger(FilesystemDigitalObjectManagerImpl.class.getName());
    
    /** The extension used for storing digital object metadata */
    protected final static String DO_EXTENSION = ".planets.do";

    /** The name of this data registry instance */
    protected String _name = null;
	/** This is the root directory of this particular Data Registry */
	protected File _root = null;

	/**
	 * @param name
	 * 		The name of the data registry
	 * @param root
	 * 		A directory that is the root for this data registry
	 * @return
	 * 		A new FilesystemDigitalObjectManagerImpl instance based upon a root directory
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(String name, File root) throws IllegalArgumentException {
		return new FilesystemDigitalObjectManagerImpl(name, root);
	}

	/**
	 * A convenience instantiator that create the File object for the user.
	 * @param name
	 * 		The name of the data registry
	 * @param rootPath
	 * 		The string path to a directory that is the root for this data registry
	 * @return
	 * 		A new FilesystemDigitalObjectManagerImpl instance based upon a root directory
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(String name, String rootPath) throws IllegalArgumentException {
		return new FilesystemDigitalObjectManagerImpl(name, new File(rootPath));
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	public List<URI> list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		ArrayList<URI> retVal = null;

		// First lets look at the passed URI, if it's null then we need to return the root 
		log.fine("Testing for null URI");
		if (pdURI == null)
		{
			log.fine("URI is empty so return root URI only");
			retVal = new ArrayList<URI>();
			try {
				retVal.add( PDURI.formDataRegistryRootURI("localhost", "8080", this._name+"/") );
			} catch (URISyntaxException e) {
				log.severe("URI Syntax exception");
				log.severe(e.getMessage());
				log.severe(e.getStackTrace().toString());
				return null;
			} 
			return retVal; 
		}
		log.fine("URI is NOT NULL");
		PDURI realPdURI;
		String fullPath = null;
		try {
			realPdURI = new PDURI(pdURI);
			fullPath = this._root.getCanonicalPath() + File.separator + realPdURI.getDataRegistryPath();
	        File searchRoot = new File(fullPath);
	        log.fine("Looking at: " + pdURI + " -> " + searchRoot.getCanonicalPath() );    
			retVal = this.listFileLocation( pdURI, searchRoot );
			
		} catch (URISyntaxException e) {
			log.severe("URI Syntax exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			log.severe("Unsupported encoding exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			return null;
		} catch (IOException e) {
			log.severe("IO exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			return null;
		}

		return retVal;
	}
	
	/**
	 * This code take the actual file location and turns it into a listing.
	 * 
	 * @param pdURI
	 * @param fullPath
	 * @return
	 * @throws URISyntaxException
	 */
	protected ArrayList<URI> listFileLocation(URI pdURI, File searchRoot ) throws URISyntaxException {
        if (searchRoot.exists() && searchRoot.isDirectory()) {
            ArrayList<URI> retVal = new ArrayList<URI>();
            // Create a filter to avoid do metadata files
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return !name.endsWith(FilesystemDigitalObjectManagerImpl.DO_EXTENSION);
                }
            };
            String[] contents = searchRoot.list(filter);
            for (String s : contents) {
                File sf = new File( searchRoot, s );
                // Create the new URI, using the multiple-argument constructors to ensure characters are properly escaped.
                if( sf.isDirectory() ) {
                    retVal.add( createNewPathUri( pdURI, pdURI.getPath() + s + "/" ) );
                } else {
                    retVal.add( createNewPathUri( pdURI, pdURI.getPath() + s ) );
                }
            }
            return retVal;
        }
        // If does not exist, or is a file.
        return null;
	}

	/**
	 * This is just a helper to create a new URI reliably, using an existing one as a template.
	 * 
	 * @param oldPathUri
	 * @param newPath
	 * @return
	 * @throws URISyntaxException
	 */
    protected URI createNewPathUri(URI oldPathUri, String newPath)
            throws URISyntaxException {
        return new URI(oldPathUri.getScheme(), oldPathUri.getUserInfo(),
                oldPathUri.getHost(), oldPathUri.getPort(), newPath, null, null);
    }
        
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		DigitalObject retObj = null;
        DigitalObject.Builder dob;

		try {
			PDURI parsedURI = new PDURI(pdURI);
			parsedURI.replaceDecodedPath(parsedURI.getDataRegistryPath() + FilesystemDigitalObjectManagerImpl.DO_EXTENSION);
			String fullPath = this._root.getCanonicalPath() + File.separator + parsedURI.getDataRegistryPath();
			StringBuilder fileData = new StringBuilder(1024);
			File xmlf = new File(fullPath);
			if( xmlf.exists() ) {
			    BufferedReader reader = new BufferedReader(new FileReader(xmlf));
			    char[] buf = new char[1024];
			    int numRead = 0;
			    while((numRead=reader.read(buf)) != -1) {
			        fileData.append(buf, 0, numRead);
			    }
			    reader.close();
			    // Re-create the digital object metadata.
                dob = new DigitalObject.Builder(fileData.toString());
                // And add the content:
                DigitalObjectContent c = Content.byReference(dob.getContent().read());
                dob.content(c);
                // If there is no title, add title to the dob.
                // TODO Not sane behaviour - if an object has been stored with no title, then that should remain the case.  The 'filename' is already in the URI, where it belongs.
                if( dob.getTitle() == null ) {
                    String title = null;
                    title = fullPath;
                    if(title.contains(".") && title.contains("/")) 
                    {
                        title = title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("."));
                    }
                    dob.title(title);
                    log.fine("Add title: " + title);
                }
			} else {
			    // Files without an associated metadata file are patched in like this:
			    File binFile = new File( this._root.getCanonicalPath() + File.separator + ( new PDURI(pdURI).getDataRegistryPath() ) );
			    if( ! binFile.exists() ) {
			        throw new DigitalObjectNotFoundException("The DigitalObject was not found!");
			    }
	            DigitalObjectContent c = Content.byReference( binFile );
	            dob = new DigitalObject.Builder( c );
	            dob.title( binFile.getName() );
			}
			// Also turn the content reference into a real file stream:
			retObj = dob.build();
		} catch (UnsupportedEncodingException e) {
			log.severe("Unsupported encoding exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to unupported encoding error", e);
		} catch (URISyntaxException e) {
			log.severe("URI Syntax exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			e.printStackTrace();
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to URI Syntax error", e);
		} catch (FileNotFoundException e) {
			log.severe("File Not Found exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotFoundException("The DigitalObject was not found", e);
		} catch (IOException e) {
			log.severe("IO exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to IO problem", e);
		}
		
		return retObj;
	}

	/**
	 * @param pdURI the URI
	 * @param digitalObject the object
	 * @throws DigitalObjectNotStoredException
	 */
	private void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		try {
		       if( digitalObject.getTitle() == null ) {
		          throw new DigitalObjectNotStoredException(
		        		"The DigitalObject titel was not found!");
		       }

			// get the path from the URI to store at
			PDURI _parsedURI = new PDURI(pdURI);
			String path = _parsedURI.getDataRegistryPath();

			// We need to append the path to the root dir of this registry for the data
			File doBinary = new File(this._root.getCanonicalPath() + 
					File.separator + path);
            File doDir = doBinary.getParentFile();
            if( ! doDir.exists() ) doDir.mkdirs();
			File doMetadata = new File(this._root.getCanonicalPath() + 
					File.separator + path + FilesystemDigitalObjectManagerImpl.DO_EXTENSION);
			
            log.fine("Storing in binary in "+doBinary.getAbsolutePath());
            log.fine("And storing in metadata in "+doMetadata.getAbsolutePath());
			
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
			URI purl = doBinary.toURI();
	        /* Create the content: */
	        DigitalObjectContent c1 = Content.byReference(purl.toURL());
	        /* Given these, we can instantiate our object: */
	        DigitalObject object = new DigitalObject.Builder(digitalObject).permanentUri(purl).content(c1).build();
			OutputStream outStream = new FileOutputStream(doMetadata);
			outStream.write(object.toXml().getBytes());
			outStream.close();
		} catch (UnsupportedEncodingException e) {
			log.severe("Unsupported encodeing exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to unupported encoding error", e);
		} catch (URISyntaxException e) {
			log.severe("URI Syntax exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to URI Syntax error", e);
		} catch (IOException e) {
			log.severe("IO exception");
			log.severe(e.getMessage());
			log.severe(e.getStackTrace().toString());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object due to IO problem", e);
		}
	}
	
	
	
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
     */
    public List<Class<? extends Query>> getQueryTypes() {
        return null;
    }

    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew()
     */
    public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
        URI pdURI;
        try {
            pdURI = PDURI.formDataRegistryRootURI("localhost", "8080", this._name+"/").resolve(UUID.randomUUID().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new DigitalObjectNotStoredException("Failed to autogenerate a suitable URI for this item.");
        }
        this.store(pdURI, digitalObject);
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        this.store(pdURI, digitalObject);
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#updateExisting()
     */
    public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
        // FIXME This should only work if the location exists already.
        this.store(pdURI, digitalObject);
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI pdURI) {
        return true;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        return null;
    }

    //=============================================================================================
	// PRIVATE METHODS
	//=============================================================================================
	/**
	 * We don't want the no arg constructor used as every file based registry should have a
	 * root directory
	 */
	@SuppressWarnings("unused")
    private FilesystemDigitalObjectManagerImpl() {
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
	protected FilesystemDigitalObjectManagerImpl(String name, File root) throws IllegalArgumentException {
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
			log.severe(message);
			throw new IllegalArgumentException(message);
		// first make sure it exists and is a directory
		} else if (root.exists()) {
			// OK root exists but it MUST be a directory
			if (!root.isDirectory()) {
				String message = root.getPath() + " is not a directory";
				log.severe(message);
				throw new IllegalArgumentException(message);
			}
		// It doesn't exist so lets create the directory
		} else {
			String message = "Directory " + root.getPath() + " doesn't exist";
			log.severe(message);
			throw new IllegalArgumentException(message);
		}

		// Name should not be null or empty
		if (name == null) {
			String message = "The supplied name should not be null";
			log.severe(message);
			throw new IllegalArgumentException(message);
		} else if (name.length() < 1) {
			String message = "The supplied name should not be empty";
			log.severe(message);
			throw new IllegalArgumentException(message);
		}
	}
}
