package eu.planets_project.tb.api.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.impl.data.DataRegistryManagerImpl;


/**
 * This is the Testbed binary file-store wrapper.  It takes copies of submitted resources 
 * and hold them, each with unique keys.  The unique key must be used to retrieve the files.
 * 
 * @author Andrew Lindley, ARC. Andrew Jackson, BL.
 */
public interface DataHandler {
	

    /* --- Adding to the repository -- */

    /**
     * This adds a file to the store, and returns it's key.
     */
    public String addFile(File f) throws IOException, FileNotFoundException;

    /**
     * 
     * @param u
     * @return
     */
    public String addByURI(URI u) throws IOException, URISyntaxException;
    
    /**
     * 
     * @param in
     * @param name
     * @return
     */
    public String addBytestream(InputStream in, String name) throws IOException;
    
    /**
     * @param b
     * @param outputFileName
     * @return
     */
    public String addBytearray(byte[] b, String outputFileName) throws IOException;
    
    /**
     * 
     * @return
     */
    public String addFromDataRegistry( DataRegistryManagerImpl dr, URI pduri ) throws IOException;
    
    
    /* -- Getting data back from the repository -- */

    /**
     * 
     * @param id
     * @return
     */
    public URI getDownloadURI(String id) throws FileNotFoundException;
    
    /**
     * 
     * @param id
     * @return
     */
    public String getName(String id) throws FileNotFoundException;
    
    /**
     * 
     * @param id
     * @return
     */
    public File getFile(String id) throws FileNotFoundException;
    
    
    /**
     * 
     * @param id
     * @return
     * @throws FileNotFoundException
     */
    public DigitalObject getDigitalObject(String id ) throws FileNotFoundException;
    
    
    /**
     * Transforms a localFileRef into a privateally accessible one
     * @param localFileRef
     * @param input: create httpfileRef with input (true) or output (false) data directory
     * e.g. if (true): http://localhost:8080/planets-testbed/inputdata/RandonNumber.doc
     * @return
     * @throws  
     */
//    URI getHttpFileRef(File localFileRef, boolean input)throws URISyntaxException, FileNotFoundException;
    
    /**
     * Transforms a given Testbed URI for a file within the Testbed's private directory
     * into a local File
     * @param uriFileRef
     * @param input: create localFileRef for input (true) or output (false) data directory
     * e.g. if (true): C:/Data/..etc../planets-testbed/inputdata/RandonNumber.doc
     * @return
     */
//    private File getLocalFileRef(URI uriFileRef, boolean input) throws FileNotFoundException;
    
     /**
     * Fetches for a given file (the resource's physical file name on the disk) its
     * original logical name which is stored within an index.
     * e.g. ce37d69b-64c0-4476-9040-72512f07bb49.TIF to Test1.TIF within the input directory
     * @param sFileRandomNumber the corresponding file name or its logical random number if none is available
     */
//    private String getInputFileIndexEntryName(File localFile);
     /**
     * Fetches for a given file (the resource's physical file name on the disk) its
     * original logical name (e.g. as received from migration service) which is stored within an index.
     * e.g. ce37d69b-64c0-4476-9040-72512f07bb49.TIF to Test1.TIF within the output directory
     * @param sFileRandomNumber the corresponding file name or its logical random number if none is available
     */
//    private String getOutputFileIndexEntryName(File localFile);
    
    /**
     * Returns the local directory whih is used for persisting experiment's input files
     * This information is extracted from the BackendResources.properties
     * @return
     */
//    private String getFileInDir();
    /**
     * Returns the local directory whih is used for persisting experiment's output files
     * This information is extracted from the BackendResources.properties
     * @return
     */
 //   private String getFileOutDir();
    

    /**
     * copies a file from one location to another
     * @param src - the source file
     * @param dst - the destination file
     * @throws IOException
     */
//    private void copy(File src, File dst) throws IOException;

    /**
     * Utility to copy data from the DR to the TB.
     * 
     * TODO Fill out this Javadoc.
     * 
     * @param dr
     * @param pduri
     * @param dst
     * @throws IOException
     */
//    private void copy(DataRegistryManagerImpl dr, URI pduri, File dst)
//    throws IOException;
    
    /**
     * @param src - the source file as byte[]
     * @param dst - the destination file
     * @throws IOException
     * @throws FileNotFoundException
     */
//    private void copy(byte[] src, File dst) throws IOException, FileNotFoundException;
    
    /**
     * Creates a mapping between the resources's physical file name (random number) and its
     * original logical name. within the input file storage location
     * @param sFileRandomNumber
     * @param sFileName
     */
//    private void setInputFileIndexEntryName(String sFileRandomNumber, String sFileName);
    /**
     * Creates a mapping between the resources's physical file name (random number) and its
     * original logical name. within the output file storage location (e.g. for migration results)
     * @param sFileRandomNumber
     * @param sFileName
     */
//    private void setOutputFileIndexEntryName(String sFileRandomNumber, String sFileName);
    
    /**
     * Takes a File object and returns a base64 encoded String of its byte array
     * @param src
     * @return
     */
//    private String encodeToBase64ByteArrayString(File src) throws IOException;
    
    /**
     * Takes a base64 encoded String of a byte array and decodes it into byte[]
     * @param sB64ByteArrayString
     * @return
     */
//    private byte[] decodeToByteArray(String sBase64ByteArrayString);
}
