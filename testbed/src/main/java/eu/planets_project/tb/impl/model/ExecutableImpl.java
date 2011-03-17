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
package eu.planets_project.tb.impl.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.model.Executable;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ExecutableImpl implements Executable, java.io.Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = -4597976985263796879L;

    @Id
	@GeneratedValue
    @XmlTransient
	private long id;
	
	private boolean bExecutionStarted = false;
	private boolean bExecutionEnded = false;
	private boolean bExecutionSuccess = false;
	private String sXMLRequest ="";
	private String sXMLResponds = "";
	protected Calendar execStartDate = null;
	protected Calendar execEndDate = null;
    /** This is a string that identifies which batch system we are running on. */
    private String batchQueueIdentifier;
    /** This is the batch-processing job identifier from whatever the batch handler is. */
    private String batchExecutionIdentifier;
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getExecutionEndDate()
	 */
	public Calendar getExecutionEndDate() {
		return this.execEndDate;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getExecutionStartDate()
	 */
	public Calendar getExecutionStartDate() {
		return this.execStartDate;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setExecutionEndDate(long)
	 */
	public void setExecutionEndDate(long timeInMillis) {
	    this.execEndDate = GregorianCalendar.getInstance();
	    this.execEndDate.setTimeInMillis(timeInMillis);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setExecutionStartDate(long)
	 */
	public void setExecutionStartDate(long timeInMillis) {
	    this.execStartDate = GregorianCalendar.getInstance();
		this.execStartDate.setTimeInMillis(timeInMillis);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getServiceXMLRequest()
	 */
	public String getServiceXMLRequest() {
		return this.sXMLRequest;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getServiceXMLResponds()
	 */
	public String getServiceXMLResponds() {
		return this.sXMLResponds;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setServiceXMLRequest(java.lang.String)
	 */
	public void setServiceXMLRequest(String xmlrequest) {
		if(xmlrequest!=null){
			this.sXMLRequest = xmlrequest;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setServiceXMLResponds(java.lang.String)
	 */
	public void setServiceXMLResponds(String xmlresponds) {
		if(xmlresponds!=null){
			this.sXMLResponds = xmlresponds;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#isExecutionCompleted()
	 */
	public boolean isExecutionCompleted(){
		return this.bExecutionEnded;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setExecutionCompleted(boolean)
	 */
	public void setExecutionCompleted(boolean b){
		this.bExecutionEnded = b;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setExecutionSuccess(boolean)
	 */
	public void setExecutionSuccess(boolean b) {
		this.bExecutionSuccess = b;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setExecutableInvoked(boolean)
	 */
	public void setExecutableInvoked(boolean b) {
		this.bExecutionStarted = b;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#isExecutableInvoked()
	 */
	public boolean isExecutableInvoked() {
		return this.bExecutionStarted;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#isExecutionSuccess()
	 */
	public boolean isExecutionSuccess() {
		return this.bExecutionSuccess;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#isExecutionRunning()
	 */
	public boolean isExecutionRunning(){
		return ((isExecutableInvoked())&&(!isExecutionCompleted()));
	}

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.Executable#getBatchExecutionIdentifier()
     */
    public String getBatchExecutionIdentifier() {
        return batchExecutionIdentifier;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.Executable#setBatchExecutionIdentifier(java.lang.String)
     */
    public void setBatchExecutionIdentifier(String batchExecutionIdentifier) {
        this.batchExecutionIdentifier = batchExecutionIdentifier;
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.Executable#getBatchSystemIdentifier()
     */
    public String getBatchSystemIdentifier() {
        return batchQueueIdentifier;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.Executable#setBatchSystemIdentifier(java.lang.String)
     */
    public void setBatchSystemIdentifier(String batchQueueIdentifier) {
        this.batchQueueIdentifier = batchQueueIdentifier;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

}
