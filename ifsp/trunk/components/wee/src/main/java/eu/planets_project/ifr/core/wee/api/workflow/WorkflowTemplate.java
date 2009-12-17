package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import eu.planets_project.ifr.core.wee.api.ReportingLog;
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
	
	 /** External property keys */
	//TODO: this should be included into the properties registry
	public static final String SER_PARAM_MIGRATE_FROM = "planets:service/migration/input/migrate_from_fmt";
	public static final String SER_PARAM_MIGRATE_TO = "planets:service/migration/input/migrate_to_fmt";
	public static final String SER_PARAMS = "planets:service/input/parameters";
	 
	 /**
	  * Defines the usage of a given parameter. It can either be uses as configuration for:
	  * a) a given WorkflowService
	  * b) general Parameter (currently not available)
	  */
	 public static enum ParameterType {ServiceParameter,GeneralParameter};
	
	/**
	 * Checks if a given Object Type is in the range of valid PlanetsServiceTypes e.g.
	 * //e.g. eu.planets_project.services.identify.Identify
	 * This information is extracted by calling: f.getType().getCanonicalName();
	 * @param declaredServiceField
	 * @return
	 */
	public boolean isServiceTypeSupported(Field declaredServiceField);
	
	/**
	 * Returns a list of all supported PlanetsServiceTypes
	 * @return
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
	 * @see getDeclaredWFServices()
	 * @return
	 */
	public List<String> getDeclaredWFServiceNames();
	
	/**
	 * A setter and getter for the workflow's payload to start upon.
	 * @param data
	 */
	public void setData(List<DigitalObject> data);
	public List<DigitalObject> getData();
	
	/*
	 * TODO AL: Are we mixing up WorkflowTemplate and WorkflowInstance information here?
	 * Actually it's the same for the data. Taking this into consideration, there no longer 
	 * is a need for a separate WorkflowInstance interface and implementation.
	 */
	/**
	 * provide access to the WorkflowInstance's UUID here
	 * @param id
	 */
	public void setWorkflowInstanceID(UUID id);
	public UUID getWorklowInstanceID();
	
	/**
	 * Returns an Agent that describes the WEE the template is processed by
	 * @return
	 */
	public Agent getWEEAgent();
	public void setWEEAgent(Agent agent);
	
	/**
	 * Adds all additional service call specific information for a service which cannot be captured within the Service stub itself
	 * but that are handed over by the xml config and that are required for invoking a certain operation.
	 * e.g. input/output format parameter for migration calls  
	 * @param forService
	 * @param serCallConfigs
	 */
	public void setServiceCallConfigs(PlanetsService forService, ServiceCallConfigs serCallConfigs);
	public ServiceCallConfigs getServiceCallConfigs(PlanetsService forService);
	
	/**
	 * Contains the workflowContext that's for this template
	 * @param wfContext
	 */
	public void setWorkflowContext(WorkflowContext wfContext);
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
    public DigitalObject storeDigitalObjectInJCR(DigitalObject digoToStore);
    
    /**
     * Get the workflowResult object that's used to record information for this call
     * @return
     */
    public WorkflowResult getWFResult();
    
    /**
     * Adds a workflow item to the workflowResult object that's used to record information for this call
     * @param wfResultItem
     */
    public void addWFResultItem(WorkflowResultItem wfResultItem);
    
    /**
     * Returns a ReportingLog object that's being used to record the workflow's execution
     * @return
     */
    public ReportingLog getWorkflowReportingLogger();
    
	/**
	 * This method contains the workflow's logic: e.g. branching, decision making etc. is pre-defined within this method
	 * - mapping of service inputs and outputs
	 * - writing results to the registries and data model
	 * - calling workflow services on the available digitalObjects (data)
	 * @return
	 * no Exceptions thrown - all information on execution success, etc. is contained within the WorkflowResult
	 */
	public WorkflowResult execute();
	
	
	/**
	 * 
	 * @return
	 */
	public String describe();
	
	

}
