package eu.planets_project.services.pc.odftoolkit;


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

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ODFValidatorServiceTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/ODFValidatorService?wsdl";

    /* A holder for the object to be tested */
    Validate ids = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        ids = ServiceCreator.createTestService(Validate.QNAME, ODFValidatorService.class, wsdlLoc );
    }

    /**
     * test the describe() method
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        System.out.println("Recieved service description: " + desc.toXmlFormatted() );
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    /**
     * Test the validate method
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @Test
    public void testValidate() throws MalformedURLException, URISyntaxException {
        // Attempt to determine the type of a simple file, by name
        testValidateThis(null, new URI("http://some"), false );
        testValidateThis(new DigitalObject.Builder( ImmutableContent.byReference(new URL("http://someother") ) ).build() , new URI("ext"),
                true);
    }
    
    /**
     * 
     * @param purl
     * @param type
     */
    private void testValidateThis( DigitalObject dob , URI type, boolean valid ) {
        /* Now pass this to the service */
        ValidateResult vr = ids.validate(dob, type, null );
        
        /* Check the result */
        System.out.println("Recieved validity: " + vr.isOfThisFormat() + ", " + vr.isValidInRegardToThisFormat() );
        System.out.println("Recieved service report: " + vr.getReport() );
        assertEquals("The returned type did not match the expected;", valid , vr.isOfThisFormat() && vr.isValidInRegardToThisFormat() ) ;
        
    }

}
