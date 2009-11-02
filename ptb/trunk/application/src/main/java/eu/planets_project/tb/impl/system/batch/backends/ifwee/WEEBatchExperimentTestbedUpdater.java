package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.BatchWorkflowResultLogImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.serialization.JaxbUtil;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;


/**
 * workflow processor specific actions for storing/extracting results when notified by the MDB.
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
	
	public WEEBatchExperimentTestbedUpdater(){
		tbWEEBatch = TestbedWEEBatchProcessor.getInstance();
		edao = ExperimentPersistencyImpl.getInstance();
		testbedMan = TestbedManagerImpl.getInstance();
		dh = new DataHandlerImpl();

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
		Experiment exp = testbedMan.getExperiment(expID);
		if(weeWFResult==null){
			this.processNotify_WorkflowFailed(expID, "WorkflowResult not available");
			return;
		}
		//create a BatchExecutionRecord
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl();
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
		
		Map<URI,List<WorkflowResultItem>> structuredResults = this.getAllWFResultItemsPerInputDigo(weeWFResult);
		//FIXME AL: We still need to crate empty executionRecords for the items that weren't processed by the wee (e.g. expSetup.getInputData and compare to the log)
		for(URI inputDigoURI : structuredResults.keySet()){
			ExecutionRecordImpl execRecord = new ExecutionRecordImpl();
			//the input Digo for all this information is about
			execRecord.setDigitalObjectReferenceCopy(inputDigoURI+"");
			
			//iterate over the results and document the migration action - all other information goes into properties.
			for(WorkflowResultItem wfResultItem : structuredResults.get(inputDigoURI)){
				
				//check if this record was about the migration action
				String action = wfResultItem.getSActionIdentifier();
				if(action.equals(WorkflowResultItem.SERVICE_ACTION_MIGRATION)){
					DigitalObject outputDigo = wfResultItem.getOutputDigitalObject();
					if(outputDigo!=null){
						//download the ResultDigo into the TB and store it's reference
						execRecord.setDigitalObjectResult(outputDigo, exp);
						Calendar start = new GregorianCalendar();
						start.setTimeInMillis(wfResultItem.getStartTime());
						Calendar end = new GregorianCalendar();
						end.setTimeInMillis(wfResultItem.getEndTime());
						execRecord.setEndDate(end);
					}
				}
				//document all other metadata for actions: identification, etc. as properties over all actions
				try{
					execRecord = this.updateProperties(execRecord, wfResultItem);
				}catch(Exception e){
					log.error("Problems crating execution record properties for a workflowResultItem "+e);
				}
			}
			
			//got all information - now add the record for this inputDigo
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
		BatchExecutionRecordImpl batchRecord = new BatchExecutionRecordImpl();
		batchRecord.setBatchRunSucceeded(false);
		
		this.helperUpdateExpWithBatchRecord(exp, batchRecord);
		//TODO AL: any more fields to set?
	}
	
	/**
	 * All actions of setting an experiment into state 'processing has started'
	 * @param expID
	 */
	public void processNotify_WorkflowStarted(long expID){
		Experiment exp = testbedMan.getExperiment(expID);
    	exp.getExperimentExecutable().setExecutableInvoked(true);
    	//testbedMan.updateExperiment(exp);
    	edao.updateExperiment(exp);
	}
	
	private void helperUpdateExpWithBatchRecord(Experiment exp,BatchExecutionRecordImpl record){
    	exp.getExperimentExecutable().getBatchExecutionRecords().add(record);
		exp.getExperimentExecutable().setExecutionCompleted(true);
		//testbedMan.updateExperiment(exp);
		edao.updateExperiment(exp);
	}
	
	
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
	private ExecutionRecordImpl updateProperties(ExecutionRecordImpl execRecord, WorkflowResultItem wfResultItem) throws IOException{
		Properties p;
		if(execRecord.getPropertiesListResult()==null){
	    	p = new Properties();
	    }
	    else{
	    	p = execRecord.getPropertiesListResult();
	    }
		//create a property name that has the action identifier as part of it.
		if((wfResultItem.getLogInfo()!=null)&&(!wfResultItem.getLogInfo().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_LOG+"_"+wfResultItem.getSActionIdentifier(), wfResultItem.getLogInfo());
		}
		if((wfResultItem.getSActionIdentifier()!=null)&&(!wfResultItem.getSActionIdentifier().equals(""))){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionIdentifier+"_"+wfResultItem.getSActionIdentifier(), wfResultItem.getSActionIdentifier());
		}
		if((wfResultItem.getServiceParameters()!=null)&&(wfResultItem.getServiceParameters().size()>0)){
			String sFormatted="";
			for(Parameter sp: wfResultItem.getServiceParameters()){
				sFormatted+=sp.getName()+" = "+sp.getValue();
			}
			p.setProperty(ExecutionRecordImpl.WFResult_Parameters+"_"+wfResultItem.getSActionIdentifier(), sFormatted);
		}
		if((wfResultItem.getExtractedInformation()!=null)&&(wfResultItem.getExtractedInformation().size()>0)){
			p.setProperty(ExecutionRecordImpl.WFResult_ExtractedInformation+"_"+wfResultItem.getSActionIdentifier(), wfResultItem.getExtractedInformation().toString());
		}
		if(wfResultItem.getStartTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionStartTime+"_"+wfResultItem.getSActionIdentifier(), wfResultItem.getStartTime()+"");
		}
		if(wfResultItem.getEndTime()!=-1){
			p.setProperty(ExecutionRecordImpl.WFResult_ActionEndTime+"_"+wfResultItem.getSActionIdentifier(), wfResultItem.getEndTime()+"");
		}
		execRecord.setPropertiesListResult(p);
		return execRecord;
	}

}
