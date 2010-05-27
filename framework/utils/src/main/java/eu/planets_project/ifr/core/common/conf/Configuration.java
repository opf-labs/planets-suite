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

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represent the results of processing a properties file. Contains zero or
 * more properties that can be retrieved by their key.
 * 
 * @author Ian Radford
 */
public interface Configuration {
	/**
	 * Get the value of a property with a given key.
	 * @param key Determines which property to get the value for.
	 * @return The value matching the given key.
	 * @throws NoSuchElementException if the given key is not in the properties file.
	 */
	String getString(String key);
	/**
	 * Get the value of a property with a given key. If it doesn't exist, return the default value
	 * @param key Determines which property to get the value for.
	 * @param defaultValue Returned if the key is not found.
	 * @return The value matching the given key (or the default if no value found).
	 */
	String getString(String key, String defaultValue);

	/**
	 * Get the value of a property with a given key as a number (int).
	 * @param key Determines which property to get the value for.
	 * @return The value matching the given key, converted to an 'int'
	 * @throws NoSuchElementException If the given key is not in the properties file.
	 * @throws ConversionException If the String value of the property cannot be converted to an 'int'
	 */
	int getInteger(String key);
	/**
	 * Get the value of a property with a given key. If it doesn't exist, return the default value.
	 * @param key Determines which property to get the value for.
	 * @param defaultValue Returned if the key is not found.
	 * @return The value matching the given key (or the default if no value found).
	 * @throws ConversionException If the String value of the property cannot be converted to an 'int'
	 */
	int getInteger(String key, int defaultValue);

	/**
	 * Get the value of a property with a given key as a URI.
	 * @param key Determines which property to get the value for.
	 * @return The value matching the given key, converted to a URI
	 * @throws NoSuchElementException If the given key is not in the properties file.
	 * @throws ConversionException If the String value of the property cannot be converted to a URI
	 */
	URI getURI(String key);
	/**
	 * Get the value of a property with a given key as a URI. If it doesn't exist, return the default value.
	 * @param key Determines which property to get the value for.
	 * @param defaultValue Returned if the key is not found.
	 * @return The value matching the given key, converted to a URI
	 * @throws NoSuchElementException If the given key is not in the properties file.
	 * @throws ConversionException If the String value of the property cannot be converted to a URI
	 */
	URI getURI(String key, URI defaultValue);
	/**
	 * Get an iterator over all the keys in the configuration
	 * @return An iterator for a collection of the keys configured
	 */
	Iterator<String> getKeys();
}
