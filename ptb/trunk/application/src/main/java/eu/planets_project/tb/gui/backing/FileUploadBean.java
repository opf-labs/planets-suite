package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
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
    }
    
    public URI getURI() throws Exception {
    	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	String authority = req.getLocalName()+":"+Integer.toString(req.getLocalPort());
    	URI uri = new URI("http",authority,"/planets-testbed/inputdata/"+this._name,null,null);
    	return uri;
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

}

