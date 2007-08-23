package eu.planets_project.tb.api.model.finals;

import java.util.Iterator;
import java.util.Vector;

public interface ExperimentTypes {

	// All available ExperimentTypes and their IDs
	public static final int EXPERIMENT_TYPE_SIMPLEMIGRATION = 0;

	public static final int EXPERIMENT_TYPE_SIMPLECHARACTERISATION = 1;

	public static final int EXPERIMENT_TYPE_MIGRATIONWORKFLOW = 2;

	public static final int EXPERIMENT_TYPE_EMULATION = 3;

	/**
	 * This method returns the experimen type's name. e.g.
	 * "EXPERIMENT_TYPE_SIMPLEMIGRATION" (String) See final int statements
	 * CONTEXT_GENERAL_ROLES and CONTEXT_EXPERIMENT_ROLES
	 * 
	 * @return
	 */
	public Vector<String> getAlLAvailableExperimentTypesNames();
	
	/**
	 * This method returns the experimen type's values. e.g.
	 * 0,1,2,3. This method may be used to check if a given ID is within the range of possible ones.
	 * 
	 * @return
	 */
	public Vector<Integer> getAlLAvailableExperimentTypeIDs();

	/**
	 * @param sExpName
	 *            full corresponding variable name. e.g.
	 *            EXPERIMENT_TYPE_SIMPLEMIGRATION
	 * @return the experiment type's ID.
	 */
	public int getExperimentTypeID(String sExpName);

	/**
	 * @param iTypeID
	 * @return full corresponding variable name. e.g. EXPERIMENT_TYPE_EMULATION
	 */
	public String getExperimentTypeName(int iTypeID);
	
	/**
	 * Checks if a given TypeID is within the given range of known and valid IDs
	 * @param iTypeID
	 * @return
	 */
	public boolean checkExperimentTypeIDisValid(int iTypeID);

}
