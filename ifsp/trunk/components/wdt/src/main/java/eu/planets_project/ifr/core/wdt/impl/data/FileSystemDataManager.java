package eu.planets_project.ifr.core.wdt.impl.data;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.soap.SOAPException;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;



/**
 * @author AnJackson
 *
 */
public class FileSystemDataManager implements DigitalObjectManager {

    // A logger for this:
    private static Logger log = Logger.getLogger(FileSystemDataManager.class.getName());
    
    //These three properties are defined within the BackendResources.properties
    private URI localDataURI;
    
    // A filename filter for this DR: UNIX-style 'no up dir and no dot-prefix hidden files':
    private class LocalDirFilter implements FilenameFilter {
        public boolean accept( File file, String name) {
            if( "..".equals(name) ) return false;
            if( name.startsWith(".") ) return false;
            return true;
        }
    }
    
    public FileSystemDataManager(){
        readProperties();
    }
    
    private void readProperties(){
        Properties properties = new Properties();

        try {
            String localDataDir;
            java.io.InputStream ResourceFile = getClass().getClassLoader()
                    .getResourceAsStream("BackendResources.properties");
            if (ResourceFile!=null){
	            properties.load(ResourceFile); 
	            
	            // See http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossProperties for more JBoss properties.
				if (properties.getProperty("JBoss.AltLocalDataDir") != null) { 
					localDataDir = properties.getProperty("JBoss.AltLocalDataDir");
				}
				else {
	                localDataDir = System.getProperty("jboss.home.dir") +
					System.getProperty("file.separator") +
					properties.getProperty("JBoss.LocalDataDir");
				}
	            
	            ResourceFile.close();
            } else {
                localDataDir = System.getProperty("jboss.home.dir") +
				System.getProperty("file.separator") +
				"planets-ftp";            	
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
            localDataURI = ldd.toURI().normalize();
            log.fine("(init) Got local data dir: " + localDataURI);
            
        } catch (IOException e) {
            log.severe("Exception: Reading JBoss.LocalDataDir from BackendResources.properties failed!"+e.toString());
            localDataURI = null;
        }
        
    }


    /**
     * Checks and validates the URI:
     * 
     * @param puri
     * @return
     */
    private URI checkURI( URI puri ) {
        if( puri == null ) return this.localDataURI;
        puri = puri.normalize();
        URI relative = this.localDataURI.relativize(puri);
        // TODO Is there a better way to enforce that the URI does not go above the local data directory uri?
        if( relative.getScheme() != null ) puri = this.localDataURI;
        return puri;
    }
    
    /**
     * Utility class to look-up the root URI for this file store:
     * @return The local data store URI: file://etc.
     */
    public URI getRootURI() {
        return localDataURI;
    }
    
    /**
     * This is an implementation of the DataManager for a local directory.
     * In this case, the URI should start with 'file'.
     * 
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        // Set up the uri, coping with null etc
        pdURI = checkURI(pdURI);
        log.fine("Listing "+pdURI);
        ArrayList<URI> aldo = new ArrayList<URI>();
        File pf = new File(pdURI);
        if( pf.isFile() ) {
            // If it is a file, return NULL.
            return null;
        } else {
            // If it is a directory, return the list on contents:
            String[] flist = pf.list( new LocalDirFilter() );
            if( flist != null ) {
                for (String pcs : flist) {
                    File pcf = new File(pf.getAbsolutePath() 
                            + File.separator + pcs);
                    aldo.add(pcf.toURI());
                }
            }
        }
        return aldo;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#read(java.net.URI)
     */
    public String read(URI pdURI) throws SOAPException {
        // FIXME Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#createLocalSandbox()
     */
    public URI createLocalSandbox() throws URISyntaxException {
        // FIXME Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
 
    	DigitalObject retObj = null;
	
//		try {
    		log.info("retrieving: " + pdURI.toString());
    		URL dobURL = null;
    		String title = null;
    		try {
    			// Add title to the dob
          title = pdURI.toString();
					if(title.contains(".") && title.contains("/")) 
					{
							title = title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("."));
					}
			    log.info("Add title: " + title);
					
    			dobURL = pdURI.toURL();
    		} catch (MalformedURLException e) {
    			log.severe("\nSelected digital object has an invalid URL!");
    		}
    		// Create DigitalObject from file reference
  		  retObj = new DigitalObject.Builder(Content.byReference(dobURL))
                  							  .title(title)
                  							  .build();
                        
            /* something wrong here
            BufferedReader reader = new BufferedReader(new FileReader(f));
			StringBuilder fileData = new StringBuilder(1024);
			char[] buf = new char[1024];
			int numRead = 0;
			while((numRead=reader.read(buf)) != -1) {
				fileData.append(buf, 0, numRead);
			}
			reader.close();
			retObj = new DigitalObject.Builder(fileData.toString()).build();
			
            
		} catch (UnsupportedEncodingException e) {
			log.error("Unsupported encoding exception");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to unupported encoding error", e);
		} catch (FileNotFoundException e) {
			log.error("File Not Found exception");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("The DigitalObject was not found", e);
		} catch (IOException e) {
			log.error("IO exception");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object due to IO problem", e);
		}
		*/
		return retObj;    
    
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI, java.io.InputStream)
     * TODO This should perhaps use a bigger buffer.
     * TODO We should check whether these stream objects store the whole file in memory. Memory requirements too high?
     */
    public void store(URI pdURI, InputStream stream) throws LoginException,
            RepositoryException, URISyntaxException {

        // Prepare the output stream:
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream( new File( pdURI ));
        } catch ( FileNotFoundException e ) {
            throw new PathNotFoundException(pdURI.toString());
        }
        
        // Pass to the output, currently byte-by-byte:
        try {
            int b;
            while ((b = stream.read()) != -1) {
                fo.write(b);
            }
        } catch (IOException e) {
            throw new RepositoryException("Could not write to file " + pdURI);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI,
     *      java.lang.String)
     */
    public void store(URI pdURI, String encodedFile) throws SOAPException {
        // FIXME Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieveBinary(java.net.URI)
     */
    @Deprecated
    public byte[] retrieveBinary(URI pdURI) throws SOAPException {
        byte[] bin  = null;
        
        try {
            File f = new File( pdURI );
            FileInputStream fin = new FileInputStream(f);
            bin = new byte[(int)f.length()];
            fin.read(bin);
        } catch( IOException e ) {
            log.severe("Failed to list DR URI." + e);
            bin = null;
            throw new SOAPException(e);
        }
        
        return bin;
    }

	public List<Class<? extends Query>> getQueryTypes() {
		// TODO Auto-generated method stub
		return null;
	}

    public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URI updateExisting(URI uri, DigitalObject digitalObject) throws DigitalObjectNotStoredException, DigitalObjectNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWritable(URI pdURI) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		// TODO Auto-generated method stub
		
	}
    
}
