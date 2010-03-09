package eu.planets_project.ifr.core.storage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * Local File Disk-Based Digital Object Cache.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:klaus.rechert@rz.uni-freiburg.de">Klaus Rechert</a>
 * 
 */
public class DigitalObjectDiskCache {

    private static final Logger log = Logger.getLogger(DigitalObjectDiskCache.class.getName());
    private static File cachedir = new File(System
            .getProperty("java.io.tmpdir"), "planets-tmp-dob-cache/");

    /**
     * @param digitalObjects the digital objects
     * @return the session ID
     */
    public static String cacheDigitalObject(DigitalObject dob) {
        String sessionId = UUID.randomUUID().toString();
        if (!cachedir.exists()) {
            if (!cachedir.mkdirs()) {
                log.severe("failed to create caching dir: " + cachedir);
                return null;
            }
        }

        log.info("write " + dob.getTitle());
        
        DigitalObjectUtils.toFile(dob, new File(cachedir, sessionId));
        
        // Also store the XML:
        String xmlfile = dob.toXml();
        try {
            FileUtils.writeStringToFile(new File(cachedir, sessionId + ".xml"), xmlfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return sessionId;
    }

    /**
     * @param sessionId The session ID
     * @return The digital object, with a local file:// reference to the binary.
     */
    public static DigitalObject recoverDigitalObject(String sessionId) 
	{
		if(!cachedir.isDirectory())
		{
			log.severe("recovering failed: " + cachedir);
			log.severe("no such directory");
			return null;
		}

		File f = new File(cachedir, sessionId);
		if(!f.exists())
		{
			log.severe("no such file or directory: " + f);
			return null;
		}
        try {
            // Look for the file:
            URL binUrl = f.toURI().toURL();
            
            DigitalObjectContent c = Content.byReference(binUrl);
            
            // Build a bare DOB:
            DigitalObject.Builder dob = new DigitalObject.Builder(c);
            
            // Attempt to patch in metadata:
            File xmlf = new File( cachedir, sessionId+".xml");
            if( xmlf.exists() ) {
                dob = new DigitalObject.Builder( FileUtils.readFileToString(xmlf));
                // Add the ref to the binary:
                dob.content(c);
            }
            return dob.build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}

    /**
     * @param digitalObjects The digital objects
     * @return The session ID
     */
    public static String cacheDigitalObjects(List<DigitalObject> digitalObjects) {
        // URGENT Make the multi-entity storage.
        String sessionId = UUID.randomUUID().toString();
        // Store ids in list of properties
        Properties prop = new Properties();
        // Loop all into storage:
        for (DigitalObject dob : digitalObjects) {
            String cacheId = cacheDigitalObject(dob);
            prop.setProperty("storage." + cacheId, cacheId);
        }
        // Now store the properties in a file:
        File propfile = new File(cachedir, sessionId);
        try {
            propfile.createNewFile();
        } catch (IOException e1) {
            log.severe("Failed to create new file. " + e1);
        }
        try {
            prop.storeToXML(new FileOutputStream(propfile),
                    "Set of digital objects", "UTF-8");
        } catch (FileNotFoundException e) {
            log.severe("Could not store properties in file. " + e);
        } catch (IOException e) {
            log.severe("Could not store properties in file. " + e);
        }
        return sessionId;
    }

    /**
     * @param sessionID The session ID
     * @return The recovered digital object
     */
    public static List<DigitalObject> recoverDigitalObjects(String sessionId) {
        // Open the list file, stored as properties:
        File propfile = new File(cachedir, sessionId);
        Properties prop = new Properties();
        try {
            prop.loadFromXML(new FileInputStream(propfile));
        } catch (InvalidPropertiesFormatException e) {
            log.severe("Could not load properties from file. " + e);
            return null;
        } catch (FileNotFoundException e) {
            log.severe("Could not load properties from file. " + e);
            return null;
        } catch (IOException e) {
            log.severe("Could not load properties from file. " + e);
            return null;
        }

        // Loop over cached objects:
        List<DigitalObject> dobs = new ArrayList<DigitalObject>();
        for (Object cacheIdo : prop.values()) {
            DigitalObject dob = recoverDigitalObject((String) cacheIdo);
            if (dob != null) {
                dobs.add(dob);
            }

        }
        return dobs;
    }

}
