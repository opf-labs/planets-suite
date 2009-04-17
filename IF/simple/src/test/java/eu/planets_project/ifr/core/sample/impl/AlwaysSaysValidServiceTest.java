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

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
import eu.planets_project.ifr.core.simple.impl.AlwaysSaysValidService;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
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
        ids = ServiceCreator.createTestService(Validate.QNAME,
                AlwaysSaysValidService.class, wsdlLoc);
    }

    /**
     * test the describe method
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        assertTrue("The ServiceDescription should not be NULL.", desc != null);
        System.out.println("Recieved service description: "
                + desc.toXmlFormatted());
    }

    /**
     * test the validate method
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @Test
    public void testValidate() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testValidateThis(null, new URI("http://some"),false, false );
        testValidateThis(new DigitalObject.Builder(ImmutableContent.byReference(new URL("http://someother")))
                .permanentUrl(new URL("http://some")).build(), new URI("ext"), true, true );
    }

    /**
     * @param purl
     * @param type
     */
    private void testValidateThis(DigitalObject dob, URI type, boolean ofThisFormat, boolean inRegardToThisFormat ) {
        /* Now pass this to the service */
        ValidateResult ir = ids.validate(dob, type, null);

        /* Check the result */
        System.out.println("Recieved is of format: " + ir.isOfThisFormat());
        System.out.println("Recieved is of format and valid: " + ir.isValidInRegardToThisFormat());
        System.out.println("Recieved service report: " + ir.getReport());
        assertEquals("The returned type did not match the expected;", ofThisFormat , ir.isOfThisFormat() );
        assertEquals("The returned type did not match the expected;", inRegardToThisFormat , ir.isValidInRegardToThisFormat() );

    }

}
