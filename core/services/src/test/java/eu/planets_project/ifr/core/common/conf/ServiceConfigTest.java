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
package eu.planets_project.ifr.core.common.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link ServiceConfig}.
 * 
 * @author Ian Radford
 */

public class ServiceConfigTest {
	/**
	 * Base location for all configuration values. This is for TEST ONLY
	 */
	private static final String baseDir = System.getProperty("app.dir")+"/src/test/resources/config";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//System.setProperty(ServiceConfig.BASE_DIR_PROPERTY, baseDir);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetConfigurationClassCheckPropsExist() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertNotNull("Expected to find properties in: " + baseDir + File.separatorChar + getClass().getName() + ".properties", config);
	}

	@Test(expected=ConfigurationException.class)
	public void testGetConfigurationClassPropsDontExist() throws ConfigurationException {
		/*
		 * Look for a config file for UnsupportedOperationException : shouldn't be any
		 * This is just a random class and has no special meaning to ServiceConfig.
		 */
		ServiceConfig.getConfiguration(java.lang.UnsupportedOperationException.class);
		fail("expected ConfigurationException to have been thrown");
	}

	@Test
	public void testGetConfigurationClassValidString() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals("testString", config.getString("testValidString"));
	}

	@Test(expected=NoSuchElementException.class)
	// Properties files can only have simple keys. This doesn't work!
	public void testGetConfigurationClassComplexKey1() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals("five", config.getString("http://one.two/three?four"));
	}

	@Test(expected=NoSuchElementException.class)
	// Properties files can only have simple keys. This doesn't work!
	public void testGetConfigurationClassComplexKey2() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals("six", config.getString("http://one.two/three?four=five"));
	}

	@Test
	public void testGetConfigurationClassComplexValue() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals("http://one.two/three?four=five", config.getString("complex"));
	}

	@Test
	public void testGetConfigurationClassDefaultString() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals("defaultValue", config.getString("testInvalidString", "defaultValue"));
	}

	@Test(expected=NoSuchElementException.class)
	public void testGetConfigurationClassNoDefaultString() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		String value = config.getString("testMissingString");
		fail("expected NoSuchElementException to have been thrown. Got: " + value);
	}

	@Test(expected=NoSuchElementException.class)
	public void testGetConfigurationClassNoDefaultPrimitive() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		int value = config.getInteger("testMissingPrimitive");
		fail("expected NoSuchElementException to have been thrown. Got: " + value);
	}

	@Test
	public void testGetConfigurationClassDefaultPrimitive() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		int value = config.getInteger("testMissingPrimitive", 1234);
		assertEquals(1234, value);
	}

	@Test
	public void testGetConfigurationClassValidNumber() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals(1234, config.getInteger("testValidNumber"));
	}

	@Test(expected=ConversionException.class)
	public void testGetConfigurationClassBadParseNumber() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		config.getInteger("testBadParseNumber");
		fail("expected ConfigurationException to have been thrown");
	}

	@Test(expected=NoSuchElementException.class)
	public void testGetConfigurationClassMissingURI() throws ConfigurationException, URISyntaxException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals(new URI("file:///opt/jboss"), config.getURI("uriXX"));
	}

	@Test
	public void testGetConfigurationClassValidURI() throws ConfigurationException, URISyntaxException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals(new URI("file:///opt/jboss"), config.getURI("uri"));
	}

	@Test
	public void testGetConfigurationClassDefaultURI() throws ConfigurationException, URISyntaxException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		URI defaultURI = new URI("file:///opt/jboss");
		assertEquals(defaultURI, config.getURI("uriXX", defaultURI));
	}

	@Test(expected=ConversionException.class)
	public void testGetConfigurationClassBadParseURI() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		config.getURI("badUri");
		fail("expected ConfigurationException to have been thrown");
	}

}
