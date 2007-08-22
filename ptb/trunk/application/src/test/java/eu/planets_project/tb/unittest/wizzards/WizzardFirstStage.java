package eu.planets_project.tb.unittest.wizzards;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;

import junit.framework.TestCase;

import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;

public class WizzardFirstStage extends TestCase{
	
	/**
	 * This method sets up and fills in a sample BasicProperties Object
	 * @return
	 */
	private BasicProperties fillInBasicProperties(){
		
		BasicPropertiesImpl props = new BasicPropertiesImpl();
		
		//addExperimentedObjectType:
		props.addExperimentedObjectType("image/gif");
		props.addExperimentedObjectType("text/html");
		
		return props;
	}
	
	/**
	 * This method sets up and fills in a sample ExperimentSetup Object
	 * @return
	 */
	private ExperimentSetup createExperimentSetup(){
		
		BasicPropertiesImpl props = (BasicPropertiesImpl)fillInBasicProperties();
		ExperimentSetupImpl expSetup = new ExperimentSetupImpl();
		
		//setBasicProperties
		expSetup.setBasicProperties(props);
		
		return expSetup;
		
	}
	
	/**
	 * Uses TestbedManager.registerNewUser()
	 * @return the experiment's entity Id.
	 */
	private long createExperiment1(){
		
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl) createExperimentSetup();
		
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		ExperimentImpl exp1 = new ExperimentImpl();
		exp1.setExperimentSetup(expSetup);
		
		//as the manager and the ExperimentImpl object are detached it is required to execute:
		/**
		 * this call persists the ExperimentImpl and registers it within the TestbedManager
		 * When the Experiment is persisted ExperimentSetupImpl-->BasicPropertiesImpl are persisted 
		 * as well through the @OneToOne(cascade={CascadeType.ALL})annotation
		**/
		long expID = manager.registerExperiment(exp1);
		//As the ID is injected by the container it is important to query the Experiment Object again.
		exp1 = (ExperimentImpl)manager.getExperiment(expID);
		
		return exp1.getEntityID();

	}
	
	/**
	 * Uses TestbedManager.createNewUser()
	 * @return the experiment's entity Id.
	 */
	private long createExperiment2(){
		
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl) createExperimentSetup();
		
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		/**
		 * this call persists the ExperimentImpl. When the Experiment is persisted 
		 * ExperimentSetupImpl-->BasicPropertiesImpl are persisted as well through the
		 * @OneToOne(cascade={CascadeType.ALL})annotation
		**/
		ExperimentImpl exp = (ExperimentImpl)manager.createNewExperiment();
		exp.setExperimentSetup(expSetup);
		
		//as the manager and the ExperimentImpl object are detached it is required to execute:
		manager.updateExperiment(exp);
		
		return exp.getEntityID();
		
	}
	
	/**
	 * Unittest, checking if the Experiment as well as it's included Objects are created
	 * Therefore take the Experiment's ID provided through createExperiment() and request the
	 * persistently stored object from the TestbedManager.
	 */
	public void testisExperimentCreated(){
		//check is SetupExperiment created
		//check are BasicProperties created
		TestbedManagerImpl manager = TestbedManagerImpl.getInstance();
		long lExperimentID = createExperiment1();
		System.out.println("ExpID= "+lExperimentID);
		ExperimentImpl exp = (ExperimentImpl)manager.getExperiment(lExperimentID);
		ExperimentSetupImpl expSetup = (ExperimentSetupImpl)exp.getExperimentSetup();
		BasicPropertiesImpl props = (BasicPropertiesImpl)expSetup.getBasicProperties();
		
		//now test if everything worked out
		assertTrue(exp!=null);
		assertTrue(expSetup!=null);
		assertTrue(props!=null);
		
		assertEquals(2,props.getExperimentedObjectTypes().size());
		assertTrue(props.getExperimentedObjectTypes().contains("image/gif"));
	}
	
	public void isExperimentPersisted(){
		
	}

}
