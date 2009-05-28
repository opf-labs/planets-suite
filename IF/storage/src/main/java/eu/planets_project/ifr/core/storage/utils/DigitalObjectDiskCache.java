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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.utils.FileUtils;

/**
 * Local File Disk-Based Digital Object Cache.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:klaus.rechert@rz.uni-freiburg.de">Klaus Rechert</a>
 * 
 */
public class DigitalObjectDiskCache {

    public static Log log = LogFactory.getLog(DigitalObjectDiskCache.class);
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
                log.error("failed to create caching dir: " + cachedir);
                return null;
            }
        }

        log.info("write " + dob.getTitle());
        FileUtils.writeInputStreamToFile(dob.getContent().read(), cachedir, sessionId);
        
        // Also store the XML:
        String xmlfile = dob.toXml();
        FileUtils.writeStringToFile(xmlfile, new File(cachedir, sessionId + ".xml"));
        
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
			log.error("recovering failed: " + cachedir);
			log.error("no such directory");
			return null;
		}

		File f = new File(cachedir, sessionId);
		if(!f.exists())
		{
			log.error("no such file or directory: " + f);
			return null;
		}
		// Look for the file:
		URL binUrl = null;
        try {
            binUrl = f.toURL();
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
        
        // Build a bare DOB:
        DigitalObject.Builder dob = null;
        DigitalObjectContent c = Content.byReference(binUrl);
        dob = new DigitalObject.Builder(c);
		
        // Attempt to patch in metadata:
        File xmlf = new File( cachedir, sessionId+".xml");
        if( xmlf.exists() ) {
            dob = new DigitalObject.Builder( FileUtils.readTxtFileIntoString(xmlf));
            // Add the ref to the binary:
            dob.content(c);
        }
		
		return dob.build();
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
            log.error("Failed to create new file. " + e1);
        }
        try {
            prop.storeToXML(new FileOutputStream(propfile),
                    "Set of digital objects", "UTF-8");
        } catch (FileNotFoundException e) {
            log.error("Could not store properties in file. " + e);
        } catch (IOException e) {
            log.error("Could not store properties in file. " + e);
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
            log.error("Could not load properties from file. " + e);
            return null;
        } catch (FileNotFoundException e) {
            log.error("Could not load properties from file. " + e);
            return null;
        } catch (IOException e) {
            log.error("Could not load properties from file. " + e);
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
