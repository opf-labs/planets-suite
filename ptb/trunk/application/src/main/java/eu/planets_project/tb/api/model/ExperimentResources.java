package eu.planets_project.tb.api.model;


public interface ExperimentResources {

	public final static int INTENSITY_LOW = 0;
	public final static int INTENSITY_MEDIUM = 1;
	public final static int INTENSITY_HIGH = 2;
	
	//List to be completed
	
	public void setNumberOfOutputFiles(int iNr);
	public int getNumberOfOutputFiles();

	public void setIntensity(int iIntensity);
	public int getIntensity();
	
}
