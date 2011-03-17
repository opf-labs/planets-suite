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
package eu.planets_project.pp.plato.test.controller;

import javax.faces.convert.ConverterException;

import org.testng.annotations.Test;

import eu.planets_project.pp.plato.converters.NumberConverter;

public class NumberConverterTest {
    private NumberConverter nc = new NumberConverter();
    
    
    @Test
    public void testNulls() {
        assert("".equals(nc.getAsString(null, null, null)));
        
        try {
            nc.getAsObject(null, null, null);
            assert(false);
        } catch (ConverterException e) {
            assert(true);
        }
    }
    

    @Test
    public void testDoubles() {
        assert(nc.getAsString(null, null, new Double(-2.3)).equals("-2.3"));
        assert(nc.getAsString(null, null, new Double(2222.88)).equals("2222.88"));
    }
    
    @Test
    public void testStrings() {
        try {
            nc.getAsObject(null, null, "test");
            assert(false);
        } catch (ConverterException e) {
            assert(true);
        }
    }
}
