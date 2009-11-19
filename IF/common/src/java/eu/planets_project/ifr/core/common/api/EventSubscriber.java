package eu.planets_project.ifr.core.common.api;
/**
 * EventSubscriber interface
 *
 * Implement this interface to register and receive notifications.
 *
 * @see eu.planets_project.ifr.core.common.api.NotificationManager;
 *
 * @author Klaus Rechert, ALUF
 */

public interface EventSubscriber
{
	public void update(PlanetsEvent e);
}
