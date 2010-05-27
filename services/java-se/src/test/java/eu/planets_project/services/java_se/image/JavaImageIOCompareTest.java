package eu.planets_project.services.java_se.image;


import eu.planets_project.ifr.core.techreg.properties.ServiceProperties;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.utils.test.ServiceCreator;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 * FIXME Need to clean up a lot before submitting.
 * 
 */
public class JavaImageIOCompareTest {
    
    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-java-se/JavaImageIOCompare?wsdl";

    /* A holder for the object to be tested */
    Compare ids = null;
    
    /**
     * Set up the testable class
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        // Use a helper function to set up the testable class:
        ids = ServiceCreator.createTestService(Compare.QNAME,
                JavaImageIOCompare.class, wsdlLoc);

    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleCompareService#describe()}.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = ids.describe();
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
    }

    /**
     * Test method for {@link eu.planets_project.ifr.core.simple.impl.SimpleCompareService#Compare(eu.planets_project.services.datatypes.DigitalObject)}.
     * @throws MalformedURLException 
     * @throws URISyntaxException 
     */
	@Test
    public void testCompare() throws MalformedURLException, URISyntaxException {
        // Same:
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-lowq-jpg.test"), 
                new File("PA/java-se/test/resources/PlanetsLogo-lowq-jpg.test"), true );
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-lowq-png.test"), 
                new File("PA/java-se/test/resources/PlanetsLogo-lowq-png.test"), true);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-lowq-png.test"), 
                new File("PA/java-se/test/resources/PlanetsLogo-lowq-jpg.test"), true);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo.png"), true);
        /* These don't seem to work in server mode, as if the JAR is not being picked up.
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo.tif"), true);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo.jp2"), true);
                */
        // Different, and indeed no alpha channel:
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-lowq-png.test"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-1.jpg"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), false);
        // Different, but same number of colour components (i.e. Alpha channel in both):
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo.gif"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-resamp-nn.png"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-resamp-bc.png"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-noalpha.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-noalpha.png"), 
                new File("PA/java-se/test/resources/PlanetsLogo-noalpha-dotted.png"), false);
        // Comparing a high-quality JPEG with poorer ones:
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), 
                new File("PA/java-se/test/resources/PlanetsLogo-lowq-jpg.test"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2-q70.jpg"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2-q80.jpg"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2-q90.jpg"), false);
        testCompareThis(new File("PA/java-se/test/resources/PlanetsLogo-2.jpg"), 
                new File("PA/java-se/test/resources/PlanetsLogo-2-q95.jpg"), false);
    }
    
    /**
     * 
     * @param purl
     * @param type
     * @throws MalformedURLException 
     */
    private void testCompareThis( File purl1, File purl2, boolean same ) throws MalformedURLException {
        System.out.println("Comparing "+purl1.getName()+" against "+purl2.getName());
        // Construct digital objects
        DigitalObjectContent c1 = Content.byReference(purl1.toURL());
        DigitalObject o1 = new DigitalObject.Builder(c1).permanentUri(purl1.toURI()).build();
        
        DigitalObjectContent c2 = Content.byReference(purl2.toURL());
        DigitalObject o2 = new DigitalObject.Builder(c2).permanentUri(purl2.toURI()).build();
        
        /* Now pass this to the service */
        CompareResult ir = ids.compare( o1, o2, null);
        
        /* Check the result */
        Boolean foundIdentical = null;
        for( Property p : ir.getProperties() ) {
//            System.out.println("Recieved property: " + p );
            if( JavaImageIOCompare.PSNR_URI.equals(p.getUri())) {
                System.out.println(p.getName()+" = "+p.getValue()+" ["+p.getUnit()+"]");
                double psnr = Double.parseDouble(p.getValue());
                if( Double.isInfinite(psnr) ) {
                    foundIdentical = true;
                } else {
                    foundIdentical = false;
                }
            }
        }
        assertTrue("The service should have been able to determine the PSNR for these images!", foundIdentical != null );
        assertTrue("The images were not correctly determined to be identical or not.", foundIdentical.booleanValue() == same);
        System.out.println("Recieved service report: " + ir.getReport() );
        System.out.println("Recieved service properties: " );
        ServiceProperties.printProperties(System.out, ir.getReport().getProperties());
        
    }

}
