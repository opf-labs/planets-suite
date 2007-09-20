/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.Entity;

import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.system.SystemMonitoring;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentExecutionImpl extends ExperimentPhaseImpl
	implements ExperimentExecution, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	 //a helper reference pointer, for retrieving the experiment in the phase
	 private long lExperimentIDRef;
	
	public ExperimentExecutionImpl(){
		lExperimentIDRef = -1;
		
		setPhasePointer(PHASE_EXPERIMENTEXECUTION);
	}
	
    /**
     * A helper reference pointer on the experiment's ID to retrieve other phases or the
     * experiment itself if this is required.
     * @return
     */
    public long getExperimentRefID(){
        return this.lExperimentIDRef;
    }

    public void setExpeirmentRefID(long lExperimentIDRef){
        this.lExperimentIDRef = lExperimentIDRef;
    }

	public Collection<Entry<URI, URI>> getExecutionDataEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	public Entry<URI, URI> getExecutionDataEntry(URI inputFileRef) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getExecutionMetadata(URI inputFile) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getExecutionOutputData(URI inputFile) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<URI> getExecutionOutputData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExecutionState(URI inputFile) {
		// TODO Auto-generated method stub
		return null;
	}

	public Calendar getScheduledExecutionDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public SystemMonitoring getSystemMonitoringData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setScheduledExecutionDate(long millis) {
		// TODO Auto-generated method stub
		
	}

	public void setScheduledExecutionDate(Calendar date) {
		// TODO Auto-generated method stub
		
	}

	public void setSystemMonitoringData(SystemMonitoring systemState) {
		// TODO Auto-generated method stub
		
	}


}
