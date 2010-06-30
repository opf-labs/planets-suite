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
package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.view.ViewResultBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ViewerWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpTypeViewer extends ExpTypeBackingBean {
    private Log log = LogFactory.getLog(ExpTypeViewer.class);
    
    /**
     * @return the viewerService
     */
    public String getViewerService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        log.info("Got params: "+expBean.getExperiment().getExperimentExecutable().getParameters() );
        log.info("Got param: "+expBean.getExperiment().getExperimentExecutable().getParameters().get(ViewerWorkflow.PARAM_SERVICE) );
        return expBean.getExperiment().getExperimentExecutable().getParameters().get(ViewerWorkflow.PARAM_SERVICE);
    }

    /**
     * @param viewerService the viewerService to set
     */
    public void setViewerService(String viewerService) {
        log.info("Setting the Viewer service to: "+viewerService);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(ViewerWorkflow.PARAM_SERVICE, viewerService);
        expBean.updateExperiment();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getStageBeans()
     */
    @Override
    public List<ExperimentStageBean> getStageBeans() {
        return getWorkflow(AdminManagerImpl.EMULATE).getStages();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return getWorkflow(AdminManagerImpl.EMULATE).getObservables();
    }
    
    HashMap<String,List<MeasurementImpl>> manualObsCache;
    long cacheExperimentID;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getManualObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getManualObservables() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if(manualObsCache==null||(cacheExperimentID != expBean.getExperiment().getEntityID())){
    		cacheExperimentID = expBean.getExperiment().getEntityID();
    		
        	//query for properties that have been added from the Ontology
        	HashMap<String,Vector<String>> ontoPropIDs = new HashMap<String, Vector<String>>();
        	for(ExperimentStageBean stage : expBean.getStages()){
        		ontoPropIDs.put(stage.getName(),expBean.getExperiment().getExperimentExecutable().getManualProperties(stage.getName()));
        	}
        	
        	//this is the static list of manual properties - normally empty
        	HashMap<String,List<MeasurementImpl>> staticWFobs = getWorkflow(AdminManagerImpl.EMULATE).getManualObservables();
        	
        	//FIXME AL: staticWFobs returns wrong items - where are they added - exclude staticWFobs for now
        	//manualObsCache = mergeManualObservables(staticWFobs, ontoPropIDs);
        	manualObsCache = mergeManualObservables(null, ontoPropIDs);
    	}
    	return manualObsCache;
    }
    
    /* ------------------------------------------- */
    
    /**
     * A Bean to hold the results on each digital object.
     */
    public class CreateViewResultsForDO  extends ResultsForDigitalObjectBean {

        List<ViewResultBean> vurl = new ArrayList<ViewResultBean>();

        /**
         * @param input
         */
        public CreateViewResultsForDO(String input) {
            super(input);
            vurl = ViewResultBean.createResultsFromExecutionRecords( this.getExecutionRecords() );
        }
        
        
        /**
         * 
         * @return
         */
        public List<ViewResultBean> getViewResults() {
            return vurl;
        }
        
        
    }
    
    /**
     * @return
     */
    public List<CreateViewResultsForDO> getViewResults() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<CreateViewResultsForDO> results = new Vector<CreateViewResultsForDO>();
        // Populate using the results:
        for( String file : expBean.getExperimentInputData().values() ) {
            CreateViewResultsForDO res = new CreateViewResultsForDO(file);
            results.add(res);
        }

        // Now return the results:
        return results;
    }

    /* 
	 * TODO AL: version 1.0 uses this structure to check for a valid workflow (exp-type specific) configuration.
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#checkExpTypeBean_Step2_WorkflowConfigurationOK()
	 */
    @Override
    public void checkExpTypeBean_Step2_WorkflowConfigurationOK() throws Exception{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#isExperimentBeanType()
	 */
	@Override
	public boolean isExperimentBeanType() {
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) return false;
		if( AdminManagerImpl.EMULATE.equals(expBean.getEtype()) ) return true;
		return false;
	}

	/*
	 * TODO AL: version 1.0: adapt this when porting this exp type to the WEE
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getWorkflowParameters()
	 */
	@Override
	public Map<String, List<Parameter>> getWorkflowParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * TODO AL: version 1.0: adapt this when porting this exp type to the WEE
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#setWorkflowParameters(Map<String,List<Parameter>> params)
	 */
	@Override
	public void setWorkflowParameters(Map<String,List<Parameter>> params) {
		// TODO Auto-generated method stub
		
	}
    
    
}
