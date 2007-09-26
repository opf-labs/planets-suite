/**
* The dummy invoker takes looks up an Experiment and extracts it's workflow.
* For a given input URI then the corresponding file is fetched, copied to the output folder
* an then it's output URI is  handed back and stored within the Experiment.
* */
package eu.planets_project.tb.impl.system.mockup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.impl.model.ExperimentApprovalImpl;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;

/**
 * @author alindley
 *
 */
public class WorkflowInvokerImpl implements
		eu.planets_project.tb.api.system.mockup.WorkflowInvoker {

	private TestbedManager manager;
	private String sOutputDir, sInputDir, sOutputFileName,sURIOutputDir;
	
	
	public WorkflowInvokerImpl(){
		manager = TestbedManagerImpl.getInstance(true);
		
		//read the properties file for settings:
		setDirNames();
		
		createOutputDir();
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.mockup.WorkflowInvoker#executeExperimentWorkflow(eu.planets_project.tb.api.model.Experiment)
	 */
	public void executeExperimentWorkflow(long lExperimentID) throws Exception{
		try{
			manager = TestbedManagerImpl.getInstance(true);
			Experiment exp = manager.getExperiment(lExperimentID);

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
	}
	
	
	public void setDirNames(){
		Properties properties = new Properties();
	    try {
	        java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
	        properties.load(ResourceFile); 
	        
	        String sJBossHome = properties.getProperty("Jboss.Home");
	        String sFileDirBase = sJBossHome+properties.getProperty("Jboss.FiledirBase");
	        sOutputDir = sFileDirBase+properties.getProperty("JBoss.FileOutDir");
	        sURIOutputDir = properties.getProperty("JBoss.FileOutDir");
	        sInputDir = sFileDirBase+properties.getProperty("JBoss.FileInDir");
	        
	        ResourceFile.close();
	        
	    } catch (IOException e) {
	    	//TODO add logg statement
	    	System.out.println("readJBossFileDirs from BackendResources failed!"+e.toString());
	    }
	}
	
	/**
	 * Checks if a given URI can be resolved as a File Object
	 * @param file
	 * @return
	 */
	private boolean fileExists(URI file){
		StringTokenizer tokenizer = new StringTokenizer(file.toString(),"/");
		String sFilename = "";
		while(tokenizer.hasMoreTokens()){
			sFilename=tokenizer.nextToken();
		}
		File f = new File(this.sInputDir+"/"+sFilename);
		if ((f.canRead())&&(f.isFile())){
			return true;
		}
		return false;
	}
    
    private URI getOutputFileURI() throws Exception {
    	URI uri = new URI("http","localhost:8080",this.sURIOutputDir+"/"+this.sOutputFileName,null,null);
    	return uri;
    }
    
    
    /**
     * Checks if output dir (as defined in the properties file) 
     * e.g. C:\DATA\Implementation\ifr_server\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\outputdata
     * exists and otherwise creates it.
     */
    private void createOutputDir(){
    	File dir = new File(sOutputDir);
    	if(!dir.exists()){
    		System.out.println("Dir does not exist: mkdirs: "+dir.toString());
    		dir.mkdirs();    
    	}
    }
    
	private void copyFile(File srcFile, File destFile) throws IOException {
		InputStream in = new FileInputStream(srcFile);
		OutputStream out = new FileOutputStream(destFile);

		byte[] buffer = new byte[4*1024];
		int bytesRead;
		while((bytesRead=in.read(buffer)) >= 0) {
			out.write(buffer, 0, bytesRead);
		}
		out.close();
		in.close();
	}
	
	/**
	 * Returns a File Oject for a given URI
	 * @param uri
	 * @return
	 */
	private File getFile(URI uri){
		if(fileExists(uri)){
			StringTokenizer tokenizer = new StringTokenizer(uri.toString(),"/");
			String sFilename = "";
			while(tokenizer.hasMoreTokens()){
				sFilename=tokenizer.nextToken();
			}
			return new File(this.sInputDir+"/"+sFilename);
		}
		return null;
	}

}
