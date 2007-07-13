package eu.planets_project.tb.test;

import javax.ejb.Local;

import eu.planets_project.tb.api.model.BasicProperties;

@Local
public interface BasicPropertyBeanWizzardLocal {
	
	public void createProperties(BasicProperties props);
	public BasicProperties finProperties(int id);

}
