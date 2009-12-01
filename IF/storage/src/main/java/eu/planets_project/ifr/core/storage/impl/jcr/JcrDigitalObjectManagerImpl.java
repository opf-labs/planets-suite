/**
 * This class manages digital objects for java content repository (jackrabbit)
 */
package eu.planets_project.ifr.core.storage.impl.jcr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;

/**
 * Implementation of the DigitalObjectManager interface for JCR.
 * 
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 * @author <a href="mailto:christian.sadilek@ait.ac.at">Christian Sadilek</a>
 * 
 */
public class JcrDigitalObjectManagerImpl implements DigitalObjectManager {
	/** The logger instance */
	private static Logger _log = Logger.getLogger(JcrDigitalObjectManagerImpl.class.getName());

	/** The name of repository for storing digital objects. */
	public static String DIGITAL_OBJECT_REPOSITORY_NAME = "java:jcr/local";

	/** The name of this data registry instance */
	protected String _name = null;

	/** This is the root directory of this particular Data Registry */
	protected File _root = null;

	/** JCRManager manages Jackrabbit functionality */
	private DOJCRManager jcrManager = null;

	private static JcrDigitalObjectManagerImpl instance = null;

	/**
	 * This is an initalization method for this singleton class
	 * @return
	 * 		A new JcrDigitalObjectManagerImpl instance
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance() throws IllegalArgumentException {
		if(instance == null)
		{
			instance = new JcrDigitalObjectManagerImpl();
		}
		return instance;
	}

	/**
	 * This is an initalization method for this singleton class with repository object
	 * @param repository object
	 * @return
	 * 		A new JcrDigitalObjectManagerImpl instance
	 * @throws IllegalArgumentException 
	 */
	public static DigitalObjectManager getInstance(Repository repository) throws IllegalArgumentException {
		if(instance == null)
		{
			instance = new JcrDigitalObjectManagerImpl(repository);
		}
		return instance;
	}

	/**
	 * Constructor for the Data Manager. Simply loads the properties and
	 * instantiates the JCR Manager. The constructor should only fail because it
	 * cannot find the properties file or the JCRManager cannot connect to a JCR
	 * instance.
	 */
	public JcrDigitalObjectManagerImpl() {
		try {
			_log.info("JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl()");
			jcrManager = new DOJCRManager(DIGITAL_OBJECT_REPOSITORY_NAME,
					_log);
		} catch (Exception _exp) {
			String _message = "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl() Cannot load resources";
			_log.log(Level.SEVERE, _message, _exp);
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
			_log.info("JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl(repository)");
			jcrManager = new DOJCRManager(repository, _log);
		} catch (Exception _exp) {
			String _message = "JcrDigitalObjectManagerImpl::JcrDigitalObjectManagerImpl() Cannot load resources";
			_log.severe(_message);
			_log.severe(_exp.getMessage());
		}
	}

	/**
	 * Returns the persisted details of the DigitalObject content identified by
	 * the passed permanent URI.
	 * 
	 * @param pdURI
	 *        The URI of the digital object content to be retrieved.
	 * @return The digital object content stream.
	 */
	public DigitalObjectContent retrieveContent(URI pdURI)
			throws ItemNotFoundException, RepositoryException,
			URISyntaxException {
		_log.info("JcrDigitalObjectManagerImpl.retrieveContentByUri()");
		return Content.byValue(jcrManager.retrieveContent(pdURI));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	public List<URI> list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		ArrayList<URI> retVal = null;

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
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI, boolean includeContent)
			throws DigitalObjectNotFoundException {
		DigitalObject retObj = null;
		try {
			retObj = jcrManager.retrieveDigitalObjectDefinition(pdURI, includeContent);
		} catch (Exception _exp) {
			_log.info("Couldn't retrieve Digital Object: " + _exp.getMessage());
			throw new DigitalObjectNotFoundException("Couldn't retrieve Digital Object", _exp);
		}
		return retObj;
	}

	/**
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
			dobj.toString();
		} catch (Exception _exp) {
			_log.info("Couldn't store Digital Object: " + _exp.getMessage());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object", _exp);
		}
	}

	/**
	 * This method stores digital object in JCR. If it is digital object by value
	 * a content should be stored in JCR independently from fetchContent flag.
	 * @param pdURI
	 *            the URI
	 * @param digitalObject
	 *            the object
	 * @param fetchContent
	 *        This flag is true if content should be stored in JCR and false if not
	 * @throws DigitalObjectNotStoredException
	 */
	public DigitalObject store(URI uri, DigitalObject digitalObject, boolean fetchContent)
			throws DigitalObjectNotStoredException 
    {
		DigitalObject res = null;
		try {
			res = jcrManager.storeDigitalObjectDefinition(uri, digitalObject, fetchContent);
		} catch (Exception _exp) {
			_log.info("Couldn't store Digital Object: " + _exp.getMessage());
			throw new DigitalObjectNotStoredException("Couldn't store Digital Object", _exp);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
	 */
	public boolean isWritable(URI pdURI) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI,
	 *      eu.planets_project.ifr.core.storage.api.query.Query)
	 */
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public URI storeAsNew(DigitalObject digitalObject) {
		// TODO Auto-generated method stub
		URI pdUri = null;
		return pdUri;
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
