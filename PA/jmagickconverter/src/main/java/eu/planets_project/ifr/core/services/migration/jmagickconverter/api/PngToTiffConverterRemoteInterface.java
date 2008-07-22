package eu.planets_project.ifr.core.services.migration.jmagickconverter.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import eu.planets_project.ifr.core.common.api.PlanetsException;

/**
 * Basic Interface for "level1" migration services. This interface just takes and returns a byte[], no additional parameters.  
 * 
 * @author : Peter Melms
 * Email : peter.melms@uni-koeln.de Created : 27.05.2008
 * 
 */

public interface PngToTiffConverterRemoteInterface {

	/**
	 * @param imageData the source image as a byte[]
	 * @return Returns a byte[] containing the migrated image
	 * @throws PlanetsException throws an exception, if the source image has the wrong format.
	 */
    @WebMethod
    @WebResult(name = "convertedImageDataByteArray")
    public abstract byte[] basicMigrateBinary(
	    @WebParam(name = "srcFileByteArray")
	    byte[] imageData) throws PlanetsException;
}