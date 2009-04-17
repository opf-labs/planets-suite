package eu.planets_project.tb.impl.data.demo.queryable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.tb.gui.backing.QueryResultListEntry;


/**
 * A DataManagerLocal demo implementation that interfaces directly to a 
 * mirror site of the NASA Blue Marble Next Generation image collection.
 *  
 * @author <a href="mailto:rainer.simon@arcs.ac.at">Rainer Simon</a>
 *
 */
public class GenericSRUQuerySource extends QuerySource {
	
	/**
	 * Logger
	 */
    private static PlanetsLogger log = PlanetsLogger.getLogger(GenericSRUQuerySource.class);

	/**
     * SRU endpoint base URL
     */
    private static String BASE_URL = "http://z3950.loc.gov:7090/voyager?version=1.1&operation=searchRetrieve";
    
    public GenericSRUQuerySource() {
    	super("LoC SRU Test Service");
    }
    
    public QueryResultListEntry[] query(String query, int limit, int offset) {
		try {
			String url = BASE_URL + "&query=" + URLEncoder.encode(query, "UTF-8") +
						 "&maximumRecords=" + limit + "&recordSchema=dc";
			log.debug(url);
			
			GetMethod sruRequest = new GetMethod(url);
			new HttpClient().executeMethod(sruRequest);
			
			SAXBuilder builder = new SAXBuilder();
			return createDigitalObjects(builder.build(sruRequest.getResponseBodyAsStream()));
		} catch (UnsupportedEncodingException e) {
			log.error(e.getClass().toString() + ": " + e.getMessage());
		} catch (HttpException e) {
			log.error(e.getClass().toString() + ": " + e.getMessage());
		} catch (IOException e) {
			log.error(e.getClass().toString() + ": " + e.getMessage());			
		} catch (JDOMException e) {
			log.error(e.getClass().toString() + ": " + e.getMessage());
		}
		return new QueryResultListEntry[0];
    }
    
    private QueryResultListEntry[] createDigitalObjects(Document dom) {
    	ArrayList<QueryResultListEntry> results = new ArrayList<QueryResultListEntry>();
    	try {
    		XPath x = XPath.newInstance("/zs:searchRetrieveResponse//zs:records//zs:record//zs:recordData//srw_dc:dc");
    		x.addNamespace(Namespace.getNamespace("srw_dc", "info:srw/schema/1/dc-schema"));
    		List list = x.selectNodes(dom);
    		Namespace dc = Namespace.getNamespace("http://purl.org/dc/elements/1.1/");
    		String title;
    		for (int i=0; i<list.size(); i++) {
    			// Parse metadata
    			title = ((Element) list.get(i)).getChildText("title", dc);
    			
    			// Create DigitalObject
    			try {
    				DigitalObject digObject = new DigitalObject.Builder(ImmutableContent.byReference(new URL("http://"))).build();
    				results.add(new QueryResultListEntry(digObject, title, "#", "-", "-"));
    			} catch (MalformedURLException mue) {
    				log.error(mue.getClass() + ": " + mue.getMessage());
    			}
    		}
    	} catch (JDOMException e) {
    		log.error(e.getClass().toString() + ": " + e.getMessage());
    	}

    	return results.toArray(new QueryResultListEntry[results.size()]);
    }
    
}
