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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import eu.planets_project.tb.impl.model.exec.InvocationRecordImpl;

/**
 * @author AnJackson
 *
 */
public class MeasurementEventImpl {
    
    /* --------------- Target ------------------ */
    
    /** If these are measurements about a service, then this is the invocation that was measured. */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    protected InvocationRecordImpl targetInvocation;

    /*
     * If the target was one or more digital object(s).
     */
    
    /** If this is about one or more digital objects, then the digital objects that were measured go here. 
     * As Data Registry URIs, stored as Strings. */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<String> digitalObjects = new HashSet<String>();
    
    /* ----------------- Context ------------ */

    /** The experiment stage */
    public static enum EXP_STAGE {
        /** Target being measured is an input to an experiment. */
        EXP_INPUT,
        /** Target being measured is during the execution of a workflow. */
        EXP_PROCESS,
        /** Target being measured is an output to an experiment */
        EXP_OUTPUT,
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
    
    /**
     * @param iri
     */
    public MeasurementEventImpl(InvocationRecordImpl targetInvocation) {
        this.targetInvocation = targetInvocation;
        this.date = Calendar.getInstance();
    }

    /**
     * @return the invocation
     */
    public InvocationRecordImpl getTargetInvocation() {
        return targetInvocation;
    }

    /**
     * @param invocation the invocation to set
     */
    public void setInvocation(InvocationRecordImpl targetInvocation) {
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
