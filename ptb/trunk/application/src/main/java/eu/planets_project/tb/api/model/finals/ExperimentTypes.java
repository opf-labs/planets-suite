package eu.planets_project.tb.api.model.finals;

import java.util.Iterator;

public interface ExperimentTypes {

	// All available ExperimentTypes and their IDs
	public final int EXPERIMENT_TYPE_SIMPLEMIGRATION = 0;

	public final int EXPERIMENT_TYPE_SIMPLECHARACTERISATION = 1;

	public final int EXPERIMENT_TYPE_MIGRATIONWORKFLOW = 2;

	public final int EXPERIMENT_TYPE_EMULATION = 3;

	/**
	 * This method returns the experimen type's name. e.g.
	 * "EXPERIMENT_TYPE_SIMPLEMIGRATION" (String) See final int statements
	 * CONTEXT_GENERAL_ROLES and CONTEXT_EXPERIMENT_ROLES
	 * 
	 * @return
	 */
	public Iterator<String> getAlLAvailableExperimentTypes();

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

}
