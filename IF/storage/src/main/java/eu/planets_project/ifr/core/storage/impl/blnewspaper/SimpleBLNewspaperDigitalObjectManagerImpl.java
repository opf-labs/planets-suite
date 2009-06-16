package eu.planets_project.ifr.core.storage.impl.blnewspaper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.Metadata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * The BLNewspaperDigitalObjectManagerImpl wraps a newspaper scan with 
 * the pageImage information (coordinates and skew, mainly) required for
 * further processing by Modify services. Modify services will blindly 
 * rely on the presence of this information in the DigitalObject's 
 * metadata field. Sort of an ugly hack...
 * 
 * @author SimonR
 *
 */
public class SimpleBLNewspaperDigitalObjectManagerImpl implements DigitalObjectManager {
	
    /**
     * Logger.
     */
    private static Log _log = LogFactory.getLog(SimpleBLNewspaperDigitalObjectManagerImpl.class);
    
    /**
     * Root URI on the file system
     */
    private URI root;
	
    /**
     * Create a digital object manager for BL newspaper scans stored on the file system.
     * @param root the directory storing image and XML files
     */
    public SimpleBLNewspaperDigitalObjectManagerImpl() {
        Properties properties = new Properties();
        
        try {
            String localDataDir;
            InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("BackendResources.properties");
            if (ResourceFile != null) {
	            properties.load(ResourceFile); 
	            
	            // See http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossProperties for more JBoss properties.
				if (properties.getProperty("JBoss.AltLocalDataDir") != null) { 
					localDataDir = properties.getProperty("JBoss.AltLocalDataDir");
				} else {
	                localDataDir = System.getProperty("jboss.home.dir") +
					System.getProperty("file.separator") +
					properties.getProperty("JBoss.LocalDataDir");
				}
	            
	            ResourceFile.close();
            } else {
                localDataDir = System.getProperty("jboss.home.dir") +
				System.getProperty("file.separator") +
				"bl-newspaper";            	
            }
            
            // Open the localDataDir
            File ldd = new File(localDataDir);
            // Create it if it does not exist:
            if( ! ldd.exists() ) {
               ldd.mkdir();
            } else {
                if( ldd.isFile() ) throw 
                    new IOException("The specified Data Registry already exists, but is a file, not a directory! : "+localDataDir);
            }
            // Attempt to convert to URI:
            root = ldd.toURI().normalize();
            _log.debug("(init) Got local data dir for bl newspaper: " + root);
            
        } catch (IOException e) {
            _log.fatal("Exception: Reading JBoss.LocalDataDir from BackendResources.properties failed!"+e.toString());
            root = null;
        }
    }
	
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
	}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable( URI pdURI ) {
    	return false;
    }
    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {    	
		ArrayList<URI> children = new ArrayList<URI>();

		if (pdURI == null) {
			// 'null' - return mirror base URL
    		children.add(root);
			return children;
		} else if (pdURI.equals(root)) {
			// Directory contents
			File searchRoot = new File(root);
			_log.info("Looking at: " + searchRoot.toString());	
			
			if (searchRoot.exists() && searchRoot.isDirectory()) {
				// Filter to avoid XML metadata files
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File f, String name) {
						return !name.endsWith(".xml");
					}
				};
				
				String[] contents = searchRoot.list(filter);
				for (String s : contents) {
					File sf = new File(searchRoot, s);
				    if(!sf.isDirectory()) {
				    	try {
				    		children.add(new URI(root + s));
				    	} catch (URISyntaxException e) {
				    		throw new RuntimeException(e);
				    	}
				    }
				}				
			}
			return children;
		}

		// Leaf node
		return null;
    }

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		// Get file reference
		_log.info("retrieving: " + pdURI.toString());
		File file = new File(pdURI);
		if (!file.exists())
			throw new DigitalObjectNotFoundException(file.getName() + " not found!");
		
		// Create DigitalObject from file reference
        DigitalObject.Builder dob = new DigitalObject.Builder(Content.byReference(file));
        dob.title(file.getName());
        
        // Open XML file
        String xmlFilename = file.toString().substring(0, file.toString().lastIndexOf('.')) + ".xml";
        File xml = new File(xmlFilename);
        if (!file.exists())
        	throw new DigitalObjectNotFoundException(xml.getName() + " not found!");
     
        // Extract relevant XML code and fill metadata
		try {
			String metadata = extractPageImageSection(xml);
			if (metadata != null)
				dob.metadata(new Metadata(new URI("application/xml"), metadata));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e); // Should never happen
		} catch (IOException e) {
			throw new DigitalObjectNotFoundException(e);
		}
        return dob.build();
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
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        throw new QueryValidationException("This implementation does not support queries.");
    }
    
    public URI getRootURI() {
    	return root;
    }
    
	private String extractPageImageSection(File file) throws IOException {	
		// Read file to string
        StringBuffer fileContents = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String aLine; 
        while((aLine = reader.readLine()) != null){
        	fileContents.append(aLine);
        }
        reader.close();
		
		// Extract using RegEx pattern 
		Pattern pageImagePattern = Pattern.compile("<pageImage>((.|\n)*?)</pageImage>");
		Matcher m = pageImagePattern.matcher(fileContents.toString());
		
		// Record first occurence...
		if (m.find())
			return m.group(1);

		// ...or null
		return null;
	}
	
	/*
	public static void main(String[] args) {
		UglyBLNewspaperDigitalObjectManagerImpl blnImpl = new UglyBLNewspaperDigitalObjectManagerImpl("c:\\bl\\");

		// List
		System.out.println("starting.");
		List<URI> identifiers = blnImpl.list(null);
		System.out.println(identifiers.size() + " found.");
			
		// Retrieve
		for (URI id : identifiers) {
			try {
				DigitalObject dob = blnImpl.retrieve(id);
				for (Metadata meta : dob.getMetadata()) {
					System.out.println("Metadata: " + meta.getContent());
				}
			} catch (Exception e) {
				System.out.println(e.getClass() + ": " + e.getMessage() );
			}
		}
		
		System.out.println("done.");
	}
	*/

}
