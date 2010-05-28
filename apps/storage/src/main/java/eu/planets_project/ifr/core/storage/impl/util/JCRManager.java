package eu.planets_project.ifr.core.storage.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.storage.api.InvocationEvent;
import eu.planets_project.ifr.core.storage.api.WorkflowDefinition;
import eu.planets_project.ifr.core.storage.api.WorkflowExecution;
import eu.planets_project.ifr.core.storage.common.PlanetsJCRAccessManager;

/**
 * Utility class called by the PLANETS DataManager.  This class manages lookup, connection
 * and sessions to a Java Content Repository.  It also provides a set of methods that wrap
 * multiple JCR API tasks into a single atomic task, e.g. adding a file to the repository.
 *   
 * @author CFwilson
 *
 */
@SecurityDomain("PlanetsRealm")
public class JCRManager {
	// Static constant holding the path to the properties file
	private static final String PROP_FILE_PATH = "eu/planets_project/ifr/core/storage/jcrmanager.properties";
	
	/**
	 * String constant for PLANETS namespace prefix
	 */
	public static final String PLANETS_NAMESPACE_PREFIX = "planets";
	/**
	 * String constant for PLANETS namespace URI
	 */
	public static final String PLANETS_NAMESPACE_URI = "http:planets_project.eu/planets";

    private InitialContext ctx = new InitialContext();
	private Properties properties = new Properties();
	private Repository repository = null;
    private Session session = null;
    private static Logger log = Logger.getLogger(JCRManager.class.getName());
	/**
	 * Constructor for JCRManager, connects to repository and initialises object.
	 * 
	 * @param	repositoryName
	 * 			JNDI name of the JCR to connect to.
	 * @throws	NamingException
	 * 			Thrown when the JNDI lookup for the Jackrabbit repository fails.  Suggests an installation / setup problem.
	 */
    public JCRManager(String repositoryName) throws NamingException {
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(PROP_FILE_PATH));
			// JNDI Lookup of the repository
	        this.repository = (Repository)this.ctx.lookup(repositoryName);
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		}
    }

    /**
     * Logs in a session to the repository.
     */
    private void getSession() throws LoginException, RepositoryException {
		/**
		 * This logs into the DR via the JBoss id propagation if possible.
		 * Falls-back to simple login.
		 * FIXME Login automatically, BUT We need to determine how to handle WS first.
         */
        session = PlanetsJCRAccessManager.loginJCRLocal();
        if( session != null ) return;
        
		// Simple login currently uses name and password found in the properties file or defaults to anonymous access
        session = repository.login(new SimpleCredentials(properties.getProperty("jcr.credentials.username", ""),
    													 properties.getProperty("jcr.credentials.password", "").toCharArray()));
        
    }

    /**
     * Method to register PLANETS namespaces, nodetypes and other JCR stuff.
     * 
     * TODO This should be part of some run once set up code, currently check and run if not already run
     */
    private void initialiseRepository() {
    	try {
    		this.getSession();
    		boolean _initialiseRepository = true;
    		
    		// Let's see if the namespace prefix for PLANETS exists
    		String[] _jcrNamespacePrefixes = this.session.getWorkspace().getNamespaceRegistry().getPrefixes();
    		for (String _namespacePrefix : _jcrNamespacePrefixes) {
    			if (_namespacePrefix.equals(PLANETS_NAMESPACE_PREFIX))
    				_initialiseRepository = false;
    		}
    		
    		// Create the PLANETS namespace
    		if (_initialiseRepository) {
	    		this.session.getWorkspace().getNamespaceRegistry().registerNamespace(PLANETS_NAMESPACE_PREFIX,
	    																			 PLANETS_NAMESPACE_URI);
    		}
    	} catch (LoginException _exp) {
    	} catch (RepositoryException _exp) {
    	} finally {
    		this.session.logout();
    	}
    }

    /**
     * Checks whether a particular JCR node exists.
     * 
     * @param	path
     * 			Node path to test for node existence.
     * @return	true if node exists at path, false otherwise.
     * @throws	RepositoryException
     */
    public boolean nodeExists(String path) throws RepositoryException {
    	boolean _retVal = false;
    	try {
    		// Get a JCR Session
    		this.getSession();
    		
    		// Get the item, wee don't need it, just find if it exists. 
    		session.getItem(path);
    		_retVal = true;
    	} catch (PathNotFoundException _exp) {
    		_retVal = false;
    	} finally {
    		session.logout();
    	}
    	return _retVal;
    }
    
    /**
     * Returns the binary content for an nt:file node as an InputStream
     * 
     * @param	path
     * 			JCR path to the file node for which the binary content is to be returned.
     * @return	The binary content of the nt:resource node that is the child of the file node.
     * @throws	LoginException
     * @throws	PathNotFoundException
     * @throws	RepositoryException
     */
    public InputStream readContent(String path) throws LoginException, PathNotFoundException, RepositoryException {
    	// The stream to be returned
    	InputStream _stream = null;
    	try {
    		this.getSession();
    		// First lets make sure that the node specified by the path exists and is an nt:file node
    		Node _node = session.getRootNode().getNode(path.substring(1));
    		// Now make sure it's an nt:file node
    		if (_node.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FILE)) {
    			// It's a nt:file node lets get jcr:content node then get the data
    			_node = _node.getNode(JCRConstants.JCR_NODE_NAME_CONTENT);
        		_stream = _node.getProperty(JCRConstants.JCR_PROPERTY_NAME_DATA).getStream();
    		} else
    			throw new PathNotFoundException();
    	} finally {
    		session.logout();
    	}
    	return _stream;
    }

    /**
     * Adds binary content to the JCR, at a new nt:file node identified by the path.
     * The binary content is taken from the input stream
     * 
     * @param	path
     * 			JCR node path for the nt:file node.
     * @param	stream
     * 			InputStream containing the binary content to be added.
     * @throws	LoginException
     * @throws	RepositoryException
     */
    public void addBinaryContent(String path, InputStream stream) throws LoginException, RepositoryException {
    	try {
    		// Get a JCR session
    		this.getSession();
    		
    		// Create the nt:resource Node
    		Node _node = this.createResourceNode(path);

			// Now just set the properties of the nt:resource node (jcr:content)
			// First add the bytestream
			_node.setProperty(JCRConstants.JCR_PROPERTY_NAME_DATA, stream);

			this.session.save();
    	} finally {
    		this.session.logout();
    	}
    }
    
    /**
     * Adds text content to the JCR, at a new nt:unstructured node identified by the path.
     * The text content is taken from the String
     * 
     * @param	path
     * 			JCR node path for the nt:unstructured node.
     * @param	content
     * 			String containing the text content to be added.
     * @throws	LoginException
     * @throws	RepositoryException
     * @throws	UnsupportedEncodingException
     */
    public void addTextContent(String path, String content) throws LoginException, RepositoryException, UnsupportedEncodingException {
    	try {
    		// Get a JCR session
    		this.getSession();
    		
    		// Create the nt:resource Node
    		Node _node = this.createResourceNode(path);

			// Now just set the properties of the nt:resource node (jcr:content)
			// First add the bytestream
			_node.setProperty(JCRConstants.JCR_PROPERTY_NAME_DATA, new String(content.getBytes(), JCRConstants.JCR_PROPERTY_DEFAULT_ENCODING));
			_node.setProperty(JCRConstants.JCR_PROPERTY_NAME_ENCODING, JCRConstants.JCR_PROPERTY_DEFAULT_ENCODING);
			
			this.session.save();
    	} catch (LoginException _exp) {
    		throw _exp;
    	} catch (RepositoryException _exp) {
    		throw _exp;
    	} finally {
    		this.session.logout();
    	}
    }

    private Node createResourceNode(String path) throws RepositoryException{
		// Carve the path into an array around the separator character
		String[] _pathArray = path.split("/");

		// Get the root node for the workspace
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
				_node = _node.addNode(_pathArray[_loop], JCRConstants.JCR_NODE_TYPE_FOLDER);
			} else {
				// Else if it exists get it and set _node to the retrieved node
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
		// We're at the end of the loop so this is the final part of the path which is the name of the file node
		// First get the last part of the path array and check to see if the node exists
		if (!_node.hasNode(_pathArray[_pathArray.length - 1])) {
			// If not then create it, we need to add an nt:file node and an nt:resource node named jcr:content that
			// holds the binary
			_node = _node.addNode(_pathArray[_pathArray.length - 1],
								  JCRConstants.JCR_NODE_TYPE_FILE);
			_node = _node.addNode(JCRConstants.JCR_NODE_NAME_CONTENT,
								  JCRConstants.JCR_NODE_TYPE_RESOURCE);
		}
		else {
			// If the node already existed then throw a file exists exception as the JCR is write once
			throw new RuntimeException("File exists");
		}

		// Set the modified date
        Calendar _rightNow = Calendar.getInstance();
        _node.setProperty(JCRConstants.JCR_PROPERTY_NAME_LASTMODIFIED, _rightNow);
        // Finally the mimetype property to a default
      	_node.setProperty(JCRConstants.JCR_PROPERTY_NAME_MIMETYPE, JCRConstants.JCR_PROPERTY_DEFAULT_MIMETYPE);
		
		return _node;
    }
    
    /**
     * Returns the binary content for an nt:file node in an InputStream
     * 
     * @param	path
     * 			JCR path to the file node for which the binary content is to be returned
     * @return	The binary content of the nt:resource node that is the child of the file node,
     * @throws	PathNotFoundException
	 *			The item doesn't exist. 
     */
    public InputStream getBinaryContent(String path) throws PathNotFoundException {
    	// The stream to be returned
    	InputStream _stream = null;
    	try {
    		this.getSession();
    		Node _node = this.getContentNode(path);
    		_stream = _node.getProperty(JCRConstants.JCR_PROPERTY_NAME_DATA).getStream();
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} finally {
    		session.logout();
    	}
    	return _stream;
    }
    
    /**
     * Returns the text content for an nt:file node as a String
     * 
     * @param	path
     * 			JCR path to the file node for which the binary content is to be returned
     * @return	The text content of the nt:resource node that is the child of the file node,
     * @throws	PathNotFoundException
	 *			The item doesn't exist. 
     */
    public String getTextContent(String path) throws PathNotFoundException {
    	// The stream to be returned
    	String _string = null;
    	try {
    		this.getSession();
    		Node _node = this.getContentNode(path);
    		_string = _node.getProperty(JCRConstants.JCR_PROPERTY_NAME_DATA).getString();
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} finally {
    		session.logout();
    	}
    	return _string;
    }

    private Node getContentNode(String path) throws LoginException, RepositoryException {
		// First lets make sure that the node specified by the path exists and is an nt:file node
		Node _node = session.getRootNode().getNode(path.substring(1));
		// Now make sure it's an nt:file node
		if (_node.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FILE)) {
			// It's a nt:file node lets get jcr:content node then get the data
			_node = _node.getNode(JCRConstants.JCR_NODE_NAME_CONTENT);
		} else
			throw new PathNotFoundException();

		return _node;
    }

    /**
     * Get and array list of nt:file and nt:folder nodes held beneath a particular nt:folder node
     * 
     * @param	path
     * 			A JCR path to an nt:folder node.
     * @return	An ArrayList of child nt:folder or nt:file nodes for the folder
     *          An empty list if the folder is empty
     *          null if the node referred to by path is not of type nt:folder
     */
    public ArrayList<String> list(String path) {
    	// Empty array for return when folder is empty
    	ArrayList<String> _list = new ArrayList<String>(0);
    	try {
    		// Get a repository session
    		this.getSession();
	    	// First navigate to the node and get the type
    		Node _node = session.getRootNode();
    		if (path.length() > 1)
    			_node = _node.getNode(path.substring(1));
    		// Check the type of the node is nt:folder
    		if (_node.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FOLDER) | _node.equals(session.getRootNode())) {
    			// It's a folder so we can get an iterator through the nodes and add the path to the list
    			NodeIterator _iterator = _node.getNodes();
    			while (_iterator.hasNext()) {
    				Node _nextNode = _iterator.nextNode();
    				// If the node is an nt:folder or nt:file add it to the list
    				if ((_nextNode.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FOLDER)) |
    					(_nextNode.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FILE)))
    					_list.add(_nextNode.getPath());
    			}
    		}
    		else if (_node.getPrimaryNodeType().getName().equals(JCRConstants.JCR_NODE_TYPE_FILE)) {
    			// It's a file so return a null list as per contract
    			_list = null;
    		}
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} finally {
    		if( session != null ) session.logout();
    	}
    	return _list;
    }

    /**
     * 
     * @param stream
     * @param path
     * @throws IOException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
	public void exportSystemView(OutputStream stream, String path) throws IOException, PathNotFoundException, RepositoryException {
    	try {
    		this.getSession();
    		session.exportSystemView(path, stream, false, false);
    	} finally {
    		session.logout();
    	}
    }

    /**
     * 
     * @param stream
     * @param path
     * @throws IOException
     * @throws LoginException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public void exportDocumentView(OutputStream stream, String path) throws IOException, LoginException, PathNotFoundException, RepositoryException {
    	try {
    		this.getSession();
    		session.exportDocumentView(path, stream, false, false);
    	} finally {
    		session.logout();
    	}
    }

    /**
     * 
     * @param stream
     * @param path
     * @throws LoginException
     * @throws RepositoryException
     * @throws IOException
     */
    public void importDocumentView(InputStream stream, String path) throws LoginException, RepositoryException, IOException  {
    	try {
    		log.fine("JCRManager.importDocumentView()");
    		log.fine("getting session");
    		this.getSession();
    		log.fine("calling createDocumentNode()");
    		this.createDocumentNode(path, false);
    		log.fine("calling session.import()");
    		session.importXML(path, stream, javax.jcr.ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
    		log.fine("saving session");
    		session.save();
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    }

	/**
	 * Persists a WorkflowDefinition object to the JCR beneath the path specified
	 * 
	 * @param	workflow
	 * 			The WorkflowDefinition object to persist in the JCR 
	 * @param	path
	 * 			Path beneath which to create the WorkflowDefinition node.
	 * @return	The String id of the created WorkflowDefinition
	 * @throws	IOException
	 * @throws	RepositoryException
	 */
    public String storeWorkflowDefinition(WorkflowDefinition workflow, String path) throws IOException, RepositoryException {
    	log.fine("JCRManager.storeWorkflowDefinition()");
    	try {
    		if (workflow == null) {
    			return null;
    		}
    		this.getSession();
    		log.fine("Checking for existence of WorkflowDefinition node");
    		String _queryString = "//WorkflowDefinition[@id=\"" + workflow.getId() + "\"]";
    		NodeIterator _nodes = this.executeQuery(_queryString);
    		if (_nodes.getSize() > 0) {
    			log.fine("WorkflowDefinition with id = " + workflow.getId() + " already exists.");
    			throw new RuntimeException("WorkflowDefinition with id = " + workflow.getId() + " already exists.");
    		}
    		path = path.concat("/WorkflowDefinition");
	    	log.fine("Creating Document node for the workflow");
	    	Node _defNode = this.createDocumentNode(path, true);
	    	log.fine("Creating workflow definition node");
	    	// Add the specific node properties from the workflow properties
	    	log.fine("Adding properties");
	    	if (workflow.getDate() != null) {
	    		Calendar _calendar = Calendar.getInstance();
		    	_calendar.setTime(workflow.getDate());
		    	_defNode.setProperty("date", _calendar);
	    	}

	    	if (workflow.getId() != null)
	    		_defNode.setProperty("id", workflow.getId());

	    	if (workflow.getOwner() != null)
	    		_defNode.setProperty("owner", workflow.getOwner());
	    	if (workflow.getVersion() != null)
	    		_defNode.setProperty("version", workflow.getVersion());
	    	if (workflow.getDescription() != null)
	    		_defNode.setProperty("description", workflow.getDescription());
	    	session.save();
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	return workflow.getId();
    }

    /**
     * Returns the persisted details of the WorkflowDefinition identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the WorkflowDefinition to be retrieved.
     * @return	The WorkflowDefinition with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     */
    public WorkflowDefinition retrieveWorkflowDefinition(String id) throws ItemNotFoundException, RepositoryException {
    	log.fine("JCRManager.retrieveWorkflowDefinition()");
    	WorkflowDefinition _retVal = null;
    	try {
    		this.getSession();
    		String _queryString = "//WorkflowDefinition[@id=\"" + id + "\"]";
    		log.fine("Query string:" + _queryString);
	    	NodeIterator _nodes = this.executeQuery(_queryString);
	    	if (_nodes.getSize() == 0) {
	    		log.fine("No WorkflowDefinition with id " + id + " found");
	    		throw new ItemNotFoundException("WorkflowDefinition id = " + id + " was not found");
	    	}
	    	else {
	        	Node _node = _nodes.nextNode();
	        	Property _propId = _node.getProperty("id");
	        	Property _propOwner = _node.getProperty("owner");
	        	Property _propVersion = _node.getProperty("version");
	        	Property _propDescription = _node.getProperty("description");
	        	Property _propDate = _node.getProperty("date");
	        	_retVal = new WorkflowDefinition(_propId.getString(), _propOwner.getString(), null);
	        	_retVal.setDate(_propDate.getDate().getTime());
	        	_retVal.setDescription(_propDescription.getString());
	        	_retVal.setVersion(_propVersion.getString());
	    	}
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	
    	return _retVal;
    }

	/**
	 * Persists a WorkflowExecution object to the JCR beneath the path specified
	 * 
	 * @param	workflow
	 * 			The WorkflowExecution object to persist in the JCR 
	 * @return	The String id of the created WorkflowExecution
	 * @throws	IOException
	 * @throws	RepositoryException
	 */
    public String storeWorkflowExecution(WorkflowExecution workflow) throws IOException, RepositoryException {
    	log.fine("JCRManager.storeWorkflowExecution()");
    	String _retVal = null;
    	try {
    		this.getSession();
    		// Find the WorfkflowExecution node that will be the parent
    		log.fine("Locating the parent WorkflowDefinition");
    		String _queryStr = "//WorkflowDefinition[@id=\"" + workflow.getWorkflowId() + "\"]";
    		log.fine("Query:" + _queryStr);
    		NodeIterator _nodes = this.executeQuery(_queryStr);
    		log.fine("Node size = " + _nodes.getSize());
    		if (_nodes.getSize() < 1) {
    			log.fine("WorkflowDefinition with id = " + workflow.getWorkflowId() + " doesn't exists.");
    			throw new RuntimeException("WorkflowDefinition with id = " + workflow.getWorkflowId() + " doesn't exists.");
    		}
    		Node _defNode = _nodes.nextNode(); 
    		String _path = _defNode.getPath().concat("/WorkflowExecution");
	    	log.fine("Creating Document node for the workflow");
	    	Node _execNode = this.createDocumentNode(_path, true);
	    	// Add mix:referencable to generate an id
	    	_execNode.addMixin("mix:referenceable");
	    	_retVal = _execNode.getUUID();
	    	log.fine("Creating workflow definition node");
	    	// Add the specific node properties from the workflow properties
	    	log.fine("Adding properties");
	    	if (workflow.getUser() != null)
	    		_execNode.setProperty("userName", workflow.getUser());
	    	if (workflow.getWorkflowId() != null)
	    		_execNode.setProperty("workflowId", workflow.getWorkflowId());
	    	session.save();
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	return _retVal;
    }

    /**
     * Returns the persisted details of the WorkflowExecution identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the WorkflowExecution to be retrieved.
     * @return	The WorkflowExecution with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     */
	public WorkflowExecution retrieveWorkflowExecution(String id) throws ItemNotFoundException, RepositoryException {
    	log.fine("JCRManager.retrieveWorkflowExecution()");
    	WorkflowExecution _retVal = null;
    	try {
    		this.getSession();
    		Node _defNode = session.getNodeByUUID(id); 
        	Property _propId = _defNode.getProperty("id");
        	Property _propUser = _defNode.getProperty("userName");
        	Property _propWorkflowId = _defNode.getProperty("workflowId");
        	_retVal = new WorkflowExecution(_propId.getString(), _propUser.getString());
        	_retVal.setWorkflowId(_propWorkflowId.getString());
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	
    	return _retVal;
    }

	/**
	 * Persists a InvocationEvent object to the JCR beneath
	 * 
	 * @param	event
	 * 			The InvocationEvent object to persist in the JCR 
	 * @param	workflowExecutionId
	 * 			The String id for the WorkflowExecution object that is the parent of the InvocationEvent. 
	 * @return	The String id of the created InvocationEvent
	 * @throws	IOException
	 * @throws	ItemNotFoundException
	 * @throws	RepositoryException
	 */
    public String storeInvocationEvent(InvocationEvent event, String workflowExecutionId) throws IOException, ItemNotFoundException, RepositoryException {
    	log.fine("JCRManager.storeWorkflowExecution()");
    	String _retVal = null;
    	try {
    		this.getSession();
    		// Find the WorfkflowExecution node that will be the parent
    		Node _execNode = session.getNodeByUUID(workflowExecutionId); 
    		String _path = _execNode.getPath().concat("/InvocationEvent");
	    	log.fine("Creating Document node for the InvocationEvent");
	    	Node _eventNode = this.createDocumentNode(_path, true);
	    	// Add mix:referencable to generate an id
	    	_eventNode.addMixin("mix:referenceable");
	    	_retVal = _eventNode.getUUID();
	    	// Add the specific node properties from the workflow properties
	    	log.fine("Adding properties");
	    	Calendar _calendar = Calendar.getInstance();
	    	_eventNode.setProperty("service", event.getService().toString());
	    	if (event.getOperation() != null)
	    		_eventNode.setProperty("operation", event.getOperation());
	    	if (event.getInFile() != null)
	    		_eventNode.setProperty("inFile", event.getInFile().toString());
	    	if (event.getOutFile() != null)
	    		_eventNode.setProperty("outFile", event.getOutFile().toString());
	    	_calendar.setTime(event.getStartDate());
	    	_eventNode.setProperty("start", _calendar);
	    	_calendar.setTime(event.getEndDate());
	    	_eventNode.setProperty("end", _calendar);
	    	session.save();
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	return _retVal;
    }

    /**
     * Returns the persisted details of the InvocationEvent identified by the passed id String.
     * 
     * @param	id
     * 			The String id of the InvocationEvnet to be retrieved.
     * @return	The InvocationEvent with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws	URISyntaxException
     */
    public InvocationEvent retrieveInvocationEvent(String id) throws ItemNotFoundException, RepositoryException, URISyntaxException {
    	log.fine("JCRManager.retrieveWorkflowExecution()");
    	InvocationEvent _retVal = null;
    	try {
    		this.getSession();
    		Node _execNode = session.getNodeByUUID(id); 
        	Property _propService = _execNode.getProperty("service");
        	Property _propOperation = _execNode.getProperty("operation");
        	Property _propInFile = _execNode.getProperty("inFile");
        	Property _propOutFile = _execNode.getProperty("outFile");
        	Property _propStart = _execNode.getProperty("start");
        	Property _propEnd = _execNode.getProperty("end");
        	_retVal = new InvocationEvent(id,
        								  new URI(_propService.getString()),
        								  _propOperation.getString(),
        								  new URI(_propInFile.getString()),
        								  new URI(_propOutFile.getString()),
        								  _propStart.getDate().getTime(),
        								  _propEnd.getDate().getTime());
    	} finally {
    		log.fine("logging out session");
    		session.logout();
    	}
    	
    	return _retVal;
    }
    
    private Node createDocumentNode(String path, Boolean force) throws IOException, RepositoryException {
		// Carve the path into an array around the separator character
		log.fine("JCRManager.createDocumentNode()");
		log.fine("splitting path array");
		String[] _pathArray = path.split("/");

		// Get the root node for the workspace
		log.fine("getting root node");
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
		log.fine("iterating through " + _pathArray.length + " elements");
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
			log.fine("element " + _loop);
			log.fine("element is called:" + _pathArray[_loop]);
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
				log.fine("adding:" + _pathArray[_loop]);
				_node = _node.addNode(_pathArray[_loop]);
				log.fine("finished the add call");
			} else {
				// Else if it exists get it and set _node to the retrieved node
				log.fine("getting:" + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
		// We're at the end of the loop so this is the final part of the path which is the name of the file node
		// First get the last part of the path array and check to see if the node exists
		log.fine("at last node now called:" + _pathArray[_pathArray.length - 1]);
		if (!_node.hasNode(_pathArray[_pathArray.length - 1]) || (force)) {
			// If not then create it, we need to add an nt:file node and an nt:resource node named jcr:content that
			// holds the binary
			log.fine("adding" + _pathArray[_pathArray.length - 1]);
			_node = _node.addNode(_pathArray[_pathArray.length - 1]);
			log.fine("finshed add call");
		}
		else {
			// If the node already existed then throw a file exists exception as the JCR is write once
			log.fine("throwing file exists exception");
			throw new IOException("File exists");
		}
		log.fine("returning node");
		return _node;
    }

    /**
     * Method which returns a list of node paths for nt:file nodes meeting the search
     * criteria.  This is derived from the passed name parameter.
     * 
     * @param	searchRoot
     * 			The JCR node that is the root of the search
     * @param	name
     * 			A String that must be contained by the JCE node name
     * @return	A String [] containing the paths to the JCR nt:file nodes matching the criteria.
     * @throws	InvalidQueryException
     * @throws	RepositoryException
     */
    public ArrayList<String> findFilesWithNameContaining(String searchRoot, String name) throws InvalidQueryException, RepositoryException {
    	ArrayList<String> _list = new ArrayList<String>(0);
    	try {
    		String _queryString = null;
    		this.getSession();

    		if ((searchRoot == null) || (searchRoot == ""))
        		_queryString = "//element(*, nt:file)";
    		else
        		_queryString = "/" + searchRoot + "//element(*, nt:file)";
    		log.fine(_queryString);
    		NodeIterator _it = this.executeQuery(_queryString);
    		
    		while (_it.hasNext()) {
    			Node _node = _it.nextNode();
    			log.fine("Node Name/" + _node.getName());
    			if (_node.getName().contains(name))
    				_list.add(_node.getPath());
    		}
    	} finally {
    		session.logout();
    	}
    	return _list;
    }

    /**
     * Method which returns a list of node paths for nt:file nodes meeting the search
     * criteria.  This is derived from the passed name parameter.
     * 
     * @param	searchRoot
     * 			The root node at which to begin the search
     * @param	ext
     * 			The file extension used to filter the search
     * @return	An array of Strings containing the paths to all nodes matching the search criteria.
     * @throws	InvalidQueryException
     * @throws	RepositoryException
     */
    public ArrayList<String> findFilesWithExtension(String searchRoot, String ext) throws InvalidQueryException, RepositoryException {
    	ArrayList<String> _list = new ArrayList<String>(0);
    	try {
    		String _queryString = null;
    		this.getSession();
    		if ((searchRoot == null) || (searchRoot == ""))
        		_queryString = "//element(*, nt:file)";
    		else
        		_queryString = "/" + searchRoot + "//element(*, nt:file)";
    		log.fine(_queryString);
    		NodeIterator _it = this.executeQuery(_queryString);
    		while (_it.hasNext()) {
    			Node _node = _it.nextNode();
    			log.fine("Node Name/" + _node.getName());
    			if (_node.getName().endsWith(ext))
    				_list.add(_node.getPath());
    		}
    	} finally {
    		session.logout();
    	}
    	return _list;
    }

    private NodeIterator executeQuery(String query) throws RepositoryException {
    	NodeIterator _retVal = null;
    	QueryManager _qm = session.getWorkspace().getQueryManager();
    	Query _query = _qm.createQuery(query, Query.XPATH);
    	QueryResult _res = _query.execute();
    	_retVal = _res.getNodes();
    	return _retVal;
    }
}
