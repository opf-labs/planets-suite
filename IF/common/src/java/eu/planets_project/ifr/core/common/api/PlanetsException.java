package eu.planets_project.ifr.core.common.api;

public class PlanetsException extends Exception
{
	public PlanetsException(String msg)
	{
		super(msg);
	}

	public PlanetsException(Throwable t)
	{
		super(t);
	}
}
