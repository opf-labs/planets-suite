package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.api.PlanetsException;

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