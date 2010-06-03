package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import java.util.logging.Logger;

import eu.planets_project.services.PlanetsException;

/**
 * Adapter Class
 * rschmidt@researchstudio.at
 */
public class OpenXMLPlanetsService implements eu.planets_project.ifr.core.common.api.L2PlanetsService
{
	private Logger log = Logger.getLogger(this.getClass().getName());	
	private OpenXMLMigration oXML = null;
	
	public OpenXMLPlanetsService (OpenXMLMigration oXML) {
		this.oXML = oXML;
	}
	
	public String invokeService(String pdm) throws PlanetsException { 
		try {
			return oXML.convertFileRef(pdm);
    } catch(Exception e) {
    	log.severe(e.toString());
    	return null;
    }
	}
}