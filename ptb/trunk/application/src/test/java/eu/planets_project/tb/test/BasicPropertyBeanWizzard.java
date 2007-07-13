package eu.planets_project.tb.test;

import eu.planets_project.tb.api.model.BasicProperties;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BasicPropertyBeanWizzard implements BasicPropertyBeanWizzardLocal{

	@PersistenceContext(unitName="testbed")
	private EntityManager manager;
	
	public void createProperties(BasicProperties props) {
		manager.persist(props);
	}

	public BasicProperties finProperties(int pKey) {
		return manager.find(BasicProperties.class, pKey);
	}

}
