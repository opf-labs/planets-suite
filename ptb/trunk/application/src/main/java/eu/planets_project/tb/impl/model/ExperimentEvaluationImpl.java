/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.model.ExperimentReport;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentEvaluationImpl extends ExperimentPhaseImpl 
        implements eu.planets_project.tb.api.model.ExperimentEvaluation, java.io.Serializable {

	//the EntityID and it's setter and getters are inherited from ExperimentPhase
	//Note: HashMap<BenchmarkGoalID, BenchmarkGoal>
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private HashMap<String, BenchmarkGoalImpl> experimentBenchmarkGoals;
	//Note: URI: inputFile is the key, String: BenchmarkGoalID
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private HashMap<URI,HashMap<String,BenchmarkGoalImpl>> fileBenchmarkGoals;
	//Note: HashMap<BenchmarkGoalID, BenchmarkGoal>

	@OneToOne(cascade={CascadeType.ALL})
	private ExperimentReportImpl report;
	
	private boolean bExpSetupImputValuesSet;
	
	private Integer experimentRating;
	
	private Integer serviceRating;
	
	//the property evaluation records for a given inputDigitalObjectRef over all stages
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private HashMap<String, ArrayList<PropertyEvaluationRecordImpl>> propertyEvalRecordsByInputDigoRef = new HashMap<String, ArrayList<PropertyEvaluationRecordImpl>>();
	//the overall experiment evaluation information HashMap<PropertyURI,Integer>
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private HashMap<String,Integer> overallPropertyEvalWeights = new HashMap<String,Integer>();
	//contains a list of digital object references that contain external evluation files (e.g. excel sheets, etc.)
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private ArrayList<String> lExternalEvalDocumentents = new ArrayList<String>();
	
	//a helper reference pointer, for retrieving the experiment in the phase
    @XmlTransient
	private long lExperimentIDRef;

	
	public ExperimentEvaluationImpl(){

		this.experimentBenchmarkGoals = new HashMap<String, BenchmarkGoalImpl>();
		this.fileBenchmarkGoals = new HashMap<URI,HashMap<String,BenchmarkGoalImpl>>();
		report = new ExperimentReportImpl();
		
		experimentRating = 0;
		serviceRating = 0;
		
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

    public void setExperimentRefID(long lExperimentIDRef){
        this.lExperimentIDRef = lExperimentIDRef;
    }
    
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateExperimentBenchmarkGoal(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Deprecated
	public void evaluateExperimentBenchmarkGoal(String addedBenchmarkGoalID,
			String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException{
		
		if(this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID)){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
			
			String oldSourceValue="";
			String oldTargetValue="";
			String oldEvaluationValue="";
			if(this.experimentBenchmarkGoals.keySet().contains(addedBenchmarkGoalID)){
				BenchmarkGoal bmTemp = experimentBenchmarkGoals.get(addedBenchmarkGoalID);
				oldSourceValue = bmTemp.getSourceValue();
				oldTargetValue = bmTemp.getTargetValue();
				oldEvaluationValue = bmTemp.getEvaluationValue();
				
				//now remove the old goal - it's being replaced
				this.experimentBenchmarkGoals.remove(addedBenchmarkGoalID);
			}
			
			if((sSourceValue!=null)&&(!sSourceValue.equals(""))){
				goal.setSourceValue(sSourceValue);
			}
			else{
				//check if we have some old values to set:
				if((sSourceValue==null)&&(oldSourceValue!=null)&&(!oldSourceValue.equals(""))){
					goal.setSourceValue(oldSourceValue);
				}
			}
			if((sTargetValue!=null)&&(!sTargetValue.equals(""))){
				goal.setTargetValue(sTargetValue);
			}
			else{
				//check if we have some old values to set:
				if((sTargetValue==null)&&(oldTargetValue!=null)&&(!oldTargetValue.equals(""))){
					goal.setTargetValue(oldTargetValue);
				}
			}
			if((sEvaluationValue!=null)&&(!sEvaluationValue.equals(""))){
				goal.setEvaluationValue(sEvaluationValue);
			}
			else{
				//check if we have some old values to set:
				if((sEvaluationValue==null)&&(oldEvaluationValue!=null)&&(!oldEvaluationValue.equals(""))){
					goal.setEvaluationValue(oldEvaluationValue);
				}
			}
			
			this.experimentBenchmarkGoals.put(goal.getID(), goal);
		}
		else{
			throw new InvalidInputException("evaluateExperimentBenchmarkGoal failed: InputBenchmarkGoalID "+addedBenchmarkGoalID+" not contained.");
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setEvaluatedExperimentBenchmarkGoals(java.util.List)
	 */
    @Deprecated
	public void setEvaluatedExperimentBenchmarkGoals (
			List<BenchmarkGoal> lBMGoals) throws InvalidInputException{
		if((lBMGoals!=null)&&(lBMGoals.size()>0)){
			Iterator<BenchmarkGoal> itBMGoals = lBMGoals.iterator();
			while(itBMGoals.hasNext()){
				BenchmarkGoal bmGoal = itBMGoals.next();
				
				//some preconditions
				if((bmGoal!=null)){
					if(!bmGoal.getSourceValue().equals("")){
						//now set the value
						this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), bmGoal.getSourceValue(),null,null);
					}
					if(!bmGoal.getTargetValue().equals("")){
						//now set the value
						this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), null, bmGoal.getTargetValue(),null);
					}
					if(!bmGoal.getEvaluationValue().equals("")){
						//now set the value
						this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), null ,null, bmGoal.getEvaluationValue());
					}
				}
			}
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setEvaluatedExperimentSourceBenchmarkGoals(java.util.List)
	 */
    @Deprecated
	public void setEvaluatedExperimentSourceBenchmarkGoals (
			List<BenchmarkGoal> addedSourceBMGoals) throws InvalidInputException{
		
		if((addedSourceBMGoals!=null)&&(addedSourceBMGoals.size()>0)){
			Iterator<BenchmarkGoal> itBMGoals = addedSourceBMGoals.iterator();
			while(itBMGoals.hasNext()){
				BenchmarkGoal bmGoal = itBMGoals.next();
				
				//some preconditions
				if((bmGoal!=null)&&(!bmGoal.getSourceValue().equals(""))){
					//now start with the evaluation
					this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), bmGoal.getSourceValue(),null, null);
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setEvaluatedExperimentTargetBenchmarkGoals(java.util.List)
	 */
    @Deprecated
	public void setEvaluatedExperimentTargetBenchmarkGoals (
			List<BenchmarkGoal> addedTargetBMGoals) throws InvalidInputException{
		
		if((addedTargetBMGoals!=null)&&(addedTargetBMGoals.size()>0)){
			Iterator<BenchmarkGoal> itBMGoals = addedTargetBMGoals.iterator();
			while(itBMGoals.hasNext()){
				BenchmarkGoal bmGoal = itBMGoals.next();
				
				//some preconditions
				if((bmGoal!=null)&&(!bmGoal.getTargetValue().equals(""))){
					//now start with the evaluation
					this.evaluateExperimentBenchmarkGoal(bmGoal.getID(), null, bmGoal.getTargetValue(), null);
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
	 */
	public int getExperimentRating() {
	    if( this.experimentRating == null ) return 0;
		return this.experimentRating;
	}

	/* (non-Javadoc)
	 */
	public void setExperimentRating(int rating) {
		this.experimentRating = rating;
	}
	
	/* (non-Javadoc)
	 */
	public int getServiceRating() {
        if( this.serviceRating == null ) return 0;
		return this.serviceRating;
	}

	/* (non-Javadoc)
	 */
	public void setServiceRating(int rating) {
		this.serviceRating = rating;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.util.Map.Entry, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Deprecated
	public void evaluateFileBenchmarkGoal(Entry<URI, URI> ioFile,
			String addedBenchmarkGoalID, String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException{
		
		if((this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID))&&(this.fileBenchmarkGoals.containsKey(ioFile.getKey()))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
			
			//get file's BenchmarkGoalSet
			HashMap<String,BenchmarkGoalImpl> hmFileGoals = this.fileBenchmarkGoals.get(ioFile.getKey());
			
			String oldSourceValue="";
			String oldTargetValue="";
			String oldEvaluationValue="";
			if(hmFileGoals.keySet().contains(addedBenchmarkGoalID)){
				BenchmarkGoal bmTemp = hmFileGoals.get(addedBenchmarkGoalID);
				oldSourceValue = bmTemp.getSourceValue();
				oldTargetValue = bmTemp.getTargetValue();
				oldEvaluationValue = bmTemp.getEvaluationValue();
				//now remove the old goal - it's being replaced
				hmFileGoals.remove(addedBenchmarkGoalID);
			}
			
			//set value:
			if((sSourceValue!=null)&&(!sSourceValue.equals(""))){
				goal.setSourceValue(sSourceValue);
			}
			else{
				//check if we have some old values to set:
				if((sSourceValue==null)&&(oldSourceValue!=null)&&(!oldSourceValue.equals(""))){
					goal.setSourceValue(oldSourceValue);
				}
			}
			if((sTargetValue!=null)&&(!sTargetValue.equals(""))){
				goal.setTargetValue(sTargetValue);
			}
			else{
				//check if we have some old values to set:
				if((sTargetValue==null)&&(oldTargetValue!=null)&&(!oldTargetValue.equals(""))){
					goal.setTargetValue(oldTargetValue);
				}
			}
			if((sEvaluationValue!=null)&&(!sEvaluationValue.equals(""))){
				goal.setEvaluationValue(sEvaluationValue);
			}
			else{
				//check if we have some old values to set:
				if((sEvaluationValue==null)&&(oldEvaluationValue!=null)&&(!oldEvaluationValue.equals(""))){
					goal.setEvaluationValue(oldEvaluationValue);
				}
			}
			
			//now put back the goal
			hmFileGoals.put(goal.getID(), goal);
		}
		else{
			throw new InvalidInputException("evaluateFileBenchmarkGoal failed. Unsupported BenchmarkGoal or InvalidInput File");
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#evaluateFileBenchmarkGoal(java.net.URI, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Deprecated
	public void evaluateFileBenchmarkGoal(URI inputFile, String addedBenchmarkGoalID,
			String sSourceValue, String sTargetValue, String sEvaluationValue) throws InvalidInputException{
		
		if((this.getInputBenchmarkGoals().keySet().contains(addedBenchmarkGoalID))&&(this.getInputFileURIs().contains(inputFile))){
			//get the input BenchmarkGoal
			BenchmarkGoalImpl goal = ((BenchmarkGoalImpl)this.getInputBenchmarkGoals().get(addedBenchmarkGoalID)).clone();
			//get file's BenchmarkGoalSet
			HashMap<String,BenchmarkGoalImpl> hmFileGoals = this.fileBenchmarkGoals.get(inputFile);
			
			//checks if for this inputfile a HashMap has already been created or if it's the first time
			boolean bMarker = false;
			if(hmFileGoals == null){
				hmFileGoals = new HashMap<String, BenchmarkGoalImpl>();
				bMarker = true;
			}
			
			String oldSourceValue="";
			String oldTargetValue="";
			String oldEvaluationValue="";
			if(hmFileGoals.keySet().size()>0){
				if(hmFileGoals.keySet().contains(addedBenchmarkGoalID)){
					BenchmarkGoal bmTemp = hmFileGoals.get(addedBenchmarkGoalID);
					oldSourceValue = bmTemp.getSourceValue();
					oldTargetValue = bmTemp.getTargetValue();
					oldEvaluationValue = bmTemp.getEvaluationValue();
					hmFileGoals.remove(addedBenchmarkGoalID);
				}
			}

			//set value:
			if((sSourceValue!=null)&&(!sSourceValue.equals(""))){
				goal.setSourceValue(sSourceValue);
			}
			else{
				//check if we have some old values to set:
				if((sSourceValue==null)&&(oldSourceValue!=null)&&(!oldSourceValue.equals(""))){
					goal.setSourceValue(oldSourceValue);
				}
			}
			if((sTargetValue!=null)&&(!sTargetValue.equals(""))){
				goal.setTargetValue(sTargetValue);
			}
			else{
				//check if we have some old values to set:
				if((sTargetValue==null)&&(oldTargetValue!=null)&&(!oldTargetValue.equals(""))){
					goal.setTargetValue(oldTargetValue);
				}
			}
			if((sEvaluationValue!=null)&&(!sEvaluationValue.equals(""))){
				goal.setEvaluationValue(sEvaluationValue);
			}
			else{
				//check if we have some old values to set:
				if((sEvaluationValue==null)&&(oldEvaluationValue!=null)&&(!oldEvaluationValue.equals(""))){
					goal.setEvaluationValue(oldEvaluationValue);
				}
			}
			
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
	@Deprecated
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
						if((bmGoal!=null)){
							if(!bmGoal.getSourceValue().equals("")){
								//now set the actual values
								this.evaluateFileBenchmarkGoal(uri, bmGoal.getID(), bmGoal.getSourceValue(),null, null);
							}
							if(!bmGoal.getTargetValue().equals("")){
								this.evaluateFileBenchmarkGoal(uri, bmGoal.getID(), null, bmGoal.getTargetValue(), null);

							}
							if(!bmGoal.getEvaluationValue().equals("")){
								this.evaluateFileBenchmarkGoal(uri, bmGoal.getID(), null, null, bmGoal.getEvaluationValue());

							}
						}
					}
				}
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoal(java.lang.String)
	 */
	@Deprecated
	public BenchmarkGoal getEvaluatedExperimentBenchmarkGoal(String goalXMLID) {
		if(this.experimentBenchmarkGoals.keySet().contains(goalXMLID)){
			return this.experimentBenchmarkGoals.get(goalXMLID);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedExperimentBenchmarkGoals()
	 */
	@Deprecated
	public Collection<BenchmarkGoal> getEvaluatedExperimentBenchmarkGoals() {
	    Collection<BenchmarkGoal> bmgs = new Vector<BenchmarkGoal>();
		if(this.experimentBenchmarkGoals.keySet().size()>0){
		    for( BenchmarkGoalImpl bg : this.experimentBenchmarkGoals.values())
		        bmgs.add(bg);
		}
		return bmgs;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoal(java.net.URI, java.lang.String)
	 */
	@Deprecated
	public BenchmarkGoal getEvaluatedFileBenchmarkGoal(URI inputFile,
			String goalXMLID) {
		if(this.fileBenchmarkGoals.keySet().contains(inputFile)){
			HashMap<String,BenchmarkGoalImpl> fileBMGoals = this.fileBenchmarkGoals.get(inputFile);
			if(fileBMGoals.containsKey(goalXMLID)){
				return fileBMGoals.get(goalXMLID);
			}	
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoals(java.net.URI)
	 */
	@Deprecated
	public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals(URI inputFile) {
	    Collection<BenchmarkGoal> bmgs =  new Vector<BenchmarkGoal>();

		if(this.fileBenchmarkGoals.keySet().contains(inputFile)){
			HashMap<String,BenchmarkGoalImpl> fileBMGoals = this.fileBenchmarkGoals.get(inputFile);
			if(fileBMGoals.keySet().size()>0){
				for( BenchmarkGoalImpl bg :  fileBMGoals.values() )
				    bmgs.add(bg);
			}
		}
		return bmgs;
	}

	

	/* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getEvaluatedFileBenchmarkGoals()
     */
	@Deprecated
    public Collection<BenchmarkGoal> getEvaluatedFileBenchmarkGoals() {
        if( this.fileBenchmarkGoals.keySet().iterator().hasNext() ) {
          return this.getEvaluatedFileBenchmarkGoals(this.fileBenchmarkGoals.keySet().iterator().next());
        } else {
          return this.getInputBenchmarkGoals().values();
        }
    }


    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getInputBenchmarkGoals()
	 */
	@Deprecated
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
	 * Fetches the experiment's executable and extracts the InputData from it's execution
	 * @return 
	 */
	private Collection<URI> getInputFileURIs(){
		
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		Experiment exp = manager.getExperiment(this.lExperimentIDRef);
		if(exp!=null){
			//contains the experiment's execution data
			ExperimentExecutable executable = exp.getExperimentExecutable();
			if(executable!=null){
				return executable.getAllInputHttpDataEntries();
			}
		}
		
		return new Vector<URI>();
	}
	
	
	/**
	 * Fetches the ExperimentSetup phase and extracts the InputBenchmarkGoals from it.
	 * @return HashMap<BenchmarkGoalXMLID, BenchmarkGoal>
	 */
	@Deprecated
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


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getAllAcceptedEvaluationValues()
	 */
	@Deprecated
	public List<String> getAllAcceptedEvaluationValues() {
		Vector<String> vRet = new Vector<String>();
		vRet.add(0,this.EVALUATION_VALUE_VERY_GOOD);
		vRet.add(1,this.EVALUATION_VALUE_GOOD);
		vRet.add(2,this.EVALUATION_VALUE_BAD);
		vRet.add(3,this.EVALUATION_VALUE_VERY_BAD);
		
		return vRet;
		
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setInputExperimentBenchmarkGoals(java.util.Collection)
	 */
	@Deprecated
	public void setInputExperimentBenchmarkGoals(
			Collection<BenchmarkGoal> addedOverallBMGoals) {
		for(BenchmarkGoal bmg : addedOverallBMGoals){
			this.experimentBenchmarkGoals.put(bmg.getID(), (BenchmarkGoalImpl)bmg);
		}
		
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setInputFileBenchmarkGoals(java.util.Map)
	 */
	@Deprecated
	public void setInputFileBenchmarkGoals(
			Map<URI, Collection<BenchmarkGoal>> addedFileBMGoals) {
		for(URI inputFileURI : addedFileBMGoals.keySet()){
			Collection<BenchmarkGoal> bmgs = addedFileBMGoals.get(inputFileURI);
			HashMap<String,BenchmarkGoalImpl> m = new HashMap<String, BenchmarkGoalImpl>();
			for(BenchmarkGoal bmg : bmgs){
				m.put(bmg.getID(), (BenchmarkGoalImpl)bmg);
			}
			
			this.fileBenchmarkGoals.put(inputFileURI, m);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#addPropertyEvaluation(eu.planets_project.tb.api.model.PropertyEvaluationRecord)
	 */
	@Deprecated
	public void addPropertyEvaluation(String inputDigoRef, PropertyEvaluationRecordImpl propEval) {
		if(this.propertyEvalRecordsByInputDigoRef!=null){
			if(propertyEvalRecordsByInputDigoRef.get(inputDigoRef)==null){
				ArrayList<PropertyEvaluationRecordImpl> l = new ArrayList<PropertyEvaluationRecordImpl>();
				l.add((PropertyEvaluationRecordImpl)propEval);
				this.propertyEvalRecordsByInputDigoRef.put(inputDigoRef, l);
			}
			else{
				this.propertyEvalRecordsByInputDigoRef.get(inputDigoRef).add((PropertyEvaluationRecordImpl)propEval);
			}
		}
		
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getPropertyEvaluations()
	 */
	@Deprecated
	public HashMap<String,ArrayList<PropertyEvaluationRecordImpl>> getPropertyEvaluations() {
		if(this.propertyEvalRecordsByInputDigoRef==null){
			return new HashMap<String,ArrayList<PropertyEvaluationRecordImpl>>();
		}
		return this.propertyEvalRecordsByInputDigoRef;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getPropertyEvaluation(java.lang.String)
	 */
	@Deprecated
	public ArrayList<PropertyEvaluationRecordImpl> getPropertyEvaluation(String inputDigitalObjectRef){
		if(this.propertyEvalRecordsByInputDigoRef!=null){
			if(this.propertyEvalRecordsByInputDigoRef.get(inputDigitalObjectRef)==null){
				this.propertyEvalRecordsByInputDigoRef.put(inputDigitalObjectRef, new ArrayList<PropertyEvaluationRecordImpl>());
			}
			return this.propertyEvalRecordsByInputDigoRef.get(inputDigitalObjectRef);
		}
		return null;
	}



	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#addOverallPropertyEvalWeights(java.lang.String, java.lang.Integer)
	 */
	@Deprecated
	public void addOverallPropertyEvalWeights(String propertyID, Integer weight) {
		if(overallPropertyEvalWeights!=null){
			this.overallPropertyEvalWeights.put(propertyID, weight);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getOverallPropertyEvalWeights()
	 */
	@Deprecated
	public HashMap<String, Integer> getOverallPropertyEvalWeights() {
		if(overallPropertyEvalWeights!=null){
			return overallPropertyEvalWeights;
		}
		return new HashMap<String, Integer>();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#getOverallPropertyEvalWeight(java.lang.String)
	 */
	@Deprecated
	public Integer getOverallPropertyEvalWeight(String propertyID){
		if(overallPropertyEvalWeights!=null){
			return this.overallPropertyEvalWeights.get(propertyID);
		}else{
			return -1;
		}
	}
	


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentEvaluation#setOverallPropertyEvalWeights(java.util.HashMap)
	 */
	@Deprecated
	public void setOverallPropertyEvalWeights(HashMap<String, Integer> propertyWeights) {
		this.overallPropertyEvalWeights = propertyWeights;
	}


	/** {@inheritDoc} */
	public void addExternalEvaluationDocument(String digitalObjectRef) {
		this.lExternalEvalDocumentents.add(digitalObjectRef);
	}
	
	/** {@inheritDoc} */
	public void removeExternalEvaluationDocument(String digitalObjectRef) {
		this.lExternalEvalDocumentents.remove(digitalObjectRef);
	}


	/** {@inheritDoc} */
	public ArrayList<String> getExternalEvaluationDocuments() {
        if( this.lExternalEvalDocumentents == null ) this.lExternalEvalDocumentents = new ArrayList<String>();
		return this.lExternalEvalDocumentents;
	}

	/** {@inheritDoc} */
	public void setExternalEvaluationDocuments(ArrayList<String> records) {
		this.lExternalEvalDocumentents = records;
	}
	
	/**
	 * A 'clean' method is called when copying an experiment using the 'save_as' button
     * @param experimentExecutable
     */
    public static void clearExperimentEvaluationRecords( ExperimentEvaluationImpl exEvalImpl ) {
    	exEvalImpl.setExperimentReport(new ExperimentReportImpl());
    	exEvalImpl.setExperimentRating(0);
    	exEvalImpl.setServiceRating(0);
    	exEvalImpl.setExternalEvaluationDocuments(new ArrayList<String>());
    }

}
