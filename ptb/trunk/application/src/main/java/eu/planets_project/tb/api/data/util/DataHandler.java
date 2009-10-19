package eu.planets_project.tb.api.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.data.DigitalObjectDirectoryLister;


/**
 * This is the Testbed binary file-store wrapper.  It takes copies of submitted resources 
 * and hold them, each with unique keys.  The unique key must be used to retrieve the files.
 * 
 * @author Andrew Lindley, ARC. Andrew Jackson, BL.
 */
public interface DataHandler {

	/**
	 * Creates a temporary File object in the Tomcats's externally available directory
	 * This might be used for exposing config files, etc. temporarily for download while the backing information is persisted in the db model.
	 * This should only be used for information that doesn't belong in any data registry!
	 * @return
	 */
	public File createTempFileInExternallyAccessableDir() throws IOException; 
	/**
	 * Extracts the external format for this given temporary file as e.g. created by 'createTempFileInExternallyAccessableDir'
	 * @param tempFileInExternalDir
	 * @return
	 * @throws URISyntaxException
	 * @throws FileNotFoundException if the file was already deleted or can't be found
	 */
	public URI getHttpFileRef(File tempFileInExternalDir) throws URISyntaxException, FileNotFoundException;	
	
    /* --- Adding to the repository -- */

    /**
     * This adds a file to the store, and returns it's key.
     */
    public URI storeFile(File f) throws IOException, FileNotFoundException;

    /**
     * 
     * @param u
     * @return
     */
    public URI storeUriContent(URI u) throws IOException, URISyntaxException;
    
    /**
     * 
     * @param in
     * @param name
     * @return
     */
    public URI storeBytestream(InputStream in, String name) throws IOException;
    
    /**
     * @param b
     * @param outputFileName
     * @return
     */
    public URI storeBytearray(byte[] b, String outputFileName) throws IOException;
    
    /**
     * @param The new DigitalObject to be stored in the user's default TB results space.
     * @param The Experiment this digital object was created by.
     * @return The URI of the new storage location.
     */
    public URI storeDigitalObject( DigitalObject dob, Experiment exp );
    
    
    /* -- Getting data back from the repository -- */
    
    /**
     * @param the id to look up.
     * @return a wrapped up Digital Object.
     */
    public DigitalObjectRefBean get( String id ) throws FileNotFoundException;
    
}
