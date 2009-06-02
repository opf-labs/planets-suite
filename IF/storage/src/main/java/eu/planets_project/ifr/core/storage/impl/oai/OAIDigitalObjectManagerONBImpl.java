package eu.planets_project.ifr.core.storage.api.query;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.OaiPmhServer;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * ONB implementation of the OAI digital object manager.
 *
 */
public class OAIDigitalObjectManagerONBImpl extends AbstractOAIDigitalObjectManagerImpl {
	
	/**
	 * Create ONB connector with default settings
	 */
	public OAIDigitalObjectManagerONBImpl() {
		super("http://archiv-test.onb.ac.at:8881/OAI-PUB", "de2aleph", "dtl2aleph");
	}
	
	/**
	 * Create ONB connector with alternative endpoint
	 * (useful for testing via SSH tunnel and localhost address) 
	 * @param endpoint
	 */
	public OAIDigitalObjectManagerONBImpl(String endpoint) {
		super(endpoint, "de2aleph", "dtl2aleph");
	}
	
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
        OaiPmhServer server = new OaiPmhServer(baseURL);
        try {
            Record rec = server.getRecord(pdURI.toString(), metaDataPrefix);
            Element recEle = rec.getMetadata();
            if (recEle != null) {

                Document doc = recEle.getDocument();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("pmh", "http://www.openarchives.org/OAI/2.0/");
                map.put("xb", "http://com/exlibris/digitool/repository/api/xmlbeans");

                XPath xpathTitle = new Dom4jXPath("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:mds/pmh:md[pmh:type='dc' and pmh:name='descriptive']/pmh:value");
                xpathTitle.setNamespaceContext(new SimpleNamespaceContext(map));

                XPath xpathURL = new Dom4jXPath("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:urls/pmh:url[@type='stream']");
                xpathURL.setNamespaceContext(new SimpleNamespaceContext(map));

                XPath xpathLabel = new Dom4jXPath("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:control/pmh:label");
                xpathLabel.setNamespaceContext(new SimpleNamespaceContext(map));


                Node dcNode = (Node) xpathTitle.selectSingleNode(doc);
                Node urlNode = (Node) xpathURL.selectSingleNode(doc);
                // Node labelNode = (Node) xpathLabel.selectSingleNode(doc);

                if (dcNode != null && urlNode != null) {
                    String title = parseRecordString(dcNode.getText(), "dcterms:alternative");
                    String url = urlNode.getText();
                    DigitalObject dio = new DigitalObject.Builder(Content.byReference(new URL(url))).title(title).permanentUri(URI.create(url)).build();

                    return dio;
                }
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return null;
        }

        return null;
    }
    
    private static String parseRecordString(String record, String titleNode) throws ParserConfigurationException, SAXException, IOException {

        //DocumentBuilderFactory and DocumentBuilder
        DocumentBuilderFactory docuFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = docuFactory.newDocumentBuilder();

        //XPath
        //XPath xpathTitle = XPathFactory.newInstance().newXPath();

        if (record != null) {
            StringReader dcStringReader = new StringReader(record);
            InputSource dcInputSource = new InputSource(dcStringReader);
            org.w3c.dom.Document dcDocument = parser.parse(dcInputSource);
            org.w3c.dom.Element dcElement = dcDocument.getDocumentElement();

            NodeList dcRecordChildren = dcElement.getChildNodes();
            for (int ii = 0; ii < dcRecordChildren.getLength(); ii++) {
                org.w3c.dom.Node dcNode = dcRecordChildren.item(ii);
                if (dcNode.getNodeName().equals(titleNode)) {
                    String title = dcNode.getTextContent();
                    return title;
                }
            }
        }
        return "";
    }
    
	/**
	 * Basic tests.
	 * @param args unused
	 */
	public static void main(String[] args) {
		OAIDigitalObjectManagerONBImpl oaiImpl = new OAIDigitalObjectManagerONBImpl("http://localhost:8881/OAI-PUB");
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MONTH, -6);
		Calendar now = Calendar.getInstance();
		
		// ListIdentifiers
		System.out.println("starting query.");
		try {
			List<URI> identifiers = oaiImpl.list(null, new QueryDateRange(start, now));
			System.out.println(identifiers.size() + " found.");
			
			// GetRecord for each identifier
			for (URI id : identifiers) {
				try {
					DigitalObject dob = oaiImpl.retrieve(id);
					System.out.println("retrieved file: " + dob.getTitle());
				} catch (DigitalObjectNotFoundException e) {
					System.out.println("couldn't retrieve file: " + e.getMessage());
				}
			}
		} catch (QueryValidationException e) {
			System.out.println("QueryValidationException: " + e.getMessage());
		}
		
		System.out.println("done.");
	}

}
