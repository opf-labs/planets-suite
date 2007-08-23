package eu.planets_project.tb.api.services.mockups;

import java.util.Vector;

import eu.planets_project.tb.api.data.DataSet;

public interface WorkflowStage {
	
	public Service getService();
	public void setService(Service service);
	
	public void setInputDataSet(Vector<DataSet> data);
	public Vector<DataSet> getInputDataSet();
	
	public void setOutputDataSet(Vector<DataSet> data);
	public Vector<DataSet> getOutputDataSet();

}
