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
package eu.planets_project.tb.impl.model.eval;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.eval.EvaluationExecutable;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.model.ExecutableImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;

/**
 * @author Andrew Lindley, ARC
 * 
 * This class contains all information that is required for evaluation service execution 
 * (i.e. XCDL descriptions for input and output file, the comparator result, the servicetemplate, metadata, etc.). 
 * This object is handed over to the service execution and results are written 
 * back to it. i.e. corresponds to the idea of an executable part of a preservation plan
 * 
 * Please note: As service currently aren't able to take http file references as input, this
 * class holds and takes only local file refs. 
 * 
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class EvaluationExecutableImpl extends ExecutableImpl implements EvaluationExecutable, java.io.Serializable, Cloneable{

	//no one-to-one annotation, as we want to persist this data by value and not per reference
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private EvaluationTestbedServiceTemplateImpl tbServiceTemplate;
	private String sXCDLSource, sXCDLTarget, sXCDLComparison;
	
	//A Log for this - transient: it's not persisted with this entity
    @SuppressWarnings("unused")
	@Transient
    @XmlTransient
	private static Log log;
    
	public EvaluationExecutableImpl(TestbedServiceTemplate template) {
		log = LogFactory.getLog(this.getClass());
		//decouple this object
		tbServiceTemplate = ((EvaluationTestbedServiceTemplateImpl)template).clone();
		//sets the object's discriminator value to "experiment" and not "template"
		tbServiceTemplate.setDiscriminator(tbServiceTemplate.DISCR_EXPERIMENT);
	}
	
	//Default Constructor required for Entity Annotation
	public EvaluationExecutableImpl(){
	}
    
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Executable#getServiceTemplate()
	 */
	public TestbedServiceTemplate getServiceTemplate() {
		return this.tbServiceTemplate;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Executable#setServiceTemplate(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void setServiceTemplate(TestbedServiceTemplate template) {
		this.tbServiceTemplate = (EvaluationTestbedServiceTemplateImpl)template;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#getXCDLForSource()
	 */
	public String getXCDLForSource() {
		return this.sXCDLSource;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#getXCDLForTarget()
	 */
	public String getXCDLForTarget() {
		return this.sXCDLTarget;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#getXCDLsComparisonResult()
	 */
	public String getXCDLsComparisonResult() {
		return this.sXCDLComparison;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#setXCDLForSource(java.lang.String)
	 */
	public void setXCDLForSource(String xcdlXML) {
		this.sXCDLSource = xcdlXML;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#setXCDLForTarget(java.lang.String)
	 */
	public void setXCDLForTarget(String xcdlXML) {
		this.sXCDLTarget = xcdlXML;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.EvaluationExecutable#setXCDLsComparisonResult(java.lang.String)
	 */
	public void setXCDLsComparisonResult(String xml){
		this.sXCDLComparison = xml;
	}
	
	public EvaluationExecutableImpl clone(){
		EvaluationExecutableImpl executable = null;
		try{
			executable = (EvaluationExecutableImpl) super.clone();
		}catch(CloneNotSupportedException e){
			//TODO add logging statement
			System.out.println("EvaluationExecutableImpl problems cloning "+e.toString());
		}
		
		return executable;
	}

}
