package eu.planets_project.ifr.core.wee.impl.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsServices;

import eu.planets_project.ifr.core.wee.api.WeeManager;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.ifr.core.wee.impl.WeeManagerImpl;
import eu.planets_project.ifr.core.wee.impl.workflow.WorkflowFactory;
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
		
		//read the xml config data and create java representation of it
		WorkflowConf wfConf = this.unmarshalWorkflowConfig(xmlWorkflowConfig);
		//use workflowFactory to create a new WorkflowInstance with the provided data
		WorkflowInstance wfInstance = WorkflowFactory.create(wfConf, digObjs);
		log.debug("created workflow instance ID: "+wfInstance.getWorkflowID() +" for template "+wfConf.getTemplate().getClazz());
		//submit the wfInstance for execution
		UUID ticket = weeManager.submitWorkflow(wfInstance);
		log.debug("submitted workflow instance ID: "+wfInstance.getWorkflowID() +" position in queue: "+weeManager.getPositionInQueue(ticket));
		//hand back the ticket
		return ticket;
	}

	/**
	 * Uses the JAXB to provide a java api for the xml config marshalling/unmarshalling
	 * @return
	 */
	private WorkflowConf unmarshalWorkflowConfig(String xml) throws Exception{
		try {
			File fxmlc = File.createTempFile("xmlconf", "tmp");
			OutputStream baos = new FileOutputStream(fxmlc);
			baos.write(xml.getBytes());
			
			JAXBContext context = JAXBContext.newInstance(WorkflowConf.class); 
			Unmarshaller um = context.createUnmarshaller(); 
			WorkflowConf wfc = (WorkflowConf) um.unmarshal(fxmlc); 
			return wfc;
		} catch (Exception e) {
			log.error("unmarshalWorkflowConfig failed",e);
			throw e;
		}
	}
}
