package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.workflow.ServiceCallConfigs;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowContext;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.DigitalObject;


/**
 * An implementation of this interface can extend the WorkflowTemplateHelper util object, which provides all
 * methods that are required for the Factory for building a WorkflowInstance by reflection
 * except execute() and describe() - which need to be implemented by a WorkflowTemplate provider.
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 12.02.2009
 *
 */
public interface WorkflowTemplate extends Serializable{
	
	//TODO: this should be included into the properties registry
	/** External property key for original file format for migration */
	public static final String SER_PARAM_MIGRATE_FROM = "planets:service/migration/input/migrate_from_fmt";
	/** External property key for target file format for migration */
	public static final String SER_PARAM_MIGRATE_TO = "planets:service/migration/input/migrate_to_fmt";
	/** External property key for service parameters */
	public static final String SER_PARAMS = "planets:service/input/parameters";
	 
	 /**
	  * Defines the usage of a given parameter.
	  * It can either be uses as configuration for:
	  * a) a given WorkflowService
	  * b) a general Parameter (currently not available)
	  */
	 public static enum ParameterType {
		  /** Service Parameter type */ ServiceParameter,
		  /** General Parameter type */ GeneralParameter
     }
	
	/**
	 * Checks if a given Object Type is in the range of valid PlanetsServiceTypes e.g.
	 * //e.g. eu.planets_project.services.identify.Identify
	 * This information is extracted by calling: f.getType().getCanonicalName();
	 * @param declaredServiceField
	 * @return true if service type is supported 
	 */
	public boolean isServiceTypeSupported(Field declaredServiceField);
	
	/**
	 * Returns a list of all supported PlanetsServiceTypes
	 * @return a java.util.List of Strings denoting supported service types
	 */
	public List<String> getSupportedServiceTypes();

	
	/**
	 * Reflects a list of all declared services (used within the workflow template's wf) that are
	 * a) in the list of getSupportedServiceTypes();
	 * b) use private or public modifiers within the implementing class
	 * no corresponding setDeclaredWFServices as this is information is extracted implicitly from the code
	 * @return List of java.lang.reflect.Field; of the PlanetsService objects
	 */
	public List<Field> getDeclaredWFServices();
	
	/**
	 * Reflects a list of all declared services names
	 * @see #getDeclaredWFServices()
	 * @return A java.util.List of Strings giving the declared workflow service names
	 */
	public List<String> getDeclaredWFServiceNames();
	
	/**
	 * A setter for the workflow's payload to start upon.
	 * @param data The java.util.List of DigitalObjects to use as payload
	 */
	public void setData(List<DigitalObject> data);

	/**
	 * A getter for the workflow's payload to start upon.
	 * @return the payload java.util.List of DigitalObjects 
	 */
	public List<DigitalObject> getData();
	
	/*
	 * TODO AL: Are we mixing up WorkflowTemplate and WorkflowInstance information here?
	 * Actually it's the same for the data. Taking this into consideration, there no longer 
	 * is a need for a separate WorkflowInstance interface and implementation.
	 */
	/**
	 * Setter for the WorkflowInstance's java.util.UUID
	 * @param id the WorkflowInstance java.util.UUID to be used
	 */
	public void setWorkflowInstanceID(UUID id);
	/**
	 * Getter for the WorkflowInstance's UUID
	 * @return the WorkflowInstance's java.util.UUID
	 */
	public UUID getWorklowInstanceID();
	
	/**
	 * Returns an Agent that describes the WEE the template is processed by
	 * @return the Agent
	 */
	public Agent getWEEAgent();

	/**
	 * SSet the Agent here
	 * @param agent
	 */
	public void setWEEAgent(Agent agent);
	
	/**
	 * Adds all additional service call specific information for a service which cannot be captured within the Service stub itself
	 * but that are handed over by the xml config and that are required for invoking a certain operation.
	 * e.g. input/output format parameter for migration calls  
	 * @param forService
	 * @param serCallConfigs
	 */
	public void setServiceCallConfigs(PlanetsService forService, ServiceCallConfigs serCallConfigs);
	/**
	 * @param forService the service for which call information requested
	 * @return the service call config information
	 */
	public ServiceCallConfigs getServiceCallConfigs(PlanetsService forService);
	
	/**
	 * Contains the workflowContext that's for this template
	 * @param wfContext
	 */
	public void setWorkflowContext(WorkflowContext wfContext);
	/**
	 * @return the template's WorkflowContext
	 */
	public WorkflowContext getWorkflowContext();
	
	
    /**
     * Takes a DigitalObject (Content by value or Content by reference) and persists it within the
     * locally available JCR repository (by using the JCRDigitalObjectManager). The returned DigitalObjects
     * always are returned with Content by reference pointing to a http content resolver servlet together with
     * an Event indicating this action.
     * In case of an exception the original object + an event indicating the failure is returned
     * @param digoToStore
     * @return
     */
    //public DigitalObject storeDigitalObjectInJCR(DigitalObject digoToStore);
    
    /**
     * a shortcut for storing a digital object in the default data registry
     * takes a digital object and stores it within the default DataRegistry (i.e. default DigitalObjectManager)
     * @param digoToStore
     * @see WorkflowTemplate#getDataRegistry()
     * @return the URI identifying the the Digital Object Stored 
     * @throws DigitalObjectManagerNotFoundException 
     * @throws DigitalObjectNotStoredException 
     */
    public URI storeDigitalObject(DigitalObject digoToStore) throws DigitalObjectManagerNotFoundException, DigitalObjectNotStoredException;
    
    /**
     * a shortcut for storing a digital object in a specified data repository
     * @param digoToStore
     * @param repositoryID the data repository identifier specified in the planets:// namespace.
     * e.g. 'planets://localhost:8080/dr/planets-jcr'
     * @param objectLocation The suggested URI to associate with the stored object
     * @return The URI identifying the object stored 
     * @throws DigitalObjectManagerNotFoundException
     * @throws DigitalObjectNotStoredException
     */
    public URI storeDigitalObjectInRepository(URI objectLocation, DigitalObject digoToStore, URI repositoryID) throws DigitalObjectManagerNotFoundException, DigitalObjectNotStoredException;

    /**
     * @param digoToStore
     * @param repositoryID
     * @return The URI identifying the object stored 
     * @throws DigitalObjectManagerNotFoundException
     * @throws DigitalObjectNotStoredException
     */
    public URI storeDigitalObjectInRepository(DigitalObject digoToStore, URI repositoryID) throws DigitalObjectManagerNotFoundException, DigitalObjectNotStoredException;

    
    /**
     * returns a handle to the DataRegistry which can then be used to subsequently storing digital objects
     * in a workflow template
     * @return a DataRegistry interface
     */
    public DataRegistry getDataRegistry();
    
    /**
     * a shortcut for retrieving a digital object stored within a data registry
     * @param digitalObjectRef A java.net.URI identifying a DigitalObject
     * @return a digital object stored within a data registry
     * @throws DigitalObjectNotFoundException 
     */
    public DigitalObject retrieveDigitalObjectDataRegistryRef(URI digitalObjectRef) throws DigitalObjectNotFoundException;
    
    /**
     * Get the workflowResult object that's used to record information for this call
     * @return the workflowResult object that's used to record information for this call
     */
    public WorkflowResult getWFResult();
    
    /**
     * Adds a workflow item to the workflowResult object that's used to record information for this call
     * @param wfResultItem
     */
    public void addWFResultItem(WorkflowResultItem wfResultItem);
    
    /**
     * Returns a ReportingLog object that's being used to record the workflow's execution
     * @return a ReportingLog object that's being used to record the workflow's execution
     */
    public ReportingLog getWorkflowReportingLogger();
    
    /**
     * This method is used for setting up a workflow, used to avoid repetition
     * in the execute method. 
     * @return the initialised WorkflowResult
     */
    public WorkflowResult initializeExecution();
    
    /**
	 * This method contains the workflow's logic: e.g. branching, decision making etc. is pre-defined within this method
	 * - mapping of service inputs and outputs
	 * - writing results to the registries and data model
	 * - calling workflow services on the available digitalObjects (data)
	 * @param dio a digital object
	 * @return the WorkflowResult post execution
	 * no Exceptions thrown - all information on execution success, etc. is contained within the WorkflowResult
	 */
	public WorkflowResult execute(DigitalObject dio);
	
	/**
	 * Build and attache items for finalizing the execution overall execution for this template
	 * e.g. overall success statements, overall workflow execution time, log-references, etc.
	 * @return The final WorkflowResult
	 */
	public WorkflowResult finalizeExecution();
	
	/**
	 * @return A java.lang.String description of the workflow
	 */
	public String describe();
	
	

}
