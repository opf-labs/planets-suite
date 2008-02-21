package eu.planets_project.tb.api.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Andrew Lindley, ARC
 * The TB file handler has the purpose of
 *  - converting local file refs into http container exposed ones
 *    e.g. local file: IFServer/\server\default\deploy\jbossweb-tomcat55.sar\ROOT.war\planets-testbed\inputdata\Text1.doc
 *         http file: file:///http://localhost:8080/planets-testbed/inputdata/Text1.doc
 * 
 *  - retrieving file specific metadata as e.g. the originally used name, etc. from the index
 *  - upload file [not yet defined]
 */
public interface DataHandler {
	
	/**
	 * Transforms a localFileRef into a publically accessible one
	 * @param localFileRef
	 * @param input: create httpfileRef with input (true) or output (false) data directory
	 * e.g. if (true): file:///http://localhost:8080/planets-testbed/inputdata/RandonNumber.doc
	 * @return
	 * @throws  
	 */
	public URI getHttpFileRef(File localFileRef, boolean input)throws URISyntaxException, FileNotFoundException;
	
	/**
	 * Transforms a given Testbed URI for a file within the Testbed's public directory
	 * into a local File
	 * @param uriFileRef
	 * @param input: create localFileRef for input (true) or output (false) data directory
	 * e.g. if (true): C:/Data/..etc../planets-testbed/inputdata/RandonNumber.doc
	 * @return
	 */
	public File getLocalFileRef(URI uriFileRef, boolean input) throws FileNotFoundException;
	
	 /**
     * Fetches for a given file (the resource's physical file name on the disk) its
     * original logical name which is stored within an index.
     * e.g. ce37d69b-64c0-4476-9040-72512f07bb49.TIF to Test1.TIF
     * @param sFileRandomNumber the corresponding file name or its logical random number if none is available
     */
	public String getIndexFileEntryName(File localFile);

}
