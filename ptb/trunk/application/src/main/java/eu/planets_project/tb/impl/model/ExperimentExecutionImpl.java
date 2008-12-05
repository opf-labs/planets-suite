/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.system.mockup.SystemMonitoring;
import eu.planets_project.tb.impl.TestbedManagerImpl;

/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentExecutionImpl extends ExperimentPhaseImpl
	implements ExperimentExecution, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	//a helper reference pointer, for retrieving the experiment in the phase
    @XmlTransient
    private long lExperimentIDRef;
    // These don't seem to be used anywhere, but removing them will break the Hibernate mapping.
	private boolean bExecutionInProgress;
	private boolean bExecuted;
	
	public ExperimentExecutionImpl(){
		lExperimentIDRef = -1;
		bExecutionInProgress = false;
		bExecuted = false;
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
    public void setExperimentRefID(long lExperimentIDRef){
        this.lExperimentIDRef = lExperimentIDRef;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecution#getMigrationOutputDataEntries()
     */
    public Collection<Map.Entry<URI,URI>> getMigrationOutputDataEntries(){
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//get the migration experiment's results
				return executable.getMigrationHttpDataEntries();
			}
		}
		return null;
	}


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecution#getMigrationOutputDataEntry(java.lang.String)
     */
    public Map.Entry<URI, URI> getMigrationOutputDataEntry(URI inputFileURI){
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//check if requested inputFileURI is part of the execution input
				boolean bContains = executable.getAllInputHttpDataEntries().contains(inputFileURI);
				if(bContains){
					//find the matching entry
					Iterator<Entry<URI,URI>> itEntry = executable.getMigrationHttpDataEntries().iterator();
					while(itEntry.hasNext()){
						Entry<URI,URI> entry = itEntry.next();
						//check if we've found the matchin inputFileURI
						if(entry.getKey().equals(inputFileURI)){
							//build return value Map and then Entry
							HashMap<URI,URI> ret = new HashMap<URI,URI>();
							ret.put(entry.getKey(), entry.getValue());
							Iterator<Entry<URI,URI>> itRet = ret.entrySet().iterator();
							while(itRet.hasNext()){
								return itRet.next();
							}
						}
					}
				}
			}
		}
		return null;
	}
    
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecution#getCharacterisationOutputDataEntries()
     */
    public Collection<Map.Entry<URI, String>> getCharacterisationOutputDataEntries(){
    	TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data - this is fetched and returned
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//get the characterisation experiment's results
				return executable.getCharacterisationHttpDataEntries();
			}
		}
		return null;
    }
    
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentExecution#getCharacterisationOutputDataEntry(java.net.URI)
     */
    public Map.Entry<URI, String> getCharacterisationOutputDataEntry(URI inputFileURI){
    	TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//check if requested inputFileURI is part of the execution input
				boolean bContains = executable.getAllInputHttpDataEntries().contains(inputFileURI);
				if(bContains){
					//find the matching entry
					Iterator<Entry<URI,String>> itEntry = executable.getCharacterisationHttpDataEntries().iterator();
					while(itEntry.hasNext()){
						Entry<URI,String> entry = itEntry.next();
						//check if we've found the matchin inputFileURI
						if(entry.getKey().equals(inputFileURI)){
							//build return value Map and then Entry
							HashMap<URI,String> ret = new HashMap<URI,String>();
							ret.put(entry.getKey(), entry.getValue());
							Iterator<Entry<URI,String>> itRet = ret.entrySet().iterator();
							while(itRet.hasNext()){
								return itRet.next();
							}
						}
					}
				}
			}
		}
		return null;
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getMigrationOutputData()
	 */
	public Collection<URI> getMigrationOutputData() {
		Collection<URI> ret = new Vector<URI>();
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null && exp.getExperimentExecutable().getOutputData() != null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//get the Migration's output data (as URI)
				return executable.getAllMigrationOutputHttpData();
			}
		}
		return ret;
	}

	public Collection<String> getCharacterisationOutputData() {
		Collection<String> ret = new Vector<String>();
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				//get the Characterisation's output data (as URI)
				return executable.getAllCharacterisationOutputHttpData();
			}
		}
		return ret;
	}
	
	public List<String> getExecutionMetadata(URI inputFile) {
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
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExecutionStartedDate()
	 */
	public Calendar getExecutionStartedDate(){
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.getExecutionStartDate();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExecutionEndedDate()
	 */
	public Calendar getExecutionEndedDate(){
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.getExecutionEndDate();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#isExecutionInProgress()
	 */
	public boolean isExecutionInvoked() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.isExecutableInvoked();
			}
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#isExecuted()
	 */
	public boolean isExecutionCompleted() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.isExecutionCompleted();
			}
		}
		return false;
	}
	
	
	public boolean isExecutionSuccess() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.isExecutionSuccess();
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getSelectedTBServiceTemplate()
	 */
	public TestbedServiceTemplate getSelectedTBServiceTemplate() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.getServiceTemplate();
			}
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getselectedTBServiceTemplateOperation()
	 */
	public TestbedServiceTemplate.ServiceOperation getselectedTBServiceTemplateOperation() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				String sOpName = executable.getSelectedServiceOperationName();
				
				//fetch the object for the selected service operation name
				return executable.getServiceTemplate().getServiceOperation(sOpName);
			}
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentExecution#getExperimentExecutable()
	 */
	public ExperimentExecutable getExperimentExecutable() {
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		return manager.getExperiment(this.lExperimentIDRef).getExperimentExecutable();
	}

}
