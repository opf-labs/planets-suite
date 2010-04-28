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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.DateFormat;

import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.data.DigitalObjectCompare;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * @author AnJackson
 *
 */
@Entity
@XmlRootElement(name = "MeasurementEvent")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementEventImpl implements Serializable, Comparable<MeasurementEventImpl> {
    /** */
    private static Log log = LogFactory.getLog(MeasurementEventImpl.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 7766240403262452970L;

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** If these are measurements about a service, then this is the invocation that was measured. */
    @ManyToOne //(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @XmlTransient
    protected ExecutionStageRecordImpl targetInvocation;

    /** If these are measurements about a workflow execution, then this is the execution that was measured. */
    @ManyToOne //(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @XmlTransient
    protected ExecutionRecordImpl targetExecution;
    // FIXME This is needed, but can lead to infinite cycles, so needs to be re-constructed on Load?

    /* ----------------- Context ------------ */

    /** The date of this Event */
    private Calendar date;

    /** The experiment stage */
    public static enum EXP_STAGE {
        /** MeasurementTarget being measured is an input to an experiment. */
        EXP_INPUT,
        /** MeasurementTarget being measured is during the execution of a workflow. */
        EXP_PROCESS,
        /** MeasurementTarget being measured is an output to an experiment */
        EXP_OUTPUT,
        /** MeasurementTarget is the overall experiment execution */
        EXP_OVERALL,
        /** MeasurementTarget is being measured outside of an experimental context. */
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
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    MeasurementAgent agent = new MeasurementAgent();
    
    
    /* --------------- Measurements, as performed by Agent upon the MeasurementTarget ------------------ */
    
    //@OneToMany(cascade=CascadeType.ALL, mappedBy="event", fetch=FetchType.EAGER)
    //private Set<MeasurementImpl> measurements = new HashSet<MeasurementImpl>();
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    private Vector<MeasurementImpl> measurements = new Vector<MeasurementImpl>();

    
    /* --------------- Constructors --------------------------- */
    
    /** For JAXB */
    @SuppressWarnings("unused")
    private MeasurementEventImpl() {
    }
    
    /**
     * @param The execution this pertains to.
     */
    public MeasurementEventImpl(ExecutionRecordImpl targetExecution) {
        this.targetExecution = targetExecution;
        this.date = Calendar.getInstance();
        this.experimentStage = EXP_STAGE.EXP_OVERALL;
    }

    /**
     * @param iri
     */
    public MeasurementEventImpl(ExecutionStageRecordImpl targetInvocation) {
        this.targetInvocation = targetInvocation;
        this.date = Calendar.getInstance();
        this.experimentStage = EXP_STAGE.EXP_PROCESS;
    }
    
    /**
     * @return
     */
    public long getId() {
        return this.id;
    }

    /**
     * @return the invocation
     */
    public ExecutionStageRecordImpl getTargetInvocation() {
        return targetInvocation;
    }
    
    /**
     * @param targetInvocation the targetInvocation to set
     */
    public void setTargetInvocation(ExecutionStageRecordImpl targetInvocation) {
        this.targetInvocation = targetInvocation;
    }

    /**
     * @param m2
     */
    public void addMeasurement(MeasurementImpl m) {
        this.measurements.add(m);
//        m.setEvent(this);
    }

    /**
     * @return
     */
    public Vector<MeasurementImpl> getMeasurements() {
        return this.measurements;
    }

    /**
     * @return
     */
    public int getMeasurmentsSize() {
        if( this.measurements == null ) return 0;
        return this.measurements.size();
    }
    
    /**
     * @return
     */
    public String getWorkflowStage() {
        return this.stage;
    }

    /**
     * @return the experimentStage
     */
    public EXP_STAGE getExperimentStage() {
        return experimentStage;
    }

    /**
     * @param experimentStage the experimentStage to set
     */
    public void setExperimentStage(EXP_STAGE experimentStage) {
        this.experimentStage = experimentStage;
    }

    /**
     * @return the stage
     */
    public String getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(String stage) {
        this.stage = stage;
    }

    /**
     * @return the agentType
     */
    public MeasurementAgent getAgent() {
        return agent;
    }

    /**
     * @param agentType the agentType to set
     */
    public void setAgent(MeasurementAgent agent) {
        this.agent = agent;
    }

    /**
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }
    
    /**
     * @return The date as a briefly formatted string. Done here because f:convertDateTime does not seem to be working.
     */
    public String getShortDate() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format( date.getTime() );
    }

    /**
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * @return the targetExecution
     */
    public ExecutionRecordImpl getTargetExecution() {
        return targetExecution;
    }


    /**
     * @param invocation the invocation to set
     */
    public void setTargetExecution(ExecutionRecordImpl targetExecution) {
        this.targetExecution = targetExecution;
    }

    /* Actions */
    
    /**
     * NOTE that this may not work if the event has been updated and persisted during the same POST.
     */
    public void deleteMeasurementEvent() {
        log.info("Deleting MeasurementEvent "+this.getId());
        TestbedManagerImpl tbm = (TestbedManagerImpl) JSFUtil.getManagedObject("TestbedManager");
        // Now update experiment.
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        BatchExecutionRecordImpl batch = expBean.getExperiment().getExperimentExecutable().getBatchExecutionRecords().iterator().next();
        ExecutionRecordImpl run = batch.getRuns().iterator().next();
        
        // Remove the Event itself:
        ExperimentPersistencyRemote db = tbm.getExperimentPersistencyRemote();
        setTargetInvocation(null);
        setTargetExecution(null);
        db.removeMeasurementEvent(this);
        log.info("deleteMeasurementEvent: Removed the event, now updating the experiment");
        
        // And clip it out of the parent:
        MeasurementEventImpl toRemove = null;
        for( MeasurementEventImpl fme : run.getMeasurementEvents() ) {
            if( fme.getId() == getId()) 
                toRemove = fme;
        }
        if( toRemove != null ) {
            boolean removed = run.getMeasurementEvents().remove(toRemove);
            if( removed == false ) {
                log.error("Removing the measurement event failed!");
            }
        } else {
            log.error("Could not find event id "+this.getId()+" in the experiment's list of events.");
        }
        
        // And save the experiment:
        DigitalObjectCompare.persistExperiment();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(MeasurementEventImpl o) {
        if( this.date != null && o != null )
            return this.date.compareTo(o.date);
        return 0;
    }

}
