package eu.planets_project.ifr.core.services.migration.jmagickconverter.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import eu.planets_project.ifr.core.common.api.PlanetsException;

public interface L2JpgToTiffConverterLocalInterface {

	@WebMethod
	@WebResult(name = "UpdatedPDM_XMLString")
	public abstract String invokeService(@WebParam(name = "PDM_XMLString")
	String pdm) throws PlanetsException;
}