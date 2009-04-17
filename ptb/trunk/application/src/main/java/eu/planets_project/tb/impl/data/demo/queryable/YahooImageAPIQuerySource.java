package eu.planets_project.tb.impl.data.demo.queryable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.gui.backing.QueryResultListEntry;

/**
 * A DataManagerLocal demo implementation that interfaces directly to a 
 * mirror site of the NASA Blue Marble Next Generation image collection.
 *  
 * @author <a href="mailto:rainer.simon@arcs.ac.at">Rainer Simon</a>
 *
 */
public class YahooImageAPIQuerySource extends QuerySource {
	
	/**
	 * Logger
	 */
    private static PlanetsLogger log = PlanetsLogger.getLogger(YahooImageAPIQuerySource.class);

	/**
     * API base URL (incl. Yahoo App ID)
     */
    private static String API_BASE_URL = "http://search.yahooapis.com/ImageSearchService/V1/imageSearch";
    private static String Y_APP_ID = "Oc5vBjrV34EdL30ngS_5VnW9PVk0jRSkwyzQO0IDDNXCsBJE4OSq5NE1NF4FToohppPX";
    private static String BASE_URL = API_BASE_URL + "?appid=" + Y_APP_ID + "&";
    
    public YahooImageAPIQuerySource() {
    	super("Yahoo! Image Search");
    }
    
    public QueryResultListEntry[] query(String query, int limit, int offset) {
    	try {
    		// Fire GET requets to Yahoo image search API
    		String queryString = BASE_URL + "query=" + URLEncoder.encode(query, "UTF-8") + "&results=" + limit + "&start=" + offset; 		
			GetMethod yahooRequest = new GetMethod(queryString);
			new HttpClient().executeMethod(yahooRequest);
			
			// Create XML DOM
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = factory.newDocumentBuilder();
	        Document dom = docBuilder.parse(yahooRequest.getResponseBodyAsStream());
	        
			// Parse DOM
			return createDigitalObjects(dom);
    	} catch (UnsupportedEncodingException e) {
    		log.error(e.getClass() + ": " + e.getMessage());
    	} catch (IOException e) {
    		log.error(e.getClass() + ": " + e.getMessage());
    	} catch (SAXException e) {
    		log.error(e.getClass() + ": " + e.getMessage());
    	} catch (ParserConfigurationException e) {
    		log.error(e.getClass() + ": " + e.getMessage());
    	}
    	
    	return new QueryResultListEntry[0];
    }
    
    private QueryResultListEntry[] createDigitalObjects(Document dom) {	
    	ArrayList<QueryResultListEntry> resultList = new ArrayList<QueryResultListEntry>();
    	
    	// Create a node iterator
        Node root = dom.getDocumentElement();
        NodeList results = root.getChildNodes();
        
        // Walk the DOM for all <result> elements
        Element aResult;
        NodeList resultFragment;
        Element aResultFragmentChild; 

        String imgUrl;
        String imgName;
        double imgSize;
        String imgFormat;

        for (int i=0; i<results.getLength(); i++) {
        	if (results.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		aResult = (Element) results.item(i);
        		if (aResult.getNodeName().equalsIgnoreCase("result")) {
        			// Node is a <result> element node
        			imgUrl = null;
        			imgName = null;
        			imgSize = 0;
        			imgFormat = null;
        			
        			resultFragment = aResult.getChildNodes();
        			for (int j=0; j<resultFragment.getLength(); j++) {
        				if (resultFragment.item(j).getNodeType() == Node.ELEMENT_NODE) {
        					aResultFragmentChild = (Element) resultFragment.item(j);
        					
        					// URL
        					if (aResultFragmentChild.getNodeName().equalsIgnoreCase("url")) {
        						imgUrl = aResultFragmentChild.getFirstChild().getNodeValue().trim();
        						
        					// Name
        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("title")) {
        						imgName = aResultFragmentChild.getFirstChild().getNodeValue().trim();
        						
        					// Filesize
        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("filesize")) {
        						try {
        							imgSize = Integer.parseInt(aResultFragmentChild.getFirstChild().getNodeValue().trim());
        							imgSize = (int) (imgSize / 10.24);
        							imgSize /= 100;
        						} catch (Exception e) {
        							// Do nothing
        						}
        						
        					// Fileformat
        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("fileformat")) {
        						imgFormat = aResultFragmentChild.getFirstChild().getNodeValue().trim();
        					}
        				}
        			}
        			
        			// If all relevant data was retrieved, create DigitalObject
			    	try {
			    		DigitalObject digObject = new DigitalObject.Builder(Content.byReference(new URL(imgUrl))).build();
			    		resultList.add(new QueryResultListEntry(digObject, imgName, imgUrl, Double.toString(imgSize), imgFormat));
			    	} catch (MalformedURLException e) {
			    		log.warn(e.getClass() + ": " + e.getMessage());
			    	}
        		}
        	}
        }
		
    	return resultList.toArray(new QueryResultListEntry[resultList.size()]);
    }
    
}
