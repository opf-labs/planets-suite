/**
 * This class is an JCR manager for digital objects
 */
package eu.planets_project.ifr.core.storage.impl.jcr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import eu.planets_project.ifr.core.common.api.PlanetsLogger;
import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotRemovedException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectTooLargeException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectUpdateException;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;


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
	public static final String CONTENT_RESOLVER_URI = "/storage-browser/jcr/contentResolver?id=";
	public static final String PROTOCOL = "http://";

	/**
	 * String constant for the error handling
	 */
	public static final String DIGITAL_OBJECT_NOT_FOUND = "Digital object not found. ";
	public static final String DIGITAL_OBJECT_TOO_LARGE = "Digital object too large. ";
	/**
	 * String constant for PLANETS namespace URI
	 */
	public static final String PERMANENT_URI = "/planets";
	
	public static final int FIRST_ENTRY = 1;
	
	/**
	 * This is a buffer size and a max byte array size for reading data 
	 * from InputStream to byte array
	 */
	public static final int BUFFER_SIZE = 1048576; 
	public static final int MAX_SIZE = BUFFER_SIZE*5; 

	/**
	 * JCR repository variables
	 */
	private Repository repository = null;
    private javax.jcr.Session session = null;
    
    /**
     * Logger for planets
     */
    private Logger _log = null;
    
	/**
	 * Constructor for DOJCRManager, connects to repository and initialises object.
	 * 
	 * @param	repositoryName
	 * 			JNDI name of the JCR to connect to.
	 * @param	logger
	 * 			A PLANETS logger instance used for logging and debugging.
	 * @throws	NamingException
	 * 			Thrown when the JNDI lookup for the Jackrabbit repository fails.  
	 *          Suggests an installation / setup problem.
	 */
    public DOJCRManager(String repositoryName, Logger logger) throws NamingException 
    {
		try {
			_log = logger;
			//TODO _log.debug("DOJCRManager constructor");

		    InitialContext ctx = new InitialContext();
			//TODO _log.debug("DOJCRManager constructor after ctx");

			// JNDI Lookup of the repository
			System.out.println("DOJCRManager constructor after load");
	        this.repository = (Repository)ctx.lookup(repositoryName);
			//TODO _log.debug("DOJCRManager call getSession() repository: " + repository.toString());
	        this.initialiseRepository();
			//TODO _log.debug("DOJCRManager after call initialiseRepository()");
		} catch (Exception _exp) {
			//TODO _log.debug("Error by repository creation: " + _exp.getMessage());
		}
    }

	/**
	 * Constructor for DOJCRManager, connects to repository and initializes object.
	 * 
	 * @param	repository
	 * 			JCR repository to connect to.
	 * @param	logger
	 * 			A PLANETS logger instance used for logging and debugging.
	 * @throws	NamingException
	 * 			Thrown when the JNDI lookup for the Jackrabbit repository fails.  
	 *          Suggests an installation / setup problem.
	 */
    public DOJCRManager(Repository _repository, Logger logger) throws NamingException 
    {
		try {
			_log = logger;
			//TODO _log.debug("***********************************************");
			//TODO _log.debug("DOJCRManager constructor with repository object");

	        this.repository = _repository;
			//TODO _log.debug("DOJCRManager call getSession() repository: " + repository.toString());
	        this.initialiseRepository();
		} catch (Exception _exp) {
			//TODO _log.debug("Error by repository creation: " + _exp.getMessage());
		}
    }

    /**
     * Logs in a session to the repository.
     * @throws LoginException
     * @throws RepositoryException
     */
    private void getSession() throws LoginException, RepositoryException 
    {
		//TODO _log.debug("DOJCRManager getSession() repository class: " + repository.getClass().getName());
        try 
        {
          session = repository.login(new SimpleCredentials(USERNAME, PASSWORD.toCharArray()));
        }
        catch (Exception e)
        {
        	//TODO _log.debug("DOJCRManager getSession() error: " + e.getMessage());
        }
    }

    /**
     * Method to open repository session.
     * Do nothing for transient repository.
     */
    private void openSession() 
    {
    	if (!repository.getClass().getName().contains(TRANSIENT_CLASS))
    	{
    	   try {
			   getSession();
			} catch (LoginException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Method to close repository session.
     * Do nothing for transient repository.
     */
    private void closeSession() 
    {
    	if (!repository.getClass().getName().contains(TRANSIENT_CLASS))
    	{
 		   //TODO _log.debug("session logout.");
		   session.logout();
    	}
    }
    
    /**
     * Method to register PLANETS namespaces, nodetypes and other JCR stuff.
     */
    private void initialiseRepository() 
    {
    	try {
    		this.getSession();
    		boolean _initialiseRepository = true;
    		
    		// Let's see if the namespace prefix for PLANETS exists
    		String[] _jcrNamespacePrefixes = 
    			this.session.getWorkspace().getNamespaceRegistry().getPrefixes();
    		for (String _namespacePrefix : _jcrNamespacePrefixes) {
    			if (_namespacePrefix.equals(PLANETS_NAMESPACE_PREFIX))
    				_initialiseRepository = false;
    		}
    		
    		// Create the PLANETS namespace
    		if (_initialiseRepository) {
	    		this.session.getWorkspace().getNamespaceRegistry().registerNamespace
	    		   (PLANETS_NAMESPACE_PREFIX, PLANETS_NAMESPACE_URI);
    		}
    	} catch (LoginException _exp) {
			//TODO _log.debug("initialiseRepository LoginException: " + _exp.getMessage());
    	} catch (RepositoryException _exp) {
			//TODO _log.debug("initialiseRepository RepositoryException: " + _exp.getMessage());
    	} finally {
    		closeSession();
    	}
    }

    /**
     * This method manages an opening or creation of node with digital object data
     * @param path
     *    The path to the node with digital object data 
     * @return node 
     *    The node with digital object data
     * @throws IOException
     * @throws RepositoryException
     */
    private Node createDocumentNode(String path) throws IOException, RepositoryException 
    {
		// Carve the path into an array around the separator character
//		//TODO _log.debug("splitting path array");
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);

		// Get the root node for the workspace
//		//TODO _log.debug("getting root node");
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
//		//TODO _log.debug("iterating through " + _pathArray.length + " elements");
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
//			//TODO _log.debug("element " + _loop);
//			//TODO _log.debug("element is called:" + _pathArray[_loop]);
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
//				//TODO _log.debug("adding:" + _pathArray[_loop]);
				_node = _node.addNode(_pathArray[_loop]);
//				//TODO _log.debug("finished the add call");
			} else {
				// Else if it exists get it and set _node to the retrieved node
//				//TODO _log.debug("getting:" + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
		// We're at the end of the loop so this is the final part of the path which is the name of the file node
		// First get the last part of the path array and check to see if the node exists
		//TODO _log.debug("at last node now called:" + _pathArray[_pathArray.length - 1]);
		if (!_node.hasNode(_pathArray[_pathArray.length - 1])) 
		{
			//TODO _log.debug("throwing file exists exception");
		}
		// If not then create it
		_node = _node.addNode(_pathArray[_pathArray.length - 1]);
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
		//TODO _log.debug("createResourceNode() path: " + path);
    	
		// Carve the path into an array around the separator character
		String[] _pathArray = path.split(DOJCRConstants.JCR_PATH_SEPARATOR);

		// Get the current node for the workspace
		Node _node = openNode(path);

		//TODO _log.debug("createResourceNode() node evaluated.");
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
	      	//TODO _log.debug("createResourceNode() add new node path: " + _node.getPath());
		}
		else {
			// If the node already existed then throw a file exists exception as the JCR is write once
	      	//TODO _log.debug("createResourceNode() file exists node path: " + _node.getPath());
			throw new RuntimeException("File exists");
		}

		// Set the modified date
        Calendar _rightNow = Calendar.getInstance();
        _node.setProperty(DOJCRConstants.JCR_LASTMODIFIED, _rightNow);
        // Finally the mimetype property to a default
      	_node.setProperty(DOJCRConstants.JCR_MIMETYPE, DOJCRConstants.DOJCR_PROPERTY_DEFAULT_MIMETYPE);

      	//TODO _log.debug("createResourceNode() node path: " + _node.getPath());
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
    public boolean nodeExists(String path) throws RepositoryException 
    {
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
//		//TODO _log.debug("path length: " + _pathArray.length);

		// Get the root node for the workspace
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path, creating the nt:folder nodes that don't exist
		// We save the last part of the path as this is the name and is hence an nt:file node
		for (int _loop = 1; _loop < _pathArray.length - 1; _loop++) {
			// Check to see if the current node has a child matching the next path part
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then create the nt:folder node and set _node to the newly created node
//				//TODO _log.debug("create nt:folder node: " + _pathArray[_loop]);
				_node = _node.addNode(_pathArray[_loop], DOJCRConstants.NT_FOLDER);
			} else {
				// Else if it exists get it and set _node to the retrieved node
//				//TODO _log.debug("get existing nt:folder node: " + _pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
//      	//TODO _log.debug("openNode() node path: " + _node.getPath());
      	dumpNode(_node);

        return _node;
    }
    
    /**
     * This method manages a retrieving of a node content
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
		//TODO _log.debug("path length: " + _pathArray.length);

		// Get the root node for the workspace
		Node _node = session.getRootNode();
		
		// Now loop through the path array and navigate the path.
		for (int _loop = 1; _loop < _pathArray.length; _loop++) {
			// Check to see if the current node has a child matching the next path part
			if (!_node.hasNode(_pathArray[_loop])) {
				// If it doesn't then do nothing
				//TODO _log.debug("retrieveDigitalObjectNode() error - not found nt:folder node: " + 
						   //_pathArray[_loop]);
			} else {
				// Else if it exists get it and set _node to the retrieved node
				//TODO _log.debug("retrieveDigitalObjectNode() get existing nt:folder node: " + 
						   //_pathArray[_loop]);
				_node = _node.getNode(_pathArray[_loop]);
			}
		}
		
      	//TODO _log.debug("retrieveDigitalObjectNode() node path: " + _node.getPath());

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
					//TODO _log.debug("storeMetadataList() error: " + e.getMessage());
		        	res = DOJCRConstants.RESULT_ERROR;
				}
			}
        }
        else
        {
        	//TODO _log.debug("storeMetadataList()  list is empty.");
        	res = DOJCRConstants.RESULT_LIST_EMPTY;
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
					//TODO _log.debug("storeEventList() error: " + e.getMessage());
		        	res = DOJCRConstants.RESULT_ERROR;
				}
			}
        }
        else
        {
        	//TODO _log.debug("storeEventList() list is empty.");
        	res = DOJCRConstants.RESULT_LIST_EMPTY;
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
     * @throws DigitalObjectUpdateException
	 */
    public int updateEvents(List<Event> eventsList, Node currentNode)
          throws DigitalObjectUpdateException
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
        if ((eventsList != null) && (eventsList.size() > 0)) 
        {
        	try {
                NodeIterator entries = currentNode.getNodes(DOJCRConstants.DOJCR_EVENTS);
                if (entries != null)
                {
                   //TODO _log.debug("updateEvents entries size: " + entries.getSize());
                }
                else
                {
             	   //TODO _log.debug("updateEvents size is null");
                }
                
                while (entries.hasNext()) 
                {
                    Node node = entries.nextNode();
                    if (node != null)
                    {
                       //TODO _log.debug("updateEvents node path: " + node.getPath());
                       node.remove();
                    }
                    else
                    {
                 	  //TODO _log.debug("updateEvents node is null");
                    }
                }
        	} catch (Exception e)
        	{
        	   //TODO _log.debug("updateEvents() error: " + e.getMessage());
         	   throw new DigitalObjectUpdateException(
        			   "updateEvents() error: " + e.getMessage());
        	}
    		
    		storeEventsList(eventsList, currentNode);
        }
    	
        return res;
    }
    
	/**
	 * Persists a meta data list to the JCR beneath
	 * 
	 * @param	MetadataList
	 * 			The meta data list to persist in the JCR 
	 * @param	current digital object node
	 * @return	The result of function 
     * @throws DigitalObjectUpdateException
     */
    public int updateMetadata(List<Metadata> metadataList, Node currentNode)
          throws DigitalObjectUpdateException
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
        if ((metadataList != null) && (metadataList.size() > 0)) 
        {
        	try {
                NodeIterator entries = currentNode.getNodes(DOJCRConstants.DOJCR_METADATA);
                if (entries != null)
                {
                   //TODO _log.debug("updateMetadata entries size: " + entries.getSize());
                }
                else
                {
             	   //TODO _log.debug("updateMetadata size is null");
                }
                
                while (entries.hasNext()) 
                {
                    Node node = entries.nextNode();
                    if (node != null)
                    {
                       //TODO _log.debug("updateMetadata node path: " + node.getPath());
                       node.remove();
                    }
                    else
                    {
                 	  //TODO _log.debug("updateMetadata node is null");
                    }
                }
        	} catch (Exception e)
        	{
        	   //TODO _log.debug("updateMetadata() error: " + e.getMessage());
         	   throw new DigitalObjectUpdateException(
        			   "updateMetadata() error: " + e.getMessage());
        	}
    		
    		storeMetadataList(metadataList, currentNode);
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
					//TODO _log.debug("storePropertyList() error: " + e.getMessage());
		        	res = DOJCRConstants.RESULT_ERROR;
				}
			}
        }
        else
        {
        	//TODO _log.debug("storePropertyList() list is empty.");
        	res = DOJCRConstants.RESULT_LIST_EMPTY;
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
    public int storeMetadata(Metadata metadataObj, Node currentNode) 
          throws IOException, ItemNotFoundException, RepositoryException 
    {
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		Node metadataNode = currentNode.addNode(DOJCRConstants.DOJCR_METADATA);
    		dumpNode(metadataNode);
    		//TODO _log.debug("Creating node for the Metadata. path: " + metadataNode.getPath());
	    	
	    	// Add the specific node properties from the metadata properties
	    	if (metadataObj.getType() != null)
	    		metadataNode.setProperty( DOJCRConstants.DOJCR_METADATA_TYPE
	    				                , metadataObj.getType().toString());
	    	if (metadataObj.getContent() != null)
	    		metadataNode.setProperty( DOJCRConstants.DOJCR_METADATA_CONTENT
	    				                , metadataObj.getContent());
	    	if (metadataObj.getName() != null)
	    		metadataNode.setProperty( DOJCRConstants.DOJCR_METADATA_NAME
	    				                , metadataObj.getName());
    		//TODO _log.debug("metadataObj.getName(): " + metadataObj.getName() + 
    				   //", content: " + metadataObj.getContent());
	    	session.save();
    	} catch (Exception e)
    	{
    		//TODO _log.debug("storeMetadata error: " + e.getMessage());
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
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		Node eventsNode = currentNode.addNode(DOJCRConstants.DOJCR_EVENTS);
    		dumpNode(eventsNode);
	    	//TODO _log.debug("Creating node for the Event. path: " + eventsNode.getPath());
	    	// Add the specific node properties from the events properties
	    	if (eventsObj.getSummary() != null)
	    		eventsNode.setProperty( DOJCRConstants.DOJCR_EVENTS_SUMMARY
	    				              , eventsObj.getSummary().toString());
	    	if (eventsObj.getDatetime() != null)
	    		eventsNode.setProperty(DOJCRConstants.DOJCR_EVENTS_DATETIME, eventsObj.getDatetime());
	    	if (eventsObj.getDuration() >= 0)
	    		eventsNode.setProperty(DOJCRConstants.DOJCR_EVENTS_DURATION, eventsObj.getDuration());
	    	if (eventsObj.getAgent() != null)
	    	{
	    		Node agentNode = eventsNode.addNode(DOJCRConstants.DOJCR_EVENTS_AGENT); 
	    		agentNode.setProperty( DOJCRConstants.DOJCR_EVENTS_AGENT_ID
	    				             , eventsObj.getAgent().getId());
	    		agentNode.setProperty( DOJCRConstants.DOJCR_EVENTS_AGENT_NAME
	    				             , eventsObj.getAgent().getName());
	    		agentNode.setProperty( DOJCRConstants.DOJCR_EVENTS_AGENT_TYPE
	    				             , eventsObj.getAgent().getType());
	    	}
	    	if (eventsObj.getProperties() != null)
	    	{
    	        res = storePropertyList(eventsObj.getProperties(), eventsNode);
        	    if (res != DOJCRConstants.RESULT_OK)
    	        {
    	        	//TODO _log.debug("Error in storePropertyList");
    	        }
	    	}
    	} catch (Exception e)
    	{
    		//TODO _log.debug("storeEvent error: " + e.getMessage());
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
    	int res = DOJCRConstants.RESULT_OK;
    	
    	try {
    		Node propertyNode = eventNode.addNode(DOJCRConstants.DOJCR_EVENTS_PROPERTIES); 
	    	// Add the specific node properties from the Property properties
	    	if (propertyObj.getUri() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_URI
	    		                        , propertyObj.getUri().toString());
	    	if (propertyObj.getName() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_NAME
	    				                , propertyObj.getName());
	    	if (propertyObj.getValue() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_VALUE
	    				                , propertyObj.getValue());
	    	if (propertyObj.getDescription() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_DESCRIPTION
	    				                , propertyObj.getDescription());
	    	if (propertyObj.getUnit() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_UNIT
	    				                , propertyObj.getUnit());
	    	if (propertyObj.getType() != null)
	    		propertyNode.setProperty( DOJCRConstants.DOJCR_EVENTS_PROPERTIES_TYPE
	    				                , propertyObj.getType());
    	} catch (Exception e)
    	{
    		//TODO _log.debug("storeProperty error: " + e.getMessage());
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
    	List<Metadata> _retVal = null;
    	_retVal = new ArrayList<Metadata>();
    	dumpNode(currentNode);
    	
    	try
    	{
           NodeIterator entries = currentNode.getNodes(DOJCRConstants.DOJCR_METADATA);
           if (entries != null)
           {
              //TODO _log.debug("retrieveMetadataList entries size: " + entries.getSize());
           }
           else
           {
        	   //TODO _log.debug("retrieveMetadataList size is null");
           }
           
           while (entries.hasNext()) 
           {
               Node node = entries.nextNode();
               if (node != null)
               {
                  //TODO _log.debug("retrieveMetadataList node path: " + node.getPath());
               }
               else
               {
            	  //TODO _log.debug("retrieveMetadataList node is null");
               }
               Metadata metadataObj = retrieveMetadata(node);
               _retVal.add(metadataObj);
           }

		} catch (Exception e)
		{
			//TODO _log.debug("retrieveMetadataList() error: " + e.getMessage());
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
			//TODO _log.debug("retrieveEventList() error: " + e.getMessage());
		}
        
    	return _retVal;
    }
    
    /**
     * Returns the persisted Property list identified by the passed node.
     * 
     * @param	node
     * 			The Property node to be retrieved.
     * @return	The Property list.
     */
    public List<eu.planets_project.services.datatypes.Property> retrievePropertyList(Node eventNode) 
    {
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
			//TODO _log.debug("retrievePropertyList() error: " + e.getMessage());
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
    public Metadata retrieveMetadata(Node node) 
          throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
    	Metadata _retVal = null;
    	try {
	    	Property _propType = node.getProperty(DOJCRConstants.DOJCR_METADATA_TYPE);
	    	Property _propContent = node.getProperty(DOJCRConstants.DOJCR_METADATA_CONTENT);
	    	Property _propName = node.getProperty(DOJCRConstants.DOJCR_METADATA_NAME);
	    	_retVal = new Metadata
	    	      ( new URI(_propType.getString())
	    	      , _propName.getString()
	    	      , _propContent.getString()
				  );
	    	_retVal.toString();
    	} catch (Exception e)
    	{
    		//TODO _log.debug("retrieveMetadata error: " + e.getMessage());
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
    public Event retrieveEvent(Node node) 
          throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
    	Event res = null;
    	try {
	    	Property _propSummary = node.getProperty(DOJCRConstants.DOJCR_EVENTS_SUMMARY);
	    	Property _propDatetime = node.getProperty(DOJCRConstants.DOJCR_EVENTS_DATETIME);
	    	Property _propDuration = node.getProperty(DOJCRConstants.DOJCR_EVENTS_DURATION);
	    	Agent agentObj = retrieveAgent(node);
	        List<eu.planets_project.services.datatypes.Property> propertyResList = 
	        	retrievePropertyList(node);
		    res = new Event
		          ( _propSummary.getString()
		          , _propDatetime.getString()
		          , _propDuration.getDouble()
		          , agentObj
		          , propertyResList
		          );
		    res.toString();
    	} catch (Exception e)
    	{
    		//TODO _log.debug("retrieveEvent error: " + e.getMessage());
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
    	eu.planets_project.services.datatypes.Property res = null;
    	try {
	    	Property _propUri = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_URI);
	    	Property _propName = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_NAME);
	    	Property _propValue = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_VALUE);
	    	Property _propDescription = 
	    		node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_DESCRIPTION);
	    	Property _propUnit = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_UNIT);
	    	Property _propType = node.getProperty(DOJCRConstants.DOJCR_EVENTS_PROPERTIES_TYPE);
		    res = new eu.planets_project.services.datatypes.Property.Builder(
		    		new URI (_propUri.getString()))
		             .name(_propName.getString())
		             .value(_propValue.getString())
		             .description(_propDescription.getString())
		             .unit(_propUnit.getString())
		             .type(_propType.getString())
		             .build();
		    res.toString();
    	} catch (Exception e)
    	{
    		//TODO _log.debug("retrieveProperty error: " + e.getMessage());
    	}
    	
    	return res;
    }
    
    /**
     * Returns the persisted Agent identified by the passed node.
     * 
     * @param	eventNode
     * 			The Event node containing Agent node to be retrieved.
     * @return	The Agent object.
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws URISyntaxException
     */
    public Agent retrieveAgent(Node eventNode) 
          throws ItemNotFoundException, RepositoryException, URISyntaxException 
    {
    	Agent res = null;
    	
    	try
    	{
           Node agentNode = eventNode.getNode(DOJCRConstants.DOJCR_EVENTS_AGENT);
           
	    	Property _propId = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_ID);
	    	Property _propName = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_NAME);
	    	Property _propType = agentNode.getProperty(DOJCRConstants.DOJCR_EVENTS_AGENT_TYPE);
	    	res = new Agent(
	    			   _propId.getString(),
					   _propName.getString(),
					   _propType.getString()
					   );
	    	res.toString();
		} catch (Exception e)
		{
			//TODO _log.debug("retrieveAgent() error: " + e.getMessage());
		}
        
    	return res;
    }
        
    /**
     * This method represents a node info.
     * @param node
     *        This is a JCR node
     */
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
    	//TODO _log.debug("[NODE] " + res);
    	}
		catch (Exception e)
		{
			//TODO _log.debug("dumpNode() error: " + e.getMessage());
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
		//TODO _log.debug("storeStringValue() value: " + value + ", constant: " + constant);
		if (value != null) 
		{
            try {
				node.setProperty(constant, value);
			} catch (ValueFormatException e) {
				e.printStackTrace();
			} catch (VersionException e) {
				e.printStackTrace();
			} catch (LockException e) {
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
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
		//TODO _log.debug("storeContent() path: " + path);
	    Value contentData;
		try {
			contentData = session.getValueFactory().createValue(streamContent);
	        Node node = createResourceNode
	              ( path 
	              + DOJCRConstants.JCR_PATH_SEPARATOR 
	              + DOJCRConstants.DOJCR_CONTENT_FOLDER
	              );
	        node.setProperty(DOJCRConstants.JCR_DATA, contentData);
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
        return res;
    }
    
    /**
     * This method stores digital object content in JCR.
     * @param streamContent
     * @param path
     * @return storing result
     */
    public int updateContent(InputStream streamContent, String path)
    {
    	int res = DOJCRConstants.RESULT_OK;
		//TODO _log.debug("updateContent() path: " + path);
	    Value contentData;
		try {
			contentData = session.getValueFactory().createValue(streamContent);
	        Node contentNode = retrieveDigitalObjectNode
               ( path
               + DOJCRConstants.JCR_PATH_SEPARATOR 
               + DOJCRConstants.DOJCR_CONTENT_FOLDER
               + DOJCRConstants.JCR_PATH_SEPARATOR 
               + DOJCRConstants.JCR_CONTENT
               );
		    dumpNode(contentNode);
	        contentNode.setProperty(DOJCRConstants.JCR_DATA, contentData);
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
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
    		res = DOJCRConstants.NODE_INDEX_BEGIN + index + DOJCRConstants.NODE_INDEX_END;
    	}
    	return res;
    }
    
    /**
     * This method estimates a method result
     * @param res
     *        This is a storing result as an integer value.
     */
    public void checkResult(int res)
    {
  	    if (res != DOJCRConstants.RESULT_OK)
	    {
  	    	switch (res)
  	    	{
      	    	case DOJCRConstants.RESULT_LIST_EMPTY:
      	        	//TODO _log.debug("List is empty.");
      	        	break;
      	    		
      	    	default:
      	        	//TODO _log.debug("Error occured.");
      	        	break;      	    		
  	    	}
	    }
    }
    
	/**
	 * This method stores digital object in JCR. 
	 * 
	 * @param pdURI
	 *            the URI
	 * @param digitalObject
	 *            the object
	 * @param includeContent
	 *        This flag is true if content should be stored in JCR and false if not
	 * @return create DigitalObject with content by reference pointing to content resolver
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
			//TODO _log.debug("storeDigitalObjectDefinition()" + digitalObject.toString());
    		openSession();

    		uri = URI.create(PERMANENT_URI + uri.toString());
			//TODO _log.debug("storeDigitalObjectDefinition() uri: " + uri.toString());
            Node doNode = createDocumentNode
                  (uri.toString().concat(DOJCRConstants.JCR_PATH_SEPARATOR + DOJCRConstants.DOJCR));
	    	dumpNode(doNode);
            String path = doNode.getPath();
	    	if (doNode.getIndex() > 1)
            {
            	path = doNode.getPath().substring
            	      (0, doNode.getPath().indexOf(DOJCRConstants.NODE_INDEX_BEGIN)); 
            }
	    	//TODO _log.debug("+++ path: " + path);
	    	URI permanentUri = URI.create(path + DOJCRConstants.JCR_PATH_SEPARATOR + doNode.getIndex());
			//TODO _log.debug("storeDigitalObjectDefinition() calculated permanentUri: " + 
					   //permanentUri.toString());
	        storeStringValue(doNode, digitalObject.getTitle(), DOJCRConstants.DOJCR_TITLE);
	        if (permanentUri != null)
	           storeStringValue(doNode, permanentUri.toString(), DOJCRConstants.DOJCR_PERMANENT_URI);
	        if (digitalObject.getFormat() != null)
	        {
	           storeStringValue
	                 ( doNode
	        	     , digitalObject.getFormat().toString()
	        	     , DOJCRConstants.DOJCR_FORMAT
	        	     );
	        }
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
	            //TODO _log.debug("store content.");
				if (includeContent)
				{
					long contentLen = digitalObject.getContent().length();
				    //TODO _log.debug("contentLen: " + contentLen);			    
				    storeContent
				       ( digitalObject.getContent().getInputStream()
				       , uri.toString().concat
				             ( DOJCRConstants.JCR_PATH_SEPARATOR 
				    		 + DOJCRConstants.DOJCR
				    		 + insertNodeIndexToPath(doNode.getIndex())
				    		 )
				       );
				}
	    	}
			
	        checkResult (storeMetadataList(digitalObject.getMetadata(), doNode));
  	        checkResult (storeEventsList(digitalObject.getEvents(), doNode));
	    	session.save();

            //TODO _log.debug("storing permanentUri: " + permanentUri.toString());
    		/*try {
                //TODO _log.debug("storing digitalObject.content: " + digitalObject.getContent().read().read());
    		} catch (IOException e) {
    			e.printStackTrace();
    		}	*/
    		if (digitalObject.getTitle() != null) {
               //TODO _log.debug("storing digitalObject.title: " + digitalObject.getTitle());
    		}
            
	    	DigitalObject.Builder b = new DigitalObject.Builder(
	    			Content.byReference(URI.create((getResolverPath() + permanentUri)).toURL()));
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (permanentUri != null) b.permanentUri(permanentUri);
		    if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getMetadata() != null) 
		    	b.metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]));
		    if (digitalObject.getEvents() != null) 
		    	b.events((Event[]) digitalObject.getEvents().toArray(new Event[0]));
            resVal = b.build();
            //TODO _log.debug("storing completed. ");
            resVal.toString();
    	} catch (Exception e) {
    		//TODO _log.debug("storeDigitalObjectDefinition() error: " + e.getMessage());    		
			throw new DigitalObjectNotStoredException("storeDigitalObjectDefinition() error: ", e);
    	} finally {
    		closeSession();
    	}

		return resVal;
	}
    
	/**
	 * This method removes digital object from JCR identified by
	 * the passed permanent URI. 
	 * 
	 * @param permanentURI
	 *        The permanent URI of digital object to be removed
	 * @return result of remove method
	 * @throws DigitalObjectNotRemovedException
	 */
	public int removeDigitalObject
	      ( URI permanentUri
	      )
		throws DigitalObjectNotRemovedException 
    {
		int res = DOJCRConstants.RESULT_OK;
    	try {    			    		
    		openSession();
     	    Node node = findNodeByPermanentUri(permanentUri);
     	    node.remove();
    	} catch (Exception e) {
    		//TODO _log.debug("removeDigitalObject() error: " + e.getMessage()); 
    		res = DOJCRConstants.RESULT_ERROR;
			throw new DigitalObjectNotRemovedException("removeDigitalObject() error: ", e);
    	} finally {
    		closeSession();
    	}

		return res;
	}

	/**
	 * This method removes all digital objects from JCR. 
	 * 
	 * @return result of remove method
	 * @throws DigitalObjectNotRemovedException
	 */
	public int removeAll()
		throws DigitalObjectNotRemovedException 
    {
		int res = DOJCRConstants.RESULT_OK;
    	try {    			    		
    		openSession();
    		session.getRootNode().getNode(PLANETS_NAMESPACE_PREFIX).remove();
    	} catch (Exception e) {
    		//TODO _log.debug("removeAll() error: " + e.getMessage()); 
    		res = DOJCRConstants.RESULT_ERROR;
			throw new DigitalObjectNotRemovedException("removeAll() error: ", e);
    	} finally {
    		closeSession();
    	}

		return res;
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
			   //TODO _log.debug("retrieveDigitalObjectDefinition() " + name + ": " + 
					   //node.getProperty(name).getString());
			   Property _prop = node.getProperty(name);
			   res = _prop.getString();
			}
		} catch (ValueFormatException e) {
			e.printStackTrace();
			//TODO _log.error(e.getMessage());
		} catch (PathNotFoundException e) {
			e.printStackTrace();
			//TODO _log.error(e.getMessage());
		} catch (RepositoryException e) {
			e.printStackTrace();
			//TODO _log.error(e.getMessage());
		}
        return res;
    }

    /**
     * This method returns HTTP content resolver path for permanent URI
     * @return path
     */
    public static String getResolverPath()
    {
    	return PROTOCOL + PlanetsServerConfig.getHostname() + ":" + 
    	       PlanetsServerConfig.getPort() + CONTENT_RESOLVER_URI;
    }
    
    /**
     * This method evaluates digital object content by reference using permanent URI
     * @param permanentUri
     * @return digital object content
     * @throws MalformedURLException
     */
    public DigitalObjectContent evaluateContentByReference(String permanentUri)
          throws MalformedURLException
    {
       DigitalObjectContent resultContent = null;
    	
   	   //TODO _log.debug("Create DO with content by reference");     	   	  
	   URI contentResolverUri = URI.create(getResolverPath() + permanentUri);
	   resultContent = Content.byReference(contentResolverUri.toURL());

	   return resultContent;
    }
    
    /**
     * This method evaluates digital object content from JCR. It could be content by value
     * or content by reference.
     * @param node
     *        The JCR node storing contentNode with content data
     * @param includeContent
     *        If this flag is true - read content by value otherwise read by reference
     * @param permanentUri
     *        This is a permanent URI needed for content resolver request
     * @return resultContent
     *         This is a content retrieved from JCR for the particular digital object
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws DigitalObjectNotFoundException
     * @throws MalformedURLException
     * @throws DigitalObjectTooLargeException
     */
    public DigitalObjectContent evaluateContent(Node node, boolean includeContent, String permanentUri)
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException, 
              MalformedURLException, DigitalObjectTooLargeException 
    {
       DigitalObjectContent resultContent = null;
	   
 	   if (includeContent)
	   {
          //TODO _log.debug("evaluateContent() retrieve content.");
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
		  
		  // Transfer bytes from InputStream to byte array
          try
          {
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
			 byte[] buf = new byte[BUFFER_SIZE];
			 int len;
//			 //TODO _log.debug("##### inputstream available: " + contentStream.available());
			 while ((len = contentStream.read(buf)) > 0) 
			 {
//				//TODO _log.debug("##### buf length: " + len + ", out.len: " + out.size());
				if (out.size() > MAX_SIZE)
				{
					//TODO _log.debug("File size is larger then " + MAX_SIZE);
					out.close();
		            contentStream.close();
					throw new DigitalObjectTooLargeException("File size is larger then " + MAX_SIZE);
				}
			    out.write(buf, 0, len);
			 }
//			 //TODO _log.debug("##### buf length: " + len + ", out.len: " + out.size());
			 byte[] byteContent = out.toByteArray();
             //TODO _log.debug("evaluateContent() byteContent.length: " + byteContent.length);
             resultContent = Content.byValue(byteContent);
			 out.close();
             contentStream.close();
          } catch (Exception e)
          {
        	 //TODO _log.debug("contentStream.close():  " + e.getMessage());
          }
	   }
 	   else
 	   {
		  resultContent = evaluateContentByReference(permanentUri);
 	   }

 	   return resultContent;
    }
    
    /**
     * This method evaluates digital object content stream from JCR.
     * @param node
     *        The JCR node storing contentNode with content data
     * @param permanentUri
     *        This is a permanent URI needed for content resolver request
     * @return resultStream
     *         This is a content stream retrieved from JCR for the particular digital object
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws DigitalObjectNotFoundException
     * @throws MalformedURLException
     */
    public InputStream evaluateContentAsStream(Node node, String permanentUri)
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException, 
              MalformedURLException 
    {
       InputStream contentStream = null;
	   
      //TODO _log.debug("evaluateContentAsSteam() retrieve content stream.");
      dumpNode(node);
       Node contentNode = retrieveDigitalObjectNode
           ( node.getPath()
           + DOJCRConstants.JCR_PATH_SEPARATOR 
           + DOJCRConstants.DOJCR_CONTENT_FOLDER
           + DOJCRConstants.JCR_PATH_SEPARATOR 
           + DOJCRConstants.JCR_CONTENT
           );
	  dumpNode(contentNode);
	  contentStream = contentNode.getProperty(DOJCRConstants.JCR_DATA).getStream();
	    
 	  return contentStream;
    }
    
    /**
     * This method returns a node in JCR for particular permanent URI
     * @param permanentUri
     *        This is an identifier for the node
     * @return node
     *         This is a node containing permanent URI
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws DigitalObjectNotFoundException
     */
    public Node findNodeByPermanentUri(URI permanentUri)
          throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException 
    {
    	Node node = null;
    	try {
        //TODO _log.debug("findNodeByPermanentUri() permanentUri: " + permanentUri.toString());

        // Carve the permanent URI into an array around the separator character
		String[] _pathArray = permanentUri.toString().split(DOJCRConstants.JCR_PATH_SEPARATOR);
        String digitalObjectIndex = _pathArray[_pathArray.length - 1];
		//TODO _log.debug("findNodeByPermanentUri() digitalObjectIndex: " + digitalObjectIndex);

        node = openNode
              ( permanentUri.toString().replace
        		      ( DOJCRConstants.DOJCR
        			  , DOJCRConstants.DOJCR + DOJCRConstants.NODE_INDEX_BEGIN 
        			                         + digitalObjectIndex 
        			                         + DOJCRConstants.NODE_INDEX_END
        			  )
        	  );
    	} catch (Exception e) {
 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
     	}

      return node;
    }
    
    /**
     * Returns the persisted details of the DigitalObject identified by the passed JCR node 
     * and content.
     * @param node
     *        This is an JCR node containing persisted data
     * @param content
     *        This is a content for digital object
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws	DigitalObjectNotFoundException
     */
    public DigitalObject fillDigitalObject(Node node, DigitalObjectContent content) 
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException 
    {
    	DigitalObject _retVal = null;
    	try {
     	   String _title = getStrProperty(node, DOJCRConstants.DOJCR_TITLE);
     	   String _format = getStrProperty(node, DOJCRConstants.DOJCR_FORMAT);
     	   String _permanentUri = getStrProperty(node, DOJCRConstants.DOJCR_PERMANENT_URI);
     	   String _manifestationOf = getStrProperty(node, DOJCRConstants.DOJCR_MANIFESTATION_OF);
	       List<Metadata> metadataResList = retrieveMetadataList(node);
	       Metadata[] metaList = (Metadata[]) metadataResList.toArray(new Metadata[0]);	
	       List<Event> eventResList = retrieveEventList(node);
	       Event[] eventList = (Event[]) eventResList.toArray(new Event[0]);

		   DigitalObject.Builder b = new DigitalObject.Builder(content);
		   if (_title != null) b.title(_title);
		   if (_permanentUri != null) b.permanentUri(URI.create(_permanentUri));
		   if (_format != null) b.format(URI.create(_format));
		   if (_manifestationOf != null) b.manifestationOf(URI.create(_manifestationOf));
		   if (metaList != null) b.metadata(metaList);
		   if (eventList != null) b.events(eventList);
           _retVal = b.build();
           //TODO _log.debug("fillDigitalObject() retrieve completed. " + _retVal.toString());
    	} catch (Exception e) {
 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
    	}
    	
    	return _retVal;
    }
    
    /**
     * Returns the persisted details of the DigitalObject identified by the passed id String.
     * 
     * @param	permanentUri
     * 			The String id of the DigitalObject to be retrieved.
     * @param includeContent
     *        If this flag is true - retrieve digital object with content otherwise without content.
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws DigitalObjectNotFoundException
     * @throws DigitalObjectUpdateException
     */
    public DigitalObject retrieveDigitalObjectDefinition(URI permanentUri, boolean includeContent) 
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException,
              DigitalObjectUpdateException
    {
    	return updateDigitalObjectExt(permanentUri, includeContent, null);
    }
    
    /**
     * This method updates persisted in JCR repository details of the DigitalObject.
     * 
	 * @param node
	 *        The node to update.
	 * @param includeContent
	 *        This is a flag to process an action including content (flag is true) or not
	 * @param newObject
	 *        This is an updated digital object
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws DigitalObjectUpdateException
     */
    public int updateDigitalObjectParameters(Node node, boolean includeContent, DigitalObject newObject)
          throws DigitalObjectUpdateException
    {
       int res = DOJCRConstants.RESULT_OK;
       
       try {
	 	   String _title = getStrProperty(node, DOJCRConstants.DOJCR_TITLE);
	 	   String _format = getStrProperty(node, DOJCRConstants.DOJCR_FORMAT);
	 	   String _permanentUri = getStrProperty(node, DOJCRConstants.DOJCR_PERMANENT_URI);
	 	   String _manifestationOf = getStrProperty(node, DOJCRConstants.DOJCR_MANIFESTATION_OF);
	       List<Metadata> metadataResList = retrieveMetadataList(node);
	       Metadata[] metaList = (Metadata[]) metadataResList.toArray(new Metadata[0]);	
	       List<Event> eventResList = retrieveEventList(node);
	       Event[] eventList = (Event[]) eventResList.toArray(new Event[0]);      
	       DigitalObjectContent content = evaluateContent(node, includeContent, _permanentUri);
	
	       if (newObject.getTitle() != null)
	           if (!newObject.getTitle().equals(_title))
	              storeStringValue( node
	            		          , newObject.getTitle()
	            		          , DOJCRConstants.DOJCR_TITLE);   
	       if (newObject.getFormat().toString() != null)
	           if (!newObject.getFormat().toString().equals(_format))
	              storeStringValue( node
	            		          , newObject.getFormat().toString()
	            		          , DOJCRConstants.DOJCR_FORMAT); 
	       if (newObject.getManifestationOf() != null)
		       if (!newObject.getManifestationOf().toString().equals(_manifestationOf))
		          storeStringValue( node
		        		           , newObject.getManifestationOf().toString()
		        		           , DOJCRConstants.DOJCR_MANIFESTATION_OF);    	   
	       if (includeContent && newObject.getContent() != null) 
	       {
	           if (!newObject.getContent().equals(content))
	           {
				  long contentLen = newObject.getContent().length();
				  //TODO _log.debug("contentLen: " + contentLen);	
				  //if persistent content is empty - store new content else update with new content
				  if (content != null)
				  {
					  updateContent(newObject.getContent().getInputStream(), node.getPath());
				  }
				  else
				  {
					  storeContent(newObject.getContent().getInputStream(), node.getPath());
				  }
	           }
	       }
	       
	       if (newObject.getMetadata() != null)
	    	   if (!newObject.getMetadata().equals(metaList))
	    		   checkResult (updateMetadata(newObject.getMetadata(), node));
	       if (newObject.getEvents() != null)
	    	   if (!newObject.getEvents().equals(eventList))
	    		   checkResult (updateEvents(newObject.getEvents(), node));
	
	       node.save();
       } catch (Exception e)
       {
    	   //TODO _log.debug("updateDigitalObjectParameters() error: " + e.getMessage());
    	   throw new DigitalObjectUpdateException(
    			   "updateDigitalObjectParameters() error: " + e.getMessage());
       }
		
       return res;
    }

    /**
     * Returns the updated persisted details of the DigitalObject identified 
     * by the passed permanent URI from newObject.
     * 
	 * @param newObject
	 *        This is an updated digital object
	 * @param includeContent
	 *        This is a flag to process an action including content (flag is true) or not
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws DigitalObjectNotFoundException
     * @throws DigitalObjectUpdateException
     */
    public DigitalObject updateDigitalObject( DigitalObject newObject
    		                                , boolean includeContent
    		                                ) 
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException,
              DigitalObjectUpdateException
    {
       return updateDigitalObjectExt(newObject.getPermanentUri(), includeContent, newObject);
    }

    /**
     * Returns the updated persisted details of the DigitalObject identified 
     * by the passed permanent URI.
     * 
     * @param permanentUri
     * 		  The String id of the DigitalObject to be retrieved.
	 * @param includeContent
	 *        This is a flag to process an action including content (flag is true) or not
	 * @param newObject
	 *        This is an updated digital object
     * @return	The DigitalObject with id property equal to the passed id String.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws DigitalObjectNotFoundException
     * @throws DigitalObjectUpdateException
     */
    public DigitalObject updateDigitalObjectExt( URI permanentUri
    		                                   , boolean includeContent
    		                                   , DigitalObject newObject
    		                                   ) 
       throws ItemNotFoundException, RepositoryException, DigitalObjectNotFoundException,
              DigitalObjectUpdateException
    {
    	DigitalObject _retVal = null;
    	Node node = null;
    	try {
    	   openSession();
    		
    	   node = findNodeByPermanentUri(permanentUri);
    	   // new object contains new parameters to be updated
    	   if (newObject != null)
    	   {
	    	   int updateRes = updateDigitalObjectParameters(node, includeContent, newObject);
	    	   if (updateRes != DOJCRConstants.RESULT_OK)
	    	   {
	    		   //TODO _log.debug("Could not update digital object. Result: " + updateRes);
	    		   throw new DigitalObjectUpdateException(
	    				   "Could not update digital object. Result: " + updateRes);
	    	   }
    	   }
	       DigitalObjectContent content = evaluateContent(node, includeContent, permanentUri.toString());
	       _retVal = fillDigitalObject(node, content);
           //TODO _log.debug("retrieveDigitalObjectDefinition() retrieve completed. " + _retVal.toString());
    	} catch (DigitalObjectTooLargeException tle) {
 		   //TODO _log.debug(DIGITAL_OBJECT_TOO_LARGE + tle.getMessage());
 	    	try {
	 		   DigitalObjectContent content = 
	 			   evaluateContentByReference(permanentUri.toString());
		       _retVal = fillDigitalObject(node, content);
 	    	} catch (MalformedURLException e) {
 	 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + " MalformedURLException: " + e.getMessage());
 	 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	     	} 
    	} catch (Exception e) {
 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
     	} finally {
    		closeSession();
    	}
    	
    	return _retVal;
    }
    
    /**
     * Returns the persisted content input stream of the DigitalObject identified 
     * by the passed permanent URI.
     * 
     * @param	permanentUri
     * 			The URI of the digital object content to be retrieved.
     * @return	The digital object content stream.
     * @throws	ItemNotFoundException
     * @throws	RepositoryException
     * @throws URISyntaxException
     * @throws DigitalObjectNotFoundException
     */
    public InputStream retrieveContentAsStream(URI permanentUri) 
          throws ItemNotFoundException, RepositoryException, 
                 URISyntaxException, DigitalObjectNotFoundException 
    {
    	//TODO _log.debug("DOJCRManager.retrieveContentAsStream()");
    	InputStream _retVal = null;
    	try {
    	   openSession();
     	   Node node = findNodeByPermanentUri(permanentUri);
           _retVal = evaluateContentAsStream(node, permanentUri.toString());
    	} catch (Exception e) {
 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
    	} finally {
    		closeSession();
    	}
    	
    	return _retVal;
    }
    
    /**
     * Returns the persisted content of the DigitalObject identified by 
     * the passed permanent URI.
     * 
     * @param  permanentUri
     * 		   The URI of the digital object content to be retrieved.
     * @return The digital object content stream.
     * @throws ItemNotFoundException
     * @throws RepositoryException
     * @throws URISyntaxException
     * @throws DigitalObjectNotFoundException
     */
    public InputStream retrieveContent(URI permanentUri) 
          throws ItemNotFoundException, RepositoryException, 
                 URISyntaxException, DigitalObjectNotFoundException 
    {
    	//TODO _log.debug("DOJCRManager.retrieveContent()");
    	InputStream _retVal = null;
    	try {
    	   openSession();
     	   Node node = findNodeByPermanentUri(permanentUri);
           _retVal = evaluateContent(node, true, permanentUri.toString()).getInputStream();
    	} catch (Exception e) {
 		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
 	       throw new DigitalObjectNotFoundException(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
    	} finally {
    		closeSession();
    	}
    	
    	return _retVal;
    }
    
    /**
     * This method retrieves the parent node for all digital objects saved in JCR
     * for particular path.
     * @param _node
     *        This node is a root node evaluated from the path.
     * @param _list
     *        This is a list containing found URIs
     */
    public void findPermanentUris(Node _node, ArrayList<URI> _list)
    {
    	try {
			// Iterate through the JCR nodes and add their permanent URIs to the list
    		// if any exist
			NodeIterator _iterator = _node.getNodes();
			while (_iterator.hasNext()) 
			{
		        Node _nextNode = _iterator.nextNode();
	        	String uriStr = getStrProperty(_nextNode, DOJCRConstants.DOJCR_PERMANENT_URI);
	        	if (uriStr != null)
	        	{
        	        URI newUri = URI.create(uriStr);
        	        if (newUri != null)
        	        {
	        		   _list.add(newUri);
    	        	}
	        	}
	        	findPermanentUris(_nextNode, _list);
			}
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} 
    }
    
    /**
     * This method retrieves the parent node for all digital objects saved in JCR
     * for particular path.
     * @param _node
     *        This node is a root node evaluated from the path.
     * @param _list
     *        This is a list containing found digital objects
     */
    public void findDigitalObjects(Node _node, ArrayList<DigitalObject> _list)
       throws DigitalObjectNotFoundException, DigitalObjectUpdateException 
    {
    	try {
			// Iterate through the JCR nodes and add their digital objects to the list
    		// if any exist
			NodeIterator _iterator = _node.getNodes();
			while (_iterator.hasNext()) 
			{
		        Node _nextNode = _iterator.nextNode();
	        	String uriStr = getStrProperty(_nextNode, DOJCRConstants.DOJCR_PERMANENT_URI);
	        	if (uriStr != null)
	        	{
        	        URI newUri = URI.create(uriStr);
        	        if (newUri != null)
        	        {
    	        		DigitalObject newDigitalObject = retrieveDigitalObjectDefinition(newUri, false);
            	        if (newDigitalObject != null)
            	        {
	        		       _list.add(newDigitalObject);
            	        }
    	        	}
	        	}
	        	findDigitalObjects(_nextNode, _list);
			}
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} 
    }
    
    /**
     * Get an array list of permanent URIs held beneath a digital object node 
     * identified by the passed path
     * 
     * @param	path
     * 			A JCR path to node.
     * @return	An ArrayList of digital object permanent URIs
     *          null if the node referred to by path is deeper then the digital object node
     */
    public ArrayList<URI> list(URI pathUri) 
    {
    	String path = pathUri.toString();
    	//TODO _log.debug("DOJCRManager.list() start path: " + path + "path length: " + path.length());
    	ArrayList<URI> _list = new ArrayList<URI>(0);
    	try {
    		// Get a repository session
     	   openSession();
	    	// First navigate to the node and get the type
    		Node _node = session.getRootNode();
    		// Find out the place of digital object node in the path
        	int digitalObjectNodeIndex = path.indexOf(DOJCRConstants.DOJCR);
        	if (digitalObjectNodeIndex > 0)
        	{ // digital object node exists in the path
       		   if (path.length() > digitalObjectNodeIndex + DOJCRConstants.DOJCR.length())
       		   {
            	   //TODO _log.debug("DOJCRManager() list() path is too deep. Return null array.");
       			   return null;
       		   }
        	}
        	if (path.length() > FIRST_ENTRY && _node.hasNode(path.substring(FIRST_ENTRY)))
    		{
    			_node = _node.getNode(path.substring(FIRST_ENTRY));
    			findPermanentUris(_node, _list);
    		}
    	} catch (LoginException _exp) {
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
    		throw new RuntimeException(_exp);
    	} finally {
    		if( session != null ) 
        		closeSession();
    	}
    	return _list;
    }
    
    /**
     * Get an array list of digital objects held beneath a digital object node 
     * identified by the passed path
     * 
     * @param	path
     * 			A JCR path to node.
     * @return	An ArrayList of digital objects
     *          null if the node referred to by path is deeper then the digital object node
     */
    public ArrayList<DigitalObject> listDigitalObject(URI pathUri) 
    {
    	String path = pathUri.toString();
    	//TODO _log.debug("DOJCRManager.list() of digital objects. start path: " + 
    			//path + "path length: " + path.length());
    	ArrayList<DigitalObject> _list = new ArrayList<DigitalObject>(0);
    	try {
    		// Get a repository session
     	   openSession();
	    	// First navigate to the node and get the type
    		Node _node = session.getRootNode();
    		// Find out the place of digital object node in the path
        	int digitalObjectNodeIndex = path.indexOf(DOJCRConstants.DOJCR);
        	if (digitalObjectNodeIndex > 0)
        	{ // digital object node exists in the path
       		   if (path.length() > digitalObjectNodeIndex + DOJCRConstants.DOJCR.length())
       		   {
            	   //TODO _log.debug("DOJCRManager.listDigitalObject() path is too deep. Return null array.");
       			   return null;
       		   }
        	}
        	if (path.length() > FIRST_ENTRY && _node.hasNode(path.substring(FIRST_ENTRY)))
    		{
    			_node = _node.getNode(path.substring(FIRST_ENTRY));
    			try {
    			   findDigitalObjects(_node, _list);
    	    	} catch (DigitalObjectNotFoundException e) {
      	  		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
		    	} catch (DigitalObjectUpdateException e) {
	  	  		   //TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + e.getMessage());
	  	     	}     			   
    		}
    	} catch (LoginException _exp) {
	  		//TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + _exp.getMessage());    		
    		throw new RuntimeException(_exp);
    	} catch (RepositoryException _exp) {
	  		//TODO _log.debug(DIGITAL_OBJECT_NOT_FOUND + _exp.getMessage());    		
    		throw new RuntimeException(_exp);
    	} finally {
    		if( session != null ) 
        		closeSession();
    	}
    	return _list;
    }

}
