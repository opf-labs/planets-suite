package eu.planets_project.tb.api.model.benchmark;

import java.util.Map;

import eu.planets_project.tb.api.model.eval.AutoEvaluationSettings;
import eu.planets_project.tb.api.model.eval.EvaluationExecutable;
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
	 * Sets (for migration experiments) the input source's characterisation value.
	 * e.g. page numbers of input files was 10. 
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setSourceValue(String sValue) throws InvalidInputException;
	public String getSourceValue();
	public void deleteSourceValue();
	
	/**
	 * Sets (for migration experiments) the output source's characterisation value.
	 * e.g. page numbers of output files was 10. 
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setTargetValue(String sValue) throws InvalidInputException;
	public String getTargetValue();
	public void deleteTargetValue();
	
	/**
	 * 
	 * @param sValue
	 * @throws InvalidInputException
	 */
	public void setEvaluationValue(String sValue)throws InvalidInputException;
	public String getEvaluationValue();
	public void deleteEvaluationValue();
	
	/**
	 * @param iWeight
	 * @throws InvalidInputException
	 */
	public void setWeight(int iWeight)throws InvalidInputException;
	public int getWeight();
	public void deleteWeight();
	
	/**
	 * Validates if the provided input matches the defined type. 
	 * @see setValue();
	 * java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.String, java.Boolean
	 * @param sValue
	 * @return
	 */
	public boolean checkValueValid(String sValue);
	
	/**
	 * Indicates if this BMGoal is backed by a valid "metric - TB evaluation mapping". i.e. an auto-evaluation servicetemplate
	 * and a configuration on how to extract the data for the given benchmark goal 
	 * @return
	 */
	public boolean isAutoEvaluatable();
	
	/**
	 * Containing (for this benchmark goal) a list of metrics, boundary values, etc.
	 * on how to evaluate the different TB evaluation criteria (e.g. 'very good') as well
	 * as the EvaluationTestbedServiceTemplate with all the Service's information actually invoking it.
	 * @param settings
	 */
	public void setAutoEvalSettings(AutoEvaluationSettings settings);
	public AutoEvaluationSettings getAutoEvalSettings();
	public void removeAutoEvalSettings();

	
	/**
	 * Contains the auto eval service extraction results, i.e. metadata on execution time,
	 * if execution has been started, is completed, the XCDL files for source and target files
	 * the comparator results, etc.
	 * @param results
	 */
	public void setAutoEvaluationExecutable(EvaluationExecutable results);
	public EvaluationExecutable getAutoEvaluationExecutable();
	public void removeAutoEvaluationExecutable();
	
	/**
	 * A flag giving an indication if the BMGoal was already successfully
	 * validated with an auto evaluation workflow or service
	 * @param b
	 */
	public void setWasAutomaticallyEvaluated(boolean b);
	public boolean isWasAutomaticallyEvaluated();

}
