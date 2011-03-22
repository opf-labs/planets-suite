package eu.planets_project.ifr.core.storage.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import javax.xml.soap.SOAPException;

//import org.jboss.annotation.ejb.LocalBinding;

import eu.planets_project.ifr.core.storage.api.InvocationEvent;
import eu.planets_project.ifr.core.storage.api.WorkflowDefinition;
import eu.planets_project.ifr.core.storage.api.WorkflowExecution;
import eu.planets_project.ifr.core.storage.api.WorkflowManager;
import eu.planets_project.ifr.core.storage.impl.util.JCRManager;

/**
 * Reference Implementation of the WorkflowManger Interface built on top of the Apache Jackrabbit JCR.
 * 
 * @author CFwilson
 *
 */
@Stateless(mappedName="data/WorkflowManager")
@Local(WorkflowManager.class)
//@LocalBinding(jndiBinding="planets-project.eu/WorkflowManager")
public class WorkflowManagerImpl implements WorkflowManager {
	// PLANETS logger
	private static Logger log = Logger.getLogger(WorkflowManagerImpl.class.getName());
	// Properties file location and holder for the DataManager
	private static final String propPath = "eu/planets_project/ifr/core/storage/datamanager.properties";
	private Properties properties = null;
	// JCRManager manages Jackrabbit functionality
	private JCRManager jcrManager = null;
	
	/**
	 * Constructor for the Data Manager. Simply loads the properties and
	 * instantiates the JCR Manager.
	 * The constructor should only fail because it cannot find the properties file or the JCRManager cannot connect
	 * to a JCR instance.
	 * 
	 * @throws	SOAPException
	 *		as can be called by web service
	 */
	public WorkflowManagerImpl() throws SOAPException {
		try {
			log.fine("WorkflowManagerImpl::WorkflowManagerImpl()");
			properties = new Properties();
			log.fine("Getting properties");
	       	properties.load(this.getClass().getClassLoader().getResourceAsStream(propPath));
			log.fine("Creating JCRManager");
	       	jcrManager = new JCRManager(properties.getProperty("planets.if.dr.default.jndi"));
		} catch (IOException _exp) {
			String _message = "WorkflowManagerImpl::WorkflowManagerImpl() Cannot load resources"; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (NamingException _exp) {
			String _message = "DataManager::WorkflowManagerImpl() Cannot connect to Repository";
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		}
	}
    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#createWorkflow(eu.planets_project.ifr.core.storage.api.WorkflowDefinition)
     */
	public String createWorkflow(WorkflowDefinition workflow) {
		String _retVal = null;
		try {
			String _path = "/planets/workflows";
			_retVal = this.jcrManager.storeWorkflowDefinition(workflow, _path);
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}


    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#getWorkflow(java.lang.String)
     */
	public WorkflowDefinition getWorkflow(String id) throws ItemNotFoundException {
		WorkflowDefinition _retVal = null;
		try {
			_retVal = this.jcrManager.retrieveWorkflowDefinition(id);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}


    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#createWorkflowInstance(eu.planets_project.ifr.core.storage.api.WorkflowExecution)
     */
	public String createWorkflowInstance(WorkflowExecution workflow) {
		String _retVal = null;
		try {
			_retVal = this.jcrManager.storeWorkflowExecution(workflow);
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}
	
    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#getWorkflowInstance(java.lang.String)
     */
	public WorkflowExecution getWorkflowInstance(String id) throws ItemNotFoundException {
		WorkflowExecution _retVal = null;
		try {
			_retVal = this.jcrManager.retrieveWorkflowExecution(id);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}


    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#createInvocationEvent(eu.planets_project.ifr.core.storage.api.InvocationEvent, java.lang.String)
     */
	public String createInvocationEvent(InvocationEvent event, String workflowExecutionId) {
		String _retVal = null;
		try {
			_retVal = this.jcrManager.storeInvocationEvent(event, workflowExecutionId);
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}
	
    /**
     * @see eu.planets_project.ifr.core.storage.api.WorkflowManager#getInvocationEvent(java.lang.String)
     */
	public InvocationEvent getInvocationEvent(String id) throws ItemNotFoundException {
		InvocationEvent _retVal = null;
		try {
			_retVal = this.jcrManager.retrieveInvocationEvent(id);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		} catch (URISyntaxException _exp) {
			throw new RuntimeException(_exp);
		}
		return _retVal;
	}
}
