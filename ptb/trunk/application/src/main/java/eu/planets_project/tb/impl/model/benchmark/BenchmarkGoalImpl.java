package eu.planets_project.tb.impl.model.benchmark;

import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.ExperimentEvaluationImpl;


/**
 * The information contained in the "type" field is validated if it matches one of the 
 * basic Java Objects: java.lang.Integer, java.lang.Long, java.lang.String, java.langBoolean
 * @author alindley
 *
 */
//@Entity
@XmlRootElement(name = "BenchmarkGoal")
@XmlAccessorType(XmlAccessType.FIELD) 
public class BenchmarkGoalImpl extends Object implements BenchmarkGoal, java.io.Serializable, Cloneable{
	
	//@Id
	//@GeneratedValue
	//private long lEntityID;
	
	private String sName, sType, sScale, sDefinition, sDescription, sVersion;
	//Note: SourceValue for the input file information, TargetValue for the output file information
	private String sSourceValue, sTargetValue;
	//Note: a predefined set of classifiers how well this target was matched
	private String sEvaluationValue;
	private int iWeight;
	private String sXMLID, sCategory;
	
	public BenchmarkGoalImpl(){
		sName = new String();
		sType = new String();
		sScale = new String();
		sDefinition = new String();
		sDescription = new String();
		sVersion = new String();
		sSourceValue = new String();
		sTargetValue = new String();
		sEvaluationValue = new String();
		iWeight = -1;
		sXMLID = new String();
		sCategory = new String();
	}
	
	//private void setEntityID(long lEntityID){
		//this.lEntityID = lEntityID;
	//}
	
	//additional ID required for EJB persistence, as sXMLID just identifies a XML benchmark goal uniquely but not a benchmark object instance
	//public long getEntityID(){
		//return this.lEntityID;
	//}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getDefinition()
	 */
	public String getDefinition() {
		return this.sDefinition;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getDescription()
	 */
	public String getDescription() {
		return this.sDescription;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getName()
	 */
	public String getName() {
		return this.sName;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getScale()
	 */
	public String getScale() {
		return this.sScale;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getType()
	 */
	public String getType() {
		return this.sType;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getCategory()
	 */
	public String getCategory() {
		return this.sCategory;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getID()
	 */
	public String getID() {
		return this.sXMLID;
	}
	

	protected void setName(String sName){
		this.sName = sName;
	}
	
	protected void setType(String sType){
		if(checkType(sType))
			this.sType = sType;
	}
	
	protected void setScale(String sScale){
		this.sScale = sScale;
	}
	
	protected void setDefinition(String sDefinition){
		this.sDefinition = sDefinition;
	}
		
	protected void setDescription(String sDescription){
		this.sDescription = sDescription;
	}
	
	protected void setCategory(String sCategory){
		this.sCategory=sCategory;
	}
	
	protected void setID(String sXMLID){
		this.sXMLID = sXMLID;
	}
	
	public String getVersion() {
		return this.sVersion;
	}
	
	protected void setVersion(String sVersion){
		this.sVersion = sVersion;
	}
	
	
	/**
	 * Validates if the information provided by setType corresponds to one of the basic Java Objects:
	 * java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.String, java.Boolean
	 * @return
	 */
	private boolean checkType(String sType){
		boolean bRet = false;
		//check if the value input matches teh supported type: java.lang.Integer
		if(Integer.class.getCanonicalName().equals(sType))
			bRet = true;
		//check if the value input matches teh supported type: java.lang.Long
		if(Long.class.getCanonicalName().equals(sType))
			bRet = true;
		//check if the value input matches teh supported type: java.lang.Float
		if(Float.class.getCanonicalName().equals(sType))
			bRet = true;
		//check if the value input matches teh supported type: java.lang.String
		if(String.class.getCanonicalName().equals(sType))
			bRet = true;
		//check if the value input matches teh supported type: java.lang.Boolean
		if(Boolean.class.getCanonicalName().equals(sType))
			bRet = true;
		return bRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#checkValueValid(java.lang.String)
	 */
	public boolean checkValueValid(String sValue) {
		try{
			//type e.g. "java.lang.Integer"
			Class obj1 = Class.forName(this.getType());
			//Integer
			if(obj1.isInstance(new Integer(10))){
				//if input is no Integer this will cause an exception
				try{
					Integer.valueOf(sValue);
					return true;
				}catch(Exception e){}
			}
			//Long
			if(obj1.isInstance(new Long(10))){
				//if input is no Long this will cause an exception
				try{
					Long.valueOf(sValue);
					return true;
				}catch(Exception e){}
			}
			//Float
			if(obj1.isInstance(new Float(10))){
				//if input is no Float this will cause an exception
				try{
				Float.valueOf(sValue);
				return true;
				}catch(Exception e){}
			}
			//String
			if(obj1.isInstance(new String())){
				return true;
			}
			//Boolean
			if(obj1.isInstance(new Boolean(true))){
				try{
					if(sValue.equals(Boolean.valueOf(sValue).toString())){
						return true;
					}
				}catch(Exception e){}
			}
			
		}catch(Exception e){
			return false;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getSourceValue()
	 */
	public String getSourceValue() {
		return this.sSourceValue;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#deleteSourceValue()
	 */
	public void deleteSourceValue(){
		this.sSourceValue = new String();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getTargetValue()
	 */
	public String getTargetValue() {
		return this.sTargetValue;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#deleteTargetValue()
	 */
	public void deleteTargetValue(){
		this.sTargetValue = new String();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getWeight()
	 */
	public int getWeight() {
		return this.iWeight;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#deleteWeight()
	 */
	public void deleteWeight(){
		this.iWeight = -1;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setSourceValue(java.lang.String)
	 */
	public void setSourceValue(String value)throws InvalidInputException{
		if(checkValueValid(value)){
			this.sSourceValue = value;
		}
		else{
			throw new InvalidInputException("Invalid value "+value);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setTargetValue(java.lang.String)
	 */
	public void setTargetValue(String value)throws InvalidInputException{
		if(checkValueValid(value)){
			this.sTargetValue = value;
		}
		else{
			throw new InvalidInputException("Invalid value "+value);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setWeight(int)
	 */
	public void setWeight(int weight) throws InvalidInputException{
		if(this.WEIGHT_MINIMUM<=weight&&weight<=this.WEIGHT_MAXIMUM){
			this.iWeight = weight;
		}
		else{
			throw new InvalidInputException("Invalid weight "+weight);
		}
	}
	
	public BenchmarkGoalImpl clone(){
		BenchmarkGoalImpl goal = null;
		try{
			goal = (BenchmarkGoalImpl) super.clone();
		}catch(CloneNotSupportedException e){
			//TODO add logging statement
			System.out.println("BenchmarkGoalImpl problems cloning "+e.toString());
		}
		
		return goal;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getEvaluationValue()
	 */
	public String getEvaluationValue() {
		return this.sEvaluationValue;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#deleteEvaluationValue()
	 */
	public void deleteEvaluationValue(){
		this.sEvaluationValue = new String();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setEvaluationValue(java.lang.String)
	 */
	public void setEvaluationValue(String value) throws InvalidInputException {
		List<String> list = new ExperimentEvaluationImpl().getAllAcceptedEvaluationValues();
		if((value!=null)&&(list.contains(value))){
			this.sEvaluationValue = value;
		}
		else{
			throw new InvalidInputException("EvaluationValue not in the range of accepted values");
		}
	}

}
