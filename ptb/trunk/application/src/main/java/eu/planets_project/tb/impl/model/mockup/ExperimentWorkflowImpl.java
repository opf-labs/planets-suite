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
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(URI)
	 */
	public void addInputData(URI uri) {
		if(!this.vInputData.contains(uri)&&!bIsExecuted){
			//now check if this corresponds to the 
			this.vInputData.add(uri);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(java.util.List)
	 */
	public void addInputData(List<URI> uris) {
		Iterator<URI> itURIs = uris.iterator();
		while(itURIs.hasNext()){
			this.addInputData(itURIs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(URI)
	 */
	public void addOutputData(URI uri) {
		if(!this.vOutputData.contains(uri)){
			this.vOutputData.add(uri);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(java.util.List)
	 */
	public void addOutputData(List<URI> URIs) {
		Iterator<URI> itURIs = URIs.iterator();
		while(itURIs.hasNext()){
			this.addOutputData(itURIs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getInputData()
	 */
	public List<URI> getInputData() {
		return this.vInputData;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getOutputData()
	 */
	public List<URI> getOutputData() {
		return this.vOutputData;
	}


	/*public String getWorkflowBPEL() {
		// TODO Auto-generated method stub
		return null;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(URI)
	 */
	public void removeInputData(URI uri) {
		if(this.vInputData.contains(uri)&&!bIsExecuted){
			this.vInputData.remove(uri);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(java.util.List)
	 */
	public void removeInputData(List<URI> URIs) {
		Iterator<URI> itURIs = URIs.iterator();
		while(itURIs.hasNext()){
			this.removeInputData(itURIs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setOutputData(java.util.List)
	 */
	public void setOutputData(List<URI> URIs) {
		this.vOutputData = new Vector<URI>();
		Iterator<URI> itURIs = URIs.iterator();
		while(itURIs.hasNext()){
			this.addOutputData(itURIs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setInputData(java.util.List)
	 */
	public void setInputData(List<URI> URIs) {
		this.vInputData = new Vector<URI>();
		Iterator<URI> itURIs = URIs.iterator();
		while(itURIs.hasNext()){
			this.addInputData(itURIs.next());
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
	

}
