/**
 * 
 */
package eu.planets_project.tb.impl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentResources implements
		eu.planets_project.tb.api.model.ExperimentResources,
		java.io.Serializable {
	
	@Id
	@GeneratedValue
	private long lExpRessourceID;
	private int iNumberOfFiles;
	
	public long getExperimentResourcesID(){
		return this.lExpRessourceID;
	}
	
	private void setExperimentResourcesID(long lID){
		this.lExpRessourceID = lID;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentResources#getNumberOfFiles()
	 */
	public int getNumberOfFiles() {
		return this.iNumberOfFiles;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentResources#setNumberOfFiles(int)
	 */
	public void setNumberOfFiles(int nr) {
		this.iNumberOfFiles = nr;
	}

}
