package eu.planets_project.ifr.core.common.impl;

import java.util.*;
import eu.planets_project.ifr.core.common.api.*;

/**
 * NotificationManagerImpl
 *
 * @author Klaus Rechert, ALUF
 */
public class NotificationManagerImpl implements NotificationManager
{
	private static NotificationManager instance = null;
	private static HashMap<EventSubscriber, ArrayList<PlanetsEvent>> backlog;
	private static HashMap<Integer, LinkedList<EventSubscriber>> activeSubscriber;
	private static HashMap<Integer, LinkedList<EventSubscriber>> suspendedSubscriber;
	private static HashMap<EventSubscriber, LinkedList<Integer>> subscriber;	

	private NotificationManagerImpl()
	{
		activeSubscriber = new HashMap();
		suspendedSubscriber = new HashMap();
		backlog = new HashMap();
	}

	/**
	 * Factory method for retrieving a NotificationManager instance.
	 *
	 * @return A NotificationManager instance
	 */
	public static NotificationManager getInstance()
	{	
		if(instance == null)
		{
			instance = new NotificationManagerImpl();
		}
		return instance;
	}

	/**
	 * Register for a specific event type classified by numeric identifier.
	 * @param sub subscriber object to be notified.
	 * @param id unique event identification.
	 */
	public void registerSubscriber(EventSubscriber sub, int id)
	{
		synchronized (this)
		{
			LinkedList<Integer> subEvents = subscriber.get(sub);
			if(subEvents == null)
			{
				subEvents = new LinkedList();
				subscriber.put(sub, subEvents);
			}

			if(subEvents.contains(id))
				return; 
			subEvents.add(id);

			LinkedList<EventSubscriber> list = activeSubscriber.get(id);
			if(list == null)
			{
				list = new LinkedList();
				activeSubscriber.put(id, list);
			}
			list.add(sub);
		}
	}

	/**
	 * Terminates an active subscribtion.
	 * @param sub subscriber.
	 * @param id event id.
	 */
	public void deregisterSubscriber(EventSubscriber sub, int id)
	{
		synchronized(this)
		{
			LinkedList<Integer> subEvents = subscriber.get(sub);
			if(!subEvents.contains(id))
				return;

			subEvents.remove(id);
			if(subEvents.isEmpty())
				subscriber.remove(sub);

			LinkedList<EventSubscriber> list = activeSubscriber.get(id);
			if(list != null)
			{
				list.remove(sub);
				if(list.isEmpty())
					activeSubscriber.remove(list);
			}
			list = suspendedSubscriber.get(id);
			if(list != null)
			{
				list.remove(sub);
				if(list.isEmpty())
					suspendedSubscriber.remove(list);
			}
		}
	}


	/**
	 * Sets the current status of an subscriber active/inactive.
	 * If an subscriber is inactive, all events occurring during this time are saved.
	 * If an subscriber becomes active again all stored events are passes as Array.
	 *
	 * @param sub
	 * @param active
	 * @return The resulting Planets events
	 */
	public PlanetsEvent[] setSubscriberActive(EventSubscriber sub, boolean active)
	{
		synchronized(this)
		{
			LinkedList<Integer> subEvents = subscriber.get(sub);
			if(subEvents == null)
				return null;
	
			LinkedList<EventSubscriber> activeList;
			LinkedList<EventSubscriber> inActiveList;
			ListIterator<Integer> iter = subEvents.listIterator();
			while(iter.hasNext())
			{
				int id = iter.next();
				activeList = activeSubscriber.get(id);
				if(activeList == null)
					continue;
				inActiveList = suspendedSubscriber.get(id);
				if(inActiveList == null)
					continue;
				if(active)
				{
					inActiveList.remove(sub);
					activeList.add(sub);
				}
				else
				{
					activeSubscriber.remove(sub);
					inActiveList.add(sub);
				}
			}
		
			if(active)
			{
				ArrayList<PlanetsEvent> buffer = backlog.get(sub);
				PlanetsEvent[] ret = new PlanetsEvent[buffer.size()];
				ret = buffer.toArray(ret);
				backlog.remove(sub);
				buffer.clear();
				return ret;
			}	
			else 
				return null;
		}
	}

	private static void notifySubscriber(LinkedList<EventSubscriber> list, PlanetsEvent e)
	{
		ListIterator<EventSubscriber> iter = list.listIterator();
		while(iter.hasNext())
		{
			EventSubscriber sub = iter.next();
			sub.update(e);
		}
	}

	private static void saveEvent(LinkedList<EventSubscriber> list, PlanetsEvent e)
	{
		ListIterator<EventSubscriber> iter = list.listIterator();
		while(iter.hasNext())
		{
			EventSubscriber sub = iter.next();
			ArrayList<PlanetsEvent> log = backlog.get(sub);
			if(log == null)
			{
				log = new ArrayList();
				backlog.put(sub, log);
			}
			log.add(e);
		}
	}

	/** 
	 * Submit a Event to the NotificationManager. 
	 *
	 * All events are dispatched to active subscribes if there any present. If 
	 * there are no subscribers for the particular event, the event is discarded.
	 *
	 * @param e
	 */
	public void addEvent(PlanetsEvent e)
	{
		synchronized(this)
		{
			int id = e.getId();
			LinkedList<EventSubscriber> activeList;
			LinkedList<EventSubscriber> inActiveList;

			activeList = activeSubscriber.get(id);
			if(activeList != null)
			{
				notifySubscriber(activeList, e);
			}

			inActiveList = suspendedSubscriber.get(id);
			if(inActiveList != null)
			{
				saveEvent(inActiveList, e);
			}
		}
	}
}

