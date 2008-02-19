/**
 * 
 */
package eu.planets_project.tb.impl.system;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.util.ServiceRequestBuilder;
import eu.planets_project.tb.api.services.util.ServiceRespondsExtractor;
import eu.planets_project.tb.api.system.ExperimentInvocationHandler;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.impl.exceptions.ServiceInvocationException;
import eu.planets_project.tb.impl.services.util.ServiceRequestBuilderImpl;
import eu.planets_project.tb.impl.services.util.ServiceRespondsExtractorImpl;

/**
 * @author Andrew Lindley, ARC
 * This class is responsible for taking the experiment's executable part, building the request, 
 * invoking the actual service, extracting the results from the responds and wrting
 * information back to the experiment's executable
 *
 */
public class ExperimentInvocationHandlerImpl implements ExperimentInvocationHandler{

	//A logger for this - transient: it's not persisted with this entity
    private Log log = LogFactory.getLog(ExperimentInvocationHandlerImpl.class);
    private String sOutputDir = "";
    //this will only be modified once, so therefore the relative position of its elements will not change
    //which is important for mapping them to their output files.
    Map<String,String> hmInputFiles = new HashMap<String,String>();
    
    public ExperimentInvocationHandlerImpl(){
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
			executable.setExecutableInvoked(true);
		try {
			//stores the inputFiles with a mapping Map<position i,fileRef>
			createInputDataMap(executable.getInputData());
			//TBServiceTemplate which is registered within the admin wizard
			TestbedServiceTemplate serTempl = executable.getServiceTemplate();
			ServiceOperation selOperation = serTempl.getServiceOperation(
					executable.getSelectedServiceOperationName()
					);
			
			ServiceRequestBuilder requBuilder = new ServiceRequestBuilderImpl(
					selOperation.getXMLRequestTemplate(),this.hmInputFiles);
			
			//take the template and build the actual service request (containing file refs)
			String serviceRequest = requBuilder.buildXMLServiceRequest();
			
			
		  //2) build the wsclient for invoking the call
			//create the web service client
			WSClientBean wsclient = createWSClient(serTempl,selOperation);
			wsclient.setXmlRequest(serviceRequest);
			executable.setServiceXMLRequest(serviceRequest);
		
		  //3) Send the request and check if completed successfully
			String message = wsclient.sendRequest();
			if(!message.equals("success-send")){
				log.error("WSClientbean error sending request");
				throw new ServiceInvocationException("error-send");
			}
			
		  //4) Fetch the results from the service's responds
			String xPathToOutput = selOperation.getXPathToOutput();
			String xmlResponds = wsclient.getXmlResponse();
			executable.setServiceXMLResponds(xmlResponds);
			ServiceRespondsExtractor respondsExtractor = new ServiceRespondsExtractorImpl(xmlResponds, xPathToOutput);
			//Structure: <int position+"",String fileRefToServiceOutput>
			Map<String,String> mapResults = respondsExtractor.getAllOutputs();
			//if migration experiment, move the file output to the TB's repository
			boolean bMigration = selOperation.getServiceOperationType().equals(
					selOperation.SERVICE_OPERATION_TYPE_MIGRATION
					);
			if(bMigration){
				//set the testbed's output file directory (e.g. tomcat55..)
				this.setDir();
				//take the migration output file refs and copy them into the TB's output dir
				//this step also renames them to their input file name
				Map<String,String> movedResults = this.copyFileOutputToOutputDir(mapResults);
				mapResults = movedResults;
			}
			
		 //5) write the characterisation results or the migration (copied and renamed) file refs
			//into the experiment's executable - this may take null values for some outputs
			Iterator<String> itKeys = mapResults.keySet().iterator();
			while(itKeys.hasNext()){
				String key = itKeys.next();
				executable.setOutputData(this.hmInputFiles.get(key), mapResults.get(key));
			}
	
			executable.setExecutionCompleted(true);
			executable.setExecutionSuccess(true);
			
		} catch (Exception e) {
			log.debug("Experiment execution failed - setting state: failure within the experiment's executable");
			executable.setExecutionCompleted(true);
			executable.setExecutionSuccess(false);
		}
	}
	
	
	/**
	 * Takes the added data and builds up a map structure with the position number
	 * as key and the fileRef as value - this is required to create an input - output mapping
	 * Map<String position+"", String localInputFileRef>
	 * @param inputData
	 */
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

	/*
		try{
			TestbedManager manager = TestbedManagerImpl.getInstance(true);
			manager.get
			ExperimentWorkflow expWorkflow = exp.getExperimentSetup().getExperimentWorkflow();

			if(expWorkflow!=null){
				Collection<URI> inputData = expWorkflow.getInputData();
			
				if((inputData!=null)&&inputData.size()>0){
					Iterator<URI> itURIs = inputData.iterator();
				
					while(itURIs.hasNext()){
						URI inputURI = itURIs.next();
						
						if(fileExists(inputURI)){
							File fInput = this.getFile(inputURI);
							this.sOutputFileName = fInput.getName();
							File fOutput = new File(this.sOutputDir+"/"+this.sOutputFileName);
							
							//create duplicate of the file
							this.copyFile(fInput, fOutput);
							//get the URI to the outputfile
							URI outputURI = this.getOutputFileURI();
							//now store results back to experiment
							expWorkflow.setOutputData(inputURI, outputURI);
					  
							//Finally update the Experiment
							manager.updateExperiment(exp);
						}
					
					}
				}
			}
			else{
				System.out.println("Error while executing ExperimentWorkflow");
			}
		}catch(Exception e){
			//TODO Logg statement
			System.out.println("Error while executing ExperimentWorkflow: "+e.toString());
		}
		
	}*/
	
	/**
	 * Takes the information provided within the TestbedServiceTEmplate to build
	 * the web service client.
	 * @return
	 */
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
		    String sFileDirBase = properties.getProperty("Jboss.FiledirBase");
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
	 *  This methods also calls renameOutput which produces an outpuf file with the same name as it's input file.
	 * @return updated migrationResult with file refs to the Testbed's output dir.
	 */
	private Map<String,String> copyFileOutputToOutputDir(Map<String,String> migrationResults){
		Map<String,String> ret = new HashMap<String,String>();
		if(migrationResults!=null){
			Iterator<String> itKeys = migrationResults.keySet().iterator();
			while(itKeys.hasNext()){
				String key = itKeys.next();
				try {
					String fileRef = migrationResults.get(key);
					File fMigrationOutput = new File(fileRef);
					//VM needs to be able to access this ref as file
					if(!fMigrationOutput.canRead()){
						//this item did not produce a valid migration output
						throw new IOException("Error reading migration output file from file ref for key: "+key);
					}
					//get the file's new name (same as it's input file) - input and output should have the same key
					String newFileName = new File(this.hmInputFiles.get(key)).getName();
					File fMovedOutput = new File(this.sOutputDir+"/"+newFileName);
					//now copy its binary data
					this.copy(fMigrationOutput, fMovedOutput);
					
					//finally update the returned migration output reference
					ret.put(key, fMovedOutput.getAbsolutePath());
					
				} catch (Exception e) {
					//no valid output for this input file - no problem
					log.debug(e.toString());
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Rename the output file according to it's input File Name
	 * i.e. with its math random number
	 * @param inputFileName
	 */
	private void renameOutputFile(File outputFile, String inputFileName){
		
	}
	
	/**
	 * copies a file from one location to another
	 * @param src - the source file
	 * @param dst - the destinationfile
	 * @throws IOException
	 */
	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}    
	
	
	/**
	 * Specifies the directory where to copy the migration output files
	 * @throws IOException
	 */
	private void setDir() throws IOException{
		Properties properties = new Properties();
	    try {
	        java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
	        properties.load(ResourceFile); 
	        
	        //Note: sFileDirBaase = ifr_server/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
	        String sFileDirBase = properties.getProperty("Jboss.FiledirBase");
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
	 * @param exec
	 */
	/*private void addOutputToExperimentExecutable(ExperimentExecutable exec, Map<String,String>){
		
	}*/

}
