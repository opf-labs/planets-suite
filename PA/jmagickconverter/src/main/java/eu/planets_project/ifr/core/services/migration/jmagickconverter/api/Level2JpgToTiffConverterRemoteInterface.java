package eu.planets_project.ifr.core.services.migration.jmagickconverter.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import eu.planets_project.ifr.core.common.api.PlanetsException;

public interface Level2JpgToTiffConverterRemoteInterface {

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.Level2JpgToTiffConverterLocalInterface#basicMigrateBinary(byte[])
	 */
	@WebMethod
	@WebResult(name = "convertedImageDataByteArray")
	public abstract byte[] basicMigrateBinary(
			@WebParam(name = "srcFileByteArray")
			byte[] imageData) throws PlanetsException;

}