/**
 * 
 */
package eu.planets_project.tb.impl.data.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.impl.DataRegistryImpl;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.system.BackendProperties;

/**
 * This information is valid for old v0.5 experiments
 * The TB file handler has the purpose of
 *  - converting local file refs into http container exposed ones
 *    e.g. local file: IFServer/\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\inputdata\Text1.doc
 *         http file: http://localhost:8080/planets-testbed/inputdata/Text1.doc
 * 
 * - retrieving file specific metadata as e.g. the originally used name, etc. from the index
 * - encoding/decoding files to base64 Strings by using the AXIS libraries
 * 
 * References returned are relative filenames w.r.t. the Testbed data directory.
 * 
 * Older versions supported full pathnames, and the resolver deals with those too.
 * -------------------
 * FIXME Future version should store references for DigitalObjects in 
 * 
 * @author Andrew Lindley, ARC. Andrew Jackson, BL.
 * 
 */ 
/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 27.10.2009
 *
 */
public class DataHandlerImpl implements DataHandler {
    
    // A reference to the data manager itself:
    DataRegistry dataReg = DataRegistryImpl.getInstance();

	// A Log for this:
    private Log log = LogFactory.getLog(DataHandlerImpl.class);
	
    //This property isdefined within the BackendResources.properties
	private String localFileDirBase;
	
	//e.g. localFileDirBase+/planets-testbed/inputdata
	private static final String IN_DIR_PATH = "/planets-testbed/inputdata";
    private String FileInDir;
	
	//e.g. localFileDirBase/planets-testbed/outputdata
    private static final String OUT_DIR_PATH = "/planets-testbed/outputdata";
	private String FileOutDir;
	
	// The new generic filestore (as opposed to using separate directories for input and output:
    private static final String FILE_DIR_PATH = "/planets-testbed/filestore";
	private String FileStoreDir;
	
	private String externallyReachableFiledir;

	//Apache AXIS Base64 encoder/decoder
	Base64 base64 = new Base64();

	/**
	 * 
	 */
	public DataHandlerImpl(){
		//read the file input and output directory from the proeprties file 
		readProperties();
	}
	
	/**
	 * 
	 */
	private void readProperties(){
	    // Set the file store directories relative to the TB data file directory:
        BackendProperties bp = new BackendProperties();
		localFileDirBase = bp.getTBFileDir();
        FileInDir = localFileDirBase + IN_DIR_PATH;
        FileOutDir = localFileDirBase + OUT_DIR_PATH;
        FileStoreDir = localFileDirBase + FILE_DIR_PATH;
        externallyReachableFiledir = bp.getExternallyReachableFiledir();
	}
	
	/* (non-Javadoc)
	 * Only possible as long as we're using the tomcat's \jbossweb-tomcat55.sar\ROOT.war
	 * as location for storing files, as this is accessible from outside.
	 * @see eu.planets_project.tb.api.data.util.DataHandler#getHttpFileRef(java.io.File)
	 */
	@Deprecated
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
	
    /* -------------------------------------------------------------------------------------------------- */
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addBytestream(java.io.InputStream, java.lang.String)
     */
    public URI storeBytestream(InputStream in, String name) throws IOException {
        
        DigitalObject.Builder dob = new DigitalObject.Builder( Content.byReference(in) );
        dob.title(name);
        URI refname = this.storeDigitalObject(dob.build());
        log.debug("Stored file '"+name+"' under reference: "+refname);
        
        return refname;
    }
    
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addBytearray(byte[], java.lang.String)
     */
    public URI storeBytearray(byte[] b, String name) throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream(b);
        return this.storeBytestream(bin, name);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addByURI(java.net.URI)
     */
    public URI storeUriContent(URI u) throws MalformedURLException, IOException {
        URI ref = null;
        InputStream in = null;
        try{
            URLConnection c = u.toURL().openConnection();
            in = c.getInputStream();
            ref = this.storeBytestream(in, u.toString());
        }
        finally{
            in.close();
        }
        return ref;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addFile(java.io.File)
     */
    public URI storeFile(File f) throws FileNotFoundException, IOException {
        return this.storeBytestream(new FileInputStream(f), f.getName());
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#storeDigitalObject(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.tb.api.model.Experiment)
     */
    public URI storeDigitalObject(DigitalObject dob, Experiment exp) {
    	DigitalObjectManager defstore = null;
    	try {
			defstore = this.dataReg.getDefaultDigitalObjectManager();
		} catch (DigitalObjectManagerNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		}
        URI defaultDomUri = this.dataReg.getDefaultDigitalObjectManagerId();
        this.log.info("Attempting to store in data registry: " + defaultDomUri);
        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
        String userid = ".";
        if( currentUser != null && currentUser.getUserid() != null ) {
            userid = currentUser.getUserid();
        }

        // Store new DO in the user space, with path based on experiment details.
        URI baseUri = null;
        try {
            if( exp == null ) { 
                baseUri = new URI(defaultDomUri.toString() + "/testbed/users/"+userid+"/digitalobjects/");
            } else {
                baseUri = new URI(defaultDomUri.toString() + "/testbed/experiments/experiment-"+exp.getEntityID()+"/");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        log.info("Attempting to store in: "+baseUri);
        
        // Pick a name for the object.
        String name = dob.getTitle();
        if( name == null || "".equals(name) ) {
        	UUID randomSequence = UUID.randomUUID();
            name = exp.getExperimentSetup().getBasicProperties().getExperimentName()+"-"+randomSequence+".digitalObject";
        }
        
        // look at the location and pick a unique name.
        URI dobUri;
        try {
            dobUri = new URI( 
                    baseUri.getScheme(), 
                    baseUri.getUserInfo(),
                    baseUri.getHost(), 
                    baseUri.getPort(),
                    baseUri.getPath() +"/"+ name, 
                    null, null );
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
            return null;
        }
    	log.info("Calling Data Registry List for " + baseUri);
        List<URI> storedDobs = dataReg.list(baseUri);
        if( storedDobs != null ) {
          int unum = 1;
          while( storedDobs.contains(dobUri) ) {
            try {
                dobUri = new URI( 
                        baseUri.getScheme(), 
                        baseUri.getUserInfo(),
                        baseUri.getHost(), 
                        baseUri.getPort(),
                        baseUri.getPath() + "/" + unum + "-" + name, 
                        null, null );
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
            unum++;
          }
        }
        
        log.info("Attempting to store at: "+dobUri);
        
        try {
            dobUri = this.storeDigitalObject(dobUri, dob);
        } catch (DigitalObjectNotStoredException e) {
            log.error("Store failed! "+e);
            e.printStackTrace();
            return null;
        }
        
        log.info("Was store at: "+dobUri);
        
        return dobUri;
    }
    private URI storeDigitalObject(DigitalObject dob) {
        return this.storeDigitalObject(dob, null);
    }
    
    private URI storeDigitalObject( URI domUri, DigitalObject dob ) throws DigitalObjectNotStoredException {
        return dataReg.storeAsNew(domUri, dob);
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#get(java.lang.String)
     */
    public DigitalObjectRefBean get(String id) {
        DigitalObjectRefBean dobr = null;
        // Lookup in Data Registry:
        try {
            dobr = this.findDOinDataRegistry(id);
        } catch (FileNotFoundException e1) {
            log.debug("File "+id+" not found in Data Registry. "+e1);
        }
        // If failed, attempt to use the older store:
        if( dobr == null ) {
            try {
                dobr = this.findDOinTestbedCache(id);
            } catch (FileNotFoundException e) {
                log.debug("File "+id+" not found in Testbed File Cache. "+e);
            }
        }
        
        // If ALL Failed, that's the BAD.
        if( dobr == null ) {
          log.error("Could not find any content for "+id);
        }
        return dobr;
    }

    /* -------------------------------------------------------------------------------------------------- */

    private DigitalObjectRefBean findDOinTestbedCache( String id ) throws FileNotFoundException {
        log.debug("Looking for "+id+" in the TB file cache.");
        
        
        // Reduce the name if this is one of the old-form names, containing the full path:
        File file = null;
        String name = null;
        if( id.contains("ROOT.war/planets-testbed") ) {
            id = id.substring(id.lastIndexOf("ROOT.war")+8);
            file = new File(localFileDirBase, id);
            name = getInputFileIndexEntryName(file);
        } else if( id.contains("ROOT.war\\planets-testbed") ) {
            id = id.substring(id.lastIndexOf("ROOT.war")+8);
            file = new File(localFileDirBase, id);
            name = getOutputFileIndexEntryName(file);
        } else {
            file = new File(FileStoreDir, id);
            name = getIndexFileEntryName(file);
        }
        
        // Also fix old forms that start with '\', escaped:
        if( id.startsWith("\\")) {
            // Regex form needs double-escaping - 4 backslashes become one:
            id.replaceAll("\\\\", "/");
        }
        
        // Check in the known locations for the files - look in each directory.
        if( file.exists() ) {
            this.log.debug("Found file: " +file.getAbsolutePath());
            // URGENT The file must be lodged somewhere!
            return new DigitalObjectRefBean(name, id, file);
        }
		throw new FileNotFoundException("Could not find file " + id);
        
    }

    private DigitalObjectRefBean findDOinDataRegistry( String id ) throws FileNotFoundException {
        // Attempt to look-up in the data registry.
        URI domUri = null;
        try {
            domUri = new URI( id );
        } catch (URISyntaxException e) {
            throw new FileNotFoundException("Could not find file " + id);
        }
        // Got a URI, is it owned?
        if( this.dataReg.hasDigitalObjectManager(domUri)) {
            try {
            	//get the digital object with it's original content
            	this.log.info("Retrieving Digital Object at " + domUri);
                DigitalObject digitalObject = this.dataReg.retrieve(domUri);
                return new DigitalObjectRefBean(digitalObject.getTitle(), domUri.toString(), domUri, digitalObject);
            } catch (DigitalObjectNotFoundException e) {
                throw new FileNotFoundException("Could not find file " + id);
            }
        }
		throw new FileNotFoundException("Could not find file " + id);
    }
	

	/* 
	 * Please note this method should not be used for any permanently stored digital objects.
	 * These should use the externally accessible contentResolver instead
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#convertFileRefsToURLAccessibleDigos(java.util.Collection)
	 */
	/*public List<DigitalObject> convertFileRefsToURLAccessibleDigos(Collection<String> localFileRefs){
		List<DigitalObject> ret = new ArrayList<DigitalObject>();
		for(String fileRef : localFileRefs){
			try {
		        //this by default returns a digo (content by value) - that's not serializable due to the javax.xml.DataHandler used
		        DigitalObjectMultiManager digoManager = new DigitalObjectMultiManager();
				DigitalObject refByValueDigo = digoManager.retrieve(new URI(fileRef));
				
				//create a statically accessible temp file that's copied and exposed as URL (through download.jsp)
				File f = DigitalObjectUtils.getAsTmpFile(refByValueDigo);
				File exposedFile = this.copyLocalFileAsTempFileInExternallyAccessableDir(f.getAbsolutePath());
				URI httpRef = this.getHttpFileRef(exposedFile);
				
				//DataHandler dh = new DataHandlerImpl();
				//DigitalObjectRefBean digoRef = dh.get(fileRef);
				//not possible as this requires authentication
				//URI httpRef = digoRef.getDownloadUri();
				
				//adding the original location as metadata record
		        URI objectRef = URI.create(fileRef);
		        
		        //build the digo by reference
				DigitalObject o = new DigitalObject.Builder(Content
		                .byReference(httpRef.toURL())).title(new File(fileRef).getName()).permanentUri(objectRef).build();
				ret.add(o);
				
				//finally only clean up this unused temp file - not the URL exposed one!
				DigitalObjectUtils.cleanDigObUtilsTmp();
				
			} catch (IOException e) {
				log.debug("DataHandler.convertFileRefsToURLAccessibleDigos - Could not resolve fileRef "+fileRef);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				log.debug("DataHandler.convertFileRefsToURLAccessibleDigos - could not resolve a URL ro the temp fileRef "+fileRef);
			} catch (DigitalObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}*/
	
	public List<DigitalObject> convertFileRefsToURLAccessibleDigos(Collection<String> localFileRefs){
		List<DigitalObject> ret = new ArrayList<DigitalObject>();
		for(String fileRef : localFileRefs){
			// FIXME: this might have been my (Carl) bad but a new DR every loop seemed insane
			//use the flag to indicate that we want to return a digital object content by reference (link to contentResolver)
		    // DataRegistry digoManager = new DataRegistryImpl();
		    try {
		    	URI uriRef = new URI(fileRef);
		    	this.log.info("Retrieving Digital Object at " + uriRef);
				DigitalObject refByValueDigo = 
					((DataRegistryImpl) this.dataReg).retrieveAsTBReference(new URI(fileRef));
				ret.add(refByValueDigo);
		    } catch (DigitalObjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}


    /* -------------------------------------------------------------------------------------------------- */
	
    /**
     * @param name
     * @return
     */
    public static String createShortDOName( String name ) {
        int lastSlash = name.lastIndexOf("/");
        if( lastSlash != -1 ) {
            return name.substring( lastSlash + 1, name.length() );
        }
        return name;
    }
    
    /* -------------------------------------------------------------------------------------------------- */

    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getInputFileIndexEntryName(java.io.File)
     */
    private String getInputFileIndexEntryName(File localFileRef){
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
    private String getOutputFileIndexEntryName(File localFileRef){
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
    
    /* -------------------------------------------------------------------------------------------------- */
    
    /**
     * checks if index file exists and if not creates it. As for the input/output
     * files a random number is created, the original fileName is stored within the index property file
     * @throws IOException 
     */
    private Properties getIndex() throws IOException{

         //check if dir was created
         File dir = new File(FileStoreDir);
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
     * Fetches for a given file (the resource's physical file name on the disk) its
     * original logical name which is stored within an index.
     * e.g. ce37d69b-64c0-4476-9040-72512f07bb49.TIF to Test1.TIF
     * @param sFileRandomNumber the corresponding file name or its logical random number if none is available
     */
    private String getIndexFileEntryName(File sFileRandomNumber){
        log.debug("Looking for name of: "+sFileRandomNumber.getAbsolutePath());
        if(sFileRandomNumber!=null){
            try {
                Properties props = this.getIndex();
                if(props.containsKey(sFileRandomNumber.getName())){
                    //return the corresponding name from the index
                    return props.getProperty(sFileRandomNumber.getName());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }   
        }
        log.warn("Could not find name for File: "+sFileRandomNumber.getName());
        //else return the physical file name
        return sFileRandomNumber.getName();
    }


    /* -------------------------------------------------------------------------------------------------- */
    
    /**
     * Creates a temporary that's deleted on exit.
     * @return
     * @throws IOException
     */
    public static File createTemporaryFile() throws IOException {
    	int lowerBound = 1; int upperBound = 9999;
		int random = (int) (lowerBound + Math.random() * ( upperBound - lowerBound) );
		File f = File.createTempFile("dataHandlerTemp"+random, null);
		f.deleteOnExit();
		return f;
    }
    
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#decodeToByteArray(java.lang.String)
	 */
	public static byte[] decodeToByteArray(String sBase64ByteArrayString) {
		return Base64.decodeBase64(sBase64ByteArrayString.getBytes());
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#encodeToBase64ByteArrayString(java.io.File)
	 */
	public static String encodeToBase64ByteArrayString(File src) throws IOException{
		byte[] b = new byte[(int)src.length()];
		FileInputStream fis =null;
		try {
			fis = new FileInputStream(src);
			fis.read(b);
		} catch (IOException e) {
			throw e;
		} finally{
			fis.close();
		}
		
		//encode String
		return Base64.encodeBase64(b).toString();
	}

    /**
     * @param inputStream
     * @param tmpFile
     * @throws IOException 
     */
    public static void storeStreamInFile(InputStream in, File f) throws IOException {
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        byte[] b = new byte[1024];
        int read_size;
        while( (read_size = in.read(b)) != -1) {
            fos.write(b, 0, read_size);
            fos.flush();
        }
        fos.close();
    }

    /**
     * TODO A factory that looks up the DataHandler in the context:
     * @return The DataHandler
     */
    public static DataHandler findDataHandler() {
        return new DataHandlerImpl();
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#createTempFileInExternallyAccessableDir()
	 */
	public File createTempFileInExternallyAccessableDir() throws IOException {
		
		log.debug("createTempFileInExternallyAccessableDir");
		//just use this to generate a name - no content within.
		File f = createTemporaryFile();
		log.debug("temp file location: "+f.getAbsolutePath());
		File dir = new File(externallyReachableFiledir);
		log.debug("directory "+externallyReachableFiledir+" exists? "+ dir.exists());
		File target = new File(dir.getAbsoluteFile()+"/"+f.getName());
		log.debug("target: "+dir.getAbsoluteFile()+"/"+f.getName()+" exists: "+target.exists());
		target.deleteOnExit();
		
		// this doesn't work on the CI - use streams instead
		// boolean success = f.renameTo(target);
		FileInputStream fis  = new FileInputStream(f);
	    FileOutputStream fos = new FileOutputStream(target);
	    try {
	        byte[] buf = new byte[1024];
	        int i = 0;
	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	    } 
	    catch (Exception e) {
	    	throw new IOException("Problems moving data from temp to externally reachable jboss-web deployer dir"+e);
	    }
	    finally {
	        if (fis != null) fis.close();
	        if (fos != null) fos.close();
	        f.delete();
	    }

		log.debug("rename success? target "+target.getAbsolutePath()+" exists?: "+target.exists()+" f deleted?: "+f.exists());
		return target;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#copyLocalFileAsTempFileInExternallyAccessableDir(java.lang.String)
	 */
	public File copyLocalFileAsTempFileInExternallyAccessableDir(String localFileRef) throws IOException{
		//get a temporary file
		log.debug("copyLocalFileAsTempFileInExternallyAccessableDir");
		File f = this.createTempFileInExternallyAccessableDir();
	 	
		//copying one file to another with FileChannel 
		//@see http://www.exampledepot.com/egs/java.nio/File2File.html
		FileChannel srcChannel = new FileInputStream(localFileRef).getChannel();
		FileChannel dstChannel = new FileOutputStream(f).getChannel();
		log.debug("before transferring via FileChannel from src-inputStream: "+localFileRef+" to dest-outputStream: "+f.getAbsolutePath());
		// Copy file contents from source to destination
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        //Close the channels
        srcChannel.close();
        dstChannel.close();
        log.debug("copyLocalFileAsTempFileInExternallyAccessableDir returning: "+f.getAbsolutePath());
		return f;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.data.util.DataHandler#getHttpFileRef(java.io.File)
	 */
	public URI getHttpFileRef(File tempFileInExternalDir)
			throws URISyntaxException, FileNotFoundException {
		//URI file ref for file to be created
		if(!tempFileInExternalDir.canRead()){
	    	throw new FileNotFoundException("getHttpFileRef for "+tempFileInExternalDir +" not found");
	    }
		//HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	   	//String authority = req.getLocalName()+":"+Integer.toString(req.getLocalPort());
	   	String authority = PlanetsServerConfig.getHostname() + ":" + PlanetsServerConfig.getPort();
	   	
   		//URI(scheme,authority,path,query,fragement)
   	    URI ret = new URI("http",authority,"/planets-testbed/"+tempFileInExternalDir.getName(),null,null);
   	    log.debug("returning httpFileRef: "+ret+" for: "+tempFileInExternalDir.getAbsolutePath());
   	    return ret;
	}

}
