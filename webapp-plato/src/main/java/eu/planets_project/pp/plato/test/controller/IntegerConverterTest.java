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

import eu.planets_project.pp.plato.converters.IntegerConverter;

public class IntegerConverterTest {
    private IntegerConverter ic = new IntegerConverter();
    @Test
    public void testNulls() {
        assert("".equals(ic.getAsString(null, null, null)));
        
        try {
            ic.getAsObject(null, null, null);
            assert(false);
        } catch (ConverterException e) {
            assert(true);
        }
    }
    
    @Test
    public void testIntegers() {
        assert(ic.getAsString(null, null, 3).equals("3"));
        assert(ic.getAsString(null, null, -1).equals("-1"));
    }
    
    @Test
    public void testDoubles() {
        try {
            ic.getAsObject(null, null, "2.2");
            assert(false);
        } catch (ConverterException e) {
            assert(true);
        }
    }
    
    @Test
    public void testStrings() {
        try {
            ic.getAsObject(null, null, "test");
            assert(false);
        } catch (ConverterException e) {
            assert(true);
        }
    }
}
