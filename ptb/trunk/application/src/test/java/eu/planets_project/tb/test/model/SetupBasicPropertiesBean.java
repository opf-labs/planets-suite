package eu.planets_project.tb.test.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import eu.planets_project.tb.impl.model.BasicProperties;

@Stateless
public class SetupBasicPropertiesBean implements SetupBasicPropertiesRemote{

	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	
	public long persistProperties(BasicProperties props) {
		manager.persist(props);
		return props.getId();
	}

	public BasicProperties findProperties(long pKey) {
		return manager.find(BasicProperties.class, pKey);
	}
	
	public void deleteProperties(long pKey){
		BasicProperties t_helper = manager.find(BasicProperties.class, pKey);
		manager.remove(t_helper);
	}

	public void deleteProperties(BasicProperties props) {
		BasicProperties t_helper = manager.find(BasicProperties.class, props.getId());
		manager.remove(t_helper);
	}

	/* (non-Javadoc)
	 * Note: The Object must be the one that is already persisted
	 * @see eu.planets_project.tb.test.model.SetupBasicPropertiesRemote#updateProperties(eu.planets_project.tb.impl.model.BasicProperties)
	 */
	public void updateProperties(BasicProperties props) {
		manager.merge(props);
	}
	
}
