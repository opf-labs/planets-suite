/**
 * 
 */
package eu.planets_project.tb.impl.data.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.impl.data.DataRegistryManagerImpl;
import eu.planets_project.tb.impl.system.BackendProperties;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.AdminManagerImpl;

/**
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
 * 
 * @author Andrew Lindley, ARC. Andrew Jackson, BL.
 * 
 */ 
public class DataHandlerImpl implements DataHandler {

	// A logger for this:
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
        localFileDirBase = BackendProperties.getTBFileDir();
        FileInDir = localFileDirBase + IN_DIR_PATH;
        FileOutDir = localFileDirBase + OUT_DIR_PATH;
        FileStoreDir = localFileDirBase + FILE_DIR_PATH;
	}
	
    /**
     * Use java.util.UUID to create unique file name for uploaded file.
     * 
     * Version 4 UUIDs are generated from a large random number and do 
     *not include the MAC address. The implementation of java.util.UUID
     * creates version 4 UUIDs.
     * There are 122 significant bits in a type 4 UUID. 2^122 is a *very* 
     * large number. Assuming a random distribution of these bits, the 
     * probability of collission is *very* low.
     */
	private String generateUniqueName(String name) {
	    // create unique filename
        String ext = name.substring(name.lastIndexOf('.'));
        return UUID.randomUUID().toString() + ext;
	}
	
    /* -------------------------------------------------------------------------------------------------- */
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addBytestream(java.io.InputStream, java.lang.String)
     */
    public String addBytestream(InputStream in, String name) throws IOException {
        File dir = new File(FileStoreDir);
        dir.mkdirs();    
        
        String refname = this.generateUniqueName(name);
        
        File f = new File(dir, refname );
        
        DataHandlerImpl.storeStreamInFile(in, f);
        
        //create an index entry mapping the logical to the physical file name
        this.setIndexFileEntryName(refname, name);
        
        log.debug("Stored file '"+name+"' under reference: "+refname);
        return refname;
    }
    
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addBytearray(byte[], java.lang.String)
     */
    public String addBytearray(byte[] b, String name) throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream(b);
        return this.addBytestream(bin, name);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addByURI(java.net.URI)
     */
    public String addByURI(URI u) throws MalformedURLException, IOException {
        String ref = null;
        InputStream in = null;
        try{
            URLConnection c = u.toURL().openConnection();
            in = c.getInputStream();
            ref = this.addBytestream(in, u.toString());
        }
        finally{
            in.close();
        }
        return ref;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addFile(java.io.File)
     */
    public String addFile(File f) throws FileNotFoundException, IOException {
        return this.addBytestream(new FileInputStream(f), f.getName());
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#addFromDataRegistry(eu.planets_project.tb.impl.data.DataRegistryManagerImpl, java.net.URI)
     */
    public String addFromDataRegistry(DataRegistryManagerImpl dr, URI pduri) throws IOException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream( dr.getDataManager(pduri).retrieveBinary(pduri) );
        } catch (SOAPException e) {
            throw new IOException("Caught "+ e.getMessage()+" on " + pduri );
        }
        return this.addBytestream(in, pduri.toString());
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getDownloadURI(java.lang.String)
     */
    public URI getDownloadURI(String id) throws FileNotFoundException {
        CachedFile cf = new CachedFile(id);
        return cf.getDownload();
    }

    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getFile(java.lang.String)
     */
    public File getFile(String id) throws FileNotFoundException {
        CachedFile cf = new CachedFile(id);
        return cf.getFile();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#getName(java.lang.String)
     */
    public String getName(String id) throws FileNotFoundException {
        CachedFile cf = new CachedFile(id);
        return cf.getName();
    }
    

    /* -------------------------------------------------------------------------------------------------- */
    public class CachedFile {
        File file;
        String name;
        URI download;
        
        /**
         * Constructor resolved the file ID.
         * Normalises to /planets-testbed/filestore/RAND.ext 
         * @param id
         * @throws FileNotFoundException 
         */
        public CachedFile(String id) throws FileNotFoundException {
            log.debug("Looking for "+id+" in the TB file cache.");
            
            // Reduce the name if this is one of the old-form names, containing the full path:
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
                log.debug("Found file: "+file.getAbsolutePath());
            } else {
                log.warn("Could not find file: "+file.getAbsolutePath());
                throw new FileNotFoundException("Could not find file "+id);
            }
            
            log.debug("For id: "+id+" got name: "+name);
            
            // Define the download URI:
            log.debug("Creating the download URL.");
            String context = "/testbed";
            if( FacesContext.getCurrentInstance() != null ) {
                HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                context = req.getContextPath();
            }
            try {
                download = new URI(null, null, context+"/reader/download.jsp","fid="+id, null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                download = null;
            }
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the download
         */
        public URI getDownload() {
            return download;
        }
        
        
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
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#setInputFileIndexEntryName(java.lang.String, java.lang.String)
     */
    private void setInputFileIndexEntryName(String sFileRandomNumber, String sFileName){
        setFileEntryName(sFileRandomNumber, sFileName,true);
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.data.util.DataHandler#setOutputFileIndexEntryName(java.lang.String, java.lang.String)
     */
    private void setOutputFileIndexEntryName(String sFileRandomNumber, String sFileName){
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
     * Creates a mapping between the resources's physical file name (random number) and its
     * original logical name.
     * @param sFileRandomNumber
     * @param sFileName
     */
    private void setIndexFileEntryName(String sFileRandomNumber, String sFileName){
        if((sFileRandomNumber!=null)&&(sFileName!=null)){
            try{
                Properties props = this.getIndex();
                props.put(sFileRandomNumber,sFileName);
                 File dir = new File(FileStoreDir);
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
    
    public static File createTemporaryFile() throws IOException {
        File f = File.createTempFile("dataHandlerTemp", null);
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

}
