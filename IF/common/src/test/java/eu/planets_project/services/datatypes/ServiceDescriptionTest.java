/**
 * 
 */
package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.identify.Identify;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>, <a
 *         href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
public class ServiceDescriptionTest {

    /**
     * A short overview of using the ServiceDescription API:
     */
    @Test
    public void sampleUsage() {
        /*
         * You can create a service description from scratch, using required
         * parameters in the builder constructor and supplying optional
         * parameters in calls on the builder:
         */
        ServiceDescription description1 = new ServiceDescription.Builder(
                "Test Service", Identify.class.getName()).author("Some One")
                .build();
        Assert.assertEquals("Some One", description1.getAuthor());
        /* Or use the more concise factory method to create a description...: */
        description1 = ServiceDescription.create("Test Service",
                Identify.class.getName()).build();
        Assert.assertEquals("Test Service", description1.getName());
        /* Or to copy a description: */
        ServiceDescription copy = ServiceDescription.copy(description1).build();
        Assert.assertEquals(description1, copy);
        /*
         * You can also use an existing description (both as an object or as the
         * XML) as a template for your new description:
         */
        ServiceDescription description2 = new ServiceDescription.Builder(
                description1).author("Another One").build();
        /*
         * This description has the original values of the template object, as
         * long as they have not been replaced:
         */
        Assert.assertEquals("Another One", description2.getAuthor());
        Assert.assertEquals("Test Service", description2.getName());
        /*
         * Service descriptions can be serialized to XML. From that, an equal
         * service description can be instantiated:
         */
        ServiceDescription description3 = ServiceDescription.of(description2
                .toXml());
        Assert.assertEquals(description2, description3);
    }

    ServiceDescription sd = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        ServiceDescription.Builder builder = new ServiceDescription.Builder(
                "A Test Service",
                "eu.planets_project.services.identify.Identify");
        builder.author("Andrew N. Jackson <Andrew.Jackson@bl.uk>");
        builder
                .description("This is just a simple test service description, used to unit test the Service Description code.");
        builder.classname(ServiceDescriptionTest.class.getCanonicalName());
        builder.furtherInfo(new URI("http://www.planets-project.eu/"));
        builder.inputFormats(new URI("planets:fmt/ext/jpg"), new URI(
                "planets:fmt/ext/jpeg"));
        builder
                .instructions("There are not special instructions for this service.");
        List<Parameter> pars = new ArrayList<Parameter>();
        pars.add( new Parameter("planets:srv/par/test", "true") );
        builder.parameters(pars);
        sd = builder.build();
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.datatypes.ServiceDescription#toXml()}.
     */
    @Test
    public void testToXml() {
        // Grab the XML
        String sdXml = sd.toXmlFormatted();
        System.out.println(sdXml);

        // Put it in a file:
        try {
            String sdfname = "serviceDescription.xml";
            BufferedWriter out = new BufferedWriter(new FileWriter(sdfname));
            out.write(sdXml);
            out.close();
            System.out.println("Wrote service description to file: " + sdfname);
        } catch (IOException e) {
            // This is not a critical part of the test, and failure can be
            // ignored.
        }

        // Re-parse and check:
        ServiceDescription nsd = ServiceDescription.of(sdXml);
        assertTrue(
                "Re-serialised ServiceDescription does not match the original. ",
                sd.equals(nsd));

    }

    /**
     * Test method for
     * {@link eu.planets_project.services.datatypes.ServiceDescription#equals(Object)}
     * .
     */
    @Test
    public void equalsWithIdentifier() {
        String name = "name";
        String type = "type";
        String id = "id1";
        /* Create a service description and a copy via XML: */
        ServiceDescription original = new ServiceDescription.Builder(name, type)
                .identifier(id).build();
        ServiceDescription copy = ServiceDescription.of(original.toXml());
        /*
         * Might seem needless, but was the original motivation for this test:
         * check if the original is still OK:
         */
        Assert.assertEquals(id, original.getIdentifier());
        Assert.assertEquals(name, original.getName());
        Assert.assertEquals(type, original.getType());
        /* At the same time, the two objects should be identical: */
        Assert.assertEquals(original, copy);
        /* But if we change the ID, no longer: */
        Assert.assertNotSame(original, new ServiceDescription.Builder(original)
                .identifier("id2").build());

    }

    /**
     * Test method for
     * {@link eu.planets_project.services.datatypes.ServiceDescription#equals(Object)}
     * .
     */
    @Test
    public void equalsWithoutIdentifier() {
        /* Create a service description and a copy via XML: */
        ServiceDescription original = new ServiceDescription.Builder("name",
                "type").build();
        ServiceDescription copy = ServiceDescription.of(original.toXml());
        /* These two objects should be identical: */
        Assert.assertEquals(original, copy);
        /* But if we change anything, no longer: */
        Assert.assertNotSame(original, new ServiceDescription.Builder(original)
                .author("me").build());
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.datatypes.ServiceDescription#equals(Object)}
     * .
     */
    @Test
    public void equalsCornerCases() {
        Assert.assertFalse(sd.equals("Some string"));
        Assert.assertFalse(sd.equals(1));
        Assert.assertFalse(sd.equals(null));
    }

    /**
     * Test method for
     * {@link eu.planets_project.services.datatypes.ServiceDescription#hashCode()}
     * .
     */
    @Test
    public void hashCodeUsage() {
        /*
         * We test the hashCode implementation by using service descriptions
         * with a HashSet:
         */
        Set<ServiceDescription> set = new HashSet<ServiceDescription>();
        set.add(sd);
        set.add(ServiceDescription.of(sd.toXml()));
        set.add(ServiceDescription.of(sd.toXml())); // all the same until here
        set.add(new ServiceDescription.Builder(sd).author("me").build());
        Assert.assertEquals(2, set.size());
    }

}
