package eu.planets_project.tb.api.services.mockups;

import java.util.List;

import eu.planets_project.tb.api.data.DataSet;

public interface WorkflowStage {
	
	public Service getService();
	public void setService(Service service);
	
	public void setInputDataSet(List<DataSet> data);
	public List<DataSet> getInputDataSet();
	
	public void setOutputDataSet(List<DataSet> data);
	public List<DataSet> getOutputDataSet();

}
