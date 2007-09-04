package eu.planets_project.tb.api.model.mockups;

import java.io.File;
import java.util.List;

import eu.planets_project.tb.api.services.mockups.Service;

public interface Workflow {
	
	public List<File> getInputData();
	public void addInputData(File file);
	public void addInputData(List<File> files);
	public void removeInputData(File file);
	public void removeInputData(List<File> files);
	public void setInputData(List<File> files);
	
	/**
	 * @return String representing the required mime-type
	 */
	public List<String> getRequiredInputFileTypes();
	public void addRequiredInputFileType(String sMimeType);
	public void removeRequiredInputFileType(String sMimeType);
	
	public List<File> getOutputData();
	public void setOutputData(List<File> files);
	public void addOutputData(File file);
	public void addOutputData(List<File> files);
	
	/**
	 * @return String representing the required mime-type
	 */
	public List<String> getRequiredOutputFileTypes();
	public void addRequiredOutputFileType(String sMimeType);
	public void removeRequiredOutputFileType(String sMimeType);
	
	public Service getService(int iPosition);
	/**
	 * Contains all Services in the order of their occurrence 
	 * i.e. List.getItem(0) represents the starting service
	 * No forks are currently supported!
	 * @return 
	 */
	public List<Service> getServices();
	public void addService(int iPosition, Service service);
	public void removeService(int iPosition);

}
