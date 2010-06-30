/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.utils;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
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
       List<MeasurementImpl> measurements = XCDLParser.parseXCDL(xcdlFile);
       assertTrue("No properties found! ", measurements != null );
       assertTrue("Zero properties found! ", measurements.size() > 0 );
       
       for( MeasurementImpl m : measurements ) {
           System.out.println("Got property "+m.getIdentifier() + " = "+m.getValue() );
       }
       
    }
    

}
