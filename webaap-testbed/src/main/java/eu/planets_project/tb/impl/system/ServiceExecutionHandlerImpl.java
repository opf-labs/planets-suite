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
package eu.planets_project.tb.impl.system;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.mockups.workflow.Workflow;
import eu.planets_project.tb.api.system.ServiceExecutionHandler;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.exceptions.ServiceInvocationException;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowDroidXCDLExtractorComparator;
import eu.planets_project.tb.impl.system.batch.TestbedBatchProcessorManager;

/**
 * @author Andrew Lindley, ARC
 * This class is responsible for taking the experiment's executable part, building the request, 
 * invoking the actual service, extracting the results from the responds and wrting
 * information back to the experiment's executable
 *
 */
public class ServiceExecutionHandlerImpl implements ServiceExecutionHandler{

	//A Log for this - transient: it's not persisted with this entity
    private Log log = LogFactory.getLog(ServiceExecutionHandlerImpl.class);
    private String sOutputDir = "";
    //this will only be modified once, so therefore the relative position of its elements will not change
    //which is important for mapping them to their output files.
    Map<String,String> hmInputFiles = new HashMap<String,String>();
    //A DataHandler util class for decoding base64 or similar value (and not ref) results
    DataHandler dh = new DataHandlerImpl();
    
    public ServiceExecutionHandlerImpl(){
    }
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.ExperimentInvoker#executeExperiment(eu.planets_project.tb.api.model.Experiment)
	 */
	public void executeExperiment(Experiment exp){
			
		 //1) Get the required data and build the request
			//ExperimentExecutable already contains the input data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if( executable == null ) {
			    log.error("executeExperiment: executable is null!");
			    return;
			}
			
			//DECIDE UPON WHICH BATCH_QUEUE WAS CHOSEN WHAT PROCESSOR TO CALL
			// Look for the batch system... 
			TestbedBatchProcessorManager tbBatchManager = TestbedBatchProcessorManager.getInstance();
			BatchProcessor bp;
			
			//a) TB-experiment types before version1.0 - these use the ExperimentWorkflow
			if(executable.getBatchSystemIdentifier().equals(BatchProcessor.BATCH_IDENTIFIER_TESTBED_LOCAL)){
				//get the specific batchProcessor implementation
				bp = tbBatchManager.getBatchProcessor(BatchProcessor.BATCH_IDENTIFIER_TESTBED_LOCAL);
				// Invoke, depending on the experiment type:
				ExperimentWorkflow ewf = executable.getWorkflow();
				log.info("Submitting workflow: "+ewf+" to batch processor: "+BatchProcessor.BATCH_IDENTIFIER_TESTBED_LOCAL);
				log.info("Got inputs #"+executable.getInputData().size());
				String queue_key = bp.submitBatch( exp.getEntityID(), ewf , executable.getInputData());
				//already set: executable.setBatchQueueIdentifier(BatchProcessor.BATCH_QUEUE_TESTBED_LOCAL);
				executable.setBatchExecutionIdentifier(queue_key);
	            executable.setExecutableInvoked(true);
	            executable.setExecutionCompleted(false);
	            log.info("Got key: "+queue_key);
			}
			
			//b) TB-experiment types using the wee batch processor
			if(executable.getBatchSystemIdentifier().equals(BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL)){
				//get the specific batchProcessor implementation
				bp = tbBatchManager.getBatchProcessor(BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL);
				log.info("Submitting workflow to batch processor: "+BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL);
				log.info("Got inputs #"+executable.getInputData().size());
				//DataHandler dh = new DataHandlerImpl();
			  //NOTE: previously submitting digital objects...
				//List<DigitalObject> digos = dh.convertFileRefsToURLAccessibleDigos(executable.getInputData());
				//submit the batch process to the WEE
				//String queue_key = bp.sumitBatch(exp.getEntityID(), digos, executable.getWEEWorkflowConfig());
			  
			  //NOTE: changed to submitting by data registry references..
				//submit the batch process to the WEE passing objects by reference (shared data registry pointers)
				List<URI> digoRefs = new ArrayList<URI>();
				for(String inputDataRef : executable.getInputData()){
					try{
						digoRefs.add(new URI(inputDataRef));
					}catch(URISyntaxException err){
						log.debug("this shouldn't happen - conversion String -> URI failed for "+inputDataRef);
					}
				}
				String queue_key = bp.sumitBatchByReference(exp.getEntityID(), digoRefs, executable.getWEEWorkflowConfig());
				
				if((queue_key!=null)&&(!queue_key.equals(""))){
					executable.setBatchExecutionIdentifier(queue_key);
					//executable.setExecutableInvoked(true);
		            executable.setExecutionCompleted(false);
		            log.info("Got key: "+queue_key);
				}else{
					//something on the batch processor went wrong....
					//FIXME: This is not really sufficient... inform notify_failed?
					executable.setExecutionCompleted(true);
					executable.setExecutionSuccess(false);
				}
	            
			}
	}
	

	

    /**
	 * Takes the added data and builds up a map structure with the position number
	 * as key and the fileRef as value - this is required to create an input - output mapping
	 * Map<String position+"", String localInputFileRef>
	 * @param inputData
	 */
	@SuppressWarnings("unused")
	private void createInputDataMap(Collection<String> inputData) {
		if(inputData!=null){
			Iterator<String> fileRefs = inputData.iterator();
			int count=0;
			while(fileRefs.hasNext()){
				this.hmInputFiles.put(count+"", fileRefs.next());
				count++;
			}
		}
		
	}
	
	/**
	 * Takes the information provided within the TestbedServiceTEmplate to build
	 * the web service client.
	 * @return
	 */
	@SuppressWarnings("unused")
	private WSClientBean createWSClient(TestbedServiceTemplate serviceTemplate, ServiceOperation selOperation) throws ServiceInvocationException{
		WSClientBean wsclient = initWSClient();
		wsclient.setWsdlURI(serviceTemplate.getEndpoint());
		String message = wsclient.analyzeWsdl();
		
		if(!message.equals("success-analyze")){
			log.error("creating WSClientbean error-analzye");
			throw new ServiceInvocationException("error-analyze");
		}
		
		wsclient.setServiceSelectItemValue(serviceTemplate.getName());
		wsclient.setOperationSelectItemValue(selOperation.getName());
		
		return wsclient;
	}
	
	/**
	 * Creates a new WSClientBean ant sets its  working dir
	 */
	private WSClientBean initWSClient(){

		//load properties from BackendResources.properties file
		Properties properties = new Properties();
		try {
			java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
			properties.load(ResourceFile); 

		    //Note: sFileDirBase = ifr_server/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
		    String sFileDirBase = BackendProperties.getTBFileDir();
		    ResourceFile.close();

		    //create a new client bean
		    WSClientBean wcb = new WSClientBean();
		    wcb.setWorkDir(sFileDirBase+"/planets/wsexecution");
		    
		    return wcb;
				
		 } catch (IOException e) {
		    log.error("read JBoss.FiledirBase from BackendResources.properties failed!"+e.toString());
		 }
		 return null;
	}
	
	/**
	 * Take the migrated file output (produced by the ServiceRespondsBuilder) and copy the
	 *  service's migration output to the Testbed's experiment/output direcotry. 
	 *  
	 * If the migration output does not correspond to a local file but rather to a URI the content is downloaded 
	 * and copied into the Testbed's experiment/output direcotry. 
	 *  
	 *  This methods also calls renameOutput which produces an outpuf file with the same name as it's input file.
	 * @return updated migrationResult with file refs to the Testbed's output dir.
	 */
	@SuppressWarnings("unused")
	private Map<String,String> copyFileOutputToOutputDir(Map<String,String> migrationResults) throws IOException{
		Map<String,String> ret = new HashMap<String,String>();
		if(migrationResults!=null){
			Iterator<String> itKeys = migrationResults.keySet().iterator();
			while(itKeys.hasNext()){
				String key = itKeys.next();
				try {
					//1)get the File ref, rename it to it's corresponding input file name and 
					//move it within the testbed's output dir
					String fileRef = migrationResults.get(key);
					File fMigrationOutput = new File(fileRef);
					//VM needs to be able to access this ref as file
					if(!fMigrationOutput.canRead()){
						//this item did not produce a valid migration output
						throw new IOException("Error reading migration output file from file ref for key: "+key);
					}
					//now copy its binary data
					String ref  = dh.storeFile(fMigrationOutput).toString();
					
					//finally update the returned migration output reference
					ret.put(key, ref);
					
				} catch (IOException e) {
					//2)no valid output FILE for this input file - no problem
					//in this case, test if it's an URI and if this is downloadable
					try{
						String suriRef = migrationResults.get(key);
						URI uriRef = new URI(suriRef);
						
                        //write the file's content as read from the stream
                        String newFileName = dh.storeUriContent(uriRef).toString();
						
						//finally update the returned migration output reference
						ret.put(key, newFileName);
					}
					catch(Exception e2){
						//no problem - we're not able to handle this output
						//not output for this input element
						log.debug(e2.toString());
					}
				}
			}
		}
		return ret;
	}   
	
	
	/**
	 * Specifies the directory where to copy the migration output files
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void setDir() throws IOException{
		Properties properties = new Properties();
	    try {
	        java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
	        properties.load(ResourceFile); 
	        
	        //Note: sFileDirBaase = ifr_server/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
	        String sFileDirBase = BackendProperties.getTBFileDir();
	        sOutputDir = sFileDirBase+properties.getProperty("JBoss.FileOutDir");
	        
	        ResourceFile.close();
	        
	        //create if it does not already exist
	        createOutputDir();
	        
	    } catch (IOException e) {
	    	log.error("read JBossFileDirs from BackendResources.proerties failed!"+e.toString());
	    	throw e;
	    }
	}
	
    /**
     * Checks if output dir (as defined in the properties file) 
     * e.g. C:\DATA\Implementation\ifr_server\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\outputdata
     * exists and otherwise creates it.
     */
    private void createOutputDir(){
    	File dir = new File(sOutputDir);
    	if(!dir.exists()){
    		log.info("Dir does not exist: mkdirs: "+dir.toString());
    		dir.mkdirs();    
    	}
    }
		

	/**
	 * Takes a given http URI and tries to download its binary content.
	 * @param uri
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private byte[] downloadBinaryFromURI(URI uri)throws FileNotFoundException, IOException{
		
		InputStream in = null;
		try{
			if(!uri.getScheme().equals("http")){
				throw new FileNotFoundException("URI schema "+uri.getScheme()+" not supported");
			}

			URLConnection c = uri.toURL().openConnection();
			in = c.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			byte[] data = bos.toByteArray();
			
			return data;
		}
		finally{
			in.close();
		}
	}
	
	
	/**
	 * In the case of a value calls (e.g. as base64 data transmission) this method
	 * extracts and decodes the data to a local file and provides a file reference
	 * An output file type (e.g. doc) can be specified, which will be used as the result's file type
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String,String> createFilesFromBase64Result(Map<String,String> migrationResults, String outputFileType){
		Map<String,String> ret = new HashMap<String,String>();
		if((migrationResults==null)||(migrationResults.size()<=0))
			return ret;
		
		Iterator<String> itKeys = migrationResults.keySet().iterator();
		while(itKeys.hasNext()){
			String key = itKeys.next();
			String sBase64value = migrationResults.get(key);
				
			//decode the base64 String
			byte[] b = DataHandlerImpl.decodeToByteArray(sBase64value);
			try {
				//get the file's new name (same as it's input file) - input and output should have the same key
				String sInputFileName = new File(this.hmInputFiles.get(key)).getName();
				String sOutputFileName ="";
				char delimP = '.';
				int p = sInputFileName.lastIndexOf(delimP);
				String origInputFileMathNr = sInputFileName.substring(0, p);
				sOutputFileName = origInputFileMathNr+"."+outputFileType;
				
				//now copy the byteArray into the file-location
				String ref = dh.storeBytearray(b, sOutputFileName).toString();
				
				//finally update the returned migration output reference
				ret.put(key, ref);
				
			} catch (FileNotFoundException e) {
				System.out.println(e.toString());
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.ServiceExecutionHandler#executeAutoEvalServices(eu.planets_project.tb.api.model.Experiment)
	 * Executable only if the current experiment phase = evaluation
	 */
	public void executeAutoEvalServices(Experiment exp) {	
		log.info("Attempting to execute the Auto Eval services.");
		//a Planets IF Java Workflow instance (mockup)
		WorkflowDroidXCDLExtractorComparator evalWorkflow = new WorkflowDroidXCDLExtractorComparator();
		
		//Get the experiment's data we want to add autoEval FileBMGoals for
		Collection<Entry<String,String>> data = exp.getExperimentExecutable().getMigrationDataEntries();

		//iterate over all experiemnt data entries
		for(Entry<String,String> dataEntry : data){
			DataHandler dh = new DataHandlerImpl();
			try {
				URI inputFileURI = dh.get(dataEntry.getKey()).getDownloadUri();
				//URI outputFileURI = dh.getHttpFileRef(new File(dataEntry.getValue()), false);
				File fInputFile = new File(dataEntry.getKey());
				File fOutputFile = new File(dataEntry.getValue());
				
				//the fileBMGoals for the file to evaluate
				Collection<BenchmarkGoal> fileBMGoals = exp.getExperimentEvaluation().getEvaluatedFileBenchmarkGoals(inputFileURI);
				
				//call the workflow and extract the data from the workflow's result for every fileBMGoal
				for(BenchmarkGoal fileBMGoal : fileBMGoals){
					//executes the workflow, extracts the data, writes results into the objects
					this.executeWorkflowAndExtractResults(fileBMGoal, fInputFile, fOutputFile, evalWorkflow);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
			}			
		}
	}
	

	/**
	 * This method executes a fully functional mockup of the following workflow:
	 * In-->Takes two files as input
	 * a) identify using Droid: to analyze a given file and to extract it's pronom Id(s)
	 * b) extract using XCDL Extractor: to extract it's XCDL representation (if it is supported)
	 * c) compare using XCDL Comparator: compares the two XCDL descriptions.
	 * Out<--returns the comparator's responds 
	 * This responds is then parsed given the XPath queries within AutoEvalServiceConfig and the BenchmarkGoal objects updated
	 * TODO: Andrew: substitute with IF workflow 
	 * @param bmGoal - the BM goal to evaluate
	 * @param f1 localFile ref input file
	 * @param f2 localFile ref output file
	 */
	private void executeWorkflowAndExtractResults(BenchmarkGoal bmGoal, File f1, File f2, Workflow evalWorkflow){
/*		FIXME ANJ Clear this up.
		//execute the Droid->XCDLExtractor->Comparator workflow
		EvaluationExecutable evalExecutable = evalWorkflow.execute(f1, f2);
		
		//persist the workflow's data
		bmGoal.setAutoEvaluationExecutable(evalExecutable);
		
		//in the case the evaluation workflow was successful 
		if(evalExecutable.isExecutableInvoked()&&evalExecutable.isExecutionSuccess()){
			
			try {
				AutoEvaluationSettings autoEvalSettings = bmGoal.getAutoEvalSettings();
				EvaluationTestbedServiceTemplateImpl evalService = (EvaluationTestbedServiceTemplateImpl)autoEvalSettings.getEvaluationService();
	
				//fetch the XCDL comparison result
				String wfResult = evalExecutable.getXCDLsComparisonResult();
				
				//try to build a w3c Document structure 
				Document document = this.buildDOM(wfResult);

			//AUSLAGERN IN EIGENE METHODE
				//Iterate over the results and extract metric and evaluation information according to the given XPath definitions
				NodeList nodes = evalService.getAllEvalResultsRootNodes(document);
				if((nodes!=null)&&(nodes.getLength()>0)){
					for(int i=0;i<nodes.getLength();i++){
						Node node = nodes.item(i);
						
						//e.g. imageHeight
						String sPropertyName = evalService.getEvalResultName(node);
						
						//mapping of TB BMGoalID to mappedPropertyName
						String mappedPropName = evalService.getMappedPropertyName(bmGoal.getID());
						
						//check if the values belong to this BMGoal
						if(sPropertyName.equals(mappedPropName)){
							//e.g. complete
							String sStatus = evalService.getEvalResultCompStatus(node);
							//check if the metric was evaluated properly
							if(sStatus.equals(evalService.getStringForCompStatusSuccess())){
								//e.g. 32
								String sSrcVal = evalService.getEvalResultSrcValue(node);
								//e.g. 32
								String sTarVal = evalService.getEvalResultTarValue(node);
								//e.g. <hammingDistance,0.000000>
								Map<String,String> metric = evalService.getEvalResultMetricNamesAndValues(node);
								
								TBEvaluationTypes type = autoEvalSettings.autoValidate(metric);
								if(type!=null){
									
									//now write the extracted auto Eval information back to the BMGoal
									bmGoal.setSourceValue(sSrcVal);
									bmGoal.setTargetValue(sTarVal);
									bmGoal.setEvaluationValue(type.screenName());
									bmGoal.setWasAutomaticallyEvaluated(true);
								}
							}
						}
					}
				}
				else{
					return;
				}
		//END AUSLAGERN IN EIGENE METHODE
			} catch (Exception e) {
				return;
			}
		}
		else{
			//in this case autoEval workflow failed (e.g. due to non supported file type) - the user must evaluate by hand
		}
		*/
	}
	

		
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.ServiceExecutionHandler#executeExperimentAndAutoEvalServices(eu.planets_project.tb.api.model.Experiment)
	 */
	public void executeExperimentAndAutoEvalServices(Experiment exp) {
		//execute the migration/characterisation service
		this.executeExperiment(exp);
		//execute the evaluation services
		this.executeAutoEvalServices(exp);
		
	}
	

}
