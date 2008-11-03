package eu.planets_project.services;

@SuppressWarnings("serial")
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
