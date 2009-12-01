package eu.planets_project.ifr.core.common.impl;

import eu.planets_project.ifr.core.common.api.PlanetsEvent;

/**
 * Example implementation of PlanetsEvent interface.
 *
 * @see eu.planets_project.ifr.core.common.api.PlanetsEvent
 */
public class PlanetsEventExample implements PlanetsEvent
{
	private int id;
	private String message;

	/**
	 * Empty constructor. 
	 *
	 * Creates a "generic" event with id 0 and "PlanetsEventExample" message.
	 */
	public PlanetsEventExample()
	{
		id = 0;
		message = "PlanetsEventExample";
	}

	/**
	 * Create a PlanetsEvent with given id and message.
	 */
	public PlanetsEventExample(int id, String message)
	{
		this.id = id;
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.common.api.PlanetsEvent#getId()
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.common.api.PlanetsEvent#getMessage()
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "PlanetsEvent " + id + ":" + message;
	}
}
