/**
 * 
 */
package eu.planets_project.tb.impl.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.impl.CommentManagerImpl;

/**
 * @author Andrew Lindley, ARC
 * The TB file handler has the purpose of
 *  - converting local file refs into http container exposed ones
 *    e.g. local file: IFServer/\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\inputdata\Text1.doc
 *         http file: file:///http://localhost:8080/planets-testbed/inputdata/Text1.doc
 * 
 * - retrieving file specific metadata as e.g. the originally used name, etc. from the index
 */
public class DataHandlerImpl implements DataHandler{

	// A logger for this:
    private Log log = LogFactory.getLog(DataHandlerImpl.class);
	//These three properties are defined within the BackendResources.properties
	//e.g. ../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
	private String localFileDirBase ="";
	//e.g. localFileDirBase+/planets-testbed/inputdata
	private String FileInDir = "";
	//e.g. localFileDirBase/planets-testbed/outputdata
	private String FileOutDir = "";
	
	public DataHandlerImpl(){
		//read the file input and output directory from the proeprties file 
		readProperties();
	}
	
	private void readProperties(){
		Properties properties = new Properties();

	    try {
	    	java.io.InputStream ResourceFile = getClass().getClassLoader()
	        		.getResourceAsStream(
	        				"eu/planets_project/tb/impl/BackendResources.properties"
	        		);
	        properties.load(ResourceFile); 
	        
	        //Note: sFileDirBaase = ifserver/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
	        localFileDirBase = properties.getProperty("Jboss.FiledirBase");
	        FileOutDir = localFileDirBase+properties.getProperty("JBoss.FileOutDir");
	        FileInDir = localFileDirBase+properties.getProperty("JBoss.FileInDir");
	        
	        ResourceFile.close();
	        
	    } catch (IOException e) {
	    	log.fatal("read Jboss.FiledirBase from BackendResources.properties failed!"+e.toString());
	    }
	}
	
	
	/* (non-Javadoc)
	 * Only possible as long as we're using the tomcat's \jbossweb-tomcat55.sar\ROOT.war
	 * as location for storing files, as this is accessible from outside.
	 * @see eu.planets_project.tb.api.data.util.DataHandler#getHttpFileRef(java.io.File)
	 */
	public URI getHttpFileRef(File localFileRef,boolean input) throws URISyntaxException, FileNotFoundException{
	    if(!localFileRef.canRead()){
	    	throw new FileNotFoundException(localFileRef +" not found");
	    }
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	   	String authority = req.getLocalName()+":"+Integer.toString(req.getLocalPort());
	   	//distinguish between inputdata and outputdata
	   	if(input){
	   		//URI input file ref to be created
	   		return new URI("http",authority,"/planets-testbed/inputdata/"+localFileRef.getName(),null,null);
	   	}
	   	else{
	   		//URI output file ref to be created
	   		return new URI("http",authority,"/planets-testbed/outputdata/"+localFileRef.getName(),null,null);
	   	}
	}
	

	
    /* (non-Javadoc)
     * Only the input file directory is searched, as input and output file have the same physical file name
     * @see eu.planets_project.tb.api.data.util.DataHandler#getIndexFileEntryName(java.io.File)
     */
    public String getIndexFileEntryName(File localFileRef){
    	if((localFileRef!=null)&&(localFileRef.canRead())){
			try {
				//get the index where a mapping of stored file name - original name is stored
				Properties props = this.getIndex();
				//fileName corresponds to a random number
				if(props.containsKey(localFileRef.getName())){
					//return the corresponding name from the index
	    			return props.getProperty(localFileRef.getName());
	    		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug("index file name for "+localFileRef.getName()+" was not found");
			}	
    	}
    	//else if no name was found return the physical file name
		return localFileRef.getName();
    }
    
    /**
     * checks if index file exists and if not creates it. As for the input/output
     * files a random number is created, the original fileName is stored within the index property file
     * @throws IOException 
     */
    private Properties getIndex() throws IOException{

    	 //check if dir was created
    	 File dir = new File(FileInDir);
         dir.mkdirs();    
         
         File f = new File(dir, "index_names.properties");
         
         //index does not exist
         if(!((f.exists())&&(f.canRead()))){
         	f.createNewFile();
         }
         	
         //read properties
         Properties properties = new Properties();
    	 FileInputStream ResourceFile = new FileInputStream(f);
    	 properties.load(ResourceFile); 
    	 return properties;
    }

}
