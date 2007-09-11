/**
 * 
 */
package eu.planets_project.tb.impl.model.mockup;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.finals.ExperimentTypes;
import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.WorkflowHandler;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.persistency.WorkflowPersistencyRemote;
import eu.planets_project.tb.api.services.mockups.Service;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.services.mockups.ServiceImpl;

/**
 * @author alindley
 *
 */
public class WorkflowHandlerImpl implements WorkflowHandler {
	
	private static WorkflowHandlerImpl instance;
	//Info: HashMap<EntityID,WorkflowTemplate>
	private HashMap<Long,Workflow> hmWorkflows;
	

	private WorkflowHandlerImpl() {
		//fillHashMap with Entities
		hmWorkflows = queryAllWorkflows();

		//TODO DELETE when Frontend is finished
		//add some dummy workflowtemplates if non are already stored
		if(this.hmWorkflows.size()<=0){
			this.helperCreateDummyWorkflowTemplates();
			hmWorkflows = queryAllWorkflows();
		}
	}
	
	public static synchronized WorkflowHandlerImpl getInstance(){
		if (instance == null){
			instance = new WorkflowHandlerImpl();
		}
		return instance;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAllWorkflows()
	 */
	public Collection<Workflow> getAllWorkflows(){
		hmWorkflows = queryAllWorkflows();
		return this.hmWorkflows.values();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAvailableWorkflowIDAndNames()
	 */
	public Map<Long,String> getAllWorkflowIDAndNames() {
		//updateIndex
		hmWorkflows = queryAllWorkflows();
		//Info: <TemplateID,TemplateName>
		HashMap<Long,String> hmIDandNames = new HashMap<Long,String>();
		Iterator<Long> itKeys = this.hmWorkflows.keySet().iterator();
		while(itKeys.hasNext()){
			long lKey = itKeys.next();
			hmIDandNames.put(lKey,this.hmWorkflows.get(lKey).getName());
		}
		return hmIDandNames;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAvailableWorkflowIDs()
	 */
	public List<Long> getAllWorkflowIDs() {
		//updateIndex
		hmWorkflows = queryAllWorkflows();
		Vector<Long> vRet = new Vector<Long>();
		Iterator<Long> itKeys = this.hmWorkflows.keySet().iterator();
		while(itKeys.hasNext()){
			vRet.add(itKeys.next());
		}
		return vRet;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAvailableWorkflowNames()
	 */
	public List<String> getAllWorkflowNames() {
		//updateIndex
		hmWorkflows = queryAllWorkflows();
		//Info: <TemplateID,TemplateName>
		Vector<String> vRet = new Vector<String>();
		Iterator<Long> itKeys = this.hmWorkflows.keySet().iterator();
		while(itKeys.hasNext()){
			vRet.add(this.hmWorkflows.get(itKeys.next()).getName());
		}
		return vRet;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getExperimentWorkflow(long)
	 */
	public ExperimentWorkflow getExperimentWorkflow(long lWorkflowEntityID){
		if(this.hmWorkflows.containsKey(lWorkflowEntityID)){
			return new ExperimentWorkflowImpl(this.hmWorkflows.get(lWorkflowEntityID));
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAllWorkflowNames(int)
	 */
	public List<String> getAllWorkflowNames(int experimentType) {
		//updateIndex
		hmWorkflows = queryAllWorkflows();
		//Info: <TemplateID,TemplateName>
		Vector<String> vRet = new Vector<String>();
		Iterator<Long> itKeys = this.hmWorkflows.keySet().iterator();
		while(itKeys.hasNext()){
			long lKey = itKeys.next();
			if(this.hmWorkflows.get(lKey).getExperimentType()==experimentType){
				vRet.add(this.hmWorkflows.get(lKey).getName());
			}
		}
		return vRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getAllWorkflows(int)
	 */
	public Collection<Workflow> getAllWorkflows(int experimentType) {
		//updateIndex
		hmWorkflows = queryAllWorkflows();
		Vector<Workflow> vRet = new Vector<Workflow>();
		Iterator<Long> itKeys = this.hmWorkflows.keySet().iterator();
		while(itKeys.hasNext()){
			long lKey = itKeys.next();
			if(this.hmWorkflows.get(lKey).getExperimentType() == experimentType){
				vRet.add(this.hmWorkflows.get(lKey));
			}
		}
		return vRet;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.WorkflowHandler#getWorkflow(int)
	 */
	public Workflow getWorkflow(long workflowID) {
		if(this.hmWorkflows.containsKey(workflowID)){
			return this.hmWorkflows.get(workflowID);
		}
		return null;
	}
	
	/**
	 * This private helper method is used to query the EntityManager (via the ExperimentPersistency) interface
	 * to retrieve all WorkflowTemplates in the data store and builds up the HashMap<ExpID,WorkflowTemplate>.
	 * @return
	 */
	private HashMap<Long,Workflow> queryAllWorkflows(){
		HashMap<Long,Workflow> hmRet = new HashMap<Long,Workflow>();
		WorkflowPersistencyRemote dao_r = this.createPersistencyHandler();
		
		List<Workflow> list = dao_r.queryAllWorkflowTemplates();
		Iterator<Workflow> itList = list.iterator();
		while(itList.hasNext()){
			Workflow template = itList.next();
			hmRet.put(template.getEntityID(), template);
		}
		
		return hmRet;

	}
	
	
	private WorkflowPersistencyRemote createPersistencyHandler(){
		try{
			Context jndiContext = getInitialContext();
			WorkflowPersistencyRemote dao_r = (WorkflowPersistencyRemote) PortableRemoteObject.narrow(
					jndiContext.lookup("WorkflowPersistencyImpl/remote"), WorkflowPersistencyRemote.class);
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

	
	/**
	 * @deprecated
	 * This method currently builds some sample workflowtemplates and 
	 * needs to be deleted when this is done by the GUI
	 */
	//TODO delete this method
	private void helperCreateDummyWorkflowTemplates(){
		//Create WorkflowTemplate1: consists of one MigrationService
		WorkflowImpl template1 = new WorkflowImpl();
		template1.setName("SimpleMigrationWorkflow1");
		template1.setToolType("doc2OpenOfficeXML");
		template1.addRequiredInputMIMEType("application/msword");
		template1.addRequiredOutputMIMEType("text/xml");
		template1.addRequiredOutputMIMEType("application/xml");
		template1.setExperimentType(ExperimentTypes.EXPERIMENT_TYPE_SIMPLE_MIGRATION);
		//create services for workflow
		Service service0 = new ServiceImpl();
			service0.setServiceName("Office Converter");
			service0.setAuthorName("Markus Reis");
			service0.setDescription("This service wrapps the Microsoft Converter and offers file conversion from Office 1997,2000,2003,XP to the Open Office XML standard format.");
			service0.setEndpointID("jboss.ws:context=ifr-sample,endpoint=SimpleCharacterisationService");
			service0.setEndpointAddress("http://dme021:8080/ifr-sample/SimpleCharacterisationService?wsdl");
			service0.setWSDL(new File("http://dme021:8080/ifr-sample/SimpleCharacterisationService?wsdl"));
			service0.addInputMIMEType("application/msword");
			service0.addOutputMIMEType("application/xml");
		template1.addWorkflowService(0, service0);
		if(template1.isValidWorkflow()){
			WorkflowPersistencyRemote dao_r = this.createPersistencyHandler();
			dao_r.persistWorkflowTemplate(template1);
		}
		
		//Create WorkflowTemplate2: consists of one MigrationService
		WorkflowImpl template2 = new WorkflowImpl();
		template2.setName("EmulationWorkflow2");
		template2.setToolType("Tiff2Jpeg");
		template2.addRequiredInputMIMEType("image/tiff");
		template2.addRequiredOutputMIMEType("image/jpeg");
		template2.setExperimentType(ExperimentTypes.EXPERIMENT_TYPE_EMULATION);
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
		template2.addWorkflowService(0, service1);
		if(template1.isValidWorkflow()){
			WorkflowPersistencyRemote dao_r = this.createPersistencyHandler();
			dao_r.persistWorkflowTemplate(template2);
		}
		
	}
}
