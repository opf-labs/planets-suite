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
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
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

	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentReportImpl report;
	private boolean bExpSetupImputValuesSet;
	//a helper reference pointer, for retrieving the experiment in the phase
	private long lExperimentIDRef;

	
	/*//constructor required for EJB persistence
	private ExperimentEvaluationImpl(){
	}*/
	
	public ExperimentEvaluationImpl(){

		this.experimentBenchmarkGoals = new HashMap<String, BenchmarkGoal>();
		this.fileBenchmarkGoals = new HashMap<URI,HashMap<String,BenchmarkGoal>>();
		report = new ExperimentReportImpl();
		lExperimentIDRef = -1;
		bExpSetupImputValuesSet = false;
		
		setPhasePointer(PHASE_EXPERIMENTEVALUATION);
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
    
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateExperimentBenchmarkGoal(eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID,
			String value) throws InvalidInputException{
		
		if(this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID)){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
			if(this.experimentBenchmarkGoals.keySet().contains(addedBenchmarkGoalID)){
				this.experimentBenchmarkGoals.remove(addedBenchmarkGoalID);
			}
			goal.setValue(value);
			this.experimentBenchmarkGoals.put(goal.getID(), goal);
		}
		else{
			throw new InvalidInputException("evaluateExperimentBenchmarkGoal failed: InputBenchmarkGoalID "+addedBenchmarkGoalID+" not contained.");
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setEvaluatedExperimentBenchmarkGoals(java.util.List)
	 */
	public void setEvaluatedExperimentBenchmarkGoals (
			List<BenchmarkGoal> addedBMGoals) throws InvalidInputException{
		
		if(addedBMGoals.size()>0){
			Iterator<BenchmarkGoal> itBMGoals = addedBMGoals.iterator();
			while(itBMGoals.hasNext()){
				BenchmarkGoal bmGoal = itBMGoals.next();
				
				//some preconditions
				if((bmGoal!=null)&&(bmGoal.getValue()!="")){
					//now start with the evaluation
					this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), bmGoal.getValue());
				}
			}
		}
		
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getExperimentReport()
	 */
	public ExperimentReport getExperimentReport() {
		return this.report;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getExperimentReportFile()
	 */
	public File getExperimentReportFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setExperimentReport(eu.planets_project.tb.api.model.ExperimentReport)
	 */
	public void setExperimentReport(ExperimentReport report) {
		this.report = (ExperimentReportImpl)report;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.util.Map.Entry, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(Entry<URI, URI> ioFile,
			String addedBenchmarkGoalID, String value) throws InvalidInputException{
		
		if((this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID))&&(this.fileBenchmarkGoals.containsKey(ioFile.getKey()))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
			
			//get file's BenchmarkGoalSet
			HashMap<String,BenchmarkGoal> hmFileGoals = this.fileBenchmarkGoals.get(ioFile.getKey());
			
			if(hmFileGoals.keySet().contains(addedBenchmarkGoalID)){
				hmFileGoals.remove(addedBenchmarkGoalID);
			}
			
			//set value:
			goal.setValue(value);
			hmFileGoals.put(goal.getID(), goal);
		}
		else{
			throw new InvalidInputException("evaluateFileBenchmarkGoal failed. Unsupported BenchmarkGoal or InvalidInput File");
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.net.URI, eu.planets_project.tb.api.model.benchmark.BenchmarkGoal, java.lang.String)
	 */
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID,
			String value) throws InvalidInputException{
		
		if((this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID))&&(this.getInputFiles().contains(inputFile))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
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
		else{
			throw new InvalidInputException("evaluateFileBenchmarkGoal failed. Unsupported BenchmarkGoal or InvalidInput File");
		}
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setEvaluatedFileBenchmarkGoals(java.util.Map)
	 */
	public void setEvaluatedFileBenchmarkGoals(
			Map<URI, List<BenchmarkGoal>> addedFileBMGoals) throws InvalidInputException{

		if(addedFileBMGoals!=null&&addedFileBMGoals.keySet().size()>0){
			Iterator<URI> itKeys = addedFileBMGoals.keySet().iterator();
			while(itKeys.hasNext()){
				URI uri = itKeys.next();
				
				//now get the BMGoals and extract ID and value
				if((addedFileBMGoals.get(uri)!=null)&&(addedFileBMGoals.get(uri).size()>0)){
					Iterator<BenchmarkGoal> itBMGoals = addedFileBMGoals.get(uri).iterator();
					
					while(itBMGoals.hasNext()){
						BenchmarkGoal bmGoal = itBMGoals.next();
						
						//some preconditions
						if((bmGoal!=null)&&(bmGoal.getValue()!="")){
							//now call the actual evaluation
							this.evaluateFileBenchmarkGoal(uri, bmGoal.getID(), bmGoal.getValue());
						}
					}
				}
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
		return new Vector<BenchmarkGoal>();
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
		return new Vector<BenchmarkGoal>();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getInputBenchmarkGoals()
	 */
	public Collection<String> getInputBenchmarkGoalIDs() {
		return this.getInputBenchmarkGoals().keySet();
	}


	/**
	 * Takes the ExperimentSetup object and extracts the added BenchmarkGoals if the benchmarkListisFinal() was set to true
	 * @param inputBenchmarkGoals
	 */
	/*private void setInput(ExperimentSetup expSetup) {
		
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
			
			this.bExpSetupImputValuesSet = true;
		}
		
	}*/
	
	/**
	 * This method checks of the Evaluation attributes have already been set (This
	 * should happen when the stage ExperimentEvaluation is set to "active".
	 * If no: it calls ExperimentEvaluation.setInput(ExperimentSetup setup) and sets the values
	 * if yes: nothing
	 * @return
	 */
	/*private void setExperimentSetupValues(){
		TestbedManager tbManager = TestbedManagerImpl.getInstance();
		//get the Experiment this phase belongs to
		Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);

		if(!this.bExpSetupImputValuesSet){
			this.setInput(thisExperiment.getExperimentSetup());
		}
		else{
			//do nothing
		}
	}*/
	
	/**
	 * Fetches the ExperimentSetup phase and extracts the InputData from it's ExperimentWorkflow
	 * @return 
	 */
	private Vector<URI> getInputFiles(){
		Vector<URI> vRet = new Vector<URI>();
		
		TestbedManager tbManager = TestbedManagerImpl.getInstance(true);
		//get the Experiment this phase belongs to
		Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);
		
		try{
			Collection<URI> inputFiles = thisExperiment.getExperimentSetup().
			getExperimentWorkflow().getInputData();
			
			if((inputFiles!=null)&&(inputFiles.size()>0)){
				Iterator<URI> itURIs = inputFiles.iterator();
				while(itURIs.hasNext()){
					URI uri = itURIs.next();
					vRet.add(uri);
				}
			}
		
			return vRet;
		}
		catch(Exception e){
			//it's possible that no ExperimentWorkflowInstance was set:
			return vRet;
		}
	}
	
	/**
	 * Fetches the ExperimentSetup phase and extracts the InputBenchmarkGoals from it.
	 * @return HashMap<BenchmarkGoalXMLID, BenchmarkGoal>
	 */
	private HashMap<String, BenchmarkGoal> getInputBenchmarkGoals(){
		HashMap<String,BenchmarkGoal> hmRet = new HashMap<String,BenchmarkGoal>();
		if(this.lExperimentIDRef!=-1){
			TestbedManager tbManager = TestbedManagerImpl.getInstance(true);
			//get the Experiment this phase belongs to
			Experiment thisExperiment = tbManager.getExperiment(this.lExperimentIDRef);
		
			List<BenchmarkGoal> inputBMGoals = thisExperiment.getExperimentSetup().getAllAddedBenchmarkGoals();
			if((inputBMGoals!=null)&&(inputBMGoals.size()>0)){
				Iterator<BenchmarkGoal> itBMGoals = inputBMGoals.iterator();
				while(itBMGoals.hasNext()){
					BenchmarkGoalImpl bmGoal = ((BenchmarkGoalImpl)itBMGoals.next()).clone();
					hmRet.put(bmGoal.getID(), bmGoal);
				}
			}
			return hmRet;
		}
		else{
			return hmRet;
		}
	}
	
}
