/**
 * 
 */
package eu.planets_project.ifr.core.sample.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
import eu.planets_project.ifr.core.simple.impl.AlwaysSaysValidService;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class AlwaysSaysValidServiceTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/AlwaysSaysValidService?wsdl";

    /* A holder for the object to be tested */
    Validate ids = null;

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
                        .create(new AlwaysSaysValidService());
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
            Service service = Service.create(url, Validate.QNAME);
            ids = service.getPort(Validate.class);
            System.out.println("INIT: Created proxy class.");
        }
        // If no remote context is configured, invoke locally:
        else {
            System.out.println("INIT: Creating a local instance.");
            ids = new AlwaysSaysValidService();
        }

    }

    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        System.out.println("Recieved service description: " + desc.toXml(true) );
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    @Test
    public void testValidate() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testValidateThis(null, new URI("http://some"), ValidateResult.Validity.INVALID );
        testValidateThis(new DigitalObject.Builder(new URL("http://some"), Content.byReference(null) ).build() , new URI("ext"), 
                ValidateResult.Validity.VALID );
    }
    
    /**
     * 
     * @param purl
     * @param type
     */
    private void testValidateThis( DigitalObject dob , URI type, ValidateResult.Validity valid ) {
        /* Now pass this to the service */
        ValidateResult ir = ids.validate(dob, type );
        
        /* Check the result */
        System.out.println("Recieved validity: " + ir.getValidity() );
        System.out.println("Recieved service report: " + ir.getReport() );
        assertEquals("The returned type did not match the expected;", valid , ir.getValidity() ) ;
        
    }

}
