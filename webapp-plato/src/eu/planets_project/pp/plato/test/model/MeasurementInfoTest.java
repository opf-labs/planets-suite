/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.test.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.measurement.MeasurementInfo;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;

public class MeasurementInfoTest {
    
    
    
    @Test
    public void testSetAsUri(){
        MeasurementInfoUri infoUri = new MeasurementInfoUri();
        Assert.assertNull(infoUri.getAsURI(), "not initialised");
        infoUri.setAsURI(null);
        Assert.assertNull(infoUri.getAsURI(), "should be clean");
        infoUri.setAsURI("");
        Assert.assertNull(infoUri.getAsURI(), "should be reset");
        
        infoUri.setAsURI("blabla://");
        Assert.assertEquals("blabla", infoUri.getScheme());
        Assert.assertEquals("blabla://", infoUri.getAsURI());
        
        infoUri.setAsURI("action://runtime");
        Assert.assertEquals("action", infoUri.getScheme());
        Assert.assertEquals("runtime", infoUri.getPath());
        Assert.assertEquals("action://runtime", infoUri.getAsURI());

        infoUri.setAsURI("outcome://object/image/dimension/width#equal");
        Assert.assertEquals("outcome", infoUri.getScheme());
        Assert.assertEquals("object/image/dimension/width", infoUri.getPath());
        Assert.assertEquals("equal", infoUri.getFragment());
        Assert.assertEquals("outcome://object/image/dimension/width#equal", infoUri.getAsURI());
        
        try {
            infoUri.setAsURI("action://runtime//performance");
            Assert.fail("invalid path - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            infoUri.setAsURI("action://runtime://performance");
            Assert.fail("invalid path - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            infoUri.setAsURI("outcome://object/image/dimension/width#equal#holla");
            Assert.fail("invalid fragment - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            infoUri.setAsURI("blabla#://");
            Assert.fail("invalid scheme - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            infoUri.setAsURI("blabla/hoho://");
            Assert.fail("invalid scheme - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            infoUri.setAsURI("blabla");
            Assert.fail("invalid scheme - should throw exception");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }
    @Test()
    public void testFromUri() {
        MeasurementInfo info = new MeasurementInfo();
        info.fromUri("outcome://object/image/dimension/width#equal");
        Assert.assertNotNull(info.getProperty(), "should be initialised");
        Assert.assertEquals(info.getProperty().getPropertyId(),"image/dimension/width"); 
        Assert.assertNotNull(info.getMetric(), "should be initialised");
        Assert.assertEquals(info.getMetric().getMetricId(),"equal"); 

        info.fromUri("");
        Assert.assertNull(info.getUri(), "should be reset");
        Assert.assertNull(info.getProperty(), "should be reset");
        Assert.assertNull(info.getMetric(), "should be reset");
        
        info.fromUri(null);
        Assert.assertNull(info.getUri(), "should be reset");
        Assert.assertNull(info.getProperty(), "should be reset");
        Assert.assertNull(info.getMetric(), "should be reset");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testFromUriInvalidCategory() {
        MeasurementInfo info = new MeasurementInfo();
        info.fromUri("object://xcl/object/image/dimension/width#equal");
    }

}
