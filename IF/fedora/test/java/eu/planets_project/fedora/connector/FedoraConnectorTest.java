package eu.planets_project.fedora.connector;

import junit.framework.TestCase;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.transform.TransformerException;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 26, 2009
 * Time: 4:00:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraConnectorTest extends TestCase {

    FedoraConnector fedoraConnector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        fedoraConnector = new FedoraConnector("fedoraAdmin","fedoraAdminPass","http://localhost:8080/fedora");
    }

    public void testCreateExamineAndPurge() throws StoreException, FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException {
        String pid = fedoraConnector.newObject();
        assertTrue(fedoraConnector.exists(pid));
        assertTrue(fedoraConnector.isPlanetsObject(pid));
        assertTrue(fedoraConnector.isDataObject(pid));
        assertTrue(fedoraConnector.isWritable(pid));

        String planetsModel = fedoraConnector.getPlanetsContentModel(pid);
        assertTrue (planetsModel.equals("info:fedora/demo:Planets_ContentModel"));
        fedoraConnector.purgeObject(pid);
    }

    public void testDatastreamWork1() throws StoreException, FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException, ParseException, TransformerException {
        String pid = fedoraConnector.newObject();
        assertTrue(fedoraConnector.exists(pid));
        assertTrue(fedoraConnector.isPlanetsObject(pid));
        assertTrue(fedoraConnector.isDataObject(pid));
        assertTrue(fedoraConnector.isWritable(pid));
        Document dc = fedoraConnector.getDatastreamXML(pid, "DC");
        Element creator = dc.createElementNS("", "dc:creator");
        Text creatortext = dc.createTextNode("Planets");
        Text doublespace = dc.createTextNode("  ");
        Text newline = dc.createTextNode("\n");
        creator.appendChild(creatortext);
        dc.getDocumentElement().appendChild(newline);
        dc.getDocumentElement().appendChild(doublespace);
        dc.getDocumentElement().appendChild(creator);
        dc.getDocumentElement().appendChild(newline);
        dc.normalizeDocument();
        String newcontent = DocumentUtils.documentToString(dc);
        newcontent= newcontent.trim();
        fedoraConnector.modifyDatastream(pid,"DC",newcontent, null);
        String storedContent = fedoraConnector.getDatastreamString(pid, "DC");
        assertEquals(newcontent,storedContent.trim());
        fedoraConnector.purgeObject(pid);
    }

    public void testDatastreamWork2() throws FedoraConnectionException, DigitalObjectManager.DigitalObjectNotFoundException, StoreException, ParseException {
        String content = "ahsddksjgldskdlfnskl\nlskdfjlsjdf\n";
        String pid = fedoraConnector.newObject();
        assertTrue(fedoraConnector.exists(pid));
        assertTrue(fedoraConnector.isPlanetsObject(pid));
        assertTrue(fedoraConnector.isDataObject(pid));
        assertTrue(fedoraConnector.isWritable(pid));
        try {
            fedoraConnector.getDatastreamString(pid,"CONTENT");
            fail("Datastream should not be there");
        } catch (DigitalObjectManager.DigitalObjectNotFoundException e) {
            //good
        }
        fedoraConnector.modifyDatastream(pid,"CONTENT",content, null);
        String storedContent = fedoraConnector.getDatastreamString(pid, "CONTENT");
        assertEquals(content,storedContent);
        fedoraConnector.purgeObject(pid);

    }

    public void testDatastreamWork3()
            throws
            FedoraConnectionException,
            DigitalObjectManager.DigitalObjectNotFoundException,
            StoreException,
            ParseException,
            IOException {
        String content = "ahsddksjgldskdlfnskl\nlskdfjlsjdf\n";
        String pid = fedoraConnector.newObject();
        assertTrue(fedoraConnector.exists(pid));
        assertTrue(fedoraConnector.isPlanetsObject(pid));
        assertTrue(fedoraConnector.isDataObject(pid));
        assertTrue(fedoraConnector.isWritable(pid));
        try {
            fedoraConnector.getDatastreamString(pid,"CONTENT");
            fail("Datastream should not be there");
        } catch (DigitalObjectManager.DigitalObjectNotFoundException e) {
            //good
        }
        fedoraConnector.modifyDatastream(pid,"CONTENT",content,null);
        
        URL url = fedoraConnector.getDatastreamURL(pid, "CONTENT");
        InputStream stream = url.openStream();
        String storedContent = convertStreamToString(stream);
        assertEquals(content,storedContent);
        fedoraConnector.purgeObject(pid);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
