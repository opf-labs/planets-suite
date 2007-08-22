package eu.planets_project.tb.test.model;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.BasicPropertiesImpl;

@Remote
public interface SetupBasicPropertiesRemote {
	
	/**
	 * This method takes a given BasicProperties Entity Bean and persists it.
	 * The return value of this method is the auto-generated ID.
	 * @param props
	 * @return
	 */
	public long persistProperties(BasicPropertiesImpl props);
	public BasicPropertiesImpl findProperties(long id);
	
	/**
	 * Fetches the given and already persisted BasicProperties object and updates it with given values.
	 * @param props The BasicProperties which is look-uped and contains the values for the update	
	 */
	public void updateProperties(BasicPropertiesImpl props);
	public void deleteProperties(long id);
	public void deleteProperties(BasicPropertiesImpl props);

}
