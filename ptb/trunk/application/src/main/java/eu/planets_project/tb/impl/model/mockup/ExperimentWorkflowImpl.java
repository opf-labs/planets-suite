/**
 * 
 */
package eu.planets_project.tb.impl.model.mockup;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.services.mockups.Service;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentWorkflowImpl implements ExperimentWorkflow, java.io.Serializable{
	
	@Id
	@GeneratedValue
	private long id;
	private Vector<File> vInputData, vOutputData;
	@OneToOne(cascade={CascadeType.ALL})
	private WorkflowImpl workflow;
	
	public ExperimentWorkflowImpl(Workflow template) {
		vInputData = new Vector<File>();
		vOutputData = new Vector<File>();
		workflow = (WorkflowImpl)template;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(java.io.File)
	 */
	public void addInputData(File file) {
		if(!this.vInputData.contains(file)){
			this.vInputData.add(file);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(java.util.List)
	 */
	public void addInputData(List<File> files) {
		Iterator<File> itFiles = files.iterator();
		while(itFiles.hasNext()){
			this.addInputData(itFiles.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(java.io.File)
	 */
	public void addOutputData(File file) {
		if(!this.vOutputData.contains(file)){
			this.vOutputData.add(file);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(java.util.List)
	 */
	public void addOutputData(List<File> files) {
		Iterator<File> itFiles = files.iterator();
		while(itFiles.hasNext()){
			this.addOutputData(itFiles.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getInputData()
	 */
	public List<File> getInputData() {
		return this.vInputData;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getOutputData()
	 */
	public List<File> getOutputData() {
		return this.vOutputData;
	}


	/*public String getWorkflowBPEL() {
		// TODO Auto-generated method stub
		return null;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(java.io.File)
	 */
	public void removeInputData(File file) {
		if(this.vInputData.contains(file)){
			this.vInputData.remove(file);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(java.util.List)
	 */
	public void removeInputData(List<File> files) {
		Iterator<File> itFiles = files.iterator();
		while(itFiles.hasNext()){
			this.removeInputData(itFiles.next());
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setOutputData(java.util.List)
	 */
	public void setOutputData(List<File> files) {
		this.vOutputData = new Vector<File>();
		Iterator<File> itFiles = files.iterator();
		while(itFiles.hasNext()){
			this.addOutputData(itFiles.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setInputData(java.util.List)
	 */
	public void setInputData(List<File> files) {
		this.vInputData = new Vector<File>();
		Iterator<File> itFiles = files.iterator();
		while(itFiles.hasNext()){
			this.addInputData(itFiles.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getWorkflowTemplate()
	 */
	public Workflow getWorkflowTemplate() {
		return this.workflow;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setWorkflowTemplate(eu.planets_project.tb.api.model.mockups.WorkflowTemplate)
	 */
	public void setWorkflowTemplate(Workflow workflow) {
		this.workflow = (WorkflowImpl)workflow;
	}
}
