/**
 * 
 */
package eu.planets_project.tb.impl.model.mockup;

import java.net.URI;
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
	private Vector<URI> vInputData, vOutputData;
	@OneToOne(cascade={CascadeType.ALL})
	private WorkflowImpl workflow;
	private boolean bIsExecuted;
	
	public ExperimentWorkflowImpl(Workflow template) {
		vInputData = new Vector<URI>();
		vOutputData = new Vector<URI>();
		workflow = (WorkflowImpl)template;
		bIsExecuted = false;
	}
	
	//Default Constructor required for Entity Annotation
	private ExperimentWorkflowImpl(){
		
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#addInputData(java.net.URI)
	 */
	public void addInputData(URI fileRef) {
		if(!this.vInputData.contains(fileRef)&&!bIsExecuted){
			//now check if this corresponds to the 
			this.vInputData.add(fileRef);
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#addInputData(java.util.List)
	 */
	public void addInputData(List<URI> fileRefs) {
		Iterator<URI> itFileRefs = fileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addInputData(itFileRefs.next());
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#addOutputData(java.net.URI)
	 */
	public void addOutputData(URI fileRef) {
		if(!this.vOutputData.contains(fileRef)){
			this.vOutputData.add(fileRef);
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#addOutputData(java.util.List)
	 */
	public void addOutputData(List<URI> fileRefs) {
		Iterator<URI> itFileRefs = fileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addOutputData(itFileRefs.next());
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getInputData()
	 */
	public List<URI> getInputData() {
		return this.vInputData;
	}


	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getOutputData()
	 */
	public List<URI> getOutputData() {
		return this.vOutputData;
	}


	/*public String getWorkflowBPEL() {
		// TODO Auto-generated method stub
		return null;
	}*/



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#removeInputData(java.net.URI)
	 */
	public void removeInputData(URI fileRef) {
		if(this.vInputData.contains(fileRef)&&!bIsExecuted){
			this.vInputData.remove(fileRef);
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#removeInputData(java.util.List)
	 */
	public void removeInputData(List<URI> fileRefs) {
		Iterator<URI> itFileRefs = fileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.removeInputData(itFileRefs.next());
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#setOutputData(java.util.List)
	 */
	public void setOutputData(List<URI> fileRefs) {
		this.vOutputData = new Vector<URI>();
		Iterator<URI> itFileRefs = fileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addOutputData(itFileRefs.next());
		}
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#setInputData(java.util.List)
	 */
	public void setInputData(List<URI> fileRefs) {
		this.vInputData = new Vector<URI>();
		Iterator<URI> itFileRefs = fileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addInputData(itFileRefs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getWorkflowTemplate()
	 */
	public Workflow getWorkflow() {
		return this.workflow;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setWorkflowTemplate(eu.planets_project.tb.api.model.mockups.WorkflowTemplate)
	 */
	public void setWorkflow(Workflow workflow) {
		if(!bIsExecuted){
			this.workflow = (WorkflowImpl)workflow;
		}
	}
	
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String getMIMEType(URI filRef){
		//TODO
		return null;
	}
}
