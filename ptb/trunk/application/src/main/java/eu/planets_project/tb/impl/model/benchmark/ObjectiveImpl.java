/**
 * 
 */
package eu.planets_project.tb.impl.model.benchmark;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author alindley
 *
 */
@Entity
public class ObjectiveImpl implements
		eu.planets_project.tb.api.model.benchmark.Objective {
	
	@Id
	@GeneratedValue
	private long lObjectiveID;
	private String sName, sDescription, sValue, sWeight, sPath, sVersion;
	
	public ObjectiveImpl(){
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getObjectiveDescription()
	 */
	public String getObjectiveDescription() {
		return this.sDescription;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getObjectiveID()
	 */
	public long getObjectiveID() {
		return this.lObjectiveID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getObjectiveName()
	 */
	public String getObjectiveName() {
		return this.sName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getObjectivePath()
	 */
	public String getObjectivePath() {
		return this.sPath;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getValue()
	 */
	public String getValue() {
		return this.sValue;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getWeight()
	 */
	public String getWeight() {
		return this.sWeight;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.sValue = value;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#setWeight(java.lang.String)
	 */
	public void setWeight(String weight) {
		this.sWeight = weight;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#getVersion()
	 */
	public String getVersion() {
		return this.sVersion;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Objective#setVersion(java.lang.String)
	 */
	public void setVersion(String version) {
		this.sVersion = version;
	}

}
