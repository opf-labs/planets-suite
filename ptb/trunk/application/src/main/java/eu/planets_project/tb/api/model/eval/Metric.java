package eu.planets_project.tb.api.model.eval;

import java.util.List;

/**
 * @author lindleyA
 * The Testbed's representation of an evaluation metric (as imported through a 
 * evaluation service template) and used for being mapped to the Testbed's evaluation
 * criteria.
 */
public interface Metric{
	
	public void setName(String sName);		
	public String getName();

	public void setType(String sType);
	public String getType();
	
	public List<String> getNumericTypes();
	
	public void setDescription(String sDescr);
	public String getDescription();
}