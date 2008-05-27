package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

import eu.planets_project.ifr.core.common.api.PlanetsException;

/**
 * Adapter Class
 * rschmidt@researchstudio.at
 */
public class Tiff2JpegPlanetsService implements eu.planets_project.ifr.core.common.api.PlanetsService
{
	private Tiff2Jpeg t2j = null;
	
	public Tiff2JpegPlanetsService(Tiff2Jpeg t2j) {
		this.t2j = t2j;
	}
	
	public String invokeService(String pdm) throws PlanetsException { 
		String ret = t2j.convertFile(pdm);
		if(ret == null) throw new PlanetsException("File Conversion Error");
		return ret;
	}
}
