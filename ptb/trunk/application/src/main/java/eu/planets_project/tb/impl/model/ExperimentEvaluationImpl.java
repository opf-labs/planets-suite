/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentEvaluationImpl extends ExperimentPhaseImpl
implements eu.planets_project.tb.api.model.ExperimentEvaluation, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	//Note: HashMap<BenchmarkGoalID, BenchmarkGoal>
	private HashMap<String, BenchmarkGoal> experimentBenchmarkGoals;
	//Note: URI: inputFile is the key, String: BenchmarkGoalID
	private HashMap<URI,HashMap<String,BenchmarkGoal>> fileBenchmarkGoals;
	//Note: HashMap<BenchmarkGoalID, BenchmarkGoal>
	//@Transient
	private HashMap<String,BenchmarkGoal> inputBenchmarkGoals;
	//@Transient
	private Vector<URI> inputFiles;
	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentReportImpl report;
	
	
	/*//constructor required for EJB persistence
	private ExperimentEvaluationImpl(){
	}*/
	
	public ExperimentEvaluationImpl(){

		this.experimentBenchmarkGoals = new HashMap<String, BenchmarkGoal>();
		this.fileBenchmarkGoals = new HashMap<URI,HashMap<String,BenchmarkGoal>>();
		inputBenchmarkGoals = new HashMap<String,BenchmarkGoal>();
		inputFiles = new Vector<URI>();
		report = new ExperimentReportImpl();
		
		setPhasePointer(PHASE_EXPERIMENTEVALUATION);
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateExperimentBenchmarkGoal(eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID,
			String value) {
		if(this.inputBenchmarkGoals.keySet().contains(addedBenchmarkGoalID)){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.inputBenchmarkGoals.get(addedBenchmarkGoalID)).clone();
			if(this.experimentBenchmarkGoals.keySet().contains(addedBenchmarkGoalID)){
				this.experimentBenchmarkGoals.remove(addedBenchmarkGoalID);
			}
			goal.setValue(value);
			this.experimentBenchmarkGoals.put(goal.getID(), goal);
		}
		
	}


	public ExperimentReport getExperimentReport() {
		return this.report;
	}

	public File getExperimentReportFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExperimentReport(ExperimentReport report) {
		this.report = (ExperimentReportImpl)report;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.util.Map.Entry, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(Entry<URI, URI> ioFile,
			String addedBenchmarkGoalID, String value) {
		
		if((this.inputBenchmarkGoals.keySet().contains(addedBenchmarkGoalID))&&(this.fileBenchmarkGoals.containsKey(ioFile.getKey()))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.inputBenchmarkGoals.get(addedBenchmarkGoalID)).clone();
			
			//get file's BenchmarkGoalSet
			HashMap<String,BenchmarkGoal> hmFileGoals = this.fileBenchmarkGoals.get(ioFile.getKey());
			
			if(hmFileGoals.keySet().contains(addedBenchmarkGoalID)){
				hmFileGoals.remove(addedBenchmarkGoalID);
			}
			
			//set value:
			goal.setValue(value);
			hmFileGoals.put(goal.getID(), goal);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.net.URI, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID,
			String value) {
		
		if((this.inputBenchmarkGoals.keySet().contains(addedBenchmarkGoalID))&&(this.inputFiles.contains(inputFile))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.inputBenchmarkGoals.get(addedBenchmarkGoalID)).clone();
			//get file's BenchmarkGoalSet
			HashMap<String,BenchmarkGoal> hmFileGoals = this.fileBenchmarkGoals.get(inputFile);
			
			boolean bMarker = false;
			if(hmFileGoals == null){
				hmFileGoals = new HashMap<String, BenchmarkGoal>();
				bMarker = true;
			}
			
			if(hmFileGoals.keySet().size()>0){
				if(hmFileGoals.keySet().contains(addedBenchmarkGoalID)){
					hmFileGoals.remove(addedBenchmarkGoalID);
				}
			}

			//set value:
			goal.setValue(value);
			hmFileGoals.put(goal.getID(), goal);
			if(bMarker){
				this.fileBenchmarkGoals.put(inputFile, hmFileGoals);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoal(java.lang.String)
	 */
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String goalXMLID) {
		if(this.experimentBenchmarkGoals.keySet().contains(goalXMLID)){
			return this.experimentBenchmarkGoals.get(goalXMLID);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoals()
	 */
	public Collection<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals() {
		if(this.experimentBenchmarkGoals.keySet().size()>0){
			return this.experimentBenchmarkGoals.values();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoal(java.net.URI, java.lang.String)
	 */
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile,
			String goalXMLID) {
		if(this.fileBenchmarkGoals.keySet().contains(inputFile)){
			HashMap<String,BenchmarkGoal> fileBMGoals = this.fileBenchmarkGoals.get(inputFile);
			if(fileBMGoals.containsKey(goalXMLID)){
				return fileBMGoals.get(goalXMLID);
			}	
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoals(java.net.URI)
	 */
	public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile) {

		if(this.fileBenchmarkGoals.keySet().contains(inputFile)){
			HashMap<String,BenchmarkGoal> fileBMGoals = this.fileBenchmarkGoals.get(inputFile);
			if(fileBMGoals.keySet().size()>0){

				return fileBMGoals.values();
			}
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getInputBenchmarkGoals()
	 */
	public Collection<String> getInputBenchmarkGoalIDs() {
		return this.inputBenchmarkGoals.keySet();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setInputBenchmarkGoals(eu.planets_project.tb.api.model.ExperimentSetup)
	 */
	public void setInput(ExperimentSetup expSetup) {
		
		if(expSetup.isBenchmarkGoalListFinal()){
			//add inputBenchmarkGoals
			Iterator<BenchmarkGoal> itInputBMGoals = expSetup.getAllAddedBenchmarkGoals().iterator();
			while(itInputBMGoals.hasNext()){
				BenchmarkGoalImpl goal = (BenchmarkGoalImpl)itInputBMGoals.next();
				goal = goal.clone();
				this.inputBenchmarkGoals.put(goal.getID(), goal);
			}

			//add inputFiles
			if(expSetup.getExperimentWorkflow()!=null){
				if(expSetup.getExperimentWorkflow().getInputData().size()>0){
					this.inputFiles.addAll(expSetup.getExperimentWorkflow().getInputData());
				}
			}
		}
		
	}
	
}
