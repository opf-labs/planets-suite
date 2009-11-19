package eu.planets_project.ifr.core.common.logging;


import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import eu.planets_project.ifr.core.common.api.NotificationManager;
import eu.planets_project.ifr.core.common.api.PlanetsEvent;
import eu.planets_project.ifr.core.common.impl.NotificationManagerImpl;

/**
 * This class implements a simple log4j appender. Dispatches PlanetsEvent instances to NotificationManager. 
 *
 * @see org.apache.log4j.Appender
 * @see org.apache.log4j.AppenderSkeleton
 */
public class NotificationManagerAppender extends AppenderSkeleton implements Appender 
{
	private NotificationManager manager = null;

	public NotificationManagerAppender()
	{
		super();
		manager = NotificationManagerImpl.getInstance();
	}

	protected void append(LoggingEvent loggingEvent)
	{
		if( manager == null )
			return;

		Object o = loggingEvent.getMessage();
		// dispatch PlanetsEvent Objects to NotificationManager
		if(o instanceof PlanetsEvent)
		{
			manager.addEvent((PlanetsEvent)o);
		}
	}

	public boolean requiresLayout() {
		return false;
	}

	public void close() {}
}
