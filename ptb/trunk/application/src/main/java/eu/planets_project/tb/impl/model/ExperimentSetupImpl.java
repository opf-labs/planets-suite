/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentResources;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentResourcesImpl;

/**
 * @author alindley
 * hmBenchmarkGoalList: 
 * Please not the BenchmarkGoalList does hold the the BenchmarkGoal object itself
 *
 */
@SuppressWarnings("serial")
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentSetupImpl extends ExperimentPhaseImpl implements
		eu.planets_project.tb.api.model.ExperimentSetup,
		java.io.Serializable{
	
	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	@OneToOne(cascade={CascadeType.ALL})
	private BasicPropertiesImpl basicProperties;
	
	//BenchmarkGoals:
	//private boolean bBenchmarkGoalListFinal;
	//the structure: HashMap<BenchmarkGoal.getXMLID,BenchmarkGoal>();
    @Lob
	private HashMap<String,BenchmarkGoalImpl> hmBenchmarkGoals;
	
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentResourcesImpl experimentResources;
	
	//a helper reference pointer, for retrieving the experiment in the phase
    @XmlTransient
	private long lExperimentIDRef;
	
	//temporary helper
	private int iSubstage;
	

	public ExperimentSetupImpl(){
		basicProperties = new BasicPropertiesImpl();
		//bBenchmarkGoalListFinal = false;
		hmBenchmarkGoals = new HashMap<String,BenchmarkGoalImpl>();
		experimentResources = new ExperimentResourcesImpl();
		iSubstage = -1;
		lExperimentIDRef = -1;
		
		setPhasePointer(PHASE_EXPERIMENTSETUP);
	}

    /**
	 * A helper reference pointer on the experiment's ID to retrieve other phases or the
	 * experiment itself if this is required.
	 * @return
	 */
	public long getExperimentRefID(){
		return this.lExperimentIDRef;
	}

	public void setExperimentRefID(long lExperimentIDRef){
		this.lExperimentIDRef = lExperimentIDRef;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getBasicProperties()
	 */
	public BasicProperties getBasicProperties() {
		return this.basicProperties;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentResources()
	 */
	public ExperimentResources getExperimentResources() {
		return this.experimentResources;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentTypeID()
	 */
	public String getExperimentTypeID() {
		return this.basicProperties.getExperimentApproach();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getExperimentTypeName()
	 */
	public String getExperimentTypeName() {
		return this.basicProperties.getExperimentApproachName(
				this.basicProperties.getExperimentApproach());
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
		this.basicProperties = (BasicPropertiesImpl)props;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setExperimentType(int)
	 */
	public void setExperimentType(String typeID) throws InvalidInputException {
		this.basicProperties.setExperimentApproach(typeID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#addBenchmarkGoal(eu.planets_project.tb.api.model.benchmark.BenchmarkGoal)
	 */
	public void addBenchmarkGoal(BenchmarkGoal goal) {
		if(this.hmBenchmarkGoals.containsKey(goal.getID()))
			this.hmBenchmarkGoals.remove(goal.getID());
			
		this.hmBenchmarkGoals.put(goal.getID(), (BenchmarkGoalImpl)goal);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#addBenchmarkGoals(java.util.List)
	 */
	public void addBenchmarkGoals(List<BenchmarkGoal> goal) {
		for(int i=0;i<goal.size();i++){
			addBenchmarkGoal(goal.get(i));
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getAllAddedBenchmarkGoals()
	 */
	public List<BenchmarkGoal> getAllAddedBenchmarkGoals() {
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<BenchmarkGoalImpl> itGoals = this.hmBenchmarkGoals.values().iterator();
		while(itGoals.hasNext()){
			ret.add(itGoals.next());
		}
		return ret;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getBenchmarkGoal(java.lang.String)
	 */
	public BenchmarkGoal getBenchmarkGoal(String goalXMLID) {
		return this.hmBenchmarkGoals.get(goalXMLID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#isBenchmarkGoalListFinal()
	 */
	/*public boolean isBenchmarkGoalListFinal() {
		return this.bBenchmarkGoalListFinal;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoal(java.lang.String)
	 */
	public void removeBenchmarkGoal(String benchmarkGoalXMLID) {
		if(this.hmBenchmarkGoals.containsKey(benchmarkGoalXMLID))
			this.hmBenchmarkGoals.remove(benchmarkGoalXMLID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoal(java.lang.String, java.lang.String)
	 */
	public void removeBenchmarkGoal(String category, String name) {
		Iterator<BenchmarkGoalImpl> itGoals = this.hmBenchmarkGoals.values().iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next();
			if (goal.getCategory().equals(category)&&goal.getName().equals(name)){
				removeBenchmarkGoal(goal.getID());
			}
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeBenchmarkGoals(java.util.List)
	 */
	public void removeBenchmarkGoals(List<String> goalXMLIDs) {
		for(int i=0;i<goalXMLIDs.size();i++){
			removeBenchmarkGoal(goalXMLIDs.get(i));
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBenchmarkGoalListFinal()
	 */
	/*public void setBenchmarkGoalListFinal() {
		this.bBenchmarkGoalListFinal = true;
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setBenchmarkGoals(java.util.List)
	 */
	public void setBenchmarkGoals(List<BenchmarkGoal> goals) {
		//delete old object
		this.hmBenchmarkGoals = new HashMap<String,BenchmarkGoalImpl>();
		this.addBenchmarkGoals(goals);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getSubStage()
	 */
	public int getSubStage() {
		return this.iSubstage;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setSubStage(int)
	 */
	public void setSubStage(int subStage) {
		this.iSubstage = subStage;
	}


	/* (non-Javadoc)
	 * Retrieves the ServiceTemplate from the ExperimentExecutable, which is
	 * persisted within the Experiment. Please note: this is null for a new experiment.
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#getServiceTemplate()
	 */
	public TestbedServiceTemplate getServiceTemplate() {
		TestbedManager tbManager = TestbedManagerImpl.getInstance(true);
		//get the Experiment this phase belongs to
		Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);
		
		ExperimentExecutable executable = thisExperiment.getExperimentExecutable();
		if(executable !=null){
			//it has already been created
			return executable.getServiceTemplate();
		}
		else{
			return null;
		}
	}


	/* (non-Javadoc)
	 * When removing the serviceTemplate also the ExperimentExecutable is removed
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#removeServiceTemplate()
	 */
	public void removeServiceTemplate() {
//		TestbedManager tbManager = TestbedManagerImpl.getInstance(true);
//		Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);
		
//		thisExperiment.removeExperimentExecutable();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentSetup#setServiceTemplate(eu.planets_project.tb.api.model.ExperimentExecutable)
	 */
	public void setServiceTemplate(TestbedServiceTemplate template) {
//		TestbedManager tbManager = TestbedManagerImpl.getInstance(true);
//		Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);
		
		//ExperimentExecutable is set and initialized for this experiment
//		if( thisExperiment.getExperimentExecutable() == null ) {
//		  ExperimentExecutable executable = new ExperimentExecutableImpl();
//		  executable.setServiceTemplate(template);
//		  thisExperiment.setExperimentExecutable(executable);
//		}
	}
	
}
