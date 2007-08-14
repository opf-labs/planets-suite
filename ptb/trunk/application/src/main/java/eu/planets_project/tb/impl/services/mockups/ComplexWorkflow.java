/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups;

import eu.planets_project.tb.api.services.mockups.BPELWorkflowHandler;
import eu.planets_project.tb.api.services.mockups.Service;

/**
 * @author alindley
 *
 */
public class ComplexWorkflow implements
		eu.planets_project.tb.api.services.mockups.ComplexWorkflow {

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#addService(eu.planets_project.tb.api.services.mockups.Service)
	 */
	public int addService(Service service) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#generateBPEL()
	 */
	public BPELWorkflowHandler generateBPEL() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#getEndPoint()
	 */
	public Service getEndPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#getServicePositionInWorkflow(long)
	 */
	public int[] getServicePositionInWorkflow(long serviceID) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#getStartPoint()
	 */
	public Service getStartPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#isWorkflowConfigured()
	 */
	public boolean isWorkflowConfigured() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#removeParameterMapping(int)
	 */
	public void removeParameterMapping(int ServicePositionInWorkflow) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#removeService(long)
	 */
	public void removeService(long serviceID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#setEndPoint(eu.planets_project.tb.api.services.mockups.Service)
	 */
	public void setEndPoint(Service startService) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#setParameterMapping(long, java.lang.String, long, java.lang.String)
	 */
	public void setParameterMapping(long InputServiceID,
			String inputParameterName, long OutputServiceID,
			String inputParameter) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#setParameterMappings(long[], java.lang.String[], long[], java.lang.String[])
	 */
	public void setParameterMappings(long[] InputServiceID,
			String[] inputParameterName, long[] OutputServiceID,
			String[] inputParameter) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.ComplexWorkflow#setStartPoint(eu.planets_project.tb.api.services.mockups.Service)
	 */
	public void setStartPoint(Service startService) {
		// TODO Auto-generated method stub

	}

}
