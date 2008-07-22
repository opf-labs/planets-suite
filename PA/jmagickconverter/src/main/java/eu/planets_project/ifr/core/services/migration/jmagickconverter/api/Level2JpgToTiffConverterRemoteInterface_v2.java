package eu.planets_project.ifr.core.services.migration.jmagickconverter.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import eu.planets_project.datamodel.TypeFiles;
import eu.planets_project.ifr.core.common.api.PlanetsException;

public interface Level2JpgToTiffConverterRemoteInterface_v2 {

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.Level2JpgToTiffconverterLocalInterface#invokeService(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.Level2JpgToTiffConverterRemoteInterface#invokeService(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.Level2JpgToTiffConverterLocalInterface#invokeService(java.lang.String)
	 */
	@WebMethod
	@WebResult(name = "UpdatedPDM_XMLString")
	public abstract String invokeService(@WebParam(name = "PDM_XMLString")
	String pdm) throws PlanetsException;


}