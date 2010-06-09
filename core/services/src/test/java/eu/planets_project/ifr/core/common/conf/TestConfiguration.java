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
import java.util.Properties;

public class TestConfiguration implements Configuration {
	private Properties props;
	public TestConfiguration(Properties props) {
		this.props = props;
	}

	public int getInteger(String key) {
		throw new RuntimeException("Unimplemented");
	}

	public int getInteger(String key, int defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

	public Iterator getKeys() {
		return props.keySet().iterator();
	}

	public String getString(String key) {
		return props.getProperty(key);
	}

	public String getString(String key, String defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

	public URI getURI(String key) {
		throw new RuntimeException("Unimplemented");
	}

	public URI getURI(String key, URI defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

}
