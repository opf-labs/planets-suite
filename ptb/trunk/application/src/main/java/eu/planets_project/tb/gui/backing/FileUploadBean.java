package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * This bean handles file upload from the local FS
 */
public class FileUploadBean
{
	
	private Log log = LogFactory.getLog(FileUploadBean.class);
    private UploadedFile _upFile;
    private String _name = "";
    private String originalName ="";
    private File tmpFile;
    private DataHandler dh;
    
    public FileUploadBean() {
        dh = new DataHandlerImpl();
    }
        
    /**
     * @return the _name
     */
    public String getName() {
        return originalName;
    }

    /**
     * @return
     */
    public String getUniqueFileName() {
        return _name;
    }

    /**
     * @return
     */
    public File getTemporaryFile() {
        return tmpFile;
    }

    /**
     * Returns a URI reference to this uploaded file.
     * e.g. http://localhost:8080/planets-testbed/inputdata/213fsz9432ljl324.doc
     * @return
     * @throws Exception
     */
    public URI getURI() throws Exception {
    	return dh.get(this._name).getDownloadUri();
    }
    
    /**
     * This is the main method, that is used to organise an upload.
     * @return
     * @param keep
     * @throws IOException
     */
    public String upload(boolean keep) throws IOException
    {
    	log.info("Uploading...");
        if (_upFile == null)
        	return "error-deploy";
        else ;
        //FacesContext facesContext = FacesContext.getCurrentInstance();
        //facesContext.getExternalContext().getApplicationMap().put("fileupload_bytes", _upFile.getBytes());
        //facesContext.getExternalContext().getApplicationMap().put("fileupload_type", _upFile.getContentType());
        //facesContext.getExternalContext().getApplicationMap().put("fileupload_name", _upFile.getName());
        log.debug("upfile.name: " + _upFile.getName());
        log.debug("upfile.contentType: " + _upFile.getContentType());
        log.debug("upfile.size: " + _upFile.getSize());
        
        if (_upFile.getSize() <= 0)
        	return "error-upload";
        else ;        
        
        // Store the original name of the file.
        originalName = _upFile.getName();
        int lastSlash = originalName.lastIndexOf("/");
        if( lastSlash != -1 ) {
            originalName = originalName.substring( lastSlash + 1, originalName.length() );
        }
        lastSlash = originalName.lastIndexOf("\\");
        if( lastSlash != -1 ) {
            originalName = originalName.substring( lastSlash + 1, originalName.length() );
        }
        log.info("Storing under name: " + originalName );
        
        // If keeping, submit to the data handler.
        if( keep ) {
          try {
            this._name = dh.storeBytestream(_upFile.getInputStream(), originalName).toString();
          } catch (IOException e) {e.printStackTrace(); return "error-upload";}

        } else {
           // If not keeping, create a temporary file and store it there.
            this.tmpFile = DataHandlerImpl.createTemporaryFile();
            DataHandlerImpl.storeStreamInFile(_upFile.getInputStream(), this.tmpFile);
            this._name = tmpFile.getAbsolutePath();
        }
        log.info("Uploading DONE");
        
        return "success-upload";
    }
    
    
    /**
     * @return the _upFile
     */
    public UploadedFile getUpFile() {
        return _upFile;
    }

    /**
     * @param file the _upFile to set
     */
    public void setUpFile(UploadedFile file) {
        if( file != null ) log.info("Setting uploaded file to: "+file.getName());
        _upFile = file;
    }
    
    /**
     * 
     * @return
     */
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

