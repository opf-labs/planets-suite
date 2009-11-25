package eu.planets_project.ifr.core.common.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
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
	private static final String baseDir = "IF/common/src/test/resources/config";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty(ServiceConfig.BASE_DIR_PROPERTY, baseDir);
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
		int value = config.getInt("testMissingPrimitive");
		fail("expected NoSuchElementException to have been thrown. Got: " + value);
	}

	@Test
	public void testGetConfigurationClassDefaultPrimitive() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		int value = config.getInt("testMissingPrimitive", 1234);
		assertEquals(1234, value);
	}

	@Test
	public void testGetConfigurationClassValidNumber() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		assertEquals(1234, config.getInt("testValidNumber"));
	}

	@Test(expected=ConversionException.class)
	public void testGetConfigurationClassBadParseNumber() throws ConfigurationException {
		Configuration config = ServiceConfig.getConfiguration(getClass());
		config.getInt("testBadParseNumber");
		fail("expected ConfigurationException to have been thrown");
	}

}
