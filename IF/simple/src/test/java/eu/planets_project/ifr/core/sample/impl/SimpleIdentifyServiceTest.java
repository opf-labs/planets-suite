/**
 * 
 */
package eu.planets_project.ifr.core.sample.impl;

import eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.test.ServiceCreator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
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

        ids = ServiceCreator.createTestService(Identify.QNAME,
                SimpleIdentifyService.class, wsdlLoc);

    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#describe()}
     * .
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        System.out.println("Recieved service description: " + desc);
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.simple.impl.SimpleIdentifyService#identify(eu.planets_project.services.datatypes.DigitalObject)}
     * .
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @Test
    public void testIdentify() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testIdentifyThis(new URL(
                "http://www.planets-project.eu/fake/adocument.pdf"), new URI(
                "planets:fmt/mime/application/pdf"));
        testIdentifyThis(
                new URL("http://www.planets-project.eu/fake/image.png"),
                new URI("planets:fmt/mime/image/png"));
    }

    /**
     * @param purl
     * @param type
     */
    private void testIdentifyThis(URL purl, URI type) {
        /* Create the content: */
        Content c1 = ImmutableContent.byReference(purl);
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUrl(purl)
                .build();

        /* Now pass this to the service */
        IdentifyResult ir = ids.identify(object, null);

        /* Check the result */
        System.out.println("Recieved type: " + ir.getTypes());
        System.out.println("Recieved service report: " + ir.getReport());
        assertEquals("The returned type did not match the expected;", type, ir
                .getTypes().get(0));

    }

}
