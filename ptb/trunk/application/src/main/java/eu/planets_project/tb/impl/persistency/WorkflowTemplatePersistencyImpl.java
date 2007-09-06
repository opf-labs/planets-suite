/**
 * 
 */
package eu.planets_project.tb.impl.persistency;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowImpl;

/**
 * @author alindley
 *
 */
@Stateless
public class WorkflowTemplatePersistencyImpl implements
		WorkflowTemplatePersistencyRemote {
	
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
	private EntityManager manager;
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#deleteWorkflowTemplate(long)
	 */
	public void deleteWorkflowTemplate(long id) {
		WorkflowImpl t_helper = manager.find(WorkflowImpl.class, id);
		manager.remove(t_helper);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#deleteWorkflowTemplate(eu.planets_project.tb.api.model.mockups.WorkflowTemplate)
	 */
	public void deleteWorkflowTemplate(Workflow template) {
		WorkflowImpl t_helper = manager.find(WorkflowImpl.class, template.getEntityID());
		manager.remove(t_helper);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#getWorkflowTemplate(long)
	 */
	public Workflow getWorkflowTemplate(long id) {
		return manager.find(WorkflowImpl.class, id);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#persistWorkflowTemplate(eu.planets_project.tb.api.model.mockups.WorkflowTemplate)
	 */
	public long persistWorkflowTemplate(Workflow template) {
		manager.persist(template);
		return template.getEntityID();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#queryAllWorkflowTemplates()
	 */
	public List<Workflow> queryAllWorkflowTemplates() {
		Query query = manager.createQuery("from WorkflowImpl");
		//Query query = manager.createQuery("from WorkflowImpl where DiscrCol='WorkflowTemplateImpl'");
		return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.persistency.WorkflowTemplatePersistencyRemote#updateWorkflowTemplate(eu.planets_project.tb.api.model.mockups.WorkflowTemplate)
	 */
	public void updateWorkflowTemplate(Workflow template) {
		manager.merge(template);
	}

}
