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

import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;

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
	private String sExperimentTypeID;

	/**
	 * 
	 */
	public WorkflowImpl() {
		id = -1;
		vServiceWorkflow = new Vector<Service>();
		vInputMimeTypes = new Vector<String>();
		vOutputMimeTypes = new Vector<String>();
		sWorkflowName = new String();
		sToolType = new String();
		sExperimentTypeID = new String();
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
	private void setEntityID(long lEntityID){
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
		boolean bRet = false;
		try{
			//Test1
			int iNumbOfServices = this.vServiceWorkflow.size();
			for(int i=0;i<iNumbOfServices;i++){
				//this checks if they are in a row or if there are any gaps
				this.vServiceWorkflow.get(i);
			}

			//Test2:
			bRet = checkInputOutputServiceType(this.vServiceWorkflow.get(0),this.vServiceWorkflow.get(iNumbOfServices-1));
			return bRet;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}
	
	/**
	 * This method checks the validity of the workflow in terms of
	 * a) if the starting service's mime-type is contained incovers the required input mime-types
	 * b) and if the last service's MIME-type covers the required output mime-types.
	 * @return
	 */
	private boolean checkInputOutputServiceType(Service startService, Service endService){
		boolean b1 = true;
		boolean b2 = true;
		Iterator<String> itInputMIMETypes = this.vInputMimeTypes.iterator();
		while(itInputMIMETypes.hasNext()){
			String mimetype = itInputMIMETypes.next();
			if(!startService.getInputMIMETypes().contains(mimetype)){
				b1 = false;
			}
		}
		Iterator<String> itOutputMIMETypes = this.vOutputMimeTypes.iterator();
		while(itOutputMIMETypes.hasNext()){
			String mimetype = itOutputMIMETypes.next();
			if(!endService.getOutputMIMETypes().contains(mimetype)){
				b2 = false;
			}
		}
		return b1&&b2;
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
			this.vServiceWorkflow.get(position);
			this.vServiceWorkflow.remove(position);
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
		AdminManager manager = AdminManagerImpl.getInstance();
		return manager.getExperimentTypeName(this.sExperimentTypeID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getExperimentType()
	 */
	public String getExperimentType() {
		return this.sExperimentTypeID;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setExperimentType(java.lang.String)
	 */
	public void setExperimentType(String experimentTypeID) {
		AdminManager manager = AdminManagerImpl.getInstance();
		if(manager.getExperimentTypeIDs().contains(experimentTypeID)){
			this.sExperimentTypeID = experimentTypeID;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredInputMIMETypes(java.util.List)
	 */
	public void addRequiredInputMIMETypes(List<String> mimeTypes) {
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.addRequiredInputMIMEType(itMimeTypes.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredOutputMIMETypes(java.util.List)
	 */
	public void addRequiredOutputMIMETypes(List<String> mimeTypes) {
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.addRequiredOutputMIMEType(itMimeTypes.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredInputMIMETypes(java.util.List)
	 */
	public void removeRequiredInputMIMETypes(List<String> mimeTypes) {
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.removeRequiredInputMIMEType(itMimeTypes.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredOutputMIMETypes(java.util.List)
	 */
	public void removeRequiredOutputMIMETypes(List<String> mimeTypes) {
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.removeRequiredOutputMIMEType(itMimeTypes.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setRequiredInputMIMETypes(java.util.List)
	 */
	public void setRequiredInputMIMETypes(List<String> mimeTypes) {
		this.vInputMimeTypes = new Vector<String>();
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.addRequiredInputMIMEType(itMimeTypes.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setRequiredOutputMIMETypes(java.util.List)
	 */
	public void setRequiredOutputMIMETypes(List<String> mimeTypes) {
		this.vOutputMimeTypes = new Vector<String>();
		Iterator<String> itMimeTypes = mimeTypes.iterator();
		while(itMimeTypes.hasNext()){
			this.addRequiredOutputMIMEType(itMimeTypes.next());
		}
	}


}
