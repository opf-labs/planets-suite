/**
 * 
 */
package eu.planets_project.ifr.core.sample.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.simple.impl.SimpleCharacterisationService;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class SimpleCharacterisationServiceTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-if-simple/SimpleCharacterisationService?wsdl";

    /* A holder for the object to be tested */
    Characterise ids = null;

    /**
     * Create the service instance to test before running test 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        ids = ServiceCreator.createTestService(Characterise.QNAME, SimpleCharacterisationService.class, wsdlLoc );

    }

    /**
     * Test the services describe() method
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        System.out.println("Recieved service description: " + desc);
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
    }

    /**
     * test the sizing of a binary
     */
    @Test
    public void testSizeABinary() {
        /* set up a binary */
        byte[] binary = new byte[(int)(Math.random()*1024*10)];
        
        /* Create the content: */
        Content c1 = Content.byValue(binary);
        
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).build();

        /* Now run the service */
        testSizing(object, binary.length);
        
    }
    
    /**
     * test sizing from a URL reference
     * @throws MalformedURLException 
     */
    @Test
    public void testSizeAFileRef() throws MalformedURLException {
        /* set up a binary */
        byte[] binary = new byte[(int)(Math.random()*1024*10)];
        File file = ByteArrayHelper.write(binary);
        
        /* Create the content: */
        Content c1 = Content.byReference(file.toURI().toURL());
        
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).build();

        /* Now run the service */
        testSizing(object, binary.length);
        
    }
    /**
     * Helper that runs the service.
     * 
     * @param object
     * @param size
     */
    private void testSizing(DigitalObject object, int size) {
        /* Init the properties */
        Properties properties = new Properties();
        properties.add(
                SimpleCharacterisationService.makePropertyURI( SimpleCharacterisationService.MIME_PROP_URI), 
                SimpleCharacterisationService.MIME_PROP_URI);
        
        /* Now pass this to the service */
        CharacteriseResult ir = ids.characterise( object, null);
        System.out.println("Got report: "+ir.getReport());
        System.out.println("Got properties: "+ir.getProperties());
        System.out.println("Got properties.size(): "+ir.getProperties().size());
        
        /* Check the result */
        long mSize = -1;
        for( Property p : ir.getProperties() ) {
            if( p.getName().equals(SimpleCharacterisationService.MIME_PROP_URI)) {
                mSize = Long.parseLong( p.getValue() );
            }
        }
        System.out.println("Recieved measured size: " + mSize );
        assertEquals("The returned size did not match the expected size;", size, mSize);
        
        System.out.println("Recieved service report: " + ir.getReport() );
    }

}
