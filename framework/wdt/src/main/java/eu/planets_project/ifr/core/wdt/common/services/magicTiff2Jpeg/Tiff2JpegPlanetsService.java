package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import eu.planets_project.services.PlanetsException;

/**
 * Adapter Class
 * rschmidt@researchstudio.at
 */
public class Tiff2JpegPlanetsService implements eu.planets_project.ifr.core.common.api.L2PlanetsService
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
