/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.io.File;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;

/**
 * @author alindley
 *
 */
@Entity
public class ExperimentEvaluationImpl extends ExperimentPhaseImpl
implements eu.planets_project.tb.api.model.ExperimentEvaluation, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	private Vector<BenchmarkGoal> experimentBenchmarkGoals;
	//Note: URI: inputFile is the key
	private HashMap<URI,List<BenchmarkGoal>> fileBenchmarkGoals;
	
	
	//constructor required for EJB persistence
	private ExperimentEvaluationImpl(){
	}
	
	public ExperimentEvaluationImpl(ExperimentSetup expSetup){
		//System.out.println("ConstructorEvaluation1: "+expSetup.getAllAddedBenchmarkGoals().size());
		//set all BenchmarkGoals added in the setup stage to the experiment's benchmark goals
		this.experimentBenchmarkGoals = (Vector<BenchmarkGoal>)expSetup.getAllAddedBenchmarkGoals();
		//System.out.println("ConstructorEvaluation2: "+this.experimentBenchmarkGoals.size());
		this.fileBenchmarkGoals = new HashMap<URI,List<BenchmarkGoal>>();
		
		setPhasePointer(PHASE_EXPERIMENTEVALUATION);
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateExperimentBenchmarkGoal(eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateExperimentBenchmarkGoal(BenchmarkGoal addedBenchmarkGoal,
			String value) {
		if(this.experimentBenchmarkGoals.contains(addedBenchmarkGoal)){
			addedBenchmarkGoal.setValue(value);
		}
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getAddedExperimentBenchmarkGoals()
	 */
	public List<BenchmarkGoal> getAddedExperimentBenchmarkGoals() {
		return this.experimentBenchmarkGoals;
	}

	public ExperimentReport getExperimentReport() {
		// TODO Auto-generated method stub
		return null;
	}

	public File getExperimentReportFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExperimentReport(ExperimentReport report) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.util.Map.Entry, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(Entry<URI, URI> ioFile,
			BenchmarkGoal addedBenchmarkGoal, String value) {
		if(this.fileBenchmarkGoals.containsKey(ioFile.getKey())){
			//get BenchmarkGoals for this file:
			List<BenchmarkGoal> goals = this.fileBenchmarkGoals.get(ioFile.getKey());
			if(goals.contains(addedBenchmarkGoal)){
				int iPosition = goals.indexOf(addedBenchmarkGoal);
				goals.get(iPosition).setValue(value);
			}
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.net.URI, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, BenchmarkGoal addedBenchmarkGoal,
			String value) {
		if(this.fileBenchmarkGoals.containsKey(inputFile)){
			//get BenchmarkGoals for this file:
			List<BenchmarkGoal> goals = this.fileBenchmarkGoals.get(inputFile);
			if(goals.contains(addedBenchmarkGoal)){
				int iPosition = goals.indexOf(addedBenchmarkGoal);
				goals.get(iPosition).setValue(value);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoal(java.lang.String)
	 */
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String goalXMLID) {
		Iterator<BenchmarkGoal> itGoals = this.experimentBenchmarkGoals.iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next();
			if (goal.getID()==goalXMLID){
				return goal;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoals()
	 */
	public List<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals() {
		System.out.println("In ExperimentEvaluationImpl1");
		System.out.println(this.experimentBenchmarkGoals.size());
		return this.experimentBenchmarkGoals;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoal(java.net.URI, java.lang.String)
	 */
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile,
			String goalXMLID) {
		if(this.fileBenchmarkGoals.keySet().contains(inputFile)){
			Iterator<BenchmarkGoal> itGoals = this.fileBenchmarkGoals.get(inputFile).iterator();
			while(itGoals.hasNext()){
				BenchmarkGoal goal = itGoals.next();
				if (goal.getID()==goalXMLID){
					return goal;
				}
			}
			
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoals(java.net.URI)
	 */
	public List<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile) {
		return this.fileBenchmarkGoals.get(inputFile);
	}
	

}
