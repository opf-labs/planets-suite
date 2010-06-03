package eu.planets_project.tb.test;

import javax.ejb.Local;

import eu.planets_project.tb.impl.TestBean;

@Local
public interface TesterLocal {

	public void createTestEntry(TestBean test);

	public TestBean findTestEntry(int pKey);
	
	public void updateExistingTestEntry(int pKey, String sName, int htableKey, String htableValue);
	
	public void deleteTestEntry(int pKey);
	
	public void deleteTestEntry(TestBean test);
}