package eu.planets_project.tb.impl.data.demo.queryable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.dom4j.Element;

import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.OAIException;

import eu.planets_project.tb.gui.backing.QueryResultListEntry;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

/**
 * 
 * @author <a href="mailto:rainer.simon@arcs.ac.at">Rainer Simon</a>
 *
 */
public class GenericOAIQuerySource extends QuerySource {
	
	/**
	 * Logger
	 */
    private static PlanetsLogger log = PlanetsLogger.getLogger(GenericSRUQuerySource.class);
    
    /**
     * OAI-style date format
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
     * OAI endpoint base URL
     */
    private String baseURL;
    
    public GenericOAIQuerySource(String baseURL, String name) {
    	super(name);
    	this.baseURL = baseURL;
    }
    
    public QueryResultListEntry[] query(String query, int limit, int offset) {
		// Need to re-think the way QuerySources are modeled... 
    	return new QueryResultListEntry[0];   
    }
    
    public QueryResultListEntry[] query(Date from, Date until) {
    	ArrayList<QueryResultListEntry> resultList = new ArrayList<QueryResultListEntry>();
    	OaiPmhServer server = new OaiPmhServer(baseURL);
    	try {
    		RecordsList list = server.listRecords("oai_dc", dateFormat.format(from), dateFormat.format(until), null);
    		for (Record rec : list.asList()) {
    			Element metadata = rec.getMetadata();
    			String title   = null;
    			String url    = null;
    			String format = null;
    			
    			for (Object child : metadata.content()) {
    				if (child instanceof Element) {
    					Element c = (Element) child;
    					
    					// Title
    					if (c.getName().equalsIgnoreCase("title"))
    						title = c.getData().toString();
    					
    					// URL
    					if (c.getName().equalsIgnoreCase("identifier")) {
    						String id = c.getData().toString();
    						if (id.startsWith("http://"))
    							url = id;
    					}
    					
    					// Format
    					if (c.getName().equalsIgnoreCase("format"))
    						format = c.getData().toString();
    				}
    			}
    			
    			if (format == null)
    				format = "?";

    			resultList.add(new QueryResultListEntry(null, title, url, "?", format));
    		}
    	} catch (OAIException e) {
    		log.error(e.getMessage());
    	}
        return resultList.toArray(new QueryResultListEntry[resultList.size()]);
    }
    
    @Override
    public boolean useOAIQueryMode() {
    	return true;
    }
    
	public static void main(String[] args) {
		new GenericOAIQuerySource("http://www.diva-portal.org/oai/OAI", "DiVA.org").query(null, null);
	}
    
}
