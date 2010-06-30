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
package eu.planets_project.tb.gui.backing.exp.view;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.view.CreateView;
import eu.planets_project.services.view.ViewStatus;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.properties.ManuallyMeasuredProperty;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.data.DigitalObjectCompare;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.backing.exp.utils.ManualMeasurementBackingBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementAgent;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementAgent.AgentType;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.properties.ManuallyMeasuredPropertyHandlerImpl;


/**
 * 
 * A request-scope bean that handles inspection of a view.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class EmulationInspector {
    /** */
    private static final Log log = LogFactory.getLog(EmulationInspector.class);

    private String experimentId;
    
    private String sessionId;
    
    private Experiment experiment;
    
    private ViewResultBean vrb;
    
    private CreateView viewService = null;
    
    private ViewStatus.Status viewStatus = ViewStatus.Status.UNKNOWN;
    
    private String newManVal;
    
    /**
     * @return the experimentId
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId the experimentId to set
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
        log.info("Setting eid = "+experimentId);
        // Lookup experiment and add
        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        this.experiment = edao.findExperiment( Long.parseLong( this.experimentId ) );
        this.vrb = null;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        log.info("Setting sid = "+sessionId);
        this.sessionId = sessionId;
        this.vrb = null;
    }
    
    /**
     * @return
     */
    public boolean isValid() {
        // Check the session for a view URL:
        if( this.getViewResultBean() == null ) {
            return false;
        }
        return true;
    }
    
    /**
     * @return
     */
    public ViewResultBean getViewResultBean() {
        if( this.vrb == null ) this.initViewResultBean();
        return this.vrb;
    }
    
    /** */
    private void initViewResultBean() {
        if( this.experiment == null ) return;
        if( this.sessionId == null || "".equals(this.sessionId)) {
            return;
        }
        // Look up this session id in the experiment:
        List<ExecutionRecordImpl> executionRecords = new ArrayList<ExecutionRecordImpl>();
        for( BatchExecutionRecordImpl batch : experiment.getExperimentExecutable().getBatchExecutionRecords() ) {
            for( ExecutionRecordImpl run : batch.getRuns() ) {
                    executionRecords.add(run);
            }
        }
        List<ViewResultBean> vrbs = ViewResultBean.createResultsFromExecutionRecords(executionRecords);
        // Look for a match:
        for( ViewResultBean vrb : vrbs ) {
            if( this.sessionId.equals(vrb.getSessionId())) {
                this.vrb = vrb;
            }
        }
        
        // Also fire up the connection to the service:
        try {
          Service serv = Service.create(vrb.getEndpoint(), CreateView.QNAME);
          this.viewService = serv.getPort(CreateView.class);
        } catch( Exception e ){
            e.printStackTrace();
        }
    }
    
    /**
     * @return
     */
    public String getViewTitle() {
        if(  this.isValid() ) {
            return "Planets Testbed Emulation Experiment: "+this.getExperimentName();
        } else {
            return "Planets Testbed Emulation Experiment: No view found.";
        }
    }
    
    /**
     * @return
     */
    public String getExperimentName() {
        if(  this.isValid() ) {
            return experiment.getExperimentSetup().getBasicProperties().getExperimentName();
        } else {
            return "";
        }
    }
    
    /**
     * @return
     */
    public ViewStatus.Status getViewStatus() {
        if( this.viewService == null ) {
            this.viewStatus = ViewStatus.Status.UNKNOWN;
            return this.viewStatus;
        }
        // Otherwise, probe:
        try {
            ViewStatus status = this.viewService.getViewStatus(this.sessionId);
            this.viewStatus = status.getState();
            return this.viewStatus;
        } catch( Exception e ) {
            e.printStackTrace();
            this.viewStatus = ViewStatus.Status.UNKNOWN;
            return this.viewStatus;
        }
        
    }
    
    /**
     * Controller to issue a shutdown request.
     * @return
     */
    public String doShutdownAction() {
        log.info("Issuing shutdown request on session"+this.sessionId);
        return "success";
    }

    /**
     * @return
     */
    public boolean isViewActive() {
        return this.viewStatus.equals(ViewStatus.Status.ACTIVE);
    }
    
    /**
     * @return
     */
    public boolean isViewInactive() {
        return this.viewStatus.equals(ViewStatus.Status.INACTIVE);
    }
    
    /**
     * @return
     */
    public boolean isViewUnknown() {
        return this.viewStatus.equals(ViewStatus.Status.UNKNOWN);
    }

    /**
     * @return the propertyPanelEnabled
     */
    public boolean isPropertyPanelEnabled() {
        Boolean enab = (Boolean) FacesContext.getCurrentInstance().getExternalContext()
        .getSessionMap().get("ManualPropertyPanelEnabled");
        if( enab == null ) return false;
        return enab;
    }

    /**
     * @param propertyPanelEnabled the propertyPanelEnabled to set
     */
    public void setPropertyPanelEnabled(boolean propertyPanelEnabled) {
        FacesContext.getCurrentInstance().getExternalContext()
        .getSessionMap().put("ManualPropertyPanelEnabled", propertyPanelEnabled);
    }
    
    /**
     * @return
     */
    private ExecutionRecordImpl getExecutionRecordForSessionId() {
        ExperimentInspector ei = (ExperimentInspector)JSFUtil.getManagedObject("ExperimentInspector");
        Experiment exp = ei.getExperimentBean().getExperiment();
        if(  this.isValid() && exp.getExperimentExecutable().getNumBatchExecutionRecords() > 0 ) {
            for( ExecutionRecordImpl exec : exp.getExperimentExecutable().getBatchExecutionRecords().iterator().next().getRuns() ) {
                ViewResultBean vrb = ViewResultBean.createViewResultBeanFromExecutionRecord(exec);
                if( vrb != null ) {
                    if( this.getSessionId().equals( vrb.getSessionId() )) {
                        return exec;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return
     */
    public MeasurementEventImpl getManualMeasurementEvent() {
        MeasurementEventImpl me = null;
        ExecutionRecordImpl res = this.getExecutionRecordForSessionId();
        if( res == null ) return null;
        Set<MeasurementEventImpl> measurementEvents = res.getMeasurementEvents();
        for( MeasurementEventImpl mee : measurementEvents ) {
            if( mee.getAgent() != null && mee.getAgent().getType() == AgentType.USER ) {
                me = mee;
            }
        }
        // If none, create one and pass it back.
        if( me == null ) {
            log.info("Creating Manual Measurement Event.");
            me = new MeasurementEventImpl(res);
            res.getMeasurementEvents().add(me);
            UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
            me.setAgent( new MeasurementAgent( user ));
            ExperimentPersistencyImpl.getInstance();
            ExperimentInspector.persistExperiment();
        }
        me.getMeasurements();
        return me;
    }
    
    /**
     * @return the newManVal
     */
    public String getNewManVal() {
        return newManVal;
    }

    /**
     * @param newManVal the newManVal to set
     */
    public void setNewManVal(String newManVal) {
        this.newManVal = newManVal;
    }

    /**
     */
    public void storeManualMeasurement() {
        // Look up the definition:
        ManuallyMeasuredPropertyHandlerImpl mm = ManuallyMeasuredPropertyHandlerImpl.getInstance();
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        ManualMeasurementBackingBean mmbb = (ManualMeasurementBackingBean)JSFUtil.getManagedObject("ManualMeasurementBackingBean");
        List<ManuallyMeasuredProperty> mps = mm.loadAllManualProperties(user.getUserid());
        ManuallyMeasuredProperty mp = null;
        for( ManuallyMeasuredProperty amp : mps ) {
            if( amp.getURI().equals(mmbb.getNewManProp()) ) mp = amp;
        }
        if( mp == null ) {
            log.error("No property ["+mmbb.getNewManProp()+"] found!");
            return;
        }
        // Lookup the event:
        MeasurementEventImpl mev = this.getManualMeasurementEvent();
        // Make the property
        Property p = new Property.Builder( URI.create(mp.getURI()) ).description(mp.getDescription()).name(mp.getName()).build();
        DigitalObjectCompare.createMeasurement(mev, p, this.sessionId, this.newManVal );
        ExperimentInspector.persistExperiment();
    }
    
    /**
     * Actions
     */
    
    /**
     * 
     */
    public void disablePropertyPanel() {
        this.setPropertyPanelEnabled( false );
    }
    
    /**
     * 
     */
    public void enablePropertyPanel() {
        this.setPropertyPanelEnabled( true );
    }
    
}
