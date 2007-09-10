/**
 * 
 */
package eu.planets_project.tb.impl.model.mockup;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import eu.planets_project.tb.api.model.finals.ExperimentTypes;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;

/**
 * @author alindley
 *
 */

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DiscrCol")
public class WorkflowImpl implements Workflow, java.io.Serializable {
	
	@Id
	@GeneratedValue
	private long id;
	private Vector<Service> vServiceWorkflow;  
	private Vector<String> vInputMimeTypes, vOutputMimeTypes;
	private String sWorkflowName, sToolType;
	private int iExperimentType;
	private Vector<Service> vWorkflow;

	/**
	 * 
	 */
	public WorkflowImpl() {
		vServiceWorkflow = new Vector<Service>();
		vInputMimeTypes = new Vector<String>();
		vOutputMimeTypes = new Vector<String>();
		vWorkflow		= new Vector<Service>();
		sWorkflowName = new String();
		sToolType = new String();
		iExperimentType = -1;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowTemplate#addWorkflowService(int, eu.planets_project.tb.api.services.mockups.Service)
	 */
	public void addWorkflowService(int position, Service service) {
		try{
			//test if already a service is registered in this position in the chain
			this.vServiceWorkflow.get(position);
		}
		catch(ArrayIndexOutOfBoundsException e){
			this.vServiceWorkflow.add(position,service);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowTemplate#getWorkflowService(int)
	 */
	public Service getWorkflowService(int position) {
		return this.vServiceWorkflow.get(position);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowTemplate#getWorkflowServices()
	 */
	public List<Service> getWorkflowServices() {
		return this.vServiceWorkflow;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredInputMIMEType(java.lang.String)
	 */
	public void addRequiredInputMIMEType(String mimeType) {
		if(!this.vInputMimeTypes.contains(mimeType)){
			StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
			if (tokenizer.countTokens()==3){
				this.vInputMimeTypes.add(mimeType);
			}
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredOutputMIMEType(java.lang.String)
	 */
	public void addRequiredOutputMIMEType(String mimeType) {
		if(!this.vOutputMimeTypes.contains(mimeType)){
			StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
			if (tokenizer.countTokens()==3){
				this.vOutputMimeTypes.add(mimeType);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getEntityID()
	 */
	public long getEntityID() {
		return this.id;
	}
	
	/**
	 * @param lEntityID
	 */
	public void setEntityID(long lEntityID){
		this.id = lEntityID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getName()
	 */
	public String getName() {
		return this.sWorkflowName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getRequiredInputMIMETypes()
	 */
	public List<String> getRequiredInputMIMETypes() {
		return this.vInputMimeTypes;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getRequiredOutputMIMETypes()
	 */
	public List<String> getRequiredOutputMIMETypes() {
		return this.vOutputMimeTypes;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getToolType()
	 */
	public String getToolType() {
		return this.sToolType;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#isValidWorkflow()
	 */
	public boolean isValidWorkflow() {
		//At the moment: returns true if there's no gap between the services
		try{
			int iNumbOfServices = this.vWorkflow.size();
			for(int i=0;i<iNumbOfServices;i++){
				//this checks if they are in a row or if there are any gaps
				this.vWorkflow.get(i);
			}
			return true;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredInputMIMEType(java.lang.String)
	 */
	public void removeRequiredInputMIMEType(String mimeType) {
		if(this.vInputMimeTypes.contains(mimeType)){
			this.vInputMimeTypes.remove(mimeType);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredOutputMIMEType(java.lang.String)
	 */
	public void removeRequiredOutputMIMEType(String mimeType) {
		if(this.vOutputMimeTypes.contains(mimeType)){
			this.vOutputMimeTypes.remove(mimeType);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeWorkflowService(int)
	 */
	public void removeWorkflowService(int position) {
		try{
			this.vWorkflow.get(position);
			this.vWorkflow.remove(position);
		}catch(ArrayIndexOutOfBoundsException e){
			
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setName(java.lang.String)
	 */
	public void setName(String workflowName) {
		this.sWorkflowName = workflowName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setToolType(java.lang.String)
	 */
	public void setToolType(String toolType) {
		this.sToolType = toolType;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getExpeirmentTypeName()
	 */
	public String getExpeirmentTypeName() {
		return new ExperimentTypesImpl().getExperimentTypeName(this.iExperimentType);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getExperimentType()
	 */
	public int getExperimentType() {
		return this.iExperimentType;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setExperimentType(int)
	 */
	public void setExperimentType(int experimentType) {
		ExperimentTypes types = new ExperimentTypesImpl();
		if(types.checkExperimentTypeIDisValid(experimentType)){
			this.iExperimentType = experimentType;
		}
	}


}
