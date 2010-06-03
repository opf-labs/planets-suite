package eu.planets_project.tb.api.model.eval;

import eu.planets_project.tb.api.model.Executable;

public interface EvaluationExecutable extends Executable{

	public String getXCDLForSource();
	public String getXCDLForTarget();
	
	public void setXCDLForSource(String xcdl);
	public void setXCDLForTarget(String xcdl);
	
	public String getXCDLsComparisonResult();
	public void setXCDLsComparisonResult(String xml);
}
