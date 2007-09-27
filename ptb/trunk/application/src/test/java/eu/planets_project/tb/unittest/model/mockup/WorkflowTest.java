package eu.planets_project.tb.unittest.model.mockup;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import eu.planets_project.tb.api.AdminManager;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowImpl;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;
import eu.planets_project.tb.impl.AdminManagerImpl;
import junit.framework.TestCase;

public class WorkflowTest extends TestCase{
	
	private Workflow workflow;
	
	public void setUp(){
		workflow = new WorkflowImpl();
	}
	
	public void testName(){
		assertEquals("",workflow.getName());
		String sName = "Test";
		workflow.setName(sName);
		assertEquals(sName,workflow.getName());
	}
	
	public void testEntityID(){
		//Test1:
		assertEquals(new Long(-1).longValue(),workflow.getEntityID());
		//Test2:
		WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
		int iNumbWFs = wfhandler.getAllWorkflowIDs().size();
		
		if(iNumbWFs>0){
			Iterator<Long> wfIDs = wfhandler.getAllWorkflowIDs().iterator();
			Workflow sampleWF = wfhandler.getWorkflow(wfIDs.next());
			assertTrue(sampleWF.getEntityID()!=-1);
		}else{
			//no Workflows in DB - cannot test the functinoality
			assertEquals("no Workflows in DB - cannot test the functinoality","");
		}
	}
	
	public void testExperimentType(){
		//Test ExperimentType and Name
		assertEquals(null,workflow.getExpeirmentTypeName());
		AdminManager adminManager = AdminManagerImpl.getInstance();
		String sExperimentTypeID = adminManager.getExperimentTypeID("simple migration");
		workflow.setExperimentType(sExperimentTypeID);
		assertEquals(adminManager.getExperimentTypeName(sExperimentTypeID),workflow.getExpeirmentTypeName());
	
		assertEquals(sExperimentTypeID, workflow.getExperimentType());
	}
	
	public void testInputMIMETypes(){
		assertEquals(0,workflow.getRequiredInputMIMETypes().size());
		//valid MIME Type consists of 3 tokens: "String/String"
		String sMimeType1 = "text/html";
		String sMimeType2 = "image/jpeg";
		String sMimeType3 = "text/xml";
		Vector<String> types = new Vector<String>();
		types.add(sMimeType1);
		types.add(sMimeType2);
		types.add(sMimeType3);
		types.add(sMimeType1);

		//Test1: addMimeType
		try {
			workflow.addRequiredInputMIMEType(sMimeType1);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(1,workflow.getRequiredInputMIMETypes().size());
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		
		//Test2: addMimeType: add duplicate mime type should not get added
		try {
			workflow.addRequiredInputMIMEType(sMimeType1);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(1,workflow.getRequiredInputMIMETypes().size());
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		
		//Test3: addMimeType
		try {
			workflow.addRequiredInputMIMEType(sMimeType2);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(2,workflow.getRequiredInputMIMETypes().size());
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType2));
		
		//Test4: setMimeTypes
		try {
			workflow.setRequiredInputMIMETypes(types);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(types.size()-1, workflow.getRequiredInputMIMETypes().size());
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType2));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType3));
		
		//Test5: removeMIMEType
		workflow.removeRequiredInputMIMEType(sMimeType1);
		assertTrue(!workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType2));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType3));
		
		//Test6: removeMIMETypes
		workflow.removeRequiredInputMIMETypes(types);
		assertEquals(0,workflow.getRequiredInputMIMETypes().size());
		
		//Test4: addMIMETypes
		try {
			workflow.addRequiredInputMIMETypes(types);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(types.size()-1,workflow.getRequiredInputMIMETypes().size());
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType2));	
		assertTrue(workflow.getRequiredInputMIMETypes().contains(sMimeType3));
		
	}
	
	public void testOutputMIMETypes(){
		assertEquals(0,workflow.getRequiredOutputMIMETypes().size());
		//valid MIME Type consists of 3 tokens: "String/String"
		String sMimeType1 = "text/html";
		String sMimeType2 = "image/jpeg";
		String sMimeType3 = "text/xml";
		Vector<String> types = new Vector<String>();
		types.add(sMimeType1);
		types.add(sMimeType2);
		types.add(sMimeType3);
		types.add(sMimeType1);

		//Test1: addMimeType
		try {
			workflow.addRequiredOutputMIMEType(sMimeType1);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(1,workflow.getRequiredOutputMIMETypes().size());
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		
		//Test2: addMimeType: add duplicate mime type should not get added
		try {
			workflow.addRequiredOutputMIMEType(sMimeType1);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(1,workflow.getRequiredOutputMIMETypes().size());
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		
		//Test3: addMimeType
		try {
			workflow.addRequiredOutputMIMEType(sMimeType2);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(2,workflow.getRequiredOutputMIMETypes().size());
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType2));
		
		//Test4: setMimeTypes
		try {
			workflow.setRequiredOutputMIMETypes(types);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(types.size()-1, workflow.getRequiredOutputMIMETypes().size());
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType2));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType3));
		
		//Test5: removeMIMEType
		workflow.removeRequiredOutputMIMEType(sMimeType1);
		assertTrue(!workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType2));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType3));
		
		//Test6: removeMIMETypes
		workflow.removeRequiredOutputMIMETypes(types);
		assertEquals(0,workflow.getRequiredOutputMIMETypes().size());
		
		//Test4: addMIMETypes
		try {
			workflow.addRequiredOutputMIMETypes(types);
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		assertEquals(types.size()-1,workflow.getRequiredOutputMIMETypes().size());
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType1));
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType2));	
		assertTrue(workflow.getRequiredOutputMIMETypes().contains(sMimeType3));
		
	}
	
	public void testToolType(){
		workflow.setToolType("jpeg2pdf");
		assertEquals("jpeg2pdf",workflow.getToolType());
	}
	
	public void testServiceAndValidWorkflow(){
		Service service0 = new ServiceImpl();
		service0.setServiceName("TestService0");
		//Test1:
		try {
			workflow.addRequiredInputMIMEType("text/html");
		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		workflow.addWorkflowService(0, service0);
		assertEquals(service0,workflow.getWorkflowService(0));
		assertEquals(false,workflow.isValidWorkflow());
		assertEquals(1,workflow.getWorkflowServices().size());
		assertTrue(workflow.getWorkflowServices().contains(service0));
		
		//Test1.2:
		workflow.removeRequiredInputMIMEType("text/html");
		assertEquals(true,workflow.isValidWorkflow());
		
		
		//Test2: must use remove before add at same position
		Service service_test = new ServiceImpl();
		workflow.addWorkflowService(0, service_test);
		assertEquals(service0,workflow.getWorkflowService(0));
		
		//Test3:
		service0.addInputMIMEType("text/html");
		service0.addInputMIMEType("text/xml");
		service0.addOutputMIMEType("text/pdf");
		try {
			workflow.addRequiredInputMIMEType("text/html");
			workflow.addRequiredOutputMIMEType("text/pdf");

		} catch (InvalidInputException e) {
			assertEquals(true,false);
		}
		workflow.removeWorkflowService(0);
		workflow.addWorkflowService(0, service0);
		assertEquals(service0,workflow.getWorkflowService(0));
		assertTrue(workflow.isValidWorkflow());
	}

}
