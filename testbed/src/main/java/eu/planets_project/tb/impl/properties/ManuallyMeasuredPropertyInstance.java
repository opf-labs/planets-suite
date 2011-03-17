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
package eu.planets_project.tb.impl.properties;

import java.io.Serializable;

import org.kohsuke.rngom.util.Uri;

import eu.planets_project.tb.api.properties.ManuallyMeasuredProperty;

/**
 * This class is used to record the value of a given measurement for manually measured properties
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 23.04.2010
 *
 */
public class ManuallyMeasuredPropertyInstance extends ManuallyMeasuredPropertyImpl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3214124159852187336L;
	private String value;
	private String mName;
	private String mDescription;
	private String mURI;
	private boolean mUserDefined;
	
	
	public ManuallyMeasuredPropertyInstance(ManuallyMeasuredProperty mp){
		super(mp.getName(),mp.getDescription(),mp.getURI(),mp.isUserCreated());
	}
	
	/**
	 * allows to immediately specify a value
	 * @param mp
	 * @param value
	 */
	public ManuallyMeasuredPropertyInstance(ManuallyMeasuredProperty mp, String value){
		super(mp.getName(),mp.getDescription(),mp.getURI(),mp.isUserCreated());
		this.setValue(value);
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		if(value!=null)
			return value;
		return "";
	}

	public String getDescription() {
		if(mDescription!=null)
			return this.mDescription;
		return "";
	}

	public String getName() {
		if(mName!=null)
			return mName;
		return "";
	}
	
	public String getURI() {
		if(mURI!=null)
			return Uri.escapeDisallowedChars(mURI);
		return "";
	}
	
	public boolean isUserCreatedProperty(){
		return mUserDefined;
	}
}
