/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.GregorianCalendar;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

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
	
	@Id
	@GeneratedValue
	private long lEntityID;
	@OneToOne(cascade={CascadeType.ALL})
	private eu.planets_project.tb.impl.model.BasicPropertiesImpl basicProperties;
	//@OneToOne(cascade={CascadeType.ALL})
	private eu.planets_project.tb.impl.model.ExperimentResourcesImpl experimentResources;
	private eu.planets_project.tb.impl.model.ExperimentObjectivesImpl experimentObjectives;
	private eu.planets_project.tb.impl.services.mockups.ComplexWorkflowImpl complexWorkflow;
	private int iExperimentTypeID;
	private String sExperimentTypeName;

	
	public ExperimentSetupImpl(){
		basicProperties = new BasicPropertiesImpl();
		experimentResources = new ExperimentResourcesImpl();
		experimentObjectives = new ExperimentObjectivesImpl();
		complexWorkflow = new ComplexWorkflowImpl();
	}
	

	public eu.planets_project.tb.api.model.BasicProperties getBasicProperties() {
		return this.basicProperties;
	}

	public eu.planets_project.tb.api.model.ExperimentResources getExperimentResources() {
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
	public eu.planets_project.tb.api.services.mockups.ComplexWorkflow getExperimentWorkflow() {
		return this.complexWorkflow;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentedObjectives()
	 */
	public eu.planets_project.tb.api.model.ExperimentObjectives getExperimentedObjectives() {
		return this.experimentObjectives;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentResources(eu.planets_project.tb.api.model.ExperimentResources)
	 */
	public void setExperimentResources(eu.planets_project.tb.api.model.ExperimentResources experimentResources) {
		this.experimentResources = (ExperimentResourcesImpl)experimentResources;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBasicProperties(eu.planets_project.tb.api.model.BasicProperties)
	 */
	public void setBasicProperties(eu.planets_project.tb.api.model.BasicProperties props) {
		this.basicProperties = (BasicPropertiesImpl) props;
	}

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
	public void setExperimentedObjectives(
			eu.planets_project.tb.api.model.ExperimentObjectives exploredObjectives) {
		this.experimentObjectives = (ExperimentObjectivesImpl) exploredObjectives;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setWorkflow(eu.planets_project.tb.api.services.mockups.ComplexWorkflow)
	 */
	public void setWorkflow(eu.planets_project.tb.api.services.mockups.ComplexWorkflow workflow) {
		this.complexWorkflow = (ComplexWorkflowImpl) workflow;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.model.ExperimentPhase#getEntityID()
	 */
	public long getEntityID() {
		return this.lEntityID;
	}
	
	private void setEntityID(long entityID){
		this.lEntityID = entityID;
	}
	
}
