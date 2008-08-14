/**
 * 
 */
package eu.planets_project.tb.impl.persistency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.persistency.CommentPersistencyRemote;
import eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.services.TestbedServiceTemplateImpl;


/**
 * @author alindley
 * This class handles the persistency of all TestbedServiceTemplates with the
 * discriminator 'template'. Others as with discriminator 'experiment' are directly
 * handled within an experiment and therefore persisted, updated and deleted with the
 * experiment's persistency handler
 */
@Stateless
public class TestbedServiceTemplatePersistencyImpl implements
		TestbedServiceTemplatePersistencyRemote {
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	
    /**
     * A Factory method to build a reference to this interface.
     * @return
     */
    public static TestbedServiceTemplatePersistencyRemote getInstance() {
        Log log = LogFactory.getLog(TestbedServiceTemplatePersistencyImpl.class);
        try {
            Context jndiContext = new javax.naming.InitialContext();
            TestbedServiceTemplatePersistencyRemote dao_r = (TestbedServiceTemplatePersistencyRemote) PortableRemoteObject
                    .narrow(jndiContext
                            .lookup("testbed/TestbedServiceTemplatePersistencyImpl/remote"),
                            TestbedServiceTemplatePersistencyRemote.class);
            return dao_r;
        } catch (NamingException e) {
            log.error("Failure in getting PortableRemoteObject: "
                    + e.toString());
            return null;
        }
    }


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#deleteTBServiceTemplate(java.lang.String)
	 */
	public void deleteTBServiceTemplate(String UUID){
		/*TestbedServiceTemplateImpl t_helper = manager.find(
				TestbedServiceTemplateImpl.class, UUID);*/
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template' and hashUUID='"+UUID+"'");
		if((query!=null)&&(query.getResultList().size()==1)){
			TestbedServiceTemplateImpl t_helper = (TestbedServiceTemplateImpl)query.getResultList().iterator().next();
			//only delete templates that are of type "template" and not used within an experiment
			if(t_helper.getDiscriminator().equals(t_helper.DISCR_TEMPLATE)){
				manager.remove(t_helper);
			}
		}
		
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#deleteTBServiceTemplate(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void deleteTBServiceTemplate(TestbedServiceTemplate template){
		TestbedServiceTemplateImpl t_helper = manager.find(TestbedServiceTemplateImpl.class, ((TestbedServiceTemplateImpl)template).getEntityID());
		//only delete templates that are of type "template" and not used within an experiment
		if(t_helper.getDiscriminator().equals(t_helper.DISCR_TEMPLATE)){
			manager.remove(t_helper);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#getTBServiceTemplate(java.lang.String)
	 */
	public TestbedServiceTemplate getTBServiceTemplate(String UUID){
		//return manager.find(TestbedServiceTemplateImpl.class, UUID);
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template' and hashUUID='"+UUID+"'");
		if((query!=null)&&(query.getResultList().size()==1)){
			Iterator<TestbedServiceTemplate> itTemplate = query.getResultList().iterator();
			return itTemplate.next();
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#persistTBServiceTemplate(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void persistTBServiceTemplate(TestbedServiceTemplate template){
		manager.persist(template);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#getAllTBServiceTemplates()
	 */
	public List<TestbedServiceTemplate> getAllTBServiceTemplates(){
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template'");
		//Query query = manager.createQuery("from WorkflowImpl where DiscrCol='WorkflowTemplateImpl'");
		return query.getResultList();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#updateTBServiceTemplate(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void updateTBServiceTemplate(TestbedServiceTemplate template){
		manager.merge(template);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#getAllTBServiceIDAndTemplates()
	 */
	public Map<String, TestbedServiceTemplate> getAllTBServiceIDAndTemplates() {
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template'");
		Iterator<TestbedServiceTemplate> itTemplates = query.getResultList().iterator();
		Map<String, TestbedServiceTemplate> ret = new HashMap<String, TestbedServiceTemplate>();
		while(itTemplates.hasNext()){
			TestbedServiceTemplate template = itTemplates.next();
			ret.put(template.getUUID(),template);
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#getAllTBServiceIDAndTags()
	 */
	public Map<String, List<ServiceTag>> getAllTBServiceIDAndTags() {
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template'");
		Iterator<TestbedServiceTemplate> itTemplates = query.getResultList().iterator();
		Map<String, List<ServiceTag>> ret = new HashMap<String, List<ServiceTag>>();
		
		while(itTemplates.hasNext()){
			List<ServiceTag> tags = new Vector<ServiceTag>();
			TestbedServiceTemplate template = itTemplates.next();
			tags.addAll(template.getAllTags());
			ret.put(template.getUUID(),tags);
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote#isServiceTemplateIDRegistered(java.lang.String)
	 */
	public boolean isServiceTemplateIDRegistered(String UUID) {
		/*if(manager.find(TestbedServiceTemplateImpl.class, UUID)!=null){
			return true;
		}
		return false;*/
		Query query = manager.createQuery("from TestbedServiceTemplateImpl where discr='template' and hashUUID='"+UUID+"'");
		if((query!=null)&&(query.getResultList().size()>0)){
			return true;
		}
		return false;
	}

}
