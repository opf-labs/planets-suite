package eu.planets_project.tb.impl.data.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.PathNotFoundException;

import javax.xml.soap.SOAPException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * A DataManagerLocal demo implementation that interfaces directly to a 
 * mirror site of the NASA Blue Marble Next Generation image collection.
 *  
 * @author <a href="mailto:rainer.simon@arcs.ac.at">Rainer Simon</a>
 *
 */
public class BlueMarbleDataManager implements DataManagerLocal {

    /**
     * Logger
     */
    private static PlanetsLogger log = PlanetsLogger.getLogger(BlueMarbleDataManager.class, "testbed-log4j.xml");
    
    /**
     * Mirror site base URL
     */   
    private static final String MIRROR_BASE_URL = "http://mirrors.arsc.edu/nasa/";
    
    /**
     * Mirror site base URI
     */
    private URI rootURI = null; 
    
    /**
     * Local buffer for the remote directory structure to increase responsiveness of the GUI 
     */
    private Hashtable<URI, URI[]> directoryCache = new Hashtable<URI, URI[]>();
    
    public BlueMarbleDataManager() {
    	try {
    		this.rootURI = new URI(MIRROR_BASE_URL);
    	} catch (URISyntaxException e) {
    		log.error("This should never happen: " + e.getMessage());
    	}
    }
    
    public URI[] list(URI pdURI) throws SOAPException {
    	if (pdURI == null)
   			// Return mirror base URL
			return new URI[] { rootURI };
    	
    	// Otherwise: Try buffer before downloading from remote mirror
    	URI[] bufferedList = directoryCache.get(pdURI);
    
    	if (bufferedList == null) {
    		// Proceed normally (and cache result if any)
    		if (pdURI.equals(rootURI)) {
       	    	try {
       				// Top level directory
       				HttpClient httpClient = new HttpClient();
       				GetMethod dirRequest = new GetMethod(MIRROR_BASE_URL);
       				httpClient.executeMethod(dirRequest);
       				
       				// Scrape directory names
       				URI[] topLvlDirs = scrapeTopLvlDirs(dirRequest.getResponseBodyAsString());
       				directoryCache.put(pdURI, topLvlDirs);
       				return topLvlDirs;
       	    	} catch (IOException e) {
       	    		log.warn("Error scraping top-level directory from Blue Marble mirror");
       	    		return null;
       	    	}
       		} else if (!(pdURI.toString().endsWith("png") || pdURI.toString().endsWith("jpg"))) {
       			try {
       	   			// Sub-directory
    				HttpClient httpClient = new HttpClient();
    				GetMethod dirRequest = new GetMethod(pdURI.toString());
    				
    				httpClient.executeMethod(dirRequest);
    				
    				// Scrape file names
    				URI[] subDirs = scrapeSubDir(dirRequest.getResponseBodyAsString(), pdURI.toString());
    				directoryCache.put(pdURI, subDirs);
    				return subDirs;
       			} catch (IOException e) {
       				log.warn("Error scraping subdirectory '" + pdURI + "' from Blue Marble mirror");
       				return null;
       			}
       		} else {
       			// Leaf node
       			return null;
       		}
    	} else {
    		return bufferedList;
    	}
   	}

    public String read(URI pdURI) throws SOAPException {
        return null;
    }

    public URI createLocalSandbox() throws URISyntaxException {
        // FIXME Auto-generated method stub
        return null;
    }

    public InputStream retrieve(URI pdURI) throws PathNotFoundException, URISyntaxException {
        try {
            File f = new File( pdURI );
            log.info("Got file: "+f.getAbsolutePath());
            log.info("Got something that exists? "+f.exists());
            FileInputStream fin = new FileInputStream( f );
            log.info("Got FileInputStream: "+fin);
            return fin;
        } catch ( FileNotFoundException e ) {
            throw new PathNotFoundException(pdURI.toString());
        }
    }
    
	public byte[] retrieveBinary(URI pdURI) throws SOAPException {
		return new byte[0];
	}

    public void store(URI pdURI, InputStream stream) {
    	log.info("Storing not supported for this DataManager");
    }


    public void store(URI pdURI, String encodedFile) throws SOAPException {
    	log.info("Storing not supported for this DataManager"); 
    }

    public void storeBinary(URI pdURI, byte[] binary) {
    	log.info("Storing not supported for this DataManager");
    }

    public URI[] findFilesWithExtension(URI pdURI, String ext)
            throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }

    public URI[] findFilesWithNameContaining(URI pdURI, String name)
            throws SOAPException {
        // TODO Auto-generated method stub
        return null;
    }

    public URI listDownladURI(URI pdURI) throws SOAPException {
        return pdURI;
    }
    
	private URI[] scrapeTopLvlDirs(String html) {
		Pattern worldDirPattern = Pattern.compile("<a href=\"world_((.|\n)*?)</a>");
		Matcher m = worldDirPattern.matcher(html);
		
		ArrayList<URI> dirs = new ArrayList<URI>();
		String match;
		while (m.find()) {
			m.find(); // Skip every odd match
			match = m.group(1);
			try {
				dirs.add(new URI(MIRROR_BASE_URL + match.substring(match.indexOf('>') + 1)));
			} catch (URISyntaxException e) {
				log.warn("Error parsing Blue Marble dir: " + match);
			}
		}
		return dirs.toArray(new URI[dirs.size()]);
	}
	
	private URI[] scrapeSubDir(String html, String baseURL) {
		Pattern worldDirPattern = Pattern.compile(" <a href=\"world((.|\n)*?)\">");
		Matcher m = worldDirPattern.matcher(html);
		ArrayList<URI> images = new ArrayList<URI>();

		String match;
		while (m.find()) {
			match = "world" + m.group(1);
			if (match.endsWith("jpg") || match.endsWith("png") ) {
				try {
					images.add(new URI(baseURL + match));
				} catch (URISyntaxException e) {
					log.warn("Error parsing Blue Marble image dir: " + match);
				}
			}
		}
		return images.toArray(new URI[images.size()]);
	}

    
}
