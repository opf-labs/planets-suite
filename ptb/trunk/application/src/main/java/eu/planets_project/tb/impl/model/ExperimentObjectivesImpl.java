/**
 * 
 */
package eu.planets_project.tb.impl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.benchmark.Objective;

/**
 * @author alindley
 *
 */
//@Entity
public class ExperimentObjectivesImpl implements
		eu.planets_project.tb.api.model.ExperimentObjectives, java.io.Serializable {
	
	//@Id
	//@GeneratedValue
	private long lExperimentObjectivesID;
	/**
	 * This implementation assumes that after the ObjectiveList is declared final no Objectives
	 * as well as the weight may get changed added or removed from the list. It's only possible to add/modify the value from this stage on.
	 */
	private boolean bObjectiveListFinal;
	/**
	 * please not the ExperimentObjectives does not only hold the pointer but holds the 
	 * Objective object itself
	 * The structure looks like HashMap<key:Objective<HashMap<[key:"value"|"weight"],data>
	 */
	private HashMap<Objective,HashMap<String,String>> hmObjectiveList;
	
	public ExperimentObjectivesImpl(){
		hmObjectiveList = new HashMap<Objective,HashMap<String,String>>();
		bObjectiveListFinal = false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#addObjective(eu.planets_project.tb.api.model.benchmark.Objective)
	 */
	public void addObjective(Objective objective) {
		boolean bContains = this.hmObjectiveList.containsKey(objective);
		if (!bContains&&!this.bObjectiveListFinal){
			HashMap<String,String> data = new HashMap<String,String>();
			data.put("value",null);
			data.put("weight",null);
			this.hmObjectiveList.put(objective, data);
		}
	}

	/* (non-Javadoc)
	 * Weight and Value may also be null e.g. in the ExperimentSetupPhase
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#addObjective(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addObjective(Objective objective, String value, String weight) {
		boolean bContains = this.hmObjectiveList.containsKey(objective);
		if (!bContains&&!this.bObjectiveListFinal){
			HashMap<String,String> data = new HashMap<String,String>();
			data.put("value",value);
			data.put("weight",weight);
			this.hmObjectiveList.put(objective, data);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#addObjectives(java.util.Vector)
	 */
	public void addObjectives(Vector<Objective> objectives) {
		Iterator<Objective> itObjectives = objectives.iterator();
		while(itObjectives.hasNext()){
			Objective obj = itObjectives.next();
			boolean bContains = this.hmObjectiveList.containsKey(obj);
			if (!bContains&&!this.bObjectiveListFinal){
				HashMap<String,String> data = new HashMap<String,String>();
				data.put("value",null);
				data.put("weight",null);
				this.hmObjectiveList.put(obj, data);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getAllAddedObjectives()
	 */
	public Vector<Objective> getAllAddedObjectives() {
		Vector<Objective> vRet = new Vector<Objective>();
		vRet.addAll(this.hmObjectiveList.keySet());
		return vRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getObjective(long)
	 */
	public Objective getObjective(long objectiveID) {
		Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
		while(itObjectives.hasNext()){
			Objective obj = itObjectives.next();
			if(obj.getObjectiveID()==objectiveID)
				return obj;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getValue()
	 */
	public String getValue(Objective Objective) {
		HashMap<String,String> hmValueWeight = this.hmObjectiveList.get(Objective);
		if(hmValueWeight.containsKey("value")){
			return hmValueWeight.get("value");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getWeight()
	 */
	public String getWeight(Objective Objective) {
		HashMap<String,String> hmValueWeight = this.hmObjectiveList.get(Objective);
		if(hmValueWeight.containsKey("weight")){
			return hmValueWeight.get("weight");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#isObjectiveListFinal()
	 */
	public boolean isObjectiveListFinal() {
		return this.bObjectiveListFinal;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#removeObjective(java.lang.String)
	 */
	public void removeObjective(long objectiveID) {
		if(!this.bObjectiveListFinal){
			Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
			while(itObjectives.hasNext()){
				Objective objective = itObjectives.next();
				if(objective.getObjectiveID() == objectiveID){
					this.hmObjectiveList.remove(objective);
				}
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#removeObjective(eu.planets_project.tb.api.model.benchmark.Objective)
	 */
	public void removeObjective(Objective Objective) {
		if(this.hmObjectiveList.containsKey(Objective)){
			this.hmObjectiveList.remove(Objective);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#removeObjectives(java.util.Vector)
	 */
	public void removeObjectives(Vector<Objective> objectives) {
		Iterator<Objective> itObjectives = objectives.iterator();
		while(itObjectives.hasNext()){
			Objective objective = itObjectives.next();
			this.removeObjective(objective);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setObjectiveListFinal()
	 */
	public void setObjectiveListFinal() {
		this.bObjectiveListFinal = true;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setObjectivesAsSelected(Objective)
	 */
	public void setObjectivesAsSelected(Objective objective) {
		this.hmObjectiveList = new HashMap<Objective,HashMap<String,String>>();
		this.addObjective(objective);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setObjectivesAsSelected(java.util.Vector)
	 */
	public void setObjectivesAsSelected(Vector<Objective> objectives) {
		this.hmObjectiveList = new HashMap<Objective,HashMap<String,String>>();
		Iterator<Objective> itObjectives = objectives.iterator();
		while(itObjectives.hasNext()){
			this.addObjective(itObjectives.next());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setValue(java.lang.String)
	 */
	public void setValue(Objective Objective, String value) {
		if(this.hmObjectiveList.containsKey(Objective)){
			HashMap<String,String> hmValueWeight = this.hmObjectiveList.get(Objective);
			hmValueWeight.remove("value");
			hmValueWeight.put("value", value);
			
			this.hmObjectiveList.remove(Objective);
			this.hmObjectiveList.put(Objective, hmValueWeight);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setWeight(java.lang.String)
	 */
	public void setWeight(Objective Objective, String weight) {
		if(this.hmObjectiveList.containsKey(Objective)){
			HashMap<String,String> hmValueWeight = this.hmObjectiveList.get(Objective);
			hmValueWeight.remove("weight");
			hmValueWeight.put("weight", weight);
			
			this.hmObjectiveList.remove(Objective);
			this.hmObjectiveList.put(Objective, hmValueWeight);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getWeight(java.lang.Long)
	 */
	public String getWeight(Long objectiveID) {
		String sRet = null;
		Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
		while(itObjectives.hasNext()){
			Objective objective = itObjectives.next();
			if (objectiveID == objective.getObjectiveID()){
				sRet = this.getWeight(objective);
			}
		}
		return sRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#getValue(java.lang.Long)
	 */
	public String getValue(Long objectiveID) {
		String sRet = null;
		Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
		while(itObjectives.hasNext()){
			Objective objective = itObjectives.next();
			if (objectiveID == objective.getObjectiveID()){
				sRet = this.getValue(objective);
			}
		}
		return sRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setValue(java.lang.Long, java.lang.String)
	 */
	public void setValue(Long objectiveID, String value) {
		Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
		while(itObjectives.hasNext()){
			Objective objective = itObjectives.next();
			//Precondition: the Objective already needs to be set with "setObjectiveAsSelected" to execute this method
			if (objectiveID == objective.getObjectiveID()){
				this.setValue(objective,value);
			}
		}

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentObjectives#setWeight(java.lang.Long, java.lang.String)
	 */
	public void setWeight(Long objectiveID, String value) {
		Iterator<Objective> itObjectives = this.hmObjectiveList.keySet().iterator();
		while(itObjectives.hasNext()){
			Objective objective = itObjectives.next();
			//Precondition: the Objective already needs to be set with "setObjectiveAsSelected" to execute this method
			if (objectiveID == objective.getObjectiveID()){
				this.setWeight(objective,value);
			}
		}

	}

}
