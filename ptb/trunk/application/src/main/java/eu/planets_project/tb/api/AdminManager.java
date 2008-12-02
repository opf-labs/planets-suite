package eu.planets_project.tb.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.model.Experiment;

public interface AdminManager {
    
	public Collection<String> getExperimentTypesNames();

	public Collection<String> getExperimentTypeIDs();
	
	public String getExperimentTypeID(String sExpTypeName);

	public String getExperimentTypeName(String sTypeID);
	
	public Map<String,String> getExperimentTypeIDsandNames();

}
