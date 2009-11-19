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
         * Access unique event id.
         * @return
         */
	int getId();	

	/** 
	 * Get event's human readable message as String.
	 * @return
	 */
	public String getMessage();

	/**
 	 * toString()
 	 * @return
 	 */
	public String toString();	
}
