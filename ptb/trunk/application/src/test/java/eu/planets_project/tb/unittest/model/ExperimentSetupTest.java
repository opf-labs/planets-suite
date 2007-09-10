package eu.planets_project.tb.unittest.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.model.benchmark.*;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;
import eu.planets_project.tb.api.model.finals.ExperimentTypes;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;

import junit.framework.TestCase;

public class ExperimentSetupTest extends TestCase{
	
	private long expID1, expID2;
	private TestbedManager manager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance();
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
	}
	
	
	public void testExperimentWorkflow(){
		Experiment exp_test = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = new ExperimentSetupImpl();

		WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
		Vector<Long> vTemplateIDs = (Vector<Long>)wfhandler.getAllWorkflowIDs();
		//Build an ExperimentWorkflow with the first given Workflow
		if(vTemplateIDs.size()>0){
			//use template to build a workflow instance
			ExperimentWorkflow expWorkflow1 = wfhandler.getExperimentWorkflow(vTemplateIDs.firstElement());
			assertNotNull(expWorkflow1);
				
			//Test2: Does it contain a Workflow
			assertNotNull(expWorkflow1.getWorkflow());
				
			//Test3: Does the workflow at least contain one service
			assertTrue(expWorkflow1.getWorkflow().getWorkflowServices().size()>0);
				
			//Test4: Does Service contain an Endpoint
			assertNotNull(expWorkflow1.getWorkflow().getWorkflowService(0).getEndpointAddress());
				
		}else{
			//Testcase cannot be completed without any Workflow in the DB
			assertTrue(false);
		}
	}
	
	//IN WORK
	/*public void testExperimentWorkflowSetData(){
		Experiment exp_test = manager.getExperiment(this.expID1);
		ExperimentSetup expSetup = new ExperimentSetupImpl();

		//now celect a workflow to use
		WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
		Vector<Long> vTemplateIDs = (Vector<Long>)wfhandler.getAllWorkflowIDs();
		
		if(vTemplateIDs.size()>0){
			//use the last available Workflow in this Experiment
			ExperimentWorkflow expWorkflow1 = wfhandler.getExperimentWorkflow(vTemplateIDs.lastElement());
			
			//now add Experiment input data
			List<String> sInputMIMETypes = expWorkflow1.getWorkflow().getRequiredInputMIMETypes();
			
			//if(sInputMIMETypes.contains("image/png"))
			//expWorkflow1.addInputData(new File("http://www.planets-project.eu/graphics/Planets_Logo.png"));
			try{
			expWorkflow1.addInputData(new File(new java.net.URL("file:http://dme021:8080/ImageMagicWS/Tiff2JpegAction?wsdl").toURI()));
			}catch(Exception e){
				System.out.println("Exception "+e.toString());
			}
			//DELTE
			try{
			java.net.URL url_test = new java.net.URL("file:http://www.planets-project.eu/graphics/Planets_Logo.png");
			File f_tester = new File(url_test.toURI());
			System.out.println("Length "+f_tester.length());
			}catch(Exception e){
				System.out.println("URI Exception"+e.toString());
			}
			//END DELETE
			
			//now store Experiment
			expSetup.setWorkflow(expWorkflow1);
			exp_test.setExperimentSetup(expSetup);
			manager.updateExperiment(exp_test);
			
			Experiment exp_found = manager.getExperiment(this.expID1);
			List<File> inputfiles = exp_found.getExperimentSetup().getExperimentWorkflow().getInputData();
			
			assertEquals(1,inputfiles.size());
			Iterator<File> itInputFiles = inputfiles.iterator();
			while(itInputFiles.hasNext()){
				File file = itInputFiles.next();
				System.out.println("file location: "+file.getAbsolutePath());
				System.out.println("can read input file: "+file.canRead());
				
				//DELETE
				try {
					FileInputStream fis = new FileInputStream(file);

				    // Here BufferedInputStream is added for fast reading.
				    BufferedInputStream bis = new BufferedInputStream(fis);
				    DataInputStream dis = new DataInputStream(bis);

				    // dis.available() returns 0 if the file does not have more lines.
				    while(dis.available()!=0) {
				    	System.out.println(dis.readLine());
				    }

				    //dispose all the resources after using them.
				    fis.close();
				    bis.close();
				    dis.close();
				    
				  }catch(FileNotFoundException e) {
				     System.out.println(e.toString());
				  }catch(IOException e) {
					  System.out.println(e.toString());
				  }
				
				//END DELETE
			}
			
			assertTrue(true);

		}
		else{
			//Testcase cannot be completed without any Workflow available in the DB
			assertTrue(false);
		}
	}*/
	
	
	//Tests for the underlying Entity Bean's methods setter and getters
	public void testSetExperimentSetup(){

		ExperimentImpl exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
		//use the private helper method to setup the ExperimentSetup
		ExperimentSetupImpl expSetup = createEnvironmentExperimentSetup(1);
		//Test1: add ExperimentSetup
			exp_find1.setExperimentSetup(expSetup);
			manager.updateExperiment(exp_find1);

			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			ExperimentSetupImpl expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			//must also have an ID assigned through @OneToOne(cascade={CascadeType.ALL})
			assertTrue(expSetup_find1.getEntityID()>0);
		
		//Test2: modify ExperimentSetup
			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			expSetup = createEnvironmentExperimentSetup(2);
			exp_find1.setExperimentSetup(expSetup);
			manager.updateExperiment(exp_find1);
			
			exp_find1 = (ExperimentImpl)manager.getExperiment(expID1);
			assertNotNull(exp_find1.getExperimentSetup());
			expSetup_find1 = (ExperimentSetupImpl)exp_find1.getExperimentSetup();
			assertEquals("ExperimentName2", exp_find1.getExperimentSetup().getBasicProperties().getExperimentName());
			assertTrue(expSetup_find1.getEntityID()>0);
			
			assertEquals(1,expSetup_find1.getAllAddedBenchmarkGoals().size());
			System.out.println("Definition :"+expSetup_find1.getBenchmarkGoal("nop1").getDefinition());
			assertEquals("nop1",expSetup_find1.getBenchmarkGoal("nop1").getID());
			
	
	}

	
	public ExperimentSetup getExperimentSetupSample(){
		//TODO: add a samle experimentSetup
		ExperimentSetup test_setup = new ExperimentSetupImpl();
		test_setup.setStartDate(new GregorianCalendar());
		test_setup.setExperimentType(ExperimentTypes.EXPERIMENT_TYPE_SIMPLE_MIGRATION);
		return test_setup;
	}
	
	
	private ExperimentSetupImpl createEnvironmentExperimentSetup(int testnr){
		ExperimentSetupImpl expSetup = new ExperimentSetupImpl();
		expSetup.setState(ExperimentPhase.STATE_IN_PROGRESS);
		//BasicProperties
		BasicPropertiesImpl props = new BasicPropertiesImpl();
		props.setConsiderations("considerations"+testnr);
		props.setExperimentName("ExperimentName"+testnr);
		expSetup.setBasicProperties(props);
		
		//BenchmarkObjectives
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		BenchmarkGoalImpl goal = (BenchmarkGoalImpl)handler.getBenchmarkGoal("nop1");
		expSetup.addBenchmarkGoal(goal);
		
		return expSetup;
	}
	
	
	/*private ExperimentResources createEnvironmentExperimentResources(int testnr){
		return null;
	}*/
	
	
	protected void tearDown(){
		try{
			manager.removeExperiment(this.expID1);
			manager.removeExperiment(this.expID2);
		}
		catch(Exception e){
		}
	}

}
