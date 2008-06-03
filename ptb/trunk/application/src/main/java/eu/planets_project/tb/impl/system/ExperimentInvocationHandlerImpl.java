/**
 * 
 */
package eu.planets_project.tb.impl.system;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.util.ServiceRequestBuilder;
import eu.planets_project.tb.api.services.util.ServiceRespondsExtractor;
import eu.planets_project.tb.api.system.ExperimentInvocationHandler;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.exceptions.ServiceInvocationException;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
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
    //A DataHandler util class for decoding base64 or similar value (and not ref) results
    DataHandler dh = new DataHandlerImpl();
    
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
			//set the testbed's output file directory (e.g. tomcat55..)
			this.setDir();
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
			boolean byValueCall = requBuilder.isCallByValue();
			
		  //2) build the wsclient for invoking the call
			//create the web service client
			WSClientBean wsclient = createWSClient(serTempl,selOperation);
			wsclient.setXmlRequest(serviceRequest);
			executable.setServiceXMLRequest(serviceRequest);
		
		  //3) Send the request and check if completed successfully
			executable.setExecutionStartDate(new GregorianCalendar().getTimeInMillis());
			String message = wsclient.sendRequest();
			executable.setExecutionEndDate(new GregorianCalendar().getTimeInMillis());
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
			//test if there was any outputdata retrieved successfully
			if((mapResults==null)||(mapResults.size()<1)){
				throw new InvalidInputException("Could not retireve output data from the xmlResult");
			}
			//if migration experiment, move the file output to the TB's repository
			boolean bMigration = selOperation.getServiceOperationType().equals(
					selOperation.SERVICE_OPERATION_TYPE_MIGRATION
					);
			if(bMigration){
				//if data was called byValue (e.g. Base64 file) and not by reference - decode the data into local file refs
				if(byValueCall){
					Map<String,String> createdResults = this.createFilesFromBase64Result(mapResults,selOperation.getOutputFileType());
					mapResults = createdResults;
				}else{
				//take the migration output file refs and copy them into the TB's output dir
				//this step also renames them to their input file name
				Map<String,String> movedResults = this.copyFileOutputToOutputDir(mapResults);
				mapResults = movedResults;
				}
			}
			
		 //5) write the characterisation results or the migration (copied and renamed) file refs
			//into the experiment's executable - this may take null values for some outputs
			Iterator<String> itKeys = mapResults.keySet().iterator();
			while(itKeys.hasNext()){
				String key = itKeys.next();
				executable.setOutputData(this.hmInputFiles.get(key), mapResults.get(key));
			}
			
		 //6) final check for execution success
			if (executable.getOutputData().size()<=0){
				//in this case no outputdata at all was extracted: experiment success is not true
				throw new InvalidInputException("Could not retireve any output data for the experiments");
			}
	
			executable.setExecutionCompleted(true);
			executable.setExecutionSuccess(true);
			
		} catch (Exception e) {
			log.debug("Experiment execution failed - setting state: failure within the experiment's executable"+e.toString());
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
	 *  
	 * If the migration output does not correspond to a local file but rather to a URI the content is downloaded 
	 * and copied into the Testbed's experiment/output direcotry. 
	 *  
	 *  This methods also calls renameOutput which produces an outpuf file with the same name as it's input file.
	 * @return updated migrationResult with file refs to the Testbed's output dir.
	 */
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
					//get the file's new name (same as it's input file) - input and output should have the same key
					String newFileName = new File(this.hmInputFiles.get(key)).getName();
					File fMovedOutput = new File(this.sOutputDir+"/"+newFileName);
					//now copy its binary data
					DataHandler dh = new DataHandlerImpl();
					dh.copy(fMigrationOutput, fMovedOutput);
					
					//finally update the returned migration output reference
					ret.put(key, fMovedOutput.getAbsolutePath());
					
				} catch (IOException e) {
					//2)no valid output FILE for this input file - no problem
					//in this case, test if it's an URI and if this is downloadable
					FileOutputStream fos = null;
					try{
						String suriRef = migrationResults.get(key);
						URI uriRef = new URI(suriRef);
						byte[] content = downloadBinaryFromURI(uriRef);
						
						//now build the outputFile
						//get the URI's file name. if it has none - use the corresponding input's file name
						char delimP = '.';
						char delimS = '/';
                        int s = suriRef.lastIndexOf(delimS);
                        int p = suriRef.lastIndexOf(delimP);
                        String fileType;
                        String newFileName;
                        String origFileName;
                        String mathRandomNr;
                        String corrInputFileName = new File(this.hmInputFiles.get(key)).getName();
                        //test if a file name can be extracted
                        if((s!=-1)&&(p!=-1)){
                        	origFileName = suriRef.substring(s+1, suriRef.length());
                        	fileType = suriRef.substring(p,suriRef.length());
                        	int h = corrInputFileName.indexOf(delimP);
                        	mathRandomNr = corrInputFileName.substring(0,h);
                        	//the nex file name is assembled from the corresponding input's file random number
                        	//and the URI's file type e.g. .docx
                        	newFileName = mathRandomNr+fileType;
                            //the original file name received when calling the service is stored in the output index
                        	DataHandler dh = new DataHandlerImpl();
                            dh.setOutputFileIndexEntryName(newFileName, origFileName);
                        }
                        else{
                        	//the file's new name will be set to the same as it's corresponding input file - input and output should have the same key
                        	newFileName = new File(this.hmInputFiles.get(key)).getName();
                        	
                        	//in this case no indexFileEntryName is written
                        }
                        
                        //write the file's content as read from the stream
						File fOutput = new File(this.sOutputDir+"/"+newFileName);
						fos = new FileOutputStream(fOutput);
						fos.write(content);
						fos.flush();
						fos.close();
						
						//finally update the returned migration output reference
						ret.put(key, fOutput.getAbsolutePath());
					}
					catch(Exception e2){
						//no problem - we're not able to handle this output
						//not output for this input element
						log.debug(e2.toString());
					}
					finally{
						fos.flush();
						fos.close();
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
	 * Takes a given http URI and tries to download its binary content.
	 * @param uri
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
	private Map<String,String> createFilesFromBase64Result(Map<String,String> migrationResults, String outputFileType){
		Map<String,String> ret = new HashMap<String,String>();
		if((migrationResults==null)||(migrationResults.size()<=0))
			return ret;
		
		Iterator<String> itKeys = migrationResults.keySet().iterator();
		while(itKeys.hasNext()){
			String key = itKeys.next();
			String sBase64value = migrationResults.get(key);
				
			//decode the base64 String
			byte[] b = dh.decodeToByteArray(sBase64value);
			try {
				//get the file's new name (same as it's input file) - input and output should have the same key
				String sInputFileName = new File(this.hmInputFiles.get(key)).getName();
				String sOutputFileName ="";
				char delimP = '.';
				int p = sInputFileName.lastIndexOf(delimP);
				String origInputFileMathNr = sInputFileName.substring(0, p);
				sOutputFileName = origInputFileMathNr+"."+outputFileType;
				File fOutputFile = new File(this.sOutputDir+"/"+sOutputFileName);
				
				//now copy the byteArray into the file-location
				dh.copy(b, fOutputFile);
				//the original file name is written into the output index
				dh.setOutputFileIndexEntryName(sOutputFileName, sOutputFileName);
				
				//finally update the returned migration output reference
				ret.put(key, fOutputFile.getAbsolutePath());
				
			} catch (FileNotFoundException e) {
				System.out.println(e.toString());
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		
		return ret;
	}
	

}
