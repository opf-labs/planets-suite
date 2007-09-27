package eu.planets_project.tb.api.model.benchmark;

import eu.planets_project.tb.impl.exceptions.InvalidInputException;

/**
 * @author alindley
 * 
 * Additional methods are required to be implemented as protected - as they should not be modifyable by any other object
 * than the BenchmarkGoalsHandler.
 * 
 * protected void setName(String sName);
 * protected void setType(String sType);
 * protected void setScale(String sScale);
 * protected void setDefinition(String sDefinition);
 * protected void setDescription(String sDescription);
 * protected void setCategory(String sCategory);
 * protected void setID(String sID);
 * protected void setVersion(String sVersion);
 */
public interface BenchmarkGoal {
	
	public static final int WEIGHT_MINIMUM 	= 0;
	public static final int WEIGHT_MEDIUM	 =3;
	public static final int WEIGHT_MAXIMUM 	= 5;
	
	//Information provided within the BenchmarkGoals XML
	public String getName();
	public String getType();
	public String getScale();
	public String getDefinition();
	public String getDescription();
	public String getCategory();
	public String getID();
	public String getVersion();
	
	//Individual information for a BenchmarGoal instance
	/**
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setSourceValue(String sValue) throws InvalidInputException;
	public String getSourceValue();
	
	/**
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setTargetValue(String sValue) throws InvalidInputException;
	public String getTargetValue();
	
	/**
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setEvaluationValue(String sValue)throws InvalidInputException;
	public String getEvaluationValue();
	
	/**
	 * @param iWeight
	 * @throws InvalidInputException
	 */
	public void setWeight(int iWeight)throws InvalidInputException;
	public int getWeight();
	
	/**
	 * Validates if the provided input matches the defined type. 
	 * @see setValue();
	 * java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.String, java.Boolean
	 * @param sValue
	 * @return
	 */
	public boolean checkValueValid(String sValue);

}
