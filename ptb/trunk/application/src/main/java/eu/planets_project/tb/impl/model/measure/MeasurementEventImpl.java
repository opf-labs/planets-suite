/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.model.measure;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;

/**
 * @author AnJackson
 *
 */
@Entity
@XmlRootElement(name = "MeasurementEvent")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementEventImpl {
    
    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    /* --------------- Target ------------------ */
    
    /** If these are measurements about a service, then this is the invocation that was measured. */
    @ManyToOne //(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    protected ExecutionStageRecordImpl targetInvocation;

    /*
     * If the target was one or more digital object(s).
     */
    
    /** If this is about one or more digital objects, then the digital objects that were measured go here. 
     * As Data Registry URIs, stored as Strings. */
    private Vector<String> digitalObjects = new Vector<String>();
    
    /* ----------------- Context ------------ */

    /** The experiment stage */
    public static enum EXP_STAGE {
        /** Target being measured is an input to an experiment. */
        EXP_INPUT,
        /** Target being measured is during the execution of a workflow. */
        EXP_PROCESS,
        /** Target being measured is an output to an experiment */
        EXP_OUTPUT,
        /** Target is being measured outside of an experimental context. */
        NO_EXP,
    }
    private EXP_STAGE experimentStage;

    
    /* 
     * If the target was examined as part of a workflow, this can be recorded here.
     */
    
    // The name of this stage of the workflow this was invoked in, if any:
    protected String stage;
    //  IN WorkflowStageIndex, WorkflowStageName, WorkflowAction
    

    /*
     * If the target was a particular workflow invocation, then that would go here.
     */
    
    /* --------------- Agent ------------------ */
    
    /** */
    public static enum AGENT_TYPE { 
        /** This measurement event was carried out by a human testbed user. */
        USER,
        /** This measurement event was carried out by a service. */
        SERVICE,
        /** This measurement event was carried out by workflow. */
        WORKFLOW
    }
    
    // Agent is service, invoked by user?
    
    /** The Agent that took these measurements. */
    private AGENT_TYPE agentType;
    
    /** A record of the identity of the Agent, if it is a User */
    private String username = null;

    /** The date of this Event */
    private Calendar date;

    /* --------------- Measurements, as performed by Agent upon the Target ------------------ */
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="event", fetch=FetchType.EAGER)
    private Set<MeasurementImpl> measurements = new HashSet<MeasurementImpl>();

    
    /* --------------- Constructors --------------------------- */
    
    /** For JAXB */
    @SuppressWarnings("unused")
    private MeasurementEventImpl() {
    }
    
    /**
     * @param iri
     */
    public MeasurementEventImpl(ExecutionStageRecordImpl targetInvocation) {
        this.targetInvocation = targetInvocation;
        this.date = Calendar.getInstance();
    }

    /**
     * @return the invocation
     */
    public ExecutionStageRecordImpl getTargetInvocation() {
        return targetInvocation;
    }

    /**
     * @param invocation the invocation to set
     */
    public void setInvocation(ExecutionStageRecordImpl targetInvocation) {
        this.targetInvocation = targetInvocation;
    }

    /**
     * @param m2
     */
    public void addMeasurement(MeasurementImpl m) {
        this.measurements.add(m);
        m.setEvent(this);
    }

    /**
     * @return
     */
    public Set<MeasurementImpl> getMeasurements() {
        return this.measurements;
    }

    /**
     * @return
     */
    public String getWorkflowStage() {
        return this.stage;
    }

}
