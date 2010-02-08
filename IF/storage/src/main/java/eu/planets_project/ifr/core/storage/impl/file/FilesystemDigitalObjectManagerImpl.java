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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;

/**
 * Implementation of the DigitalObjectManager interface based upon a file system.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class FilesystemDigitalObjectManagerImpl extends DigitalObjectManagerBase {
	/** The logger instance */
    private static Logger log = Logger.getLogger(FilesystemDigitalObjectManagerImpl.class.getName());
    
    /** The extension used for storing digital object metadata */
    protected final static String DO_EXTENSION = ".planets.do";

	/** This is the root directory of this particular Data Registry */
	protected File _root = null;
	
	/** Public statics for the property names used to configure an instance */
	public final static String PATH_KEY = "manager.path";

    //=============================================================================================
	// CONSTRUCTORS
	//=============================================================================================
    /**
     * No Arg Constructor, we don't want people to implement this 
     */
    protected FilesystemDigitalObjectManagerImpl() {
    	super(null);
	}

    /**
     * {@inheritDoc}
     * @param config 
     */
    public FilesystemDigitalObjectManagerImpl(Configuration config) {
    	super(config);
    	try {
        	String path = config.getString(PATH_KEY);
        	this.checkConstructorArguments(new File(path));
        	this._root = new File(path);
    	} catch (NoSuchElementException e) {
    		throw new IllegalArgumentException("Path property with key " + PATH_KEY + " not found in config");
    	}
    }

	//=============================================================================================
	// BASE CLASS METHODS
	//=============================================================================================
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
	 */
	@Override
	public List<URI> list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		ArrayList<URI> retVal = null;

		// First lets look at the passed URI, if it's null then we need to return the root 
		log.fine("Testing for null URI");
		if (pdURI == null)
		{
			log.fine("URI is empty so return root URI only");
			retVal = new ArrayList<URI>();
			retVal.add( this.id ); 
			return retVal; 
		}
		log.fine("URI is NOT NULL");
		PDURI realPdURI;
		String fullPath = null;
		try {
			realPdURI = new PDURI(pdURI);
			fullPath = this._root.getCanonicalPath() + File.separator + realPdURI.getDataRegistryPath();
			log.info("Full path is " + fullPath);
	        File searchRoot = new File(fullPath);
	        log.info("Looking at: " + pdURI + " -> " + searchRoot.getCanonicalPath() );    
			retVal = this.listFileLocation( pdURI, searchRoot );
			
			if (retVal != null) {
				log.info("Listing URIs");
				for (URI uri : retVal) {
					log.info("URI in list: " + uri);
				}
			} else {
				log.info("RetVal is NULL");
			}
			
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
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#retrieve(java.net.URI)
	 */
	@Override
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		DigitalObject retObj = null;
        DigitalObject.Builder dob;

        // Look for the XML:
		try {
	        // All files should have a binary:
	        // Files without an associated metadata file are patched in like this:
	        File binFile = new File( this._root.getCanonicalPath() + File.separator + ( new PDURI(pdURI).getDataRegistryPath() ) );
	        if( ! binFile.exists() ) {
	            throw new DigitalObjectNotFoundException("The DigitalObject was not found!");
	        }
	        DigitalObjectContent c = Content.byReference( binFile );
            
            // Look for the XML:
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
                // Attach the content:
                dob.content(c);
			} else {
                dob = new DigitalObject.Builder(c);
            }
            // If there is no title, add title to the dob.
            // TODO Not the originally intended behaviour - if an object has been stored with no title, then that should remain the case.  The 'filename' is already in the URI, where it belongs.
            if( dob.getTitle() == null ) {
                String title = null;
                title = new PDURI(pdURI).getLeafname();
                dob.title(title);
                log.info("Add title: " + title);
            }
			// Ensure the PDURI is fixed up:
			dob.permanentUri(pdURI);
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
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getQueryTypes()
     */
    @Override
	public List<Class<? extends Query>> getQueryTypes() {
        return null;
    }

    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerbASE#storeAsNew()
     */
    @Override
	public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
		log.fine("FileSysDOM storeAsNew");
        URI pdURI;
        log.fine("Putting URI together");
        log.fine("ID = " + this.id);
		pdURI = this.id.resolve(this.name + "/" + UUID.randomUUID().toString());
		log.fine("Calling store " + pdURI);
        this.store(pdURI, digitalObject);
		log.fine("returning URI");
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    @Override
	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        this.store(pdURI, digitalObject);
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#updateExisting()
     */
    @Override
	public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
    	// First lets call retrieve to see if this exists
    	this.retrieve(pdURI);
        this.store(pdURI, digitalObject);
        return pdURI;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#isWritable(java.net.URI)
     */
    @Override
	public boolean isWritable(URI pdURI) {
        return true;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    @Override
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        return null;
    }

    //=============================================================================================
	// FACTORY METHODS
	//=============================================================================================
	/**
	 * @param config
	 * 		A config object with the DOM details 
	 * @return
	 * 		A new FilesystemDigitalObjectManagerImpl instance based upon a root directory
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(Configuration config) throws IllegalArgumentException {
		return new FilesystemDigitalObjectManagerImpl(config);
	}

    //=============================================================================================
	// PRIVATE & PROTECTED METHODS
	//=============================================================================================
	private void checkConstructorArguments(File root) throws IllegalArgumentException {
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
			log.info(message);
			root.mkdirs();
		}
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
        	log.info("OK " + searchRoot + " exists");
            ArrayList<URI> retVal = new ArrayList<URI>();
            // Create a filter to avoid do metadata files
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return !name.endsWith(FilesystemDigitalObjectManagerImpl.DO_EXTENSION);
                }
            };
            String[] contents = searchRoot.list(filter);
            log.info("Contents String[] has " + contents.length + " elements");
            for (String s : contents) {
                File sf = new File( searchRoot, s );
                // Create the new URI, using the multiple-argument constructors to ensure characters are properly escaped.
                if( sf.isDirectory() ) {
                	log.fine(sf + " is a directory");
                	URI uri = createNewPathUri( pdURI, pdURI.getPath() + "/" + s + "/" );
                	log.fine("Adding URI " + uri + " to list");
                    retVal.add( uri );
                } else {
                	log.fine(sf + " is a file");
                	URI uri = createNewPathUri( pdURI, pdURI.getPath() +"/"+ s ).normalize();            
                	log.fine("Adding URI " + uri + " to list");
                    retVal.add( uri );
                }
            }
            return retVal;
        }
        log.info("Either " + searchRoot + " doesn't exist OR it is file");
        // If does not exist, or is a file.
        return null;
	}

	/**
	 * @param pdURI the URI
	 * @param digitalObject the object
	 * @throws DigitalObjectNotStoredException
	 */
	private void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		try {
			log.fine("testing title");
		       if( digitalObject.getTitle() == null ) {
		          throw new DigitalObjectNotStoredException(
		        		"The DigitalObject titel was not found!");
		       }

			// get the path from the URI to store at
			log.fine("Getting new PDURI from " + pdURI);
			PDURI _parsedURI = new PDURI(pdURI);
			log.fine("getting dr path");
			String path = _parsedURI.getDataRegistryPath();
			log.fine("path is " + path);

			// We need to append the path to the root dir of this registry for the data
			log.fine("New binary file");
			File doBinary = new File(this._root.getCanonicalPath() + 
					File.separator + path);
			log.fine("getting dir dir");
            File doDir = doBinary.getParentFile();
    		log.fine("mking dir");
            if( ! doDir.exists() ) doDir.mkdirs();
    		log.info("creating metadata");
			File doMetadata = new File(this._root.getCanonicalPath() + 
					File.separator + path + FilesystemDigitalObjectManagerImpl.DO_EXTENSION);
			
            log.info("Storing in binary in "+doBinary.getAbsolutePath());
            log.info("And storing in metadata in "+doMetadata.getAbsolutePath());
			
			// Persist the object to a file
			InputStream inStream = digitalObject.getContent().getInputStream();
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
	        DigitalObject object = new DigitalObject.Builder(digitalObject).permanentUri(pdURI).content(c1).build();
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
	 * This is just a helper to create a new URI reliably, using an existing one as a template.
	 * 
	 * @param oldPathUri
	 * @param newPath
	 * @return
	 * @throws URISyntaxException
	 */
    static protected URI createNewPathUri(URI oldPathUri, String newPath)
            throws URISyntaxException {
        return new URI(oldPathUri.getScheme(), oldPathUri.getUserInfo(),
                oldPathUri.getHost(), oldPathUri.getPort(), newPath, null, null);
    }
}
