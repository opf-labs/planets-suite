/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.ifr.core.common.api;

/**
 * NotificationManager interface
 *
 * Interface class for a generic NotificationManager.
 * @see eu.planets_project.ifr.core.common.impl.NotificationManagerImpl for an example implementation.
 *
 * @author Klaus Rechert, ALUF
 */
public interface NotificationManager
{
	/**
 	 * Register for a specific event type classified by numeric identifier.
 	 * @param sub subscriber object to be notified.
 	 * @param id unique event identification.
 	 */
	public void registerSubscriber(EventSubscriber sub, int id);

	/**
 	 * Terminates an active subscribtion.
 	 * @param sub subscriber.
 	 * @param id event id.
 	 */
	public void deregisterSubscriber(EventSubscriber sub, int id);

	/**
	 * Sets the current status of an subscriber active/inactive.
	 * If an subscriber is inactive, all events occurring during this time are saved.
	 * If an subscriber becomes active again all stored events are passes as Array.
	 *
	 * @param sub
	 * @param active
	 * @return The resulting Planets events
	 */
	public PlanetsEvent[] setSubscriberActive(EventSubscriber sub, boolean active);

	/** 
 	 * Submit a Event to the NotificationManager. 
 	 *
 	 * All events are dispatched to active subscribes if there any present. If 
 	 * there are no subscribers for the particular event, the event is discarded.
 	 *
 	 * @param e
 	 */
	public void addEvent(PlanetsEvent e);
}
