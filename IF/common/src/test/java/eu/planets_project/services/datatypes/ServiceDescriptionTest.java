/**
 * 
 */
package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceDescriptionTest {

    ServiceDescription sd = null;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        ServiceDescription.Builder builder = new ServiceDescription.Builder("A Test Service", "eu.planets_project.services.identify.Identify");
        builder.author("Andrew N. Jackson <Andrew.Jackson@bl.uk>");
        builder.description("This is just a simple test service description, used to unit test the Service Description code.");
        builder.classname(ServiceDescriptionTest.class.getCanonicalName());
        builder.furtherInfo(new URI("http://www.planets-project.eu/"));
        builder.inputFormats(new URI("planets:fmt/ext/jpg"),new URI("planets:fmt/ext/jpeg"));
        builder.instructions("There are not special instructions for this service.");
        Parameters pars = new Parameters();
        pars.add("planets:srv/par/test", "true");
        builder.parameters(pars);
        sd=builder.build();
    }

    /**
     * Test method for {@link eu.planets_project.services.datatypes.ServiceDescription#toXml()}.
     */
    @Test
    public void testToXml() {
        // Grab the XML
        String sdXml = sd.toXml(true);
        System.out.println(sdXml);
        
        // Put it in a file:
        try {
            String sdfname = "serviceDescription.xml";
            BufferedWriter out = new BufferedWriter(new FileWriter(sdfname));
            out.write(sdXml);
            out.close();
            System.out.println("Wrote service description to file: " + sdfname);
        } catch (IOException e) {
            // This is not a critical part of the test, and failure can be ignored.
        }
        
        // Re-parse and check:
        ServiceDescription nsd = ServiceDescription.of(sdXml);
        assertTrue("Re-serialised ServiceDescription does not match the original. ", sd.equals(nsd) );
        
    }

}
