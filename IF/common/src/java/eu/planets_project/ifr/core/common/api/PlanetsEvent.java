package eu.planets_project.ifr.core.common.api;

/**
 * PlanetsEvent interface
 *
 * @see eu.planets_project.ifr.core.common.impl.PlanetsEventExample
 * 
 * @author Klaus Rechert, ALUF
 */
public interface PlanetsEvent
{
	/**
         * @return The unique event id
         */
	int getId();	

	/** 
	 * @return The event's human readable message as String
	 */
	public String getMessage();

	
	/**
	 * @return A string representation of this event
	 */
	public String toString();	
}
