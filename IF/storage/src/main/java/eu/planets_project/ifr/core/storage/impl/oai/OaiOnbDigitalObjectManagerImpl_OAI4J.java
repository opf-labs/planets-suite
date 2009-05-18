/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.planets_project.ifr.core.storage.impl.oai;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
import se.kb.oai.OAIException;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;

/**
 *
 * @author georg
 */
public class OaiOnbDigitalObjectManagerImpl_OAI4J implements DigitalObjectManager {

    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new UnsupportedOperationException("Not supported via OAI.");
    }

    public boolean isWritable(URI pdURI) {
        return false;
    }

    public List<URI> list(URI pdURI) {
        // Perform OAI-PMH request without time range ('from' and 'until' are optional in OAI-PMH!)
        try {
            return list(pdURI, null);
        } catch (QueryValidationException e) {
            // Since query is null, this can never happen
            return new ArrayList<URI>();
        }
    }

    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        String baseURL = "http://archiv-test.onb.ac.at:8881/OAI-PUB";
        String from = "2009-03-19T10:50:20Z";
        String until = "2009-03-19T19:10:20Z";
        String set = "dtl2aleph";
        String metadataPrefix = "de2aleph";

        if (pdURI == null) {
            // OAI hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
            ArrayList<URI> resultList = new ArrayList<URI>();

            OaiPmhServer server = new OaiPmhServer(baseURL);
            try {
                IdentifiersList list;
                if (q == null) {
                    list = server.listIdentifiers(metadataPrefix);
                } else {
                    if (!(q instanceof QueryDateRange)) {
                        throw new QueryValidationException("Unsupported query type");
                    }

                    list = server.listIdentifiers(metadataPrefix, from, until, set);
                }

                for (Header header : list.asList()) {
                    try {
                        resultList.add(new URI(header.getIdentifier()));
                    } catch (URISyntaxException e) {
//                        log.warn("Illegal identifier returned from " + baseURL + ": " + header.getIdentifier());
                    }
                }
            } catch (OAIException e) {
//                log.error(e.getMessage());
            }
            return resultList;
        } else {
            return new ArrayList<URI>();
        }
    }

    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
        String baseURL = "http://archiv-test.onb.ac.at:8881/OAI-PUB";
        String metadataPrefix = "de2aleph";
        OaiPmhServer server = new OaiPmhServer(baseURL);
        try {
            Record rec = server.getRecord(pdURI.toString(), metadataPrefix);
            Element recEle = rec.getMetadata();
            if (recEle != null) {

                Document doc = recEle.getDocument();

                HashMap map = new HashMap();
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
                Node labelNode = (Node) xpathLabel.selectSingleNode(doc);


                if (dcNode != null && urlNode != null) {
                    String title = parseRecordString(dcNode.getText(), "dcterms:alternative");
                    String url = urlNode.getText();
                    DigitalObject dio = new DigitalObject.Builder(Content.byReference(new URL(url))).title(title).permanentUri(URI.create(url)).build();

                    return dio;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl_OAI4J.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return null;
    }

    public List<Class<? extends Query>> getQueryTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
