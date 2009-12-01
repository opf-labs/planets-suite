package eu.planets_project.fedora;

import junit.framework.TestCase;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.datatypes.*;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

import java.io.*;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 26, 2009
 * Time: 5:29:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraObjectManagerTest extends TestCase {

    FedoraObjectManager fedoraObjectManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        fedoraObjectManager = new FedoraObjectManager("fedoraAdmin","fedoraAdminPass","http://localhost:8080/fedora");
    }

    public void testCreateRetrieveUpdate() throws IOException, DigitalObjectManager.DigitalObjectNotStoredException, DigitalObjectManager.DigitalObjectNotFoundException {
        String dcmetadata = "<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">\n" +
                "  <dc:title>TestObject</dc:title>\n" +
                "</oai_dc:dc>";
        String content = "sdflkjsdlfjsdl\nslkdfkjsdlkjfsld\n";
        DigitalObjectContent DOcontent = Content.byValue(content.getBytes("UTF-8"));

        DigitalObject.Builder builder = new DigitalObject.Builder(DOcontent);
        builder.format(URI.create("info:pronom/x-fmt/283"));
        builder.metadata(new Metadata(URI.create("http://www.openarchives.org/OAI/2.0/oai_dc/"),"DC",dcmetadata));
        builder.title("TestObject");
        DigitalObject object = builder.build();
        URI pdURI = fedoraObjectManager.storeAsNew(object);
        DigitalObject storedObject = fedoraObjectManager.retrieve(pdURI);
        assertEquals(storedObject.getTitle(),object.getTitle());
        assertEquals(storedObject.getFormat(),object.getFormat());
        assertEquals(storedObject.getMetadata().get(0).getContent().trim(),object.getMetadata().get(0).getContent().trim());
        BufferedReader storedStream = new BufferedReader(new InputStreamReader(storedObject.getContent().getInputStream()));

        BufferedReader newStream = new BufferedReader(new InputStreamReader(object.getContent().getInputStream()));
        while (storedStream.ready() && newStream.ready()){
            assertEquals(storedStream.readLine(),newStream.readLine());
        }



        String dcmetadata2 = "<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">\n" +
                "  <dc:title>TestObject</dc:title>\n" +
                "</oai_dc:dc>";
        String content2 = "sdflkjsdlfjsdl\nslkdfkjsld\nskjdflskd";
        DigitalObjectContent DOcontent2 = Content.byValue(content.getBytes("UTF-8"));

        DigitalObject.Builder builder2 = new DigitalObject.Builder(DOcontent);
        builder2.format(URI.create("info:pronom/x-fmt/280"));
        builder2.metadata(new Metadata(URI.create("http://www.openarchives.org/OAI/2.0/oai_dc/"),"DC",dcmetadata));
        builder2.title("TestObject");
        DigitalObject object2 = builder2.build();
        fedoraObjectManager.updateExisting(pdURI,object2);
        DigitalObject restoredObject = fedoraObjectManager.retrieve(pdURI);

        assertEquals(restoredObject.getTitle(),object2.getTitle());
        assertEquals(restoredObject.getFormat(),object2.getFormat());
        assertEquals(restoredObject.getMetadata().get(0).getContent().trim(),object2.getMetadata().get(0).getContent().trim());
        BufferedReader restoredStream = new BufferedReader(new InputStreamReader(restoredObject.getContent().getInputStream()));

        BufferedReader newStream2 = new BufferedReader(new InputStreamReader(object2.getContent().getInputStream()));
        while (restoredStream.ready() && newStream2.ready()){
            assertEquals(restoredStream.readLine(),newStream2.readLine());
        }

    }
}
