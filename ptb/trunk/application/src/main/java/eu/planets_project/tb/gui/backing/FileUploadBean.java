package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;


import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This bean handles file upload from the local FS
 */
public class FileUploadBean
{
	
	private Log log = LogFactory.getLog(FileUploadBean.class);
	private String uploadDir = "";
    private UploadedFile _upFile;
    private String _name = "";
    private String originalName ="";
    
    public FileUploadBean() {

    }
    
    public void setUploadDir(String dir) {
 	   uploadDir = dir;
    }
    
    
    public String getUploadDir() {
 	   return uploadDir;
    }    
    
    
    public UploadedFile getUpFile()
    {
        return _upFile;
    }

    public void setUpFile(UploadedFile upFile)
    {
        _upFile = upFile;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
        originalName = name;
    }
    
    /**
     * Returns a URI reference to this uploaded file.
     * e.g. http://localhost:8080/planets-testbed/inputdata/213fsz9432ljl324.doc
     * @return
     * @throws Exception
     */
    public URI getURI() throws Exception {
    	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	String authority = req.getLocalName()+":"+Integer.toString(req.getLocalPort());
    	URI uri = new URI("http",authority,"/planets-testbed/inputdata/"+this._name,null,null);
    	return uri;
    }
    
    
    /**
     * Returns a localFile reference to this uploaded file.
     * ../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war/planets-testbed/inputdata/213fsz9432ljl324.doc
     * @return
     */
    public String getLocalFileRef(){
    	return this.uploadDir+"/"+this._name;
    }

    public String upload() throws IOException
    {
    	log.debug("Uploading...");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (_upFile == null)
        	return "error-deploy";
        else ;
        facesContext.getExternalContext().getApplicationMap().put("fileupload_bytes", _upFile.getBytes());
        facesContext.getExternalContext().getApplicationMap().put("fileupload_type", _upFile.getContentType());
        facesContext.getExternalContext().getApplicationMap().put("fileupload_name", _upFile.getName());
        log.debug("upfile.name: " + _upFile.getName());
        log.debug("upfile.contentType: " + _upFile.getContentType());
        log.debug("upfile.size: " + _upFile.getSize());
        
        if (_upFile.getSize() <= 0)
        	return "error-upload";
        else ;        
        
        try {        	
        	File dir = new File(uploadDir);
        	dir.mkdirs();    
        	
        	originalName = _upFile.getName();
        	 
        	// create unique filename
        	String ext = _upFile.getName().substring(_upFile.getName().lastIndexOf('.'));
        	
        	//Use java.util.UUID to create unique file name for uploaded file
        	/*Version 4 UUIDs are generated from a large random number and do 
    		 *not include the MAC address. The implementation of java.util.UUID
    		 * creates version 4 UUIDs.
    		 * There are 122 significant bits in a type 4 UUID. 2^122 is a *very* 
    		 * large number. Assuming a random distribution of these bits, the 
    		 * probability of collission is *very* low.
    		 */
        	this._name = new UUID(20,122).randomUUID().toString() + ext;
        	
        	File f = new File(dir, this._name );
        	f.createNewFile();
        	log.debug("Writing byte[] to file: " + f.getCanonicalPath());
        	FileOutputStream fos = new FileOutputStream(f);
        	fos.write(_upFile.getBytes());
        	fos.flush();
        	fos.close();
        	log.debug("Writing byte[] to file: DONE");
        	
        	//create an index entry mapping the logical to the physical file name
        	this.setIndexFileEntryName(this._name,this.originalName);
        	
        } catch (IOException e) {e.printStackTrace(); return "error-upload";}
        
        log.debug("Uploading DONE");
        
        return "success-upload";
    }

    
    public boolean isUploaded()
    {
    	if (getUpFile() != null) {    		
    		if (getUpFile().getSize() > 0)
    			return true;
    		else 
    			return false;
    	}
    	else
    		return false;
        //FacesContext facesContext = FacesContext.getCurrentInstance();
        //return facesContext.getExternalContext().getApplicationMap().get("fileupload_bytes")!=null;
    }
    
    /**
     * checks if index file exists and if not creates it. As for the input/output
     * files a random number is created, the original fileName is stored within the index property file
     * @throws IOException 
     */
    public Properties getIndex() throws IOException{

    	 //check if dir was created
    	 File dir = new File(uploadDir);
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
    
    /**
     * Creates a mapping between the resources's physical file name (random number) and its
     * original logical name.
     * @param sFileRandomNumber
     * @param sFileName
     */
    public void setIndexFileEntryName(String sFileRandomNumber, String sFileName){
    	if((sFileRandomNumber!=null)&&(sFileName!=null)){
    		try{
    			Properties props = this.getIndex();
    			props.put(sFileRandomNumber,sFileName);
    	    	 File dir = new File(uploadDir);
    			props.store(new FileOutputStream(new File(dir, "index_names.properties")), null);
    		}catch(Exception e){
    			//TODO: loog
    		}
    	}
    }
    
    /**
     * Fetches for a given file (the resource's physical file name on the disk) its
     * original logical name which is stored within an index.
     * e.g. ce37d69b-64c0-4476-9040-72512f07bb49.TIF to Test1.TIF
     * @param sFileRandomNumber the corresponding file name or its logical random number if none is available
     */
    public String getIndexFileEntryName(String sFileRandomNumber){
    	if(sFileRandomNumber!=null){
			try {
				Properties props = this.getIndex();
				if(props.containsKey(sFileRandomNumber)){
					//return the corresponding name from the index
	    			return props.getProperty(sFileRandomNumber);
	    		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	}
    	//else return the physical file name
		return sFileRandomNumber;
    }

}

