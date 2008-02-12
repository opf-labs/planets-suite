package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AdminManager {
	
	public Collection<String> getExperimentTypesNames();

	public Collection<String> getExperimentTypeIDs();

	/**
	 * Returns the ID for a given experiment type name.
	 * e.g. "
	 * @param sExpTypeName
	 * @return
	 */
	public String getExperimentTypeID(String sExpTypeName);

	public String getExperimentTypeName(String sTypeID);
	
	public Map<String,String> getExperimentTypeIDsandNames();

}
