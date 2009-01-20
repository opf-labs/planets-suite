/**
 * 
 */
package eu.planets_project.tb.utils;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
//import eu.planets_project.tb.impl.system.BackendProperties;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class XCDLParserTest {
    
    XCDLParser xp = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testParser() throws Exception {
       File xcdlFile = new File("application/src/test/resources/xcdl/test1Out.xcdl");
       List<MeasurementRecordImpl> measurements = XCDLParser.parseXCDL(xcdlFile);
       assertTrue("No properties found! ", measurements != null );
       assertTrue("Zero properties found! ", measurements.size() > 0 );
       
       for( MeasurementRecordImpl m : measurements ) {
           System.out.println("Got property "+m.getIdentifier() + " = "+m.getValue() );
       }
       
    }
    

}
