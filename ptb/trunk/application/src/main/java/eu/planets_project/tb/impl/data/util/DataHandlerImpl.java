/**
 * 
 */
package eu.planets_project.tb.impl.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.jcr.PathNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.impl.data.DataRegistryManagerImpl;
import eu.planets_project.tb.impl.CommentManagerImpl;

/**
 * @author Andrew Lindley, ARC
 * The TB file handler has the purpose of
 *  - converting local file refs into http container exposed ones
 *    e.g. local file: IFServer/\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\inputdata\Text1.doc
 *         http file: http://localhost:8080/planets-testbed/inputdata/Text1.doc
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
	//e.g. /planets-testbed/inputdata
	private String FileInSubDir = "";
	//e.g. localFileDirBase/planets-testbed/outputdata
	private String FileOutDir = "";
	//e.g. /planets-testbed/outputdata
	private String FileOutSubDir = "";
	
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
	        FileOutSubDir = properties.getProperty("JBoss.FileOutDir");
	        FileOutDir = localFileDirBase+FileOutSubDir;
	        FileInSubDir = properties.getProperty("JBoss.FileInDir");
	        FileInDir = localFileDirBase+FileInSubDir;
	        
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
	   		//URI(scheme,authority,path,query,fragement)
	   		return new URI("http",authority,"/planets-testbed/inputdata/"+localFileRef.getName(),null,null);
	   	}
	   	else{
	   		//URI output file ref to be created
	   		return new URI("http",authority,"/planets-testbed/outputdata/"+localFileRef.getName(),null,null);
	   	}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#getLocalFileRef(java.net.URI, boolean)
	 */
	public File getLocalFileRef(URI uriFileRef, boolean input) throws FileNotFoundException{
		boolean bException = false;
		if(uriFileRef!=null){
			String sFileName = "";
			
			HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String expScheme = "http";
			String expAuthority = req.getLocalName()+":"+Integer.toString(req.getLocalPort());
		   
			if(!expScheme.equalsIgnoreCase(uriFileRef.getScheme()))
		   		bException = true;
		   	
		   	if(!uriFileRef.getAuthority().equalsIgnoreCase(expAuthority))
		   		bException = true;

		   	//for an input URI
		   	if(input){
		   		int i = uriFileRef.getPath().indexOf(this.FileInSubDir);
		   		if(i==-1)
		   			bException = true;
		   		sFileName = uriFileRef.getPath().substring(i+FileInSubDir.length()+1, uriFileRef.getPath().length());
		   	}
		   	//for an output URI
		 	if(!input){
		 		int i = uriFileRef.getPath().indexOf(this.FileOutSubDir);
		   		if(i==-1)
		   			bException = true;
		   		sFileName = uriFileRef.getPath().substring(i+FileOutSubDir.length()+1, uriFileRef.getPath().length());
		   	}
		 	
		 	//check if the parsing process occured without any failure
		 	if(!bException){
		 		File f;
		 		if(input){
		 			f = new File(FileInDir,sFileName);
		 		}
		 		else{
		 			f = new File(FileOutDir,sFileName);
		 		}
		 		if(!f.canRead()){
		 			throw new FileNotFoundException(f.getName() +" not accessible");
		 		}
		 		return f;
		 	}
		 	else{
		 		throw new FileNotFoundException(uriFileRef +" not according to Testbed schema");
		 	}
		}
		return null;	
	}
	
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getInputFileIndexEntryName(java.io.File)
     */
    public String getInputFileIndexEntryName(File localFileRef){
    	if((localFileRef!=null)&&(localFileRef.canRead())){
			try {
				//get the index where a mapping of stored file name - original name is stored
				Properties props = this.getInputDirIndex();
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
    
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getInputFileIndexEntryName(java.io.File)
     */
    public String getOutputFileIndexEntryName(File localFileRef){
    	if((localFileRef!=null)&&(localFileRef.canRead())){
			try {
				//get the index where a mapping of stored file name - original name is stored
				Properties props = this.getOutputDirIndex();
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
     * checks if for the input files an index file exists and if not creates it. As for the input
     * files a random number is created, the original fileName is stored within the index property file
     * @throws IOException 
     */
    private Properties getInputDirIndex() throws IOException{
    	//get index from input file dir
    	return getIndex(true);
    }
    
    /**
     * checks if for the input files an index file exists and if not creates it. As for the input
     * files a random number is created, the original fileName is stored within the index property file
     * @throws IOException 
     */
    private Properties getOutputDirIndex() throws IOException{
    	//get index from output file dir
    	return getIndex(false);
    }
    
    /**
     * Returns the index properties either of the input or for the output files of 
     * and experiment
     * @param bInputIndex
     * @return
     */
    private Properties getIndex(boolean bInputIndex) throws IOException{
    	File dir;
    	//inputIndex
    	if(bInputIndex){
    		dir = new File(FileInDir);
    	}
    	//outputIndex
    	else{
    		dir = new File(FileOutDir);
    	}
    	
    	//for both: check if dir was created
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
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getFileInDir()
     */
    public String getFileInDir(){
    	return this.FileInDir;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getFileOutDir()
     */
    public String getFileOutDir(){
    	return this.FileOutDir;
    }
    

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#copy(java.io.File, java.io.File)
	 */
	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		// Perform the copy.
		this.copy(in, out);
	}

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#copy(eu.planets_project.tb.impl.data.DataRegistryManagerImpl, java.net.URI, java.io.File)
     */
    public void copy(DataRegistryManagerImpl dr, URI pduri, File dst)
            throws IOException {
        
        InputStream in = null;
        try {
          in = dr.getDataManager(pduri).retrieve(pduri);
        } catch ( PathNotFoundException e ) {
            throw new IOException("Caught "+ e.getMessage()+" on " + pduri );
        } catch ( URISyntaxException e ) {
            throw new IOException("Caught "+ e.getMessage()+" on " + pduri );
        }
        OutputStream out = new FileOutputStream(dst);
        // Perform the copy.
        this.copy(in, out);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#setInputFileIndexEntryName(java.lang.String, java.lang.String)
     */
    public void setInputFileIndexEntryName(String sFileRandomNumber, String sFileName){
    	setFileEntryName(sFileRandomNumber, sFileName,true);
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#setOutputFileIndexEntryName(java.lang.String, java.lang.String)
     */
    public void setOutputFileIndexEntryName(String sFileRandomNumber, String sFileName){
    	setFileEntryName(sFileRandomNumber, sFileName,false);
    }
    
    /**
     * @param sFileRandomNumber
     * @param sFileName
     * @param inputIndex indicates if this shall be written to the (true) inputFileIndex or (false) outputFileIndex
     */
    private void setFileEntryName(String sFileRandomNumber, String sFileName, boolean inputIndex){
    	if((sFileRandomNumber!=null)&&(sFileName!=null)){
    		try{
    			Properties props;
    			File dir;
    			//check if written to input or output file index
    			if(inputIndex){
    				props = this.getInputDirIndex();
    				dir = new File(FileInDir);
    			}
    			else{
    				props = this.getOutputDirIndex();
    				dir = new File(FileOutDir);
    			}
    			props.put(sFileRandomNumber,sFileName);
    			props.store(new FileOutputStream(new File(dir, "index_names.properties")), null);
    		}catch(Exception e){
    			//TODO: loog
    		}
    	}
    }

    /**
     * Private utility to raw-copy between streams.
     */
    private void copy(InputStream in, OutputStream out) throws IOException {
        try{
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        catch(IOException e){
            throw e;
        } finally{
            in.close();
            out.close();
        }
    }

}
