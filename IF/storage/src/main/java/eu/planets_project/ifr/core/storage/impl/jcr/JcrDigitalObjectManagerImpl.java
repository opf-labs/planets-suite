/**
 * This class manages digital objects for java content repository (jackrabbit)
 */
package eu.planets_project.ifr.core.storage.impl.jcr;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Property;

/**
 * Implementation of the DigitalObjectManager interface for JCR.
 * 
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 * @author <a href="mailto:christian.sadilek@ait.ac.at">Christian Sadilek</a>
 * 
 */
public class JcrDigitalObjectManagerImpl implements DigitalObjectManager 
{
	/** The logger instance */
	private static Logger _log = Logger.getLogger(JcrDigitalObjectManagerImpl.class.getName());

	/** The name of repository for storing digital objects. */
	public static String DIGITAL_OBJECT_REPOSITORY_NAME = "java:jcr/local";
	
	public static String INGEST_EVENT = "planets://repository/event/ingest";

	/** The name of this data registry instance */
	protected String _name = null;

    /**
     * Root URI on the file system
     */
    private URI root;

	/** JCRManager manages Jackrabbit functionality */
	private DOJCRManager jcrManager = null;

	private static volatile JcrDigitalObjectManagerImpl instance = null;

	
	/**
	 * This is an initialization method for this singleton class
	 * @return
	 * 		A new JcrDigitalObjectManagerImpl instance
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance() throws IllegalArgumentException 
	{
		if(instance == null)
		{
			synchronized(JcrDigitalObjectManagerImpl.class)
			{
				if(instance == null)
				{
				   instance = new JcrDigitalObjectManagerImpl();
				}
			}
		}
		return instance;
	}

	/**
	 * This is an initialization method for this singleton class with repository object
	 * @param repository object
	 * @return
	 * 		A new JcrDigitalObjectManagerImpl instance
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(Repository repository) 
	      throws IllegalArgumentException 
	{
		if(instance == null)
		{
			synchronized(JcrDigitalObjectManagerImpl.class)
			{
				if(instance == null)
				{
				   instance = new JcrDigitalObjectManagerImpl(repository);
				}
			}
		}
		return instance;
	}

	/**
	 * Constructor for the Data Manager. Simply loads the properties and
	 * instantiates the JCR Manager. The constructor should only fail because it
	 * cannot find the properties file or the JCRManager cannot connect to a JCR
	 * instance.
	 */
	private JcrDigitalObjectManagerImpl() {
		try {
			_log.log(Level.INFO, "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl()");
			jcrManager = new DOJCRManager(DIGITAL_OBJECT_REPOSITORY_NAME, _log);
			initRootDir();
		} catch (Exception _exp) {
			String _message = "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl() Cannot load resources";
			_log.log(Level.INFO, _message, _exp);
		}
	}

	/**
	 * Constructor for the Data Manager with repository parameter. Simply loads the properties and
	 * instantiates the JCR Manager. The constructor should only fail because it
	 * cannot find the properties file or the JCRManager cannot connect to a JCR
	 * instance.
	 * @param repository 
	 *    This is a repository for JCR
	 */
	public JcrDigitalObjectManagerImpl(Repository repository) {
		try {
			_log.log(Level.INFO, "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl(repository)");
			jcrManager = new DOJCRManager(repository, _log);
		} catch (Exception _exp) {
			String _message = "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl() Cannot load resources";
			_log.log(Level.INFO, _message, _exp);
		}
	}

	/**
	 * Returns the persisted details of the DigitalObject content identified by
	 * the passed permanent URI.
	 * 
	 * @param pdURI
	 *        The URI of the digital object content to be retrieved.
	 * @return The digital object content.
	 * @throws ItemNotFoundException
	 * @throws RepositoryException
	 * @throws URISyntaxException
	 */
	public DigitalObjectContent retrieveContent(URI pdURI)
			throws ItemNotFoundException, RepositoryException,
			URISyntaxException 
    {
		_log.log(Level.INFO, "JcrDigitalObjectManagerImpl.retrieveContent()");
		DigitalObjectContent resultContent = null;
		try {
			resultContent = Content.byValue(jcrManager.retrieveContent(pdURI));
		} catch (DigitalObjectNotFoundException e) {
			e.printStackTrace();
		}
		return resultContent;
	}

	/**
	 * Returns the persisted details of the DigitalObject content identified by
	 * the passed permanent URI.
	 * 
	 * @param pdURI
	 *        The URI of the digital object content to be retrieved.
	 * @return The digital object content stream.
	 * @throws ItemNotFoundException
	 * @throws RepositoryException
	 * @throws URISyntaxException
	 * @throws DigitalObjectNotFoundException
	 */
	public InputStream retrieveContentAsStream(URI pdURI)
			throws ItemNotFoundException, RepositoryException,
			URISyntaxException, DigitalObjectNotFoundException 
    {
		_log.log(Level.INFO, "JcrDigitalObjectManagerImpl.retrieveContentAsStream()");
		return jcrManager.retrieveContentAsStream(pdURI);		
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	public List<URI> list(URI pdURI) 
	{
		// The return array of URIs contains the contents for the passed URI
		ArrayList<URI> retVal = null;

		retVal = jcrManager.list(pdURI);
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#listDigitalObject(java.net.URI)
	 */
	public List<DigitalObject> listDigitalObject(URI pdURI) 
	{
		// The return array of digital objects contains the contents for the passed URI
		ArrayList<DigitalObject> retVal = null;

		retVal = jcrManager.listDigitalObject(pdURI);
		return retVal;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		return retrieve(pdURI, true);
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
	public int remove(URI permanentURI)
			throws DigitalObjectNotRemovedException {
		int res = DOJCRConstants.RESULT_OK;
		try {
			res = jcrManager.removeDigitalObject(permanentURI);
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't remove Digital Object: " + _exp.getMessage(), _exp);
			res = DOJCRConstants.RESULT_ERROR;
			throw new DigitalObjectNotRemovedException("Couldn't remove Digital Object", _exp);
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
			throws DigitalObjectNotRemovedException {
		int res = DOJCRConstants.RESULT_OK;
		try {
			res = jcrManager.removeAll();
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't remove all digital objects: " + _exp.getMessage(), _exp);
			res = DOJCRConstants.RESULT_ERROR;
			throw new DigitalObjectNotRemovedException("Couldn't remove all digital objects", _exp);
		}
		return res;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI, boolean includeContent)
			throws DigitalObjectNotFoundException 
	{
		DigitalObject retObj = null;
		try {
			retObj = jcrManager.retrieveDigitalObjectDefinition(pdURI, includeContent);
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't retrieve Digital Object: " + _exp.getMessage(), _exp);
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object", _exp);
		}
		return retObj;
	}

	/**
	 * This method updates digital object with parameter from newObject.
	 * @param newObject
	 *        This is an updated digital object
	 * @param includeContent
	 *        This is a flag to process an action including content (flag is true) or not
	 * @return result digital object
	 * @throws DigitalObjectNotFoundException
	 */
	public DigitalObject updateDigitalObject( DigitalObject newObject
			                                , boolean includeContent
			                                )
			throws DigitalObjectUpdateException 
	{
		DigitalObject retObj = null;
		try {
			retObj = jcrManager.updateDigitalObject(newObject, includeContent);
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't update Digital Object: " + _exp.getMessage(), _exp);
			throw new DigitalObjectUpdateException("Couldn't update Digital Object", _exp);
		}
		return retObj;
	}

	/**
	 * This method stores digital object in JCR repository using passed pdURI
	 * @param pdURI
	 *            the URI
	 * @param digitalObject
	 *            the object
	 * @throws DigitalObjectNotStoredException
	 */
	public void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException 
    {
		try {

			DigitalObject dobj = store(pdURI, digitalObject, true);
			_log.log(Level.INFO, dobj.toXml());	
			
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't store Digital Object: ",_exp);
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object", _exp);
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
	public DigitalObject store(URI uri, DigitalObject digitalObject, boolean includeContent)
			throws DigitalObjectNotStoredException 
    {
		DigitalObject res = null;
		try {
			Event eIngest = buildIngestEvent(includeContent);
			List<Event> lEvents = digitalObject.getEvents();
			//add the ingest event
			lEvents.add(eIngest);
			
			//create an updated DigitalObject
			DigitalObject digoUpdated = new DigitalObject.Builder(digitalObject.getContent())
	      	 	  .title(digitalObject.getTitle())
			      .permanentUri(digitalObject.getPermanentUri())
	              .manifestationOf(digitalObject.getManifestationOf())
	              .format(digitalObject.getFormat())
	              .metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]))
	              .events((Event[]) lEvents.toArray(new Event[0]))
			      .build();	
			
			//call store
			res = jcrManager.storeDigitalObjectDefinition(uri, digoUpdated, includeContent);
		} catch (Exception _exp) {
			_log.log(Level.INFO, "Couldn't store Digital Object: ",_exp);
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object", _exp);
		}
		return res;
	}
	
	
	/**
	 * Create an ingest event for this repository
	 * @param isIncludeContent
	 * @return
	 */
	public Event buildIngestEvent(boolean isIncludeContent){
		//create the ingest event
		List<Property> pList = new ArrayList<Property>();
		Property pIngestContent = new Property.Builder(URI.create("planets://data/repository/jcr/contentByReference"))
        	.name("content by reference")
        	.value(new Boolean(isIncludeContent).toString())
        	.description("This flag is true if content should be stored in JCR and false if not")
        	.unit("boolean")
        	.type("repository characteristic")
        	.build();
		pList.add(pIngestContent);
		//FIXME: cheating with the duration, but null not allowed
		Event eIngest = new Event(INGEST_EVENT, System.currentTimeMillis()+"", new Double(100), this.getAgent(), pList);
		return eIngest;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
	 */
	public boolean isWritable(URI pdURI) {
		return true;
	}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException 
    {
        throw new QueryValidationException("This implementation does not support queries.");
    }
    
	public URI storeAsNew(DigitalObject digitalObject) {
		URI pdUri = null;
		return pdUri;
	}

    public URI getRootURI() 
    {
    	return root;
    }
 
    /**
     * Create a digital object manager for digital objects stored in JCR.
     * root is the directory storing JCR node tree.
     */
    public void initRootDir() 
    {
       root = URI.create(DOJCRManager.PERMANENT_URI);
    }
    
    
    private Agent agent = null;
    /**
     * Returns the Agent of the repository system.
     * @return
     */
    public Agent getAgent(){
    	if(agent==null)
    		agent = new Agent("JCR Repository v1.0", "The Planets Jackrabbit Content Repository", "planets://data/repository");
    	return agent;
    }

	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		// TODO Auto-generated method stub
		return null;
	}

	public URI updateExisting(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException,
			DigitalObjectNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
    
   

	
}
