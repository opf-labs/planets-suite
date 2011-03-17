/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/*COMMENT IN AGAIN
package eu.planets_project.tb.unittest.model;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecution;
import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.system.ExperimentInvocationHandler;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowHandlerImpl;
import eu.planets_project.tb.impl.system.ExperimentInvokerImpl;

public class ExperimentExecutionTest extends TestCase{

	private long expID1, expID2;
	private TestbedManager manager;
	
	protected void setUp(){
		manager = TestbedManagerImpl.getInstance(true);
		//create two new test Experiments
		ExperimentImpl exp1 = (ExperimentImpl)manager.createNewExperiment();
		expID1 = exp1.getEntityID();
			
		ExperimentImpl exp2 = (ExperimentImpl)manager.createNewExperiment();
		expID2 = exp2.getEntityID();	
		
	}
	
	public void testExecuteExperiment() {
		//setupTest:
			ExperimentInvocationHandler wfinvoker = new ExperimentInvokerImpl();
			Experiment exp = manager.getExperiment(this.expID1);
			WorkflowHandler wfhandler = WorkflowHandlerImpl.getInstance();
			Workflow workflow = null;
			//now build a sample workflow
			Collection<Workflow> workflows = wfhandler.getAllWorkflows();
			if((workflows!=null)&&(workflows.size()>0)){
				Iterator<Workflow> itWFs = workflows.iterator();
				while(itWFs.hasNext()){
					workflow = itWFs.next();
				}
			}else{
				assertEquals(true,false);
			}
			ExperimentWorkflow wf = new ExperimentExecutableImpl(workflow);
		//Not generic for Unittests must crate sample input
			try{
				URI input1 = new URI("http://localhost:8080/testbed/planets-testbed/inputdata/-1171883584.jpg");
				wf.addInputData(input1);
				exp.getExperimentSetup().setWorkflow(wf);
				exp.getExperimentSetup().setState(ExperimentSetup.STATE_COMPLETED);
				exp.getExperimentApproval().setState(ExperimentApproval.STATE_COMPLETED);
				exp.getExperimentExecution().setState(ExperimentExecution.STATE_IN_PROGRESS);
				manager.updateExperiment(exp);
				
				//now execute the experiment
				//wfinvoker.executeExperimentWorkflow(exp.getEntityID());
				manager.executeExperiment(exp);
				assertEquals(false,exp.getExperimentExecution().isExecutionInProgress());
				assertEquals(true,exp.getExperimentExecution().isExecuted());

				//now check if the migration succeeded 
				manager = TestbedManagerImpl.getInstance(true);
				Experiment expUpdated = manager.getExperiment(this.expID1);
				assertNotNull(expUpdated.getExperimentExecution().getExecutionOutputData(input1));
				assertEquals(Experiment.PHASENAME_EXPERIMENTEXECUTION,expUpdated.getCurrentPhase().getPhaseName());
				assertNotNull(expUpdated.getExperimentExecution().getExecutionDataEntry(input1).getValue());
			}catch(Exception e){
				System.out.println("Problem in running ExecuteExperimentWorkflowTest "+e.toString());
				assertEquals(true,false);
			}
			assertEquals(true,true);

		}
	
	protected void tearDown(){
		try{
			manager.removeExperiment(this.expID1);
			manager.removeExperiment(this.expID2);
		}
		catch(Exception e){
		}
	}
}
COMMENT IN AGAIN*/
