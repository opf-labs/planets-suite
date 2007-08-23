

package eu.planets_project.tb.api.services.mockups;


/**
 * @author alindley
 * "Predefined workflows" or "Complex workflows" are workflows coming from the IF Workflow Designer (or
 * any other BPEL compliant tool) and already contain the actual service orchestration, the
 * configuration of services as well as the selection and chaining of data. All this information can
 * be defined and modelled within the IF Workflow Designer and is finally exported and
 * expressed as a BPEL file.
 *
 */
public interface ComplexWorkflow {
	
	public Service getStartPoint();
	public void setStartPoint(Service startService);
	
	public Service getEndPoint();
	public void setEndPoint(Service startService);
	
	/**
	 * This method adds a Service to the workflow and returns the position where in 
	 * the chain it has been added
	 * @param service
	 * @return
	 */
	public int addService(Service service);
	public void removeService(long lServiceID);
	
	public void setParameterMapping(long InputServiceID, String sInputParameterName, long OutputServiceID, String sInputParameter);
	/**
	 * This method would remove the parameter mapping for the given position
	 * e.g. ServicePositionInWorkflow 1 would remove mappigns for Service1-Service2
	 * @param ServicePositionInWorkflow
	 */
	public void removeParameterMapping(int ServicePositionInWorkflow);
	/**
	 * The list of arrays must have the same amount of items for every parameter.
	 * mapping: input[1,1], output[1,1]
	 */
	public void setParameterMappings(long InputServiceID[], String sInputParameterName[], long OutputServiceID[], String sInputParameter[]);
	
	/**
	 * The first Service is assigned position zero.
	 * @param lServiceID
	 * @return
	 */
	public int[] getServicePositionInWorkflow(long lServiceID);
	
	/**
	 * This method indicates if all input and output mappings betweent the services
	 * have been assigned properly.
	 * @return
	 */
	public boolean isWorkflowConfigured();
	
	/**
	 * @return BPEL for this ExperimentWorkflow
	 */
	public BPELWorkflowHandler generateBPEL();

}
