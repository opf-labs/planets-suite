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
package eu.planets_project.tb.impl.model.eval;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.tb.api.model.eval.Metric;

public class MetricImpl implements Metric,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8195188725783701476L;
	private String sName = "";
	private String sType ="";
	private String sDescription ="";
	private String[] nummericTypes = new String[]{"java.lang.Integer","java.lang.Long","java.lang.Double","java.lang.Float"};
	
	public MetricImpl(String sName, String sType){
		init(sName,sType,null);
	}
	
	public MetricImpl(String sName, String sType, String sDescription){
		init(sName,sType,sDescription);	
	}
	
	private void init(String sName, String sType, String sDescription){
		if(sName!=null)
			this.setName(sName);
		if(sType!=null)
			this.setType(sType);
		if(sDescription!=null)
			this.setDescription(sDescription);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setName(java.lang.String)
	 */
	public void setName(String sName){
		this.sName = sName;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getName()
	 */
	public String getName(){
		return this.sName;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setType(java.lang.String)
	 */
	public void setType(String sType){
		this.sType = sType;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getType()
	 */
	public String getType(){
		return this.sType;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getNumericTypes()
	 */
	public List<String> getNumericTypes(){
		return Arrays.asList(this.nummericTypes);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setDescription(java.lang.String)
	 */
	public void setDescription(String sDescr){
		this.sDescription = sDescr;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getDescription()
	 */
	public String getDescription(){
		return this.sDescription;
	}
}