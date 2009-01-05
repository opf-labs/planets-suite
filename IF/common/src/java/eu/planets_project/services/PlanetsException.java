package eu.planets_project.services;

/**
 * Planets specific exception
 */
@SuppressWarnings("serial")
public class PlanetsException extends Exception
{
	/**
	 * Construct from passed message
	 * @param msg
	 */
	public PlanetsException(String msg)
	{
		super(msg);
	}

	/**
	 * Construct from passed throwable
	 * @param t
	 */
	public PlanetsException(Throwable t)
	{
		super(t);
	}
}
