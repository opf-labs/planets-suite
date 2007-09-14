package eu.planets_project.tb.impl.model.benchmark;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;


/**
 * The information contained in the "type" field is validated if it matches one of the 
 * basic Java Objects: java.lang.Integer, java.lang.Long, java.lang.String, java.langBoolean
 * @author alindley
 *
 */
//@Entity
public class BenchmarkGoalImpl implements BenchmarkGoal, java.io.Serializable{
	
	//@Id
	//@GeneratedValue
	//private long lEntityID;
	
	private String sName, sType, sScale, sDefinition, sDescription, sVersion, sValue;
	private int iWeight;
	private String sXMLID, sCategory;
	
	public BenchmarkGoalImpl(){
		sName = new String();
		sType = new String();
		sScale = new String();
		sDefinition = new String();
		sDescription = new String();
		sVersion = new String();
		sValue = new String();
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
		boolean bRet = false;
		try{
			//type e.g. "java.lang.Integer"
			Class obj1 = Class.forName(this.getType());
			try{
				//Integer
				if(obj1.isInstance(new Integer(10))){
					//if input is no Integer this will cause an exception
					Integer.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Long
				if(obj1.isInstance(new Long(10))){
					//if input is no Long this will cause an exception
					Long.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Float
				if(obj1.isInstance(new Float(10))){
					//if input is no Float this will cause an exception
					Float.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//String
				if(obj1.isInstance(new String())){
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Boolean
				if(obj1.isInstance(new Boolean(true))){
					if(sValue.equals(Boolean.valueOf(sValue).toString())){
						bRet = true;
					}
				}
			}catch(Exception e){}
			
		}catch(Exception e){
			bRet = false;
		}
		return bRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getValue()
	 */
	public String getValue() {
		return this.sValue;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#getWeight()
	 */
	public int getWeight() {
		return this.iWeight;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		if(checkValueValid(value))
			this.sValue = value;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoal#setWeight(int)
	 */
	public void setWeight(int weight) {
		if(this.WEIGHT_MINIMUM<=weight&&weight<=this.WEIGHT_MAXIMUM){
			this.iWeight = weight;
		}
	}

}
