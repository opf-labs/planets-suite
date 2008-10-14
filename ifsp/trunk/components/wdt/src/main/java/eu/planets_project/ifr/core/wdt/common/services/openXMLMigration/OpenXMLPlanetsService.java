package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import org.apache.commons.logging.Log;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * Adapter Class
 * rschmidt@researchstudio.at
 */
public class OpenXMLPlanetsService implements eu.planets_project.ifr.core.common.api.L2PlanetsService
{
	private Log log = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	private OpenXMLMigration oXML = null;
	
	public OpenXMLPlanetsService (OpenXMLMigration oXML) {
		this.oXML = oXML;
	}
	
	public String invokeService(String pdm) throws PlanetsException { 
		try {
			return oXML.convertFileRef(pdm);
    } catch(Exception e) {
    	log.error(e);
    	return null;
    }
	}
}