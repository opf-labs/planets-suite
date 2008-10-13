package eu.planets_project.services;

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
