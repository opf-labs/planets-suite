package eu.planets_project.ifr.core.common.api;

import eu.planets_project.ifr.core.common.api.PlanetsException;
	
/**
 *    Planets Service Interface
 *
 * @author Rainer Schmidt, ARC
 */
 
public interface PlanetsService {
	
	public String invokeService(String pdm) throws PlanetsException;
	
}