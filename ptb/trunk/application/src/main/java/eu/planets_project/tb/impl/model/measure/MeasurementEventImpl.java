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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import eu.planets_project.tb.impl.model.exec.DigitalObjectRecordImpl;
import eu.planets_project.tb.impl.model.exec.InvocationRecordImpl;

/**
 * @author AnJackson
 *
 */
public class MeasurementEventImpl {

    /** If these are measurements about a service, then this is the invocation that was measured. */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    protected InvocationRecordImpl invocation;

    /** If this is about one or more digital objects, then the digital objects that were measured go here. */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<DigitalObjectRecordImpl> inputs = new HashSet<DigitalObjectRecordImpl>();

    
    /** */
    public static enum AGENT { 
        /** This measurement event was carried out by a human testbed user. */
        USER,
        /** This measurement event was carried out by a service. */
        SERVICE,
        /** This measurement event was carried out by workflow. */
        WORKFLOW
    }
    
    /** The Agent that took these measurements. */
    private AGENT agent;
    
    /** A record of the identity of the Agent, if it is a User */
    private String username = null;

    /* --------------- Measurement performed AT Stage of agent workflow ------------------ */
    // The name of this stage:
    protected String stage;
    
    // TODO AT WorkflowInput, WorkflowProcess, WorkflowOutput
    //  IN WorkflowStageIndex, WorkflowStageName, WorkflowAction
    // I think we need to record the ID of the entity that is being measured. e.g. D.O. URL
    // I think we probably need to record the Type of the entity?
    
    // FIXME Move or copy these back into the Measurement, as different things might be measured for the same target ??? */
    protected String target;
    public static final String TARGET_SERVICE = "Service";
    public static final String TARGET_DIGITALOBJECT = "Digital Object";
    public static final String TARGET_DIGITALOBJECT_DIFF = "Comparison of Two Digital Objects";
    public static final String TARGET_WORKFLOW = "Workflow";
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="event", fetch=FetchType.EAGER)
    private Set<MeasurementImpl> measurements = new HashSet<MeasurementImpl>();
    
    /**
     * @param iri
     */
    public MeasurementEventImpl(InvocationRecordImpl invocation) {
        this.invocation = invocation;
    }

    /**
     * @return the invocation
     */
    public InvocationRecordImpl getInvocation() {
        return invocation;
    }

    /**
     * @param invocation the invocation to set
     */
    public void setInvocation(InvocationRecordImpl invocation) {
        this.invocation = invocation;
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
