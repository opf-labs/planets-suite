package eu.planets_project.tb.impl.model.benchmark;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.benchmark.Benchmark;


/**
 * The information contained in the "type" field is validated if it matches one of the 
 * basic Java Objects: java.lang.Integer, java.lang.Long, java.lang.String, java.langBoolean
 * @author alindley
 *
 */
//@Entity
public class BenchmarkImpl implements Benchmark, java.io.Serializable{
	
	//@Id
	//@GeneratedValue
	//private long lEntityID;
	
	private String sName, sType, sScale, sDefinition, sDescription, sVersion;
	private String sXMLID, sCategory;
	
	public BenchmarkImpl(){
		sName = new String();
		sType = new String();
		sScale = new String();
		sDefinition = new String();
		sDescription = new String();
		sVersion = new String();
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
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getDefinition()
	 */
	public String getDefinition() {
		return this.sDefinition;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getDescription()
	 */
	public String getDescription() {
		return this.sDescription;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getName()
	 */
	public String getName() {
		return this.sName;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getScale()
	 */
	public String getScale() {
		return this.sScale;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getType()
	 */
	public String getType() {
		return this.sType;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getCategory()
	 */
	public String getCategory() {
		return this.sCategory;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getID()
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



}