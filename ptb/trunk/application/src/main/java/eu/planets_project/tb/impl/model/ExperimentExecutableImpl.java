/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeIdentify;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.services.TestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;

/**
 * @author alindley
 * 
 * This class contains all information that is required for the experiment's service execution. 
 * This object is handed over to the service execution and results are written 
 * back to it. i.e. corresponds to the idea of an executable part of a preservation plan
 * 
 * Please note: As service currently aren't able to take http file references as input, this
 * class holds and takes only local file refs. 
 * 
 * Beware: OutputFileRef must not always be the pointer to a file. e.g. for characterisation services
 * this will correspond to a String. 
 */
@SuppressWarnings("serial")
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentExecutableImpl extends ExecutableImpl implements ExperimentExecutable, java.io.Serializable{

    /** A hashmap for the parameters */
    private HashMap<String,String> parameters = new HashMap<String,String>();
    
	//hashmap of local file refs for input and output data of service execution
	//note: C:/DATA/ rather than http://localhost:8080/testbed/
	private HashMap<String,String> hmInputOutputData = new HashMap<String,String>();
	
    /** The list of automatically measurable properties that should be measured during the experiment. */
    private Vector<String> properties = new Vector<String>();

    /** The log of executed experiment results */
//    @OneToMany(mappedBy="experimentExecutable")
    private Vector<BatchExecutionRecordImpl> executionRecords = new Vector<BatchExecutionRecordImpl>();
    //private List<BatchExecutionRecordImpl> executionRecords;
    
    /** The workflow-type is also stored here. */
    private String workflowType;
    
	//no one-to-one annotation, as we want to persist this data by value and not per reference
	private TestbedServiceTemplateImpl tbServiceTemplate;
	private String sSelectedServiceOperationName="";

	//A logger for this - transient: it's not persisted with this entity
    @Transient
    @XmlTransient
	private static Log log = PlanetsLogger.getLogger(ExperimentExecutableImpl.class,"testbed-log4j.xml");
	
/*	
	public ExperimentExecutableImpl(TestbedServiceTemplate template) {
		//decouple this object
		tbServiceTemplate = ((TestbedServiceTemplateImpl)template).clone();
		//sets the object's discriminator value to "experiment" and not "template"
		tbServiceTemplate.setDiscriminator(tbServiceTemplate.DISCR_EXPERIMENT);
		
	}
	*/
	
	//Default Constructor required for Entity Annotation
	public ExperimentExecutableImpl(){
	}
	
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#getParameters()
     */
    public HashMap<String, String> getParameters() {
        if( parameters == null )
            parameters = new HashMap<String,String>();
        return parameters;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#setParameters(java.util.HashMap)
     */
    public void setParameters(HashMap<String, String> pars) {
        parameters = pars;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#getProperties()
     */
    public Vector<String> getProperties() {
        return properties;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#setProperties(java.util.Vector)
     */
    public void setProperties(Vector<String> props) {
        properties = props;
    }
    
    /**
     * @return the executionRecords
     */
    public List<BatchExecutionRecordImpl> getBatchExecutionRecords() {
        return executionRecords;
    }
    
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#getNumExecutionRecords()
     */
    public int getNumBatchExecutionRecords() {
        if( this.getBatchExecutionRecords() == null ) {
            return 0;
        } else {
            return this.getBatchExecutionRecords().size();
        }
    }

    /**
     * @param executionRecords the executionRecords to set
     */
    public void setBatchExecutionRecords(List<BatchExecutionRecordImpl> executionRecords) {
        this.executionRecords = new Vector<BatchExecutionRecordImpl>(executionRecords);
    }

    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#addInputData(java.lang.String)
	 */
	public void addInputData(String localFileRef) {
		if(!this.hmInputOutputData.containsKey(localFileRef)){
			//add new InputFileRef and set OutputFileRef null
			this.hmInputOutputData.put(localFileRef, null);
			//add Mapping
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#addInputData(java.util.List)
	 */
	public void addInputData(Collection<String> localFileRefs) {
		Iterator<String> itFileRefs = localFileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addInputData(itFileRefs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#getInputData()
	 */
	public Collection<String> getInputData() {
		return this.hmInputOutputData.keySet();
	}
	
	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        if( this.getInputData() == null ) return 0;
        return this.getInputData().size();
    }

    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#removeInputData(java.lang.String)
	 */
	public void removeInputData(String localFileRef) {
		//InputData represented by keys
		if(this.hmInputOutputData.keySet().contains(localFileRef)){
			this.hmInputOutputData.remove(localFileRef);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#removeAllInputData()
	 */
	public void removeAllInputData(){
		this.hmInputOutputData = new HashMap<String,String>();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#removeInputData(java.util.List)
	 */
	public void removeInputData(Collection<String> localFileRefs) {
		Iterator<String> itFileRefs = localFileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.removeInputData(itFileRefs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#setInputData(java.util.List)
	 */
	public void setInputData(Collection<String> localFileRefs) {
		this.hmInputOutputData = new HashMap<String,String>();
		Iterator<String> itFileRefs = localFileRefs.iterator();
		while(itFileRefs.hasNext()){
			this.addInputData(itFileRefs.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#getOutputData()
	 */
	public Collection<String> getOutputData() {
		Vector<String> vRet = new Vector<String>();
		Iterator<String> itOutput = this.hmInputOutputData.values().iterator();
		while(itOutput.hasNext()){
			String output = itOutput.next();
			if(output!=null){
				vRet.add(output);
			}
		}
		return vRet;
	}
	


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#setOutputData(java.util.Collection)
	 */
	public void setOutputData(Collection<Entry<String, String>> ioLocalFileRefs) {
		Iterator<Entry<String,String>> itIOFiles = ioLocalFileRefs.iterator();
		while(itIOFiles.hasNext()){
			this.setOutputData(itIOFiles.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#setOutputData(java.lang.String, java.lang.String)
	 */
	public void setOutputData(String inputFileRef, String outputFileRef) {
		if(this.hmInputOutputData.containsKey(inputFileRef)){
			this.hmInputOutputData.put(inputFileRef, outputFileRef);
		}
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.ExperimentExecutable#setOutputData(java.util.Map.Entry)
	 */
	public void setOutputData(Entry<String, String> ioFileRef) {
		//check if the inputURI is known - don't care about what's the output data (e.g. null allowed)
		if(this.hmInputOutputData.keySet().contains(ioFileRef.getKey())){
			this.hmInputOutputData.put(ioFileRef.getKey(), ioFileRef.getValue());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getInputHttpFileRef(java.lang.String)
	 */
	public URI getInputHttpFileRef(String localFileRef) {
		DataHandler dh = new DataHandlerImpl();
		if(localFileRef!=null){
			try {
				return dh.getDownloadURI(localFileRef);
				
			} catch (FileNotFoundException e) {
				log.debug("Exception while building URI for InputFile");
			}
		}
		return null;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getAllInputHttpDataEntries()
	 */
	public Collection<URI> getAllInputHttpDataEntries(){
		List<URI> ret = new Vector<URI>();
		DataHandler dh = new DataHandlerImpl();
		Iterator<String> itInputData =  this.getInputData().iterator();
		while(itInputData.hasNext()){
			try {
				URI uri = dh.getDownloadURI(itInputData.next());
				if(uri!=null){
					ret.add(uri);
				}
			} catch (FileNotFoundException e) {
				log.debug("Exception while building URI for all Input data");
			}
			
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getOutputHttpFileRef(java.lang.String)
	 */
	public URI getOutputHttpFileRef(String localFileRef) {
		DataHandler dh = new DataHandlerImpl();
		if(localFileRef!=null){
			try {
				return dh.getDownloadURI(localFileRef);
				
			} catch (FileNotFoundException e) {
				log.debug("Exception while building URI for OutputFile");
			}
		}
		return null;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getAllMigrationOutputHttpData()
	 */
	public Collection<URI> getAllMigrationOutputHttpData(){
		List<URI> ret = new Vector<URI>();
		DataHandler dh = new DataHandlerImpl();
		Iterator<String> itOutputData =  this.getOutputData().iterator();
		while(itOutputData.hasNext()){
			try {
				URI uri = dh.getDownloadURI(itOutputData.next());
				if(uri!=null){
					ret.add(uri);
				}
			} catch (FileNotFoundException e) {
				log.debug("Exception while building URI for all output data");
			}
			
		}
		return ret;
	}
	


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getAllCharacterisationOutputHttpData()
	 */
	public Collection<String> getAllCharacterisationOutputHttpData(){

		return this.getOutputData();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getCharacterisationDataEntries()
	 */
	public Collection<Entry<String, String>> getCharacterisationDataEntries() {
		return this.hmInputOutputData.entrySet();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getOutputDataEntries()
	 */
	public Collection<Entry<String, String>> getOutputDataEntries() {
		return this.hmInputOutputData.entrySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getCharacterisationHttpDataEntries()
	 */
	public Collection<Entry<URI, String>> getCharacterisationHttpDataEntries() {
		HashMap<URI,String> hmRet = new HashMap<URI,String>();
		Iterator<Entry<String,String>> itEntries = this.hmInputOutputData.entrySet().iterator();
		
		while(itEntries.hasNext()){
			//Entry<localFileRef,characterisation result>
			Entry<String,String> entry = itEntries.next();
			URI uri = this.getInputHttpFileRef(entry.getKey());
			if(uri!=null){
				hmRet.put(uri, entry.getValue());
			}
		}
		return hmRet.entrySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getMigrationDataEntries()
	 */
	public Collection<Entry<String, String>> getMigrationDataEntries() {
		return this.hmInputOutputData.entrySet();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getMigrationHttpDataEntries()
	 */
	public Collection<Entry<URI, URI>> getMigrationHttpDataEntries() {
		HashMap<URI,URI> hmRet = new HashMap<URI,URI>();
		Iterator<Entry<String,String>> itEntries = this.hmInputOutputData.entrySet().iterator();
		
		while(itEntries.hasNext()){
			//Entry<localFileRef,characterisation result>
			Entry<String,String> entry = itEntries.next();
			URI uriInput = this.getInputHttpFileRef(entry.getKey());
			URI uriOutput = this.getOutputHttpFileRef(entry.getValue());
			
			if((uriInput!=null)&&(uriOutput!=null)){
				hmRet.put(uriInput,uriOutput);
			}
		}
		return hmRet.entrySet();
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getMigrationHttpDataEntry(java.lang.String)
	 */
	public Map.Entry<URI, URI> getMigrationHttpDataEntry(String localFileInputRef){
		if(this.hmInputOutputData.containsKey(localFileInputRef)){
			String outputFileRef = this.hmInputOutputData.get(localFileInputRef);
			HashMap<URI,URI> hmRet = new HashMap<URI,URI>();
			
			//get the URI return values for the local file ref
			URI inputFile = this.getOutputHttpFileRef(outputFileRef);
			URI outputFile = this.getInputHttpFileRef(localFileInputRef);
			if((inputFile!=null)&&(outputFile!=null)){
				hmRet.put(inputFile, outputFile);
				
				Iterator<Entry<URI,URI>> itRet = hmRet.entrySet().iterator();
				while(itRet.hasNext()){
					//return the Entry
					return itRet.next();
				}
			}
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getCharacterisationHttpDataEntry(java.lang.String)
	 */
	public Map.Entry<URI, String> getCharacterisationHttpDataEntry(String localFileInputRef){
		if(this.hmInputOutputData.containsKey(localFileInputRef)){
			String outputFileRef = this.hmInputOutputData.get(localFileInputRef);
			HashMap<URI,String> hmRet = new HashMap<URI,String>();
			
			//get the URI return values for the local file ref
			URI inputFile = this.getOutputHttpFileRef(outputFileRef);

			if(inputFile!=null){
				hmRet.put(inputFile, outputFileRef);
				
				Iterator<Entry<URI,String>> itRet = hmRet.entrySet().iterator();
				while(itRet.hasNext()){
					//return the Entry
					return itRet.next();
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#getSelectedServiceOperationName()
	 */
	public String getSelectedServiceOperationName() {
		return this.sSelectedServiceOperationName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecutable#setSelectedServiceOperationName(java.lang.String)
	 */
	public void setSelectedServiceOperationName(String sOperationName) {
		if(sOperationName!=null){
			//need to check if the registered ServiceTemplate contains this operation name
			if(this.tbServiceTemplate.getAllServiceOperationNames().contains(sOperationName)){
				this.sSelectedServiceOperationName = sOperationName;
			}
		}
		
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Executable#getServiceTemplate()
	 */
	public TestbedServiceTemplate getServiceTemplate() {
		return this.tbServiceTemplate;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Executable#setServiceTemplate(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void setServiceTemplate(TestbedServiceTemplate template) {
		this.tbServiceTemplate = (TestbedServiceTemplateImpl)template;
	}

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecutable#getWorkflow(String expType)
     */
	public ExperimentWorkflow getWorkflow() {
	    //The Workflow is transient, and so must be possible to configure on demand.
	    try {
	        return this.buildWorkflow();
	    } catch (Exception e) {
	        log.error("Error while building workflow: "+e);
	        e.printStackTrace();
	        return null;
	    }
	}

    /**
     * 
     * @param expType
     * @throws Exception
     */
    public void setWorkflowType( String expType ) throws Exception {
        this.workflowType = expType;
        this.buildWorkflow();
    }

    /**
     * 
     * @throws Exception
     */
    private ExperimentWorkflow buildWorkflow() throws Exception {
        ExperimentBean expBean = (ExperimentBean) JSFUtil.getManagedObject("ExperimentBean");
        Experiment exp = expBean.getExperiment();
        // Check for workflowType
        if( this.workflowType == null ) {
            log.error("Workflow Type is null!");
            this.workflowType = exp.getExperimentSetup().getExperimentTypeID();
            log.info("Set workflow type to: " + this.workflowType);
            if( this.workflowType == null ) return null;
        }
        
        // Invoke, depending on the experiment type, using any old WF as the source:
        // FIXME Can this be made cleaner?  What about passing back more meaningful errors?
        ExpTypeBackingBean bb = new ExpTypeIdentify();
        ExperimentWorkflow expwf = bb.getWorkflow(this.workflowType);
        if( expwf != null ) {
            expwf.setParameters(getParameters());
            return expwf;
        }
        
        // FAILED:
        if( AdminManagerImpl.isExperimentDeprecated(exp) ) {
            log.error("Executing old-style experiment - Should Not Happen!");
            throw new Exception( "Executing old-style experiment - Should Not Happen! "+this.workflowType);
            
        } else {
            log.error("Unknown experiment type: "+this.workflowType);
            throw new Exception( "Unknown experiment type: "+this.workflowType );
        }
    }

}
