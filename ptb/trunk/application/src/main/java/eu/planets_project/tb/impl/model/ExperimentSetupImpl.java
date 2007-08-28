/**
 * 
 */
package eu.planets_project.tb.impl.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.ExperimentObjectives;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.services.mockups.ComplexWorkflow;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentObjectivesImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;
import eu.planets_project.tb.impl.services.mockups.ComplexWorkflowImpl;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentSetupImpl extends ExperimentPhaseImpl implements
		eu.planets_project.tb.api.model.ExperimentSetup,
		java.io.Serializable{
	
	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	@OneToOne(cascade={CascadeType.ALL})
	private BasicPropertiesImpl basicProperties;
	//@OneToOne(cascade={CascadeType.ALL})
	private ExperimentResourcesImpl experimentResources;
	private ExperimentObjectivesImpl experimentObjectives;
	private ComplexWorkflowImpl complexWorkflow;
	private int iExperimentTypeID;
	private String sExperimentTypeName;

	
	public ExperimentSetupImpl(){
		basicProperties = new BasicPropertiesImpl();
		experimentResources = new ExperimentResourcesImpl();
		experimentObjectives = new ExperimentObjectivesImpl();
		complexWorkflow = new ComplexWorkflowImpl();
	}
	

	public BasicProperties getBasicProperties() {
		return this.basicProperties;
	}

	public ExperimentResources getExperimentResources() {
		return this.experimentResources;
	}

	public int getExperimentTypeID() {
		return this.iExperimentTypeID;
	}

	public String getExperimentTypeName() {
		return this.sExperimentTypeName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentWorkflow()
	 */
	public ComplexWorkflow getExperimentWorkflow() {
		return this.complexWorkflow;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentedObjectives()
	 */
	public ExperimentObjectives getExperimentedObjectives() {
		return this.experimentObjectives;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentResources(eu.planets_project.tb.api.model.ExperimentResources)
	 */
	public void setExperimentResources(ExperimentResources experimentResources) {
		this.experimentResources = (ExperimentResourcesImpl)experimentResources;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBasicProperties(eu.planets_project.tb.api.model.BasicProperties)
	 */
	public void setBasicProperties(BasicProperties props) {
		this.basicProperties = (BasicPropertiesImpl) props;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentType(int)
	 */
	public void setExperimentType(int typeID) {
		ExperimentTypesImpl finalsExperimentTypes = new ExperimentTypesImpl();
		boolean bOK= finalsExperimentTypes.checkExperimentTypeIDisValid(typeID);
		if  (bOK){
			this.iExperimentTypeID = typeID;
			this.sExperimentTypeName = finalsExperimentTypes.getExperimentTypeName(typeID);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentedObjectives(eu.planets_project.tb.api.model.ExperimentObjectives)
	 */
	public void setExperimentedObjectives(ExperimentObjectives exploredObjectives) {
		this.experimentObjectives = (ExperimentObjectivesImpl) exploredObjectives;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setWorkflow(eu.planets_project.tb.api.services.mockups.ComplexWorkflow)
	 */
	public void setWorkflow(ComplexWorkflow workflow) {
		this.complexWorkflow = (ComplexWorkflowImpl) workflow;
	}
	
}
