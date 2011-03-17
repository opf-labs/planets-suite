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
package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.BatchWorkflowResultLogImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget.TargetType;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.serialization.JaxbUtil;


/**
 * workflow processor specific actions for storing/extracting results when notified by the MDB.
 *  - responsible for parsing the WEE's result and builds up the TB specific Measurements and Events 
 * 
 * Note: The idea was that Measurements performed on Services during the Workflow Execution are stored on the ExecutionStageRecords.
 * Whereas the Analysis operations are stored as MeasurementEvents on the ExecutionRecord
 * 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 22.10.2009
 *
 */
public class WEEBatchExperimentTestbedUpdater {
	
	private static final Log log = LogFactory.getLog(WEEBatchExperimentTestbedUpdater.class);
	
	private TestbedWEEBatchProcessor tbWEEBatch;
	private ExperimentPersistencyRemote edao;
	private TestbedManager testbedMan;
	private DataHandler dh;
	private DataRegistry dataRegistry;
	
	public WEEBatchExperimentTestbedUpdater(){
		tbWEEBatch = TestbedWEEBatchProcessor.getInstance();
		edao = ExperimentPersistencyImpl.getInstance();
		testbedMan = TestbedManagerImpl.getInstance();
		dh = new DataHandlerImpl();
		dataRegistry = DataRegistryFactory.getDataRegistry();

	}
	 
	/*public void processNotify_WorkflowQueued(){
		
	}
	
	public void processNotify_WorkflowRunning(){
		
	}*/
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution. In here all the TB specific mapping is done.
	 * 	1. extract digital objects created
	 *	2. store the WorkflowResult 
	 *	3. set all the stage information
	 * @param expID
	 * @param weeWFResult
	 */
	public void processNotify_WorkflowCompleted(long expID, WorkflowResult weeWFResult){
		log.info("processing WEEBatchExperiment: processNotify_WorkflowCompleted");
		Experiment exp = testbedMan.getExperiment(expID);
		if(weeWFResult==null){
			log.info("processing WEEBatchExperiment: wfResult = null -> processing notify_WorkflowFailed");
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not available");
			return;
		}
		//create a BatchExecutionRecord
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl( (ExperimentExecutableImpl) exp.getExperimentExecutable() );
		//startTime
		Calendar c1 = new GregorianCalendar();
		c1.setTimeInMillis(weeWFResult.getStartTime());
		batchRecord.setStartDate(c1);
		
		//endTime
		Calendar c2 = new GregorianCalendar();
		c2.setTimeInMillis(weeWFResult.getEndTime());
		batchRecord.setStartDate(c2);
	
		BatchWorkflowResultLogImpl wfResultLog = new BatchWorkflowResultLogImpl();
		try {
			//try serializing the workflow result log- as this is the way it needs to be stored
			String wfResultxml = JaxbUtil.marshallObjectwithJAXB(WorkflowResult.class, weeWFResult);
			log.debug("Successfully serialized the workflowResult Log via Jaxb" );
			//store the wfResultLog in the db model bean
			wfResultLog.setSerializedWorkflowResult(wfResultxml);
		} catch (Exception e) {
			log.debug("Problems serializing wfResultLog object",e);
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not serializable");
			return;
		}
		
		batchRecord.setWorkflowExecutionLog(wfResultLog);
		batchRecord.setBatchRunSucceeded(true);
		
		//now iterate over the results and extract and store all crated digos
		List<ExecutionRecordImpl> execRecords = new ArrayList<ExecutionRecordImpl>();
		
		//group related wfResult items per input digital objects 
		Map<URI,List<WorkflowResultItem>> structuredResults = this.getAllWFResultItemsPerInputDigo(weeWFResult);
		//FIXME AL: We still need to crate empty executionRecords for the items that weren't processed by the wee (e.g. expSetup.getInputData and compare to the log)
		for(URI inputDigoURI : structuredResults.keySet()){
			int actionCounter = 0;
			ExecutionRecordImpl execRecord = new ExecutionRecordImpl(batchRecord);
			//the input Digo for all this information is about
			// FIXME This appears to be the resolved URI, not the proper Planets DR URI:
			execRecord.setDigitalObjectReferenceCopy(inputDigoURI+"");
			Properties p = new Properties();
			//iterate over the results and document the migration action - all other information goes into properties.
			for(WorkflowResultItem wfResultItem : structuredResults.get(inputDigoURI)){
				
				//1. check if this record was about the migration action
				String action = wfResultItem.getSActionIdentifier();
				if(action.startsWith(WorkflowResultItem.SERVICE_ACTION_MIGRATION)){
					URI outputDigoRef = wfResultItem.getOutputDigitalObjectRef();
					if(outputDigoRef!=null){
						//DigitalObject outputDigo = dataRegistry.retrieve(outputDigoRef);
						//1.a download the ResultDigo into the TB and store it's reference - if it's the final migration producing the output object
						if(action.equals(WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION)){
							//documenting the final output object
							URI tbUri = execRecord.setDigitalObjectResult(outputDigoRef, exp);
							//FIXME: currently not possible to mix DIGO and PROPERTY result: 
							p.put(ExecutionRecordImpl.RESULT_PROPERTY_URI, tbUri.toString());
						}
						else{
						//1.b documenting the interim results in a multi-migration-workflow
							//DataHandler dh = new DataHandlerImpl();
					        //URI tbUri = dh.storeDigitalObject(outputDigo, exp);
					        p.put(ExecutionRecordImpl.RESULT_PROPERTY_INTERIM_RESULT_URI+"["+actionCounter+"]", outputDigoRef.toString());
						}
						Calendar start = new GregorianCalendar();
						start.setTimeInMillis(wfResultItem.getStartTime());
                        execRecord.setStartDate(start);
						Calendar end = new GregorianCalendar();
						end.setTimeInMillis(wfResultItem.getEndTime());
                        execRecord.setEndDate(end);
					}
				}
			
				//1b. every service action gets persisted as a stage record
				ExecutionStageRecordImpl stageRecord = fillInExecutionStageRecord(wfResultItem,actionCounter,execRecord,action,exp.getEntityID());
	            execRecord.getStages().add(stageRecord);
				
				//2. or about some general reporting information
				if(action.startsWith(WorkflowResultItem.GENERAL_WORKFLOW_ACTION)){
					execRecord.setReportLog(this.parseReportLog(wfResultItem));
				}

				//3. document all other metadata for actions: identification, etc. as properties over all actions
				try{
					this.updateProperties(actionCounter, p, wfResultItem);
				}catch(Exception e){
					log.error("processing WEEBatchExperiment: Problems crating execution record properties for a workflowResultItem "+e);
				}
				actionCounter++;
			}
			try {
				execRecord.setPropertiesListResult(p);
			} catch (IOException e) {
				log.debug("processing WEEBatchExperiment: Problem adding properties to executionRecord: "+e);
			}
			
			//got all information - now add the record for this inputDigo
			log.info("processing WEEBatchExperiment: Adding an execution record: "+inputDigoURI);
			execRecords.add(execRecord);
		}
		batchRecord.setRuns(execRecords);

		this.helperUpdateExpWithBatchRecord(exp, batchRecord);
	}
	
	/**
	 * All actions of mapping/saving WorkflowResult object into the Testbed's db model for a 
	 * completed workflow execution
	 * @param expID
	 * @param failureReason
	 */
	public void processNotify_WorkflowFailed(long expID,String failureReason){
		Experiment exp = testbedMan.getExperiment(expID);
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl((ExperimentExecutableImpl)exp.getExperimentExecutable());
		batchRecord.setBatchRunSucceeded(false);
		
		this.helperUpdateExpWithBatchRecord(exp, batchRecord);
		//TODO AL: any more fields/events/measurements to extract?
	}
	
	/**
	 * All actions of setting an experiment into state 'processing has started'
	 * @param expID
	 */
	public void processNotify_WorkflowStarted(long expID){
		Experiment exp = testbedMan.getExperiment(expID);
    	exp.getExperimentExecutable().setExecutableInvoked(true);
    	if ( exp.getExperimentExecutable().getBatchExecutionRecords() != null ) {
            log.info("processNotify_WorkflowStarted: Updating the experiment 'started': #"+exp.getExperimentExecutable().getBatchExecutionRecords().size());
    	} else {
            log.info("processNotify_WorkflowStarted: Updating the experiment 'started': "+exp.getExperimentExecutable().getBatchExecutionRecords());
    	}
    	//testbedMan.updateExperiment(exp);
    	edao.updateExperiment(exp);
	}
	
	private void helperUpdateExpWithBatchRecord(Experiment exp,BatchExecutionRecordImpl record){
        if ( exp.getExperimentExecutable().getBatchExecutionRecords() != null ) {
            log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: #"+exp.getExperimentExecutable().getBatchExecutionRecords().size());
            if ( exp.getExperimentExecutable().getBatchExecutionRecords().size() > 0 )
                log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: #"+exp.getExperimentExecutable().getBatchExecutionRecords().iterator().next().getRuns().size());
        } else {
            log.info("helperUpdateExpWithBatchRecord: Adding new BatchExecutionRecord to this Experiment: "+exp.getExperimentExecutable().getBatchExecutionRecords());
        }
    	exp.getExperimentExecutable().getBatchExecutionRecords().add(record);
		exp.getExperimentExecutable().setExecutionCompleted(true);
        exp.getExperimentExecution().setState(Experiment.STATE_COMPLETED);
        exp.getExperimentEvaluation().setState(Experiment.STATE_IN_PROGRESS);   
		//testbedMan.updateExperiment(exp);
		edao.updateExperiment(exp);
	}
	
	
	/**
	 * Takes a Wee WorkflowResult object and creates a map with DigoPermanentURI of the inputDigital object
	 * and all of it's WorkflowResultItems that were created. Please note: it does not take the InputDigos the TB submitted the job with,
	 * but all object's that were recorded in the WorkflowResultItem.setInputDigitalObject and groups them by 
	 * the getAboutExecutionDigoRef()
	 * @param wfResult
	 * @param digoPermURI
	 * @return
	 */
	private Map<URI,List<WorkflowResultItem>> getAllWFResultItemsPerInputDigo(WorkflowResult wfResult){
		//the structure: Map<InputDigoPermanentURI, List<WorkflowResultItem>>
		Map<URI,List<WorkflowResultItem>> ret = new HashMap<URI,List<WorkflowResultItem>>();
		for(WorkflowResultItem wfResultItem : wfResult.getWorkflowResultItems()){
			
			URI digoCalledInExecute = wfResultItem.getAboutExecutionDigoRef();
			//the permanentURI is the reference for the TB stored Digos when submitting
			if(digoCalledInExecute!=null){
				//check if we already extracted any information for this digo
				if(!ret.keySet().contains(digoCalledInExecute)){
					//create new record for this digo
					List<WorkflowResultItem> resItems = new ArrayList<WorkflowResultItem>();
					ret.put(digoCalledInExecute, resItems);
				}

				//now update the return object
				ret.get(digoCalledInExecute).add(wfResultItem);
			}
		}
		return ret;
	}
	
	
	/**
	 * Properties contain screen readable information for stage5 for a given ExecutionRecord
	 * @param execRecord
	 * @param wfResultItem
	 * @return
	 * @throws IOException
	 */
	private Properties updateProperties(int count, Properties p, WorkflowResultItem wfResultItem) throws IOException{

		//create a property name that has the action identifier as part of it.
		if((wfResultItem.getServiceEndpoint()!=null)&&(!wfResultItem.getServiceEndpoint().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceEndpoint+"["+count+"]", wfResultItem.getServiceEndpoint());
		}
		if((wfResultItem.getLogInfo()!=null)&&(wfResultItem.getLogInfo().size()>0)){
			p.setProperty(ExecutionRecordImpl.WFResult_LOG+"["+count+"]", wfResultItem.getLogInfo().toString());
		}
		if((wfResultItem.getSActionIdentifier()!=null)&&(!wfResultItem.getSActionIdentifier().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionIdentifier+"["+count+"]", wfResultItem.getSActionIdentifier());
		}
		if((wfResultItem.getServiceParameters()!=null)&&(wfResultItem.getServiceParameters().size()>0)){
			String sFormatted="";
			for(Parameter sp: wfResultItem.getServiceParameters()){
				sFormatted+="["+sp.getName()+" = "+sp.getValue()+"] ";
			}
			p.setProperty(ExecutionRecordImpl.WFResult_Parameters+"["+count+"]", sFormatted);
		}
		if((wfResultItem.getExtractedInformation()!=null)&&(wfResultItem.getExtractedInformation().size()>0)){
			p.setProperty(ExecutionRecordImpl.WFResult_ExtractedInformation+"["+count+"]", wfResultItem.getExtractedInformation().toString());
		}
		if(wfResultItem.getStartTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionStartTime+"["+count+"]", wfResultItem.getStartTime()+"");
		}
		if(wfResultItem.getEndTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionEndTime+"["+count+"]", wfResultItem.getEndTime()+"");
		}
		if(wfResultItem.getServiceReport()!=null){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceReport+"["+count+"]", wfResultItem.getServiceReport().toString());
		}
		if(wfResultItem.getServiceDescription()!=null){
			p.setProperty(ExecutionRecordImpl.WFResult_ServiceDescription+"["+count+"]", wfResultItem.getServiceDescription().toString());
		}
		return p;
	}
	
	/**
	 * Looks at the given set of extracted properties (for a given input digo)
	 * and builds up the ResultLog if available. (i.e. log on how the workflow was processed on this item, 
	 * e.g. A->B, B->C, C did not terminate properly
	 * @param p
	 * @return
	 */
	private List<String> parseReportLog(WorkflowResultItem wfResultItem){
		List<String> ret = new ArrayList<String>();
		if((wfResultItem.getSActionIdentifier()!=null)&&
		   (wfResultItem.getSActionIdentifier().startsWith(WorkflowResultItem.GENERAL_WORKFLOW_ACTION))){
			ret = wfResultItem.getLogInfo();
		}
		return ret;
	}
	
	/**
	 * Takes a workflow result item and filles in the TB's ExecutionStageRecord from it.
	 * @param wfResultItem: the workflow result item we're building the execution stage record for
	 * @param execRecord the parent record that takes the overall workflow's result
	 * @param stageName a stage name to store this information for
	 * @return
	 */
	private ExecutionStageRecordImpl fillInExecutionStageRecord(WorkflowResultItem wfResultItem, int actionCounter, ExecutionRecordImpl execRecord, String stageName, long eid){
		 ExecutionStageRecordImpl stage = new ExecutionStageRecordImpl(execRecord,"["+actionCounter+"] "+stageName);
		 //TODO: AL: for now just filling in the endpoint and serviceRecord information
         try {
        	 //1. set the stage's endpoint
			 stage.setEndpoint(new URL(wfResultItem.getServiceEndpoint()));
			 
			 //2. create the service record information
            if( stage.getEndpoint() != null ) {
                log.info("Recording info about endpoint: "+stage.getEndpoint());
                Calendar exectime = new GregorianCalendar();
                exectime.setTimeInMillis(wfResultItem.getStartTime());
                stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( eid, stage.getEndpoint(), exectime ) );
            }
		} catch (MalformedURLException e) {
			log.debug("can't set stage's endpoint."+e);
		}
		
		//3. record the wfResultItems's information as Testbed Measurements (e.g. extractedInformation, executionTimes, etc.)
		recordMeasurements(stage, stageName, wfResultItem);
		
		return stage;
		
	}
	
	private void recordMeasurements(ExecutionStageRecordImpl stage, String actionIdentifier, WorkflowResultItem wfResultItem){
		log.debug("extracting Measurements for stage-record: "+stage.getStage());
		
		if(actionIdentifier.equals(WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION)){
			createMeasurementAboutIdentification(stage,wfResultItem.getInputDigitalObjectRef()+"", wfResultItem.getExtractedInformation());
		}
		if((actionIdentifier.equals(WorkflowResultItem.SERVICE_ACTION_MIGRATION))||(stage.getStage().equals(WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION))){
			createMeasurementAboutMigration(stage, wfResultItem.getInputDigitalObjectRef()+"", wfResultItem.getOutputDigitalObjectRef()+"", wfResultItem);
		}
		if(actionIdentifier.equals(WorkflowResultItem.SERVICE_ACTION_COMPARE)){
			createMeasurementAboutComparison(stage, wfResultItem.getInputDigitalObjectRef()+"", wfResultItem.getOutputDigitalObjectRef()+"", wfResultItem.getExtractedInformation());
		}
		if(actionIdentifier.equals(WorkflowResultItem.GENERAL_WORKFLOW_ACTION)){
			//TODO decide what to pull out here
		}else{
			//record the service execution time
			createMeasurementAboutServiceExecTime(stage,wfResultItem.getStartTime(), wfResultItem.getEndTime(),wfResultItem.getInputDigitalObjectRef()+"", wfResultItem.getOutputDigitalObjectRef()+"");

		}
	}
	
	/**
	 * Takes information about an extracted identification workflow operation and updates the Testbed's Measurement model
	 * @param mev
	 * @param dobURI1: the digital object URI the measurement was about
	 * @param value: the extracted value
	 */
	private void createMeasurementAboutIdentification(ExecutionStageRecordImpl execStageRec, String dobURI1, List<String> extractedInformation){
		log.debug("extracting Measurement about identification operation for digo: "+dobURI1);
		//This encapsulates a reference to the entity that the measurement belongs to.
        MeasurementTarget target = new MeasurementTarget();
        target.setType(TargetType.DIGITAL_OBJECT);
        target.getDigitalObjects().add(0, dobURI1);
        
        //add the extracted information
		for(String value : extractedInformation){
	    	MeasurementImpl m = new MeasurementImpl();
	    	String actionIdentifier = WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION;
	        Property p = new Property.Builder(helperCreatePropertyURI(actionIdentifier)).value(value).description("Planets "+actionIdentifier+" Service Operation Result").name(actionIdentifier).build(); 
	    	m.setProperty(p);
	        //m.setMeasurementType( MeasurementType.DOB);
	        m.setTarget(target);
	        execStageRec.addMeasurement(m);
	    }
	}
	
	private void createMeasurementAboutServiceExecTime(ExecutionStageRecordImpl execStageRec, long start, long end, String inputDigoRef, String outputDigoRef){
		MeasurementTarget target = new MeasurementTarget();
        target.setType(TargetType.SERVICE);
        Vector<String> aboutObjects = new Vector<String>();
        if(inputDigoRef!=null)
        	aboutObjects.add(inputDigoRef);
        if(outputDigoRef!=null)
        	aboutObjects.add(outputDigoRef);
        if(aboutObjects.size()>0)
        	target.setDigitalObjects(aboutObjects);
        
        URI execStartURI = URI.create("planets://workflow/service/execution/start");
        URI execEndURI = URI.create("planets://workflow/service/execution/end");
        Property pStart = new Property.Builder(execStartURI).value(start+"").description("Planets Service Wrapper Execution start-time measured in milli-seconds").name("service execution start time").build(); 
        Property pEnd = new Property.Builder(execEndURI).value(end+"").description("Planets Service Wrapper Execution end-time measured in milli-seconds").name("service execution end time").build(); 
       
        MeasurementImpl mStart = new MeasurementImpl();
        //mStart.setMeasurementType( MeasurementType.SERVICE);  
        mStart.setProperty(pStart);
        mStart.setTarget(target);
        execStageRec.addMeasurement(mStart);
        
        MeasurementImpl mEnd = new MeasurementImpl();
        //mEnd.setMeasurementType( MeasurementType.SERVICE);  
        mEnd.setProperty(pEnd);
        mEnd.setTarget(target);
        execStageRec.addMeasurement(mEnd);
	}
	
	private URI helperCreatePropertyURI(String propName){
		return URI.create("planets://workflow/"+propName);
	}
	
	private void createMeasurementAboutMigration(ExecutionStageRecordImpl execStageRec, String inputDigoRef, String outputDigoRef, WorkflowResultItem wfResItem) {
		//extract the information about the system's memory, etc.
		if((wfResItem.getServiceReport()!=null)&&(wfResItem.getServiceReport().getProperties()!=null)){
			
			MeasurementTarget target = new MeasurementTarget();
	        target.setType(TargetType.SERVICE);
	        Vector<String> aboutObjects = new Vector<String>();
	        if(inputDigoRef!=null)
	        	aboutObjects.add(inputDigoRef);
	        if(outputDigoRef!=null)
	        	aboutObjects.add(outputDigoRef);
	        if(aboutObjects.size()>0)
	        	target.setDigitalObjects(aboutObjects);
	        
			for(Property p : wfResItem.getServiceReport().getProperties()){
				MeasurementImpl m = new MeasurementImpl();
		        //m.setMeasurementType( MeasurementType.SERVICE); 
		        m.setProperty(p);
		        m.setTarget(target);
		        execStageRec.addMeasurement(m);
			}
		}
	}
	
    private void createMeasurementAboutComparison(ExecutionStageRecordImpl execStageRec, String inputDigoRef, String outputDigoRef, List<String> extractedInformation) {
    	MeasurementTarget target = new MeasurementTarget();
        target.setType(TargetType.SERVICE);
        Vector<String> aboutObjects = new Vector<String>();
        if(inputDigoRef!=null)
        	aboutObjects.add(inputDigoRef);
        if(outputDigoRef!=null)
        	aboutObjects.add(outputDigoRef);
        if(aboutObjects.size()>0)
        	target.setDigitalObjects(aboutObjects);
    	
    	//add the extracted information
		for(String value : extractedInformation){
	    	MeasurementImpl m = new MeasurementImpl();
	    	String actionIdentifier = WorkflowResultItem.SERVICE_ACTION_COMPARE;
	        Property p = new Property.Builder(helperCreatePropertyURI(actionIdentifier)).value(value).description("Planets "+actionIdentifier+" Service Operation Result").name(actionIdentifier).build(); 
	    	m.setProperty(p);
	        //m.setMeasurementType( MeasurementType.DOB);
	        m.setTarget(target);
	        execStageRec.addMeasurement(m);
	    }
    }
	

    /**
     * creates an event that's about the workflow in general
     * @param execRec
     * @return
     */
    /*private MeasurementEventImpl createGeneralWFMeasurementEvent(ExecutionRecordImpl execRec) {
        MeasurementEventImpl me = new MeasurementEventImpl(execRec);
        MeasurementAgent agent = new MeasurementAgent(this.getWEEAgent(),MeasurementAgent.AgentType.WORKFLOW);
        execRec.getMeasurementEvents().add(me);
        return me;
    }*/
    
    /**
     * creates an event that's about a workflow service
     * @param execStageRec
     * @return
     */
    /*private MeasurementEventImpl createWFServiceMeasurementEvent(ExecutionStageRecordImpl execStageRec) {
    	MeasurementEventImpl me = new MeasurementEventImpl(execStageRec);
    	MeasurementAgent agent = new MeasurementAgent(this.getServiceAgent(),MeasurementAgent.AgentType.SERVICE);
    	me.setAgent(agent);
    	execStageRec.getMeasurementEvents().add(me);
    	return me;
    }*/
    
    /*private Agent getWEEAgent(){
    	Agent agentWEE = new Agent("Planets-WEE-v1.0", "The Planets Workflow Execution Engine", "planets://workflow/processor");
    	return agentWEE;
    }
    
    private Agent getServiceAgent(){
    	Agent agentService = new Agent("Planets-Service", "A Planets Service called by the Planets Workflow Execution Engine", "planets://workflow/service");
    	return agentService;
    }*/
    
    
	/*private ExecutionRecordImpl createExecutionRecordToExperiment(long eid, WorkflowResult wfr, String filename) {
    DataHandler dh = new DataHandlerImpl();
    try {
        ExecutionRecordImpl rec = new ExecutionRecordImpl();
        rec.setDigitalObjectReferenceCopy(filename);
        try {
            rec.setDigitalObjectSource(dh.get(filename).getName());
        } catch (FileNotFoundException e) {
            rec.setDigitalObjectSource(filename);
        }
        // FIXME Set this in the job somewhere:
        rec.setDate(Calendar.getInstance());
        List<ExecutionStageRecordImpl> stages = rec.getStages();
        
        if( wfr != null && wfr.getStages() != null ) {
            // Examine the result:
            if( WorkflowResult.RESULT_DIGITAL_OBJECT.equals(wfr.getResultType())) {
                rec.setDigitalObjectResult( (DigitalObject) wfr.getResult(), exp );
                
            } else if(WorkflowResult.RESULT_CREATEVIEW_RESULT.equals(wfr.getResultType()) ) {
                CreateViewResult cvr = (CreateViewResult) wfr.getResult( );
                Properties vp = new Properties();
                vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_SESSION_ID, cvr.getSessionIdentifier());
                vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_VIEW_URL, cvr.getViewURL().toString());
                vp.setProperty(ExecutionRecordImpl.RESULT_PROPERTY_CREATEVIEW_ENDPOINT_URL, wfr.getMainEndpoint().toString() );
                rec.setPropertiesListResult(vp);
                
            } else {
                rec.setResultType(ExecutionRecordImpl.RESULT_MEASUREMENTS_ONLY);
            }
            
            // Now pull out the stages, which include the measurements etc:
            for( ExecutionStageRecordImpl stage : wfr.getStages() ) {
                // FIXME Can this be done from the session's Service Registry instead, please!?
                if( stage.getEndpoint() != null ) {
                    log.info("Recording info about endpoint: "+stage.getEndpoint());
                    stage.setServiceRecord( ServiceBrowser.createServiceRecordFromEndpoint( eid, stage.getEndpoint(), Calendar.getInstance() ) );
                }
                // Re-reference this stage object from the Experiment:
                stages.add(stage);
            }
        }

        batch.getRuns().add(rec);
        log.info("Added records ("+batch.getRuns().size()+") for "+rec.getDigitalObjectSource());
    } catch( Exception e ) {
        log.error("Exception while parsing Execution Record.");
        e.printStackTrace();
    }
    
}*/
}

