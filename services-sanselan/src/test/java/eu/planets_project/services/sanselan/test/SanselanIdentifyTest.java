package eu.planets_project.services.sanselan.test;


import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.sanselan.SanselanIdentify;
import eu.planets_project.services.utils.test.ServiceCreator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class SanselanIdentifyTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-sanselan/SanselanIdentify?wsdl";

    /* A holder for the object to be tested */
    Identify ids = null;
    
    /**
     * set up tests by creating a service to test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        // Use a helper function to set up the testable class:
        ids = ServiceCreator.createTestService(Identify.QNAME,
                SanselanIdentify.class, wsdlLoc);

    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#describe()}.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#identify(eu.planets_project.services.datatypes.DigitalObject)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     */
    @Test
    public void testIdentify() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testIdentifyThis(new File("PA/sanselan/test/resources/PlanetsLogo-lowq-jpg.test").toURI(), new URI("planets:fmt/ext/jpeg"));
        testIdentifyThis(new File("PA/sanselan/test/resources/PlanetsLogo-lowq-png.test").toURI(), new URI("planets:fmt/ext/png"));
    }
    
    /**
     * 
     * @param purl
     * @param type
     * @throws MalformedURLException 
     */
    private void testIdentifyThis( URI purl, URI type ) throws MalformedURLException {
        /* Create the content: */
        DigitalObjectContent c1 = Content.byReference(purl.toURL());
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).build();
        
        /* Now pass this to the service */
        IdentifyResult ir = ids.identify(object, null);
        
        /* Check the result */
        System.out.println("Recieved type: " + ir.getTypes() );
        System.out.println("Recieved service report: " + ir.getReport() );
        assertEquals("The returned type did not match the expected;", type, ir
                .getTypes().get(0));
        
    }

}
