package eu.planets_project.tb.api.persistency;

import java.util.List;
import javax.ejb.Remote;

import eu.planets_project.tb.api.model.mockups.Workflow;

@Remote
public interface WorkflowTemplatePersistencyRemote {
	
	public long persistWorkflowTemplate(Workflow template);
	public Workflow getWorkflowTemplate(long id);
	
	public void updateWorkflowTemplate(Workflow template);
	public void deleteWorkflowTemplate(long id);
	public void deleteWorkflowTemplate(Workflow template);
	public List<Workflow> queryAllWorkflowTemplates();
	//public boolean queryIsWorkflowTemplateNameUnique(String sName);


}
