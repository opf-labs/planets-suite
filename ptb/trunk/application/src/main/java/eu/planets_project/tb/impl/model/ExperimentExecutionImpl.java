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

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.system.SystemMonitoring;
import eu.planets_project.tb.api.system.mockup.WorkflowInvoker;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.system.mockup.WorkflowInvokerImpl;

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
	 private boolean bExecutionInProgress;
	
	public ExperimentExecutionImpl(){
		lExperimentIDRef = -1;
		bExecutionInProgress = false;
		
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

    /**
     * @param lExperimentIDRef
     */
    public void setExpeirmentRefID(long lExperimentIDRef){
        this.lExperimentIDRef = lExperimentIDRef;
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExecutionDataEntries()
	 */
	public Collection<Entry<URI, URI>> getExecutionDataEntries() {
		TestbedManager manager = new TestbedManagerImpl().getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			return exp.getExperimentSetup().getExperimentWorkflow().getDataEntries();
		}
		else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExecutionDataEntry(java.net.URI)
	 */
	public Entry<URI, URI> getExecutionDataEntry(URI inputFileRef) {
		TestbedManager manager = new TestbedManagerImpl().getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			return exp.getExperimentSetup().getExperimentWorkflow().getDataEntry(inputFileRef);
		}
		else{
			return null;
		}
	}

	public List<String> getExecutionMetadata(URI inputFile) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExecutionOutputData(java.net.URI)
	 */
	public URI getExecutionOutputData(URI inputFile) {
		TestbedManager manager = new TestbedManagerImpl().getInstance(true);
		
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			return exp.getExperimentSetup().getExperimentWorkflow().getDataEntry(inputFile).getValue();
		}
		else{
			return null;
		}
	}

	public Collection<URI> getExecutionOutputData() {
		TestbedManager manager = new TestbedManagerImpl().getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			return exp.getExperimentSetup().getExperimentWorkflow().getOutputData();
		}
		else{
			return null;
		}
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

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#executeExperiment()
	 */
	public void executeExperiment() throws Exception{
		this.setExecutionInProgress();
		WorkflowInvoker wfinvoker = new WorkflowInvokerImpl();
		wfinvoker.executeExperimentWorkflow(this.lExperimentIDRef);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#isExecutionInProgress()
	 */
	public boolean isExecutionInProgress() {
		return this.bExecutionInProgress;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#setExecutionInProgress(boolean)
	 */
	public void setExecutionInProgress() {
		this.bExecutionInProgress = true;
	}


}
