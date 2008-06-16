package eu.planets_project.ifr.core.simple.api;

import java.net.URL;

import javax.activation.DataHandler;


/**
 * A RemoteInterface for a simple characterisation service
 * @author Markus Reis, Austrian Research C. - ARC
 */
public interface SimpleCharacterisationRemoteInterface {

	/**
	 * characterises a file (referenced by its file URL)
	 * and returns the MIME type
	 * @param fileURL
	 * @return MIME-Type
	 */
	public String characteriseFile(String fileURL);
	
	/**
	 * characterises files (referenced by their file URLs)
	 * and returns the MIME types (in the same order)
	 * @param fileURLs
	 * @return MIME-Types
	 */	
	public String[] characteriseFiles(String[] fileURLs);
	
	/**
	 * characterises a file (referenced by its file URL)
	 * and returns the MIME type
	 * @param fileURL
	 * @return MIME-Type
	 */	
	public String characteriseFileURL(URL fileURL);
	
	/**
	 * characterises files (referenced by their file URLs)
	 * and returns the MIME types (in the same order)
	 * @param fileURLs
	 * @return MIME-Types
	 */		
	public String[] characteriseFileURLs(URL[] fileURLs);
	
	/**
	 * characterises a file
	 * and returns the MIME type
	 * @param fileData
	 * @return MIME-Type
	 */	
	public String characteriseFileDH(DataHandler fileData);
	
	/**
	 * characterises files
	 * and returns the MIME types (in the same order)
	 * @param fileData
	 * @return MIME-Types
	 */		
	public String[] characteriseFileDHs(DataHandler[] fileData);
}