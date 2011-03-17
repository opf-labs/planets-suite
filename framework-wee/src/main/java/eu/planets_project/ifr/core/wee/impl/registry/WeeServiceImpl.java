package eu.planets_project.ifr.core.wee.impl.registry;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.wee.api.WeeManager;
import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.ifr.core.wee.impl.WeeManagerImpl;
import eu.planets_project.ifr.core.wee.impl.workflow.WorkflowFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;

@Stateless(name=WeeServiceImpl.NAME)
@Remote(WeeService.class)
@RemoteBinding(jndiBinding = "planets-project.eu/"+WeeService.NAME+"/remote")
@WebService(name= WeeServiceImpl.NAME, 
			serviceName = WeeService.NAME, 
			targetNamespace = PlanetsServices.NS,
			endpointInterface = "eu.planets_project.ifr.core.wee.api.wsinterface.WeeService")
//@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class WeeServiceImpl implements WeeService, Serializable{

	public static final String NAME = "WorkflowExecutionManager";
	private Log log = LogFactory.getLog(WeeServiceImpl.class);
	private static final long serialVersionUID = 5568083679737239315L;
	WorkflowConfigUtil wfConfUtil = new WorkflowConfigUtil();
	WeeManager weeManager;
	
	public WeeServiceImpl(){
		weeManager = WeeManagerImpl.getWeeManagerInstance();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.wsinterface.WeeService#getPositionInQueue(java.util.UUID)
	 */
	public int getPositionInQueue(UUID ticket) throws Exception {
		return weeManager.getPositionInQueue(ticket);
	}
	
	/** {@inheritDoc} */
	public int getProgress(UUID ticket) throws Exception {
		return weeManager.getProgress(ticket);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.wsinterface.WeeService#getResult(java.util.UUID)
	 */
	public WorkflowResult getResult(UUID ticket) throws Exception {
		return weeManager.getExecutionResult(ticket);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.wsinterface.WeeService#getStatus(java.util.UUID)
	 */
	public String getStatus(UUID ticket) throws Exception {
		return weeManager.getStatus(ticket).name();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.wsinterface.WeeService#submitWorkflow(eu.planets_project.services.datatypes.DigitalObject, java.lang.String, java.lang.String)
	 */
	public UUID submitWorkflow(ArrayList<DigitalObject> digObjs,
			String workflowTemplateName, String xmlWorkflowConfig)
			throws Exception {
		
		wfConfUtil.checkValidXMLConfig(xmlWorkflowConfig);
		//read the xml config data and create java representation of it
		WorkflowConf wfConf = WorkflowConfigUtil.unmarshalWorkflowConfig(xmlWorkflowConfig);
		//use workflowFactory to create a new WorkflowInstance with the provided data
		WorkflowInstance wfInstance = WorkflowFactory.create(wfConf, digObjs);
		log.debug("created workflow instance ID: "+wfInstance.getWorkflowID() +" for template "+wfConf.getTemplate().getClazz());
		//submit the wfInstance for execution
		UUID ticket = weeManager.submitWorkflow(wfInstance);
		log.debug("submitted workflow instance ID: "+wfInstance.getWorkflowID() +" position in queue: "+weeManager.getPositionInQueue(ticket));
		//hand back the ticket
		return ticket;
	}

	
	/** {@inheritDoc} */
	public UUID submitWorkflowByReference(ArrayList<URI> digObjRefs,
			String workflowTemplateName, String xmlWorkflowConfig)
			throws Exception {
		
		ArrayList<DigitalObject> lDigObjs = new ArrayList<DigitalObject>();
		DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
		
		//now iterate over the payload and build digital objects
		for(URI digoRefURI : digObjRefs){
			try{
				DigitalObject digo = dataRegistry.retrieve(digoRefURI);
				lDigObjs.add(digo);	
			}
			catch(DigitalObjectNotFoundException e){
				log.debug("skipping data reference: "+digoRefURI);
			}
		}
		return this.submitWorkflow(lDigObjs, workflowTemplateName, xmlWorkflowConfig);
	}
	
}
