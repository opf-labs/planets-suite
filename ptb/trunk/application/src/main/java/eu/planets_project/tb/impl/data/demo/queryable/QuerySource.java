package eu.planets_project.tb.impl.data.demo.queryable;

import java.io.File;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
// import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerImpl;
import eu.planets_project.tb.gui.backing.QueryResultListEntry;
import eu.planets_project.tb.impl.data.FileSystemDataManager;

/**
 * Abstract base class for all query-able data sources
 * 
 * @author SimonR
 *
 */
public abstract class QuerySource {
	
	/**
	 * A logger
	 */
	private Log log = LogFactory.getLog(QuerySource.class);
	
	/**
	 * Display name of this data source
	 */
	private String sourceName;
	
	public QuerySource(String sourceName) {
		this.sourceName = sourceName;
	}
	
    public String getName() {
    	return sourceName;
    }
    
    public abstract QueryResultListEntry[] query(String query, int limit, int offset);
    
    public int ingest(QueryResultListEntry[] results) {
		// Store in FS-Implementation of the DigitalOjectManager
    	/*
		FileSystemDataManager fsdm = new FileSystemDataManager();
		DigitalObjectManager doManager = DigitalObjectManagerImpl.getInstance("default", new File(fsdm.getRootURI()));
		
		int successCtr = 0;
		for (int i=0; i<results.length; i++) {
			QueryResultListEntry r = results[i]; 
			try {
				doManager.store(new URI("planets://localhost:8080/dr/default/" + r.getName()), r.getDigitalObject());
				successCtr++;
			} catch (Exception anyException) {
				log.info(anyException.getClass().toString() + ": " + anyException.getMessage());
			}
		}
		*/
		return 1; // successCtr;
    }

}
