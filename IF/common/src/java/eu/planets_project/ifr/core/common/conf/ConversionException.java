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

public class ConversionException extends RuntimeException {
	private static final long serialVersionUID = 4168024286827885968L;

	public ConversionException() {
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
