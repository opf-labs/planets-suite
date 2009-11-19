package eu.planets_project.ifr.core.common.api;

/**
 * Basic Planets Service Interface for level-1-services, 
 * only takes a byte[] and returns another one containing the migrated image.
 *
 *  @author : Peter Melms
 *  Email  : peter.melms@uni-koeln.de
 *  Created : 27.05.2008
 *
 */
public interface PlanetsBasicService {
	
	public byte[] basicMigrateBinary(byte[] imageData) throws PlanetsException;

}
