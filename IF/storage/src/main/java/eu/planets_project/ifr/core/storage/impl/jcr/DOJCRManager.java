/**
 * This class is an JCR manager for digital objects
 */
package eu.planets_project.ifr.core.storage.impl.jcr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.services.utils.FileUtils;

import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;


/**
 * This class manages lookup, connection
 * and sessions to a Java Content Repository for DigitalObject.  
 *
 */
public class DOJCRManager {
	// Define user data for session login
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	
	// Define repository class
	private static final String TRANSIENT_CLASS = "Transient";
	
	/**
	 * String constant for PLANETS namespace prefix
	 */
	public static final String PLANETS_NAMESPACE_PREFIX = "planets";
	
	/**
	 * String constant for PLANETS namespace URI
	 */
	public static final String PLANETS_NAMESPACE_URI = "http:planets_project.eu/planets";

	/**
	 * String constant for content resolver URI
	 */
	public static final String CONTENT_RESOLVER_URI = "http://localhost:8080/contentResolver?id=";

	/**
	 * String constant for PLANETS namespace URI
	 */
	public static final String PERMANENT_URI = "/planets";
	
	public static final int FIRST_ENTRY = 1;

	private Repository repository = null;
    private javax.jcr.Session session = null;
    private Logger _log = null;
    
	/**
	 * Constructor for DOJCRManager, connects to repository and initialises object.
	 * 
	 * @param	repositoryName
	 * 			JNDI name of the JCR to connect to.
	 * @param	logger
	 * 			A PLANETS logger instance used for logging and debugging.
	 * @throws	NamingException
	 * 			Thrown when the JNDI lookup for the Jackrabbit repository fails.  Suggests an installation / setup problem.
	 */
    public DOJCRManager(String repositoryName, Logger logger) throws NamingException {
		try {
			_log = logger;
			_log.info("DOJCRManager constructor");

		    InitialContext ctx = new InitialContext();
			_log.info("DOJCRManager constructor after ctx");

			// JNDI Lookup of the repository
			System.out.println("DOJCRManager constructor after load");
	        this.repository = (Repository)ctx.lookup(repositoryName);
			_log.info("DOJCRManager call getSession() repository: " + repository.toString());
	        this.initialiseRepository();
			_log.info("DOJCRManager after call initialiseRepository()");
		} catch (Exception _exp) {
			_log.info("Error by repository creation: " + _exp.getMessage());
		}
    }

	/**
	 * Constructor for DOJCRManager, connects to repository and initialises object.
	 * 
	 * @param	repository
	 * 			JCR repository to connect to.
	 * @param	logger
	 * 			A PLANETS logger instance used for logging and debugging.
	 * @throws	NamingException
	 * 			Thrown when the JNDI lookup for the Jackrabbit repository fails.  Suggests an installation / setup problem.
	 */
    public DOJCRManager(Repository _repository, Logger logger) throws NamingException {
		try {
			_log = logger;
			_log.info("***********************************************");
			_log.info("DOJCRManager constructor with repository object");

	        this.repository = _repository;
			_log.info("DOJCRManager call getSession() repository: " + repository.toString());
	        this.initialiseRepository();
//			_log.info("DOJCRManager after call initialiseRepository()");
		} catch (Exception _exp) {
			_log.info("Error by repository creation: " + _exp.getMessage());
		}
    }

    /**
     * Logs in a session to the repository.
     */
    private void getSession() throws LoginException, RepositoryException 
    {
		_log.info("DOJCRManager getSession() repository class: " + repository.getClass().getName());
        try 
        {
          session = repository.login(new SimpleCredentials(USERNAME, PASSWORD.toCharArray()));
        }
        catch (Exception e)
        {
        	_log.info("DOJCRManager getSession() error: " + e.getMessage());
        }
//		_log.info("DOJCRManager getSession() result session isLive: " + session.isLive());        
//		_log.info("DOJCRManager getSession() result session repository: " + session.getRepository().toString());        
//		_log.info("DOJCRManager getSession() result session root node: " + session.getRootNode().toString());        
//		_log.info("DOJCRManager getSession() end session: " + session.toString());   
    }

    /**
     * Method to open repository session.
     */
    private void openSession() 
    {
    	if (!repository.getClass().getName().contains(TRANSIENT_CLASS))
    	{
    	   try {
			   getSession();
			} catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Method to close repository session.
     */
    private void closeSession() 
    {
    	if (!repository.getClass().getName().contains(TRANSIENT_CLASS))
    	{
 		   _log.info("session logout.");
		   session.logout();
    	}
    }
    
    /**
     * Method to register PLANETS namespaces, nodetypes and other JCR stuff.
     * 
     * TODO This should be part of some run once set up code, currently check and run if not already run
     */
    private void initialiseRepository() 
    {
    	try {
//			_log.info("DOJCRManager initialiseRepository()");
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
			_log.info("initialiseRepository LoginException: " + _exp.getMessage());
    	} catch (RepositoryException _exp) {
			_log.info("initialiseRepository RepositoryException: " + _exp.getMessage());
    	} finally {
    		closeSession();
    	}
    }

    /**
     * This method manages a node with digital object data
     * @param path
     *    The path to the node with digital object data 
     * @return node 
     *    The node with digital object data
     * @throws RepositoryException
     */
    private Node createDocumentNode(String path) throws IOException, RepositoryException 
    {
		// Carve the path into an array around the separator character
//		_log.info("splitting path array");
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);

		// Get the root node for the workspace
//		_log.info("getting root node");
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
//		_log.info("iterating through " + _pathArray.length + " elements");
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
//			_log.info("element " + _loop);
//			_log.info("element is called:" + _pathArray[_loop]);
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
//				_log.info("adding:" + _pathArray[_loop]);
				_node = _node.addNode(_pathArray[_loop]);
//				_log.info("finished the add call");
			} else {
				// Else if it exists get it and set _node to the retrieved node
//				_log.info("getting:" + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
		// We're at the end of the loop so this is the final part of the path which is the name of the file node
		// First get the last part of the path array and check to see if the node exists
		_log.info("at last node now called:" + _pathArray[_pathArray.length - 1]);
		if (!_node.hasNode(_pathArray[_pathArray.length - 1])) 
		{
			_log.info("throwing file exists exception");
		}
			// If not then create it, we need to add an nt:file node and an nt:resource node named jcr:content that
			// holds the binary
//			_log.info("adding" + _pathArray[_pathArray.length - 1]);
			_node = _node.addNode(_pathArray[_pathArray.length - 1]);
//			_log.info("finished add call");
/*		}
		else {
			// If the node already existed then throw a file exists exception as the JCR is write once
			_log.info("throwing file exists exception");
			throw new IOException("File exists");
		}*/
//		_log.info("returning node");
		return _node;
    }

    /**
     * This method manages a node with binary data content
     * @param path
     *    The path to the node with binary data content
     * @return node 
     *    The node with binary data content
     * @throws RepositoryException
     */
    private Node createResourceNode(String path) throws RepositoryException 
    {
		_log.info("createResourceNode() path: " + path);
    	
		// Carve the path into an array around the separator character
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);

		// Get the current node for the workspace
		Node _node = openNode(path);

		_log.info("createResourceNode() node evaluated.");
		dumpNode(_node);
		// We're at the end of the loop so this is the final part of the path which is the name of the file node
		// First get the last part of the path array and check to see if the node exists
		if (!_node.hasNode(_pathArray[_pathArray.length - 1])) 
		{
			// If not then create it, we need to add an nt:file node and an nt:resource node named jcr:content that
			// holds the binary
			_node = _node.addNode(_pathArray[_pathArray.length - 1],
					              DOJCRConstants.NT_FILE);
			dumpNode(_node);
			_node = _node.addNode(DOJCRConstants.JCR_CONTENT,
								  DOJCRConstants.NT_RESOURCE);
			dumpNode(_node);
	      	_log.info("createResourceNode() add new node path: " + _node.getPath());
		}
		else {
			// If the node already existed then throw a file exists exception as the JCR is write once
	      	_log.info("createResourceNode() file exists node path: " + _node.getPath());
			throw new RuntimeException("File exists");
		}

		// Set the modified date
        Calendar _rightNow = Calendar.getInstance();
        _node.setProperty(DOJCRConstants.JCR_LASTMODIFIED, _rightNow);
        // Finally the mimetype property to a default
      	_node.setProperty(DOJCRConstants.JCR_MIMETYPE, DOJCRConstants.DOJCR_PROPERTY_DEFAULT_MIMETYPE);

      	_log.info("createResourceNode() node path: " + _node.getPath());
		dumpNode(_node);
		
		return _node;
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
     * This method manages an opening or creating of a node
     * @param path
     *    The path to the node 
     * @return node 
     *    The current node 
     * @throws RepositoryException
     */
    private Node openNode(String path) throws RepositoryException
    {
		// Carve the path into an array around the separator character
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);
//		_log.info("path length: " + _pathArray.length);

		// Get the root node for the workspace
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
//				_log.info("create nt:folder node: " + _pathArray[_loop]);
				_node = _node.addNode(_pathArray[_loop], DOJCRConstants.NT_FOLDER);
			} else {
				// Else if it exists get it and set _node to the retrieved node
//				_log.info("get existing nt:folder node: " + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
//      	_log.info("openNode() node path: " + _node.getPath());
      	dumpNode(_node);

        return _node;
    }
    
    /**
     * This method manages an opening or creating of a node
     * @param path
     *    The path to the node 
     * @return node 
     *    The current node 
     * @throws RepositoryException
     */
    private Node retrieveDigitalObjectNode(String path) throws RepositoryException
    {
		// Carve the path into an array around the separator character
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);
		_log.info("path length: " + _pathArray.length);

		// Get the root node for the workspace
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
		for (int _loop = 1; _loop < _pathArray.length; _loop++) {
			// Check to see if the current node has a child matching the next path part
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
				_log.info("retrieveDigitalObjectNode() error - not found nt:folder node: " + _pathArray[_loop]);
			} else {
				// Else if it exists get it and set _node to the retrieved node
				_log.info("retrieveDigitalObjectNode() get existing nt:folder node: " + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
      	_log.info("retrieveDigitalObjectNode() node path: " + _node.getPath());

        return _node;
    }
    
	/**
	 * Persists a Metadata list to the JCR beneath
	 * 
	 * @param	metadataList
	 * 			The Metadata list to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 */
    public int storeMetadataList(List<Metadata> metadataList, Node currentNode) 
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
		_log.info("storeMetadataList()");
		dumpNode(currentNode);
		ListIterator<Metadata> iter = metadataList.listIterator();
		
        if ((metadataList != null) && (metadataList.size() > 0)) 
        {
			while(iter.hasNext())
			{
				Metadata metadataObj = iter.next();
				try
				{
				   storeMetadata(metadataObj, currentNode);
				} catch (Exception e)
				{
					_log.info("storeMetadataList() error: " + e.getMessage());
				}
			}
        }
        else
        {
        	_log.info("storeMetadataList() error - list is empty.");
        	res = DOJCRConstants.RESULT_ERROR;
        }
        
        return res;
    }
    
	/**
	 * Persists an Event list to the JCR beneath
	 * 
	 * @param	EventList
	 * 			The Event list to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 */
    public int storeEventsList(List<Event> eventsList, Node currentNode) 
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
		ListIterator<Event> iter = eventsList.listIterator();
		
        if ((eventsList != null) && (eventsList.size() > 0)) 
        {
			while(iter.hasNext())
			{
				Event eventsObj = iter.next();
				try
				{
				   storeEvent(eventsObj, currentNode);
				} catch (Exception e)
				{
					_log.info("storeEventList() error: " + e.getMessage());
				}
			}
        }
        else
        {
        	_log.info("storeEventList() error - list is empty.");
        	res = DOJCRConstants.RESULT_ERROR;
        }
        
        return res;
    }
    
	/**
	 * Persists a Property list to the JCR beneath
	 * 
	 * @param	propertyList
	 * 			The Property list to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 */
    public int storePropertyList
          ( List<eu.planets_project.services.datatypes.Property> propertyList
          , Node eventNode
          ) 
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
		ListIterator<eu.planets_project.services.datatypes.Property> iter = propertyList.listIterator();
		
        if ((propertyList != null) && (propertyList.size() > 0)) 
        {
			while(iter.hasNext())
			{
				eu.planets_project.services.datatypes.Property propertyObj = iter.next();
				try
				{
				   storeProperty(propertyObj, eventNode);
				} catch (Exception e)
				{
					_log.info("storePropertyList() error: " + e.getMessage());
				}
			}
        }
        else
        {
        	_log.info("storePropertyList() error - list is empty.");
        	res = DOJCRConstants.RESULT_ERROR;
        }
        
        return res;
    }
    
	/**
	 * Persists a Metadata object to the JCR beneath
	 * 
	 * @param	metadataObj
	 * 			The Metadata object to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 * @throws	IOException
	 * @throws	ItemNotFoundException
	 * @throws	RepositoryException
	 */
    public int storeMetadata(Metadata metadataObj, Node currentNode) throws IOException, ItemNotFoundException, RepositoryException 
    {
    	_log.info("DOJCRManager.storeMetadata()");
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		//this.getSession();
    		Node metadataNode = currentNode.addNode(DOJCRConstants.DOJCR_METADATA);
    		dumpNode(metadataNode);
    		_log.info("Creating node for the Metadata. path: " + metadataNode.getPath());
	    	
	    	// Add the specific node properties from the metadata properties
	    	if (metadataObj.getType() != null)
//	    	{
//	    		_log.info("metadataObj.getType(): " + metadataObj.getType().toString());
	    		metadataNode.setProperty(DOJCRConstants.DOJCR_METADATA_TYPE, metadataObj.getType().toString());
//	    	}
	    	if (metadataObj.getContent() != null)
	    		metadataNode.setProperty(DOJCRConstants.DOJCR_METADATA_CONTENT, metadataObj.getContent());
	    	if (metadataObj.getName() != null)
	    		metadataNode.setProperty(DOJCRConstants.DOJCR_METADATA_NAME, metadataObj.getName());
    		_log.info("metadataObj.getName(): " + metadataObj.getName() + ", content: " + metadataObj.getContent());
	    	session.save();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	return res;
    }

	/**
	 * Persists an Event object to the JCR beneath
	 * 
	 * @param	eventsObj
	 * 			The Event object to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 * @throws	IOException
	 * @throws	ItemNotFoundException
	 * @throws	RepositoryException
	 */
    public int storeEvent(Event eventsObj, Node currentNode) 
          throws IOException, ItemNotFoundException, RepositoryException 
    {
    	_log.info("DOJCRManager.storeEvent()");
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		//this.getSession();
    		Node eventsNode = currentNode.addNode(DOJCRConstants.DOJCR_EVENTS);
    		dumpNode(eventsNode);
	    	_log.info("Creating node for the Event. path: " + eventsNode.getPath());
	    	// Add the specific node properties from the events properties
	    	if (eventsObj.getSummary() != null)
	    		eventsNode.setProperty(DOJCRConstants.DOJCR_EVENTS_SUMMARY, eventsObj.getSummary().toString());
	    	if (eventsObj.getDatetime() != null)
	    		eventsNode.setProperty(DOJCRConstants.DOJCR_EVENTS_DATETIME, eventsObj.getDatetime());
	    	if (eventsObj.getDuration() >= 0)
	    		eventsNode.setProperty(DOJCRConstants.DOJCR_EVENTS_DURATION, eventsObj.getDuration());
	    	if (eventsObj.getAgent() != null)
	    	{
	    		Node agentNode = eventsNode.addNode(DOJCRConstants.DOJCR_EVENTS_AGENT); 
	    		agentNode.setProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_ID, eventsObj.getAgent().getId());
	    		agentNode.setProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_NAME, eventsObj.getAgent().getName());
	    		agentNode.setProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_TYPE, eventsObj.getAgent().getType());
	    	}
	    	if (eventsObj.getProperties() != null)
	    	{
    	        res = storePropertyList(eventsObj.getProperties(), eventsNode);
        	    if (res != DOJCRConstants.RESULT_OK)
    	        {
    	        	_log.info("Error in storePropertyList");
    	        }
	    	}
//	    	session.save();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	return res;
    }

	/**
	 * Persists a Property object to the JCR beneath
	 * 
	 * @param	propertyObj
	 * 			The Property object to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
	 * @throws	IOException
	 * @throws	ItemNotFoundException
	 * @throws	RepositoryException
	 */
    public int storeProperty(eu.planets_project.services.datatypes.Property propertyObj, Node eventNode) 
          throws IOException, ItemNotFoundException, RepositoryException 
    {
    	_log.info("DOJCRManager.storeProperty()");
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		//this.getSession();
    		Node propertyNode = eventNode.addNode(DOJCRConstants.DOJCR_EVENTS_PROPERTIES); 
	    	_log.info("Creating node for the Property. path: " + propertyNode.getPath());
	    	// Add the specific node properties from the Property properties
	    	if (propertyObj.getUri() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_URI, propertyObj.getUri().toString());
	    	if (propertyObj.getName() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_NAME, propertyObj.getName());
	    	if (propertyObj.getValue() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_VALUE, propertyObj.getValue());
	    	if (propertyObj.getDescription() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_DESCRIPTION, propertyObj.getDescription());
	    	if (propertyObj.getUnit() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_UNIT, propertyObj.getUnit());
	    	if (propertyObj.getType() != null)
	    		propertyNode.setProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_TYPE, propertyObj.getType());
//	    	session.save();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	return res;
    }

    /**
     * Returns the persisted Metadata list identified by the passed node.
     * 
     * @param	node
     * 			The Metadata node to be retrieved.
     * @return	The Metadata list.
     */
    public List<Metadata> retrieveMetadataList(Node currentNode) 
    {
    	_log.info("DOJCRManager.retrieveMetadataList()");
    	List<Metadata> _retVal = null;
    	_retVal = new ArrayList<Metadata>();
    	dumpNode(currentNode);
    	
    	try
    	{
           NodeIterator entries = currentNode.getNodes(DOJCRConstants.DOJCR_METADATA);
           if (entries != null)
           {
              _log.info("retrieveMetadataList entries size: " + entries.getSize());
           }
           else
           {
        	   _log.info("retrieveMetadataList size is null");
           }
           
           while (entries.hasNext()) 
           {
               Node node = entries.nextNode();
               if (node != null)
               {
                  _log.info("retrieveMetadataList node path: " + node.getPath());
               }
               else
               {
            	  _log.info("retrieveMetadataList node is null");
               }
               Metadata metadataObj = retrieveMetadata(node);
               _retVal.add(metadataObj);
           }

		} catch (Exception e)
		{
			_log.info("retrieveMetadataList() error: " + e.getMessage());
		}
        
    	return _retVal;
    }
    
    /**
     * Returns the persisted Event list identified by the passed node.
     * 
     * @param	node
     * 			The Event node to be retrieved.
     * @return	The Event list.
     */
    public List<Event> retrieveEventList(Node currentNode) 
    {
    	_log.info("DOJCRManager.retrieveEventList()");
    	List<Event> _retVal = null;
    	_retVal = new ArrayList<Event>();
    	
    	try
    	{
           NodeIterator entries = currentNode.getNodes(DOJCRConstants.DOJCR_EVENTS);
           
           while (entries.hasNext()) 
           {
               Node node = entries.nextNode();
               Event eventObj = retrieveEvent(node);
               _retVal.add(eventObj);
           }

		} catch (Exception e)
		{
			_log.info("retrieveEventList() error: " + e.getMessage());
		}
        
    	return _retVal;
    }
    
    /**
     * Returns the persisted eu.planets_project.services.datatypes.Property list identified by the passed node.
     * 
     * @param	node
     * 			The Property node to be retrieved.
     * @return	The Property list.
     */
    public List<eu.planets_project.services.datatypes.Property> retrievePropertyList(Node eventNode) 
    {
    	_log.info("DOJCRManager.retrievePropertyList()");
    	List<eu.planets_project.services.datatypes.Property> _retVal = null;
    	_retVal = new ArrayList<eu.planets_project.services.datatypes.Property>();
    	
    	try
    	{
           NodeIterator entries = eventNode.getNodes(DOJCRConstants.DOJCR_EVENTS_PROPERTIES);
           
           while (entries.hasNext()) 
           {
               Node node = entries.nextNode();
               eu.planets_project.services.datatypes.Property propertyObj = retrieveProperty(node);
               _retVal.add(propertyObj);
           }

		} catch (Exception e)
		{
			_log.info("retrievePropertyList() error: " + e.getMessage());
		}
        
    	return _retVal;
    }
    
    /**
     * Returns the persisted details of the Metadata identified by the passed node.
     * 
     * @param	node
     * 			The String id of the Metadata node to be retrieved.
     * @return	The Metadata object.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws	URISyntaxException
     */
    public Metadata retrieveMetadata(Node node) throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
//    	_log.info("DOJCRManager.retrieveMetadata()");
    	Metadata _retVal = null;
    	try {
//    		this.getSession();
	    	Property _propType = node.getProperty(DOJCRConstants.DOJCR_METADATA_TYPE);
	    	Property _propContent = node.getProperty(DOJCRConstants.DOJCR_METADATA_CONTENT);
	    	Property _propName = node.getProperty(DOJCRConstants.DOJCR_METADATA_NAME);
	    	_retVal = new Metadata
	    	      ( new URI(_propType.getString())
	    	      , _propName.getString()
	    	      , _propContent.getString()
				  );
	    	_retVal.toString();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	
    	return _retVal;
    }
    
    /**
     * Returns the persisted details of the Event identified by the passed node.
     * 
     * @param	node
     * 			The String id of the Event node to be retrieved.
     * @return	The Event object.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws	URISyntaxException
     */
    public Event retrieveEvent(Node node) throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
//    	_log.info("DOJCRManager.retrieveEvent()");
    	Event res = null;
    	try {
//    		this.getSession();
	    	Property _propSummary = node.getProperty(DOJCRConstants.DOJCR_EVENTS_SUMMARY);
	    	Property _propDatetime = node.getProperty(DOJCRConstants.DOJCR_EVENTS_DATETIME);
	    	Property _propDuration = node.getProperty(DOJCRConstants.DOJCR_EVENTS_DURATION);
	    	Agent agentObj = retrieveAgent(node);
	        List<eu.planets_project.services.datatypes.Property> propertyResList = retrievePropertyList(node);
		    res = new Event
		          ( _propSummary.getString()
		          , _propDatetime.getString()
		          , _propDuration.getDouble()
		          , agentObj
		          , propertyResList
		          );
		    res.toString();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	
    	return res;
    }
    
    /**
     * Returns the persisted details of the Property identified by the passed node.
     * 
     * @param	node
     * 			The String id of the Property node to be retrieved.
     * @return	The Property object.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws	URISyntaxException
     */
    public eu.planets_project.services.datatypes.Property retrieveProperty(Node node) 
          throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
//    	_log.info("DOJCRManager.retrieveProperty() node path: " + node.getPath());
    	eu.planets_project.services.datatypes.Property res = null;
    	try {
//    		this.getSession();
	    	Property _propUri = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_URI);
	    	Property _propName = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_NAME);
	    	Property _propValue = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_VALUE);
	    	Property _propDescription = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_DESCRIPTION);
	    	Property _propUnit = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_UNIT);
	    	Property _propType = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_TYPE);
		    res = new eu.planets_project.services.datatypes.Property.Builder(new URI (_propUri.getString()))
		             .name(_propName.getString())
		             .value(_propValue.getString())
		             .description(_propDescription.getString())
		             .unit(_propUnit.getString())
		             .type(_propType.getString())
		             .build();
		    res.toString();
    	} finally {
    		//_log.info("logging out session");
//    		session.logout();
    	}
    	
    	return res;
    }
    
    /**
     * Returns the persisted Agent identified by the passed node.
     * 
     * @param	node
     * 			The Agent node to be retrieved.
     * @return	The Agent object.
     */
    public Agent retrieveAgent(Node eventNode) throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
//    	_log.info("DOJCRManager.retrieveAgent()");
    	Agent res = null;
    	
    	try
    	{
           Node agentNode = eventNode.getNode(DOJCRConstants.DOJCR_EVENTS_AGENT);
           
	       	try {
	//    		this.getSession();
	    	Property _propId = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_ID);
	    	Property _propName = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_NAME);
	    	Property _propType = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_TYPE);
	    	res = new Agent(
	    			   _propId.getString(),
					   _propName.getString(),
					   _propType.getString()
					   );
	    	res.toString();
	    	} finally {
	    		//_log.info("logging out session");
	//    		session.logout();
	    	}
		} catch (Exception e)
		{
			_log.info("retrieveAgent() error: " + e.getMessage());
		}
        
    	return res;
    }
        
    public void dumpNode(Node node)
    {
    	String res = "";
    	try {
			if (node.getName().length() > 0)
			{
	    		res = res + "name: " + node.getName() + "; ";
			}
			if (node.getPath().length() > 0)
			{
				res = res + "path: " + node.getPath() + "; ";
			}
			if (node.hasProperties())
			{
				res = res + "hasProperties" + "; ";
			}
			if (node.getPrimaryNodeType() != null)
			{
				res = res + "type: " + node.getPrimaryNodeType().getName() + "; ";
			}
			if (node.getIndex() >= 0)
			{
				res = res + "index: " + node.getIndex() + "; ";
			}
			if (node.getDepth() >= 0)
			{
				res = res + "depth: " + node.getDepth() + "; ";
			}
			if (node.holdsLock())
			{
				res = res + "holdsLock" + "; ";
			}
			if (node.isCheckedOut())
			{
				res = res + "isCheckedOut" + "; ";
			}
			if (node.isLocked())
			{
				res = res + "isLocked" + "; ";
			}
			if (node.isModified())
			{
				res = res + "isModified" + "; ";
			}
			if (node.isNew())
			{
				res = res + "isNew" + "; ";
			}
    	_log.info("[NODE] " + res);
    	}
		catch (Exception e)
		{
			_log.info("dumpNode() error: " + e.getMessage());
		}
    }
    
    /**
     * This method stores a string value in JCR.
     * @param node
     *        The current node
     * @param value
     *        The property value
     * @param constant
     *        The property JCR constant
     */
    public void storeStringValue(Node node, String value, String constant)
    {
		_log.info("storeStringValue() value: " + value + ", constant: " + constant);
		if (value != null) 
		{
            try {
				node.setProperty(constant, value);
			} catch (ValueFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VersionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            _log.info("storeStringValue() after store " + constant + ": " + value);
    	}
    }
    
    /**
     * This method stores digital object content in JCR.
     * @param streamContent
     * @param path
     * @return storing result
     */
    public int storeContent(InputStream streamContent, String path)
    {
    	int res = DOJCRConstants.RESULT_OK;
		_log.info("storeContent() path: " + path);
	    Value contentData;
		try {
			contentData = session.getValueFactory().createValue(streamContent);
		    try {
				_log.info("store content contentData available: " + contentData.getStream().available());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Node node = createResourceNode
	              ( path 
	              + DOJCRConstants.JCR_PATH_SEPARATOR 
	              + DOJCRConstants.DOJCR_CONTENT_FOLDER
	              );
	        node.setProperty(DOJCRConstants.JCR_DATA, contentData);
		} catch (UnsupportedRepositoryOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return res;
    }
    
    /**
     * This method extends path with node index
     * @param index
     *    The node index
     * @return res
     *    The string representing node path extension
     */
    private String insertNodeIndexToPath(int index)
    {
    	String res = "";
    	if (index > FIRST_ENTRY)
    	{
    		res = "[" + index + "]";
    	}
    	return res;
    }
    
	/**
	 * This method stores digital object in JCR. If it is digital object by value
	 * a content should be stored in JCR independently from fetchContent flag.
	 * @param pdURI
	 *            the URI
	 * @param digitalObject
	 *            the object
	 * @param includeContent
	 *        This flag is true if content should be stored in JCR and false if not
	 * @throws DigitalObjectNotStoredException
	 */
	public DigitalObject storeDigitalObjectDefinition
	      ( URI uri
	      , DigitalObject digitalObject
	      , boolean includeContent
	      )
		throws DigitalObjectNotStoredException 
    {
		DigitalObject resVal = digitalObject;
    	try {    			    		
			_log.info("storeDigitalObjectDefinition()" + digitalObject.toString());
    		openSession();

    		uri = URI.create(PERMANENT_URI + uri.toString());
			_log.info("storeDigitalObjectDefinition() uri: " + uri.toString());
            Node doNode = createDocumentNode
                  (uri.toString().concat(DOJCRConstants.JCR_PATH_SEPARATOR + DOJCRConstants.DOJCR));
	    	dumpNode(doNode);
            String path = doNode.getPath();
	    	if (doNode.getIndex() > 1)
            {
            	path = doNode.getPath().substring(0, doNode.getPath().length() - 3);
            }
	    	_log.info("+++ path: " + path);
	    	URI permanentUri = URI.create(path + DOJCRConstants.JCR_PATH_SEPARATOR + doNode.getIndex());
			_log.info("storeDigitalObjectDefinition() calculated permanentUri: " + permanentUri.toString());
	        storeStringValue(doNode, digitalObject.getTitle(), DOJCRConstants.DOJCR_TITLE);
	        if (permanentUri != null)
	           storeStringValue(doNode, permanentUri.toString(), DOJCRConstants.DOJCR_PERMANENT_URI);
	        if (digitalObject.getFormat() != null)
	           storeStringValue(doNode, digitalObject.getFormat().toString(), DOJCRConstants.DOJCR_FORMAT);
	        if (digitalObject.getManifestationOf() != null)
	        {
   	           storeStringValue
   	              ( doNode
   	              , digitalObject.getManifestationOf().toString()
   	              , DOJCRConstants.DOJCR_MANIFESTATION_OF
   	              );
	        }
	    	
	        if (digitalObject.getContent() != null) 
	    	{
	            _log.info("store content. ");
	            // Check - is it content by value (InputStream != null) or by reference  (InputStream = null)
				InputStream streamContent = digitalObject.getContent().read();
				if (includeContent)
				{
		            if (streamContent != null)
		            {
					    _log.info("store content by value.");	
//					    int is = streamContent.read();
//					    _log.info("is: " + is);
					    storeContent
					       ( streamContent
					       , uri.toString().concat( DOJCRConstants.JCR_PATH_SEPARATOR 
					    		                  + DOJCRConstants.DOJCR
					    		                  + insertNodeIndexToPath(doNode.getIndex()))
					       );
		            }
//		            else
//		            {
//					    _log.info("store content by reference.");		            	
//		        		URI purl = new File(uri).toURI();
//		        		DigitalObjectContent doContent = Content.byReference(purl.toURL());
//					    storeContent(doContent.read(), uri.toString().concat(DOJCRConstants.JCR_PATH_SEPARATOR + DOJCRConstants.DOJCR));
//		            }
				}
	    	}
			
  	        int res = storeMetadataList(digitalObject.getMetadata(), doNode);
      	    if (res != DOJCRConstants.RESULT_OK)
  	        {
  	        	_log.info("Error in storeMetadataList");
  	        }

  	        res = storeEventsList(digitalObject.getEvents(), doNode);
      	    if (res != DOJCRConstants.RESULT_OK)
  	        {
  	        	_log.info("Error in storeEventsList");
  	        }
	    	session.save();
	    	resVal = new DigitalObject.Builder(digitalObject.getContent()).permanentUri(permanentUri).build();
            _log.info("storing completed. ");
    	} catch (Exception e) {
    		_log.info("storeDigitalObjectDefinition() error: " + e.getMessage());    		
			throw new DigitalObjectNotStoredException("storeDigitalObjectDefinition() error: ", e);
    	} finally {
    		closeSession();
    	}

		return resVal;
	}

    /**
     * This method checks and retrieves a string property from JCR
     * @param node
     *    This is a node to which property belongs
     * @param name
     *    This is a property name
     * @return property content as a string
     */
    public String getStrProperty(Node node, String name)
    {
	    String res = null;
        try {
			if (node.hasProperty(name))
			{
			   _log.info("retrieveDigitalObjectDefinition() " + name + ": " + 
					   node.getProperty(name).getString());
			   Property _prop = node.getProperty(name);
			   res = _prop.getString();
			}
			else
			{
			   _log.severe("retrieveDigitalObjectDefinition() property: " + name + " not found in this node.");
			}
		} catch (ValueFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_log.severe(e.getMessage());
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_log.severe(e.getMessage());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_log.severe(e.getMessage());
		}
        return res;
    }

    /**
     * Returns the persisted details of the DigitalObject identified by the passed id String.
     * 
     * @param	permanentUri
     * 			The String id of the DigitalObject to be retrieved.
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     */
    public DigitalObject retrieveDigitalObjectDefinition(URI permanentUri, boolean includeContent) 
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException 
    {
    	DigitalObject _retVal = null;
    	try {
    		openSession();
            _log.info("retrieveDigitalObjectDefinition() get DO with permanentUri: " + permanentUri.toString());

            // Carve the permanent URI into an array around the separator character
    		String[] _pathArray = permanentUri.toString().split(DOJCRConstants.JCR_PATH_SEPARATOR);
            String digitalObjectIndex = _pathArray[_pathArray.length - 1];
    		_log.info("digitalObjectIndex: " + digitalObjectIndex);

            Node node = openNode(
            		permanentUri.toString().replace
            		      ( DOJCRConstants.DOJCR
            			  , DOJCRConstants.DOJCR + "[" + digitalObjectIndex + "]")
            			  );
	    		
     	   String _title = getStrProperty(node, DOJCRConstants.DOJCR_TITLE);
     	   String _format = getStrProperty(node, DOJCRConstants.DOJCR_FORMAT);
     	   String _permanentUri = getStrProperty(node, DOJCRConstants.DOJCR_PERMANENT_URI);
     	   String _manifestationOf = getStrProperty(node, DOJCRConstants.DOJCR_MANIFESTATION_OF);
	       List<Metadata> metadataResList = retrieveMetadataList(node);
	       Metadata[] metaList = (Metadata[]) metadataResList.toArray(new Metadata[0]);	
	       List<Event> eventResList = retrieveEventList(node);
	       Event[] eventList = (Event[]) eventResList.toArray(new Event[0]);

	       DigitalObjectContent content = null;
    	   
     	   if (includeContent)
    	   {
	          _log.info("retrieveDigitalObjectDefinition() retrieve content.");
	          dumpNode(node);
	           Node contentNode = retrieveDigitalObjectNode
	               ( node.getPath()
	               + DOJCRConstants.JCR_PATH_SEPARATOR 
	               + DOJCRConstants.DOJCR_CONTENT_FOLDER
	               + DOJCRConstants.JCR_PATH_SEPARATOR 
	               + DOJCRConstants.JCR_CONTENT
	               );
    		  dumpNode(contentNode);
   			  InputStream contentStream = 
   				  (InputStream) contentNode.getProperty(DOJCRConstants.JCR_DATA).getStream();
			  byte[] byteContent = FileUtils.writeInputStreamToBinary(contentStream);				
	          _log.info("retrieveDigitalObjectDefinition() byteContent.length: " + byteContent.length);
			  content = Content.byValue(byteContent);
    	   }
     	   else
     	   {
     	   	  _log.info("Create DO with content by reference");     	   	  
     		  URI contentResolverUri = URI.create(CONTENT_RESOLVER_URI + _permanentUri);
     		  content = Content.byReference(contentResolverUri.toURL());
     	   }

		   DigitalObject.Builder b = new DigitalObject.Builder(content);
		   if (_title != null) b.title(_title);
		   if (_permanentUri != null) b.permanentUri(URI.create(_permanentUri));
		   if (_format != null) b.format(URI.create(_format));
		   if (_manifestationOf != null) b.manifestationOf(URI.create(_manifestationOf));
		   if (metaList != null) b.metadata(metaList);
		   if (eventList != null) b.events(eventList);
           _retVal = b.build();
           _log.info("retrieveDigitalObjectDefinition() retrieve completed. " + _retVal.toString());
    	} catch (Exception e) {
		   _log.info("retrieveDigitalObjectDefinition() Could not found the digital object. " + e.getMessage());
	       throw new DigitalObjectNotFoundException("Could not found the digital object. " + e.getMessage());
    	} finally {
    		closeSession();
    	}
    	
    	return _retVal;
    }
    
    /**
     * Returns the persisted details of the DigitalObject content identified by the passed permanent URI.
     * 
     * @param	permanentUri
     * 			The URI of the digital object content to be retrieved.
     * @return	The digital object content stream.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     */  
    public InputStream retrieveContent(URI permantentUri) 
          throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
    	_log.info("DOJCRManager.retrieveContent()");
    	InputStream _retVal = null;
    	try {
    		openSession();
    		String _queryString = "//DigitalObject[@permanent_uri=\"" + permantentUri.toString() + "\"]";
    		_log.info("Query string:" + _queryString);
	    	NodeIterator _nodes = this.executeQuery(_queryString);
	    	if (_nodes.getSize() == 0) {
	    		_log.info("No digital object with permantentUri " + permantentUri + " found");
	    		throw new ItemNotFoundException("Digital object permantentUri = " + permantentUri + " was not found");
	    	}
	    	else {
	        	Node _node = _nodes.nextNode();
	    		_log.info("Node found - path: " + _node.getPath() + ", name: " + _node.getName());
   			  _retVal = (InputStream) _node.getProperty(DOJCRConstants.DOJCR_CONTENT).getStream();
	    	}
    	} finally {
    		closeSession();
    	}
    	
    	return _retVal;
    }

    /**
     * This method use XPATH to find queried nodes
     * @param query
     * @return node
     *         The node requested node
     * @throws RepositoryException
     */
    private NodeIterator executeQuery(String query) throws RepositoryException {
    	NodeIterator _retVal = null;
    	QueryManager _qm = session.getWorkspace().getQueryManager();
    	Query _query = _qm.createQuery(query, Query.XPATH);
    	QueryResult _res = _query.execute();
    	_retVal = _res.getNodes();
    	return _retVal;
    }
    
}
