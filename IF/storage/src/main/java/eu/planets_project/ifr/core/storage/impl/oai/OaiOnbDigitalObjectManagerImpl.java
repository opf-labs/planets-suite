/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.planets_project.ifr.core.storage.impl.oai;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author onb
 */
public class OaiOnbDigitalObjectManagerImpl implements DigitalObjectManager {

    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new UnsupportedOperationException("Not supported via OAI.");
    }

    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
        String baseURL = "http://archiv-test.onb.ac.at:8881/OAI-PUB";
        String metadataPrefix = "de2aleph";
        try {
            GetRecord gr = new GetRecord(baseURL, pdURI.toString(), metadataPrefix);

            //XPath
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(ctx);

            Document doc = gr.getDocument();

            String dcRecordString = xpath.evaluate("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:mds/pmh:md[pmh:type='dc' and pmh:name='descriptive']/pmh:value", doc);

//            System.out.println(parseRecordString(dcRecordString, "dcterms:alternative"));

            byte[] binary = fetch(doc, "/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:urls/pmh:url[@type='stream']");

            DigitalObject dio =new DigitalObject.Builder(ImmutableContent.byValue(binary)).title(parseRecordString(dcRecordString, "dcterms:alternative")).build();

            return dio;

        } catch (XPathExpressionException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<URI> list(URI pdURI) {
        String baseURL = "http://archiv-test.onb.ac.at:8881/OAI-PUB";
        String from = "2009-03-19T10:50:20Z";
        String until = "2009-03-19T19:10:20Z";
        String set = "dtl2aleph";
        String metadataPrefix = "de2aleph";
        ListIdentifiers li;

        ArrayList<URI> ids = new ArrayList<URI>();

        try {
            li = new ListIdentifiers(baseURL, from, until, set, metadataPrefix);
            NodeList nsTest = li.getNodeList("//*[local-name()='identifier']");
            for (int i = 0; i < nsTest.getLength(); i++) {
                Element id = (Element) nsTest.item(i);
                ids.add(new URI(id.getTextContent()));
            }
        } catch (IOException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(OaiOnbDigitalObjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }
    
    

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryModes()
     */
    public List<Class<? extends Query>> getQueryModes() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI pdURI) {
        return false;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#setQuery(eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public void setQuery(Query q) {
        
    }

    private byte[] fetch(Document urls, String xpathExp) throws XPathExpressionException, MalformedURLException, IOException {
        //XPath
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(ctx);

        String toFetch = xpath.evaluate(xpathExp, urls);
//        System.out.println("fetch:" + toFetch);
//        System.out.println(toFetch);
        URL url = new URL(toFetch);
        InputStream is = url.openStream();
        byte[] binary = FileUtils.writeInputStreamToBinary(is);
        return binary;
    }

    private static String parseRecordString(String record, String titleNode) throws ParserConfigurationException, SAXException, IOException {

        //DocumentBuilderFactory and DocumentBuilder
        DocumentBuilderFactory docuFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = docuFactory.newDocumentBuilder();

        //XPath
        //XPath xpath = XPathFactory.newInstance().newXPath();

        if (record != null) {
            StringReader dcStringReader = new StringReader(record);
            InputSource dcInputSource = new InputSource(dcStringReader);
            Document dcDocument = parser.parse(dcInputSource);
            Element dcElement = dcDocument.getDocumentElement();

            NodeList dcRecordChildren = dcElement.getChildNodes();
            for (int ii = 0; ii < dcRecordChildren.getLength(); ii++) {
                Node dcNode = dcRecordChildren.item(ii);
                if (dcNode.getNodeName().equals(titleNode)) {
                    String title = dcNode.getTextContent();
                    return title;
                }
            }
        }
        return "";
    }
    NamespaceContext ctx = new NamespaceContext() {

        public String getNamespaceURI(String prefix) {
            String uri;
            if (prefix.equals("pmh")) {
                uri = "http://www.openarchives.org/OAI/2.0/";
            } else if (prefix.equals("xb")) {
                uri = "http://com/exlibris/digitool/repository/api/xmlbeans";
            } else {
                uri = null;
            }
            return uri;
        }

        // Dummy implementation - not used!
        public Iterator getPrefixes(String val) {
            return null;
        }

        // Dummy implemenation - not used!
        public String getPrefix(String uri) {
            return null;
        }
    };
}
