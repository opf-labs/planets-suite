/**
 * 
 */
package eu.planets_project.tb.impl.model.mockup;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

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
	//private Vector<URI> vInputData, vOutputData;
	private HashMap<URI,URI> hmInputOutputData;
	@OneToOne(cascade={CascadeType.PERSIST})
	private WorkflowImpl workflow;
	
	public ExperimentWorkflowImpl(Workflow template) {
		//vInputData = new Vector<URI>();
		//vOutputData = new Vector<URI>();
		workflow = (WorkflowImpl)template;
		//Info: HashMap<InputURI,OutputURI>
		hmInputOutputData = new HashMap<URI,URI>();
	}
	
	//Default Constructor required for Entity Annotation
	private ExperimentWorkflowImpl(){
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#addInputData(java.net.URI)
	 */
	public void addInputData(URI fileRef) {
		if(!this.hmInputOutputData.containsKey(fileRef)){
			//add new InputFileRef and set OutputFileRef null
			this.hmInputOutputData.put(fileRef, null);
			//add Mapping
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
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getInputData()
	 */
	public Collection<URI> getInputData() {
		return this.hmInputOutputData.keySet();
	}


	/*public String getWorkflowBPEL() {
		// TODO Auto-generated method stub
		return null;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#removeInputData(java.net.URI)
	 */
	public void removeInputData(URI fileRef) {
		//InputData represented by keys
		if(this.hmInputOutputData.keySet().contains(fileRef)){
			this.hmInputOutputData.remove(fileRef);
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
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#setInputData(java.util.List)
	 */
	public void setInputData(List<URI> fileRefs) {
		this.hmInputOutputData = new HashMap<URI,URI>();
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
		this.workflow = (WorkflowImpl)workflow;
	}
	
	
	/*private String getMIMEType(URI filRef){
		//TODO
		return null;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getDataEntries()
	 */
	public Collection<Entry<URI, URI>> getDataEntries() {
		return this.hmInputOutputData.entrySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getDataEntry(java.net.URI)
	 */
	public Entry<URI, URI> getDataEntry(URI inputFileRef) {
		if(this.hmInputOutputData.containsKey(inputFileRef)){
			URI outputFileRef = this.hmInputOutputData.get(inputFileRef);
			HashMap<URI,URI> hmRet = new HashMap<URI,URI>();
			hmRet.put(inputFileRef, outputFileRef);
			Iterator<Entry<URI,URI>> itRet = hmRet.entrySet().iterator();
			while(itRet.hasNext()){
				return itRet.next();
			}
			return null;
		}else{
			//inputFileRef not known
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#getOutputData()
	 */
	public Collection<URI> getOutputData() {
		Vector<URI> vRet = new Vector<URI>();
		Iterator<URI> itOutput = this.hmInputOutputData.values().iterator();
		while(itOutput.hasNext()){
			URI output = itOutput.next();
			if(output!=null){
				vRet.add(output);
			}
		}
		return vRet;
	}
	


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#setOutputData(java.util.Collection)
	 */
	public void setOutputData(Collection<Entry<URI, URI>> ioFileRefs) {
		Iterator<Entry<URI,URI>> itIOFiles = ioFileRefs.iterator();
		while(itIOFiles.hasNext()){
			this.setOutputData(itIOFiles.next());
		}
	}

	public void setOutputData(URI inputFileRef, URI outputFileRef) {
		if(this.hmInputOutputData.containsKey(inputFileRef)){
			this.hmInputOutputData.put(inputFileRef, outputFileRef);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentWorkflow#setOutputData(java.util.Map.Entry)
	 */
	public void setOutputData(Entry<URI, URI> ioFileRef) {
		//check if the inputURI is known - don't care about what's the output data (e.g. null allowed)
		if(this.hmInputOutputData.keySet().contains(ioFileRef.getKey())){
			this.hmInputOutputData.put(ioFileRef.getKey(), ioFileRef.getValue());
		}
	}
}
