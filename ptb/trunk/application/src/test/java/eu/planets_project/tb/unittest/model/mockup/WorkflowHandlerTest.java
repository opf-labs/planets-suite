package eu.planets_project.tb.unittest.model.mockup;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.persistency.WorkflowPersistencyRemote;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowImpl;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;
import junit.framework.TestCase;

public class WorkflowHandlerTest extends TestCase{
	
	WorkflowHandler wfhandler;
	long testWorkflowID;
	String testWorkflowName;
	
	
	protected void setUp(){
		//store a new test Workflow
		createAndStoreTestWorkflow();
		wfhandler = WorkflowHandlerImpl.getInstance();
	}
	
	
	public void testAvailableWorkflowIDs(){
		
		Vector<Long> vWorkflowIDs = (Vector<Long>)wfhandler.getAllWorkflowIDs();
		if( vWorkflowIDs.size()>0){
			//see if sample WorkflowID is contained
			assertTrue(vWorkflowIDs.contains(testWorkflowID));
		}else{
			//Testcase cannot be completed without any Workflow in the DB
			assertTrue(false);
		}
	}
	
	
	public void testAvailableWorkflowIDsAndNames(){
		HashMap<Long,String> hmIDNames = (HashMap<Long,String>)wfhandler.getAllWorkflowIDAndNames();
		if(hmIDNames.keySet().size()>0){
			//see if sample WorkflowID is contained
			assertTrue(hmIDNames.keySet().contains(testWorkflowID));
			assertTrue(hmIDNames.values().contains(testWorkflowName));
		}else{
			//Testcase cannot be completed without any Workflow in the DB
			assertTrue(false);
		}
	}
	
	
	public void testGetWorkflowsPerType(){
		Iterator<Workflow> workflows = wfhandler.getAllWorkflows(
				AdminManagerImpl.getInstance().getExperimentTypeID("simple migration")).iterator();
		boolean bFound = false;
		while(workflows.hasNext()){
			if(workflows.next().getEntityID() == this.testWorkflowID){
				bFound = true;
			}
		}
		assertTrue(bFound);
	}
	
	
	public void testGetWorkflowNamesPerType(){
		Iterator<String> workflows = wfhandler.getAllWorkflowNames(
				AdminManagerImpl.getInstance().getExperimentTypeID("simple migration")).iterator();
		boolean bFound = false;
		while(workflows.hasNext()){
			if(workflows.next().equals(this.testWorkflowName)){
				bFound = true;
			}
		}
		assertTrue(bFound);
	}
	
	
	public void testGetExperimentWorkflow(){
		Collection<Workflow> vWorkflows = wfhandler.getAllWorkflows();
		//Build an ExperimentWorkflow with the first given Workflow
		if(vWorkflows.size()>0){
			//use template to build a workflow instance
			ExperimentWorkflow expWorkflow1 = wfhandler.getExperimentWorkflow(this.testWorkflowID);
			assertNotNull(expWorkflow1);
			
			//Test2: Does it contain our Test Workflow
			assertNotNull(expWorkflow1.getWorkflow());
			assertEquals(this.testWorkflowID,expWorkflow1.getWorkflow().getEntityID());
			
			//Test3: Does the workflow at least contain one service
			assertTrue(expWorkflow1.getWorkflow().getWorkflowServices().size()>0);
			
			//Test4: Does Service contain an Endpoint
			assertNotNull(expWorkflow1.getWorkflow().getWorkflowService(0).getEndpointAddress());
			
		}else{
			//Testcase cannot be completed without any Workflow in the DB
			assertTrue(false);
		}
	}
	
	protected void tearDown(){
		try{
			WorkflowPersistencyRemote dao_r = this.createPersistencyHandler();
			dao_r.deleteWorkflowTemplate(this.testWorkflowID);
		}catch(Exception e){
			
		}
	}
	
	
	private void createAndStoreTestWorkflow(){
		//Create WorkflowTemplate2: consists of one MigrationService
		WorkflowImpl workflow_test = new WorkflowImpl();
		testWorkflowName="TestWorkflow12345";
		workflow_test.setName(testWorkflowName);
		workflow_test.setToolType("Tiff2Jpeg");
		try {
			workflow_test.addRequiredInputMIMEType("image/tiff");
			workflow_test.addRequiredOutputMIMEType("image/jpeg");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}		
		AdminManager manager = AdminManagerImpl.getInstance();
		workflow_test.setExperimentType(manager.getExperimentTypeID("simple migration"));
		//create services for workflow
		Service service1 = new ServiceImpl();
			service1.setServiceName("Tiff2Jpeg Action Converter");
			service1.setAuthorName("Test User");
			service1.setDescription("Currently no description available");
			service1.setEndpointID("jboss.ws:context=ImageMagicWS,endpoint=Tiff2JpegAction");
			service1.setEndpointAddress("http://dme021:8080/ImageMagicWS/Tiff2JpegAction?wsdl");
			service1.setWSDL(new File("http://dme021:8080/ImageMagicWS/Tiff2JpegAction?wsdl"));
			service1.addInputMIMEType("image/tiff");
			service1.addOutputMIMEType("image/jpeg");
		workflow_test.addWorkflowService(0, service1);
		if(workflow_test.isValidWorkflow()){
			WorkflowPersistencyRemote dao_r = this.createPersistencyHandler();
			testWorkflowID = dao_r.persistWorkflowTemplate(workflow_test);
		}
	}
	
	private WorkflowPersistencyRemote createPersistencyHandler(){
		try{
			Context jndiContext = getInitialContext();
			WorkflowPersistencyRemote dao_r = (WorkflowPersistencyRemote) PortableRemoteObject.narrow(
					jndiContext.lookup("testbed/WorkflowPersistencyImpl/remote"), WorkflowPersistencyRemote.class);
			return dao_r;
		}catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Failure in getting PortableRemoteObject: "+e.toString());
			return null;
		}
	}
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}

}
