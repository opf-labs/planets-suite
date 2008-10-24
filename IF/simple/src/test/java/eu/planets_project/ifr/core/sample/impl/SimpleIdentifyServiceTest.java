/**
 * 
 */
package eu.planets_project.ifr.core.sample.impl;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class SimpleIdentifyServiceTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/SimpleIdentifyService?wsdl";

    /* A holder for the object to be tested */
    Identify ids = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        URL url = null;

        // Set up the remote version, if applicable:
        if ("standalone".equals(System.getenv("pserv.test.context"))
                || "server".equals(System.getenv("pserv.test.context"))) {

            /* In the standalone case, start up the test endpoint. */
            if (System.getenv("pserv.test.context").equals("standalone")) {

                System.out.println("INIT: Setting up temporary test server.");

                // Set up a temporary service with the code deployed at the
                // specified address:
                Endpoint testEndpoint = Endpoint
                        .create(new SimpleIdentifyService());
                url = new URL(
                        "http://localhost:18367" + wsdlLoc );
                testEndpoint.publish(url.toString());

            }
            // In the server case, pick the server config up:
            else {
                String host = System.getenv("pserv.test.host")+":"+System.getenv("pserv.test.port");
                System.out
                        .println("INIT: Configuring against server at " + host );
                url = new URL( "http://" + host + wsdlLoc );

            }

            System.out.println("INIT: Creating the proxied service class.");
            Service service = Service.create(url, Identify.QNAME);
            ids = service.getPort(Identify.class);
            System.out.println("INIT: Created proxy class.");
        }
        // If no remote context is configured, invoke locally:
        else {
            System.out.println("INIT: Creating a local instance.");
            ids = new SimpleIdentifyService();
        }

    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#describe()}.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        System.out.println("Recieved service description: " + desc);
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#identify(eu.planets_project.services.datatypes.DigitalObject)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     */
    @Test
    public void testIdentify() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testIdentifyThis(new URL("http://www.planets-project.eu/fake/adocument.pdf"), new URI("planets:fmt/mime/application/pdf"));
        testIdentifyThis(new URL("http://www.planets-project.eu/fake/image.png"), new URI("planets:fmt/mime/image/png"));
    }
    
    /**
     * 
     * @param purl
     * @param type
     */
    private void testIdentifyThis( URL purl, URI type ) {
        /* Create the content: */
        Content c1 = Content.byReference(purl);
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(purl, c1).build();
        
        /* Now pass this to the service */
        IdentifyResult ir = ids.identify(object);
        
        /* Check the result */
        System.out.println("Recieved type: " + ir.getType() );
        System.out.println("Recieved service report: " + ir.getReport() );
        assertEquals("The returned type did not match the expected;", type, ir
                .getType());
        
    }

}
