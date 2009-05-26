package eu.planets_project.ifr.core.storage.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.utils.FileUtils;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:klaus.rechert@rz.uni-freiburg.de">Klaus Rechert</a>
 *
 */
public class DigitalObjectDiskCache {

	public static Log log = LogFactory.getLog(DigitalObjectDiskCache.class);
	private static File cachedir = new File(System.getProperty("java.io.tmpdir"), "planets-tmp-dob-cache/");

	/**
	* @param digitalObjects
	* @return
	*/
	public static String cacheDigitalObject(DigitalObject dob) 
	{       
		String sessionId = UUID.randomUUID().toString();
		if(!cachedir.exists()) {
			if(!cachedir.mkdirs())
			{
				log.error("failed to create caching dir: " + cachedir);
				return null;
			}
		}

		log.info("write " + dob.getTitle());
		FileUtils.writeInputStreamToFile(dob.getContent().read(), cachedir, sessionId);
		return sessionId;
	}

	/**
	* @param sessionId
	* @return
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
		
		DigitalObject dob = null;
		try {
			DigitalObjectContent c = Content.byReference(f.toURL());
			dob = new DigitalObject.Builder(c).build();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return dob;
	}

    /**
     * @param digitalObjects
     * @return
     */
    public static String cacheDigitalObjects(List<DigitalObject> digitalObjects) {
        String sessionId = UUID.randomUUID().toString();
        // URGENT Make the multi-entity storage.
        return null;
    }

    /**
     * @param sessionIdentifier
     * @return
     */
    public static File recoverDigitalObjects(String sessionId) {
        // URGENT Make the multi-entity recovery.
        return null;
    }
    
}

