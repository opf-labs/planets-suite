/*
 * DataManager.java
 *
 * Created on 02 July 2007, 08:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jws.soap.SOAPBinding;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.DataManagerRemote;
import eu.planets_project.ifr.core.storage.common.FileHandler;
import eu.planets_project.ifr.core.storage.impl.util.JCRManager;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;

/**
 * The DataManager is a stateless EJB implementation of the PLANETS
 * Interoperability Framework Data Registry. It implements the two public Data
 * Registry interfaces:<br/>
 * <ul>
 * <li>DataManagerLocal<br/>A local interface that is intended for use by
 * other EJBs/Web apps running on the same JBOSS instance.</li>
 * <li>DataManagerRemote<br/>A web service interface presenting a sub-set of
 * the Data Registry functionality to remote clients</li>
 * </ul>
 * <br/> The Data Registry allows users to<br/>
 * <ul>
 * <li>Store and retrieve binary data as byte arrays.</li>
 * <li>Perform some basic searches on the stored binary details.</li>
 * <li>Store and retrieve PLANETS workflow details.</li>
 * <li>Store XML documents and query them via XPath.</li>
 * </ul>
 * <br/> The web service interface provided by DataManageRemote only allows
 * search and retrieval of stroed entities. Because of security issues the
 * storage functionality of the Data Registry is only available using the
 * DataManagerLocal interace.<p/> IMPORTANT Due to the flux in the API/intent
 * for the Data registry during development I'd like to prune the methods if
 * possible. With this aim I've annotated a number of the API methods as
 * deprecated. If you're using / require any of these methods let me know email :
 * carl.wilson@bl.uk The methods will not be removed if they're required by
 * anyone or before Dec 2008.
 * 
 * @author CFwilson
 */
@javax.jws.WebService(name="DataManager", targetNamespace="http://planets-project.eu/ifr/core/storage/data", serviceName="DataManager")
@Stateless(mappedName="data/LocalDataManager")
@Local(DataManagerLocal.class)
@Remote(DataManagerRemote.class)
@LocalBinding(jndiBinding="planets-project.eu/DataManager/local")
@RemoteBinding(jndiBinding="planets-project.eu/DataManager/remote")
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@javax.jws.soap.SOAPBinding(style = SOAPBinding.Style.RPC)
@SecurityDomain("PlanetsRealm")
//@RunAs("admin")
public class DataManager implements DataManagerRemote, DataManagerLocal {

	// PLANETS logger
    private static Logger log = Logger.getLogger(DataManager.class.getName());
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
	public DataManager() throws SOAPException {
		try {
			log.fine("DataManager::DataManager()");
			properties = new Properties();
			log.fine("Getting properties");
	       	properties.load(this.getClass().getClassLoader().getResourceAsStream(propPath));
			log.fine("Creating JCRManager");
	       	jcrManager = new JCRManager(properties.getProperty("planets.if.dr.default.jndi"));
		} catch (IOException _exp) {
			String _message = "DataManager::DataManger() Cannot load resources"; 
			log.fine(_message+": "+ _exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (NamingException _exp) {
			String _message = "DataManager::DataManger() Cannot connect to Repository";
			log.fine(_message+": "+ _exp.getMessage());;
			throw new SOAPException(_message, _exp);
		}
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#list(java.net.URI)
     */
	@javax.jws.WebMethod()
	public URI[] list(URI pdURI) throws SOAPException {
    		log.fine("DataManager::list(URI pdURI)");
		URI[] _retVal = null;
		PDURI _pdURI = null;
		String _path = null;
		
		try {
			log.fine("Testing for null URI");
			if (pdURI == null)
			{
				log.fine("URI is empty so return root");
				_retVal = new URI[1];
				log.fine("Assigning array item");
				_retVal[0] = PDURI.formDataRegistryRootURI(properties.getProperty("planets.server.hostname"), properties.getProperty("planets.server.port"), properties.getProperty("planets.if.dr.default.name")); 
				return _retVal; 
			}
			log.fine("URI is NOT NULL");
			_pdURI = new PDURI(pdURI);
			log.fine("parsing the Data Registry path");
			_path = _pdURI.getDataRegistryPath();
			log.fine("Data Registry path is:" + _path);
		} catch (URISyntaxException _exp) {
			log.fine(_exp.getMessage());
			throw new SOAPException(_exp);
		}

		try {
			ArrayList<String> _pathList = jcrManager.list(_path);
			if (_pathList != null) {
				_retVal = new URI[_pathList.size()];
				int _arrayCount = 0;
				log.fine("Cycling through returned paths, there are " + _pathList.size() + " elements");
				for (String _string : _pathList) {
					log.fine("Getting a new URI for:" + _string);
					_pdURI.replaceDecodedPath(_string);
					_retVal[_arrayCount++] = _pdURI.getURI();
					log.fine("New URI added:" + _retVal[_arrayCount - 1]);
				}
			}
		} catch (URISyntaxException _exp) {
			log.fine(_exp.getMessage());
			throw new SOAPException(_exp);
		}

		return _retVal;
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#listDownladURI(java.net.URI)
     */
	@javax.jws.WebMethod()
	public URI listDownladURI(URI pdURI) throws SOAPException {
    	log.fine("DataManager::listDownloadURI(URI pdURI)");
    	URI _retVal = null;
    	PDURI _parsedURI = null;
    	try {
        	_parsedURI = new PDURI(pdURI);
    		// Check that the item exists, if not throw a SOAP Exception
    		if (!this.jcrManager.nodeExists(_parsedURI.getDataRegistryPath())) {
    			String _message = "DataManager.listDownloadURI() Cannot locate item " + pdURI.toASCIIString(); 
    			log.fine(_message);;
    			throw new SOAPException(_message);
    		}
    	} catch (URISyntaxException _exp) {
			String _message = "DataManager.listDownloadURI() " + pdURI.toASCIIString() + " is not a PLANETS Data Registry URI."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
    	} catch (RepositoryException _exp) {
			String _message = "DataManager.listDownloadURI() Repository call failed."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
    	}
	// Now get the JBOSS Server and port
    String _serverName = properties.getProperty("planets.server.hostname");
	String _port = properties.getProperty("planets.server.port");
	String _webdavRoot = properties.getProperty("jcr.local.webdav.root");


    	try {
        	// Return a download WEB_DAV URL
    		_retVal = new URI("http://" + _serverName + ":" + _port + _webdavRoot + _parsedURI.getDataRegistryPath());
    	} catch (URISyntaxException _exp) {
			String _message = "DataManager.listDownloadURI() Cannot get server URI."; 
			log.fine(_message+": "+_exp.getMessage());;
			throw new SOAPException(_message, _exp);
    	}
		return _retVal; 
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#retrieve(java.net.URI)
     */
	@Deprecated
	public InputStream retrieve(URI pdURI) throws PathNotFoundException, URISyntaxException {
		log.fine("DataManager::openFileStream(URI pdURI)");
		InputStream _stream = null;
		PDURI _parsedURI = new PDURI(pdURI);
		try {
			_stream = jcrManager.readContent(_parsedURI.getDataRegistryPath());
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (PathNotFoundException _exp) {
			log.fine("DataManager::openFileStream() Couldn't find path");
			throw _exp;
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		}
		return _stream;
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#store(java.net.URI, java.io.InputStream)
     */
	@Deprecated
	public void store(URI pdURI, InputStream stream) throws LoginException, RepositoryException, URISyntaxException {
		log.fine("DataManager::writeFileStream(URI pdURI, FileInputStream stream)");
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			jcrManager.addBinaryContent(_parsedURI.getDataRegistryPath(), stream);
		} catch (LoginException _exp) {
			log.fine("DataManager::writeFileStream() Couldn't log user into jcr");
			throw _exp;
		} catch (RepositoryException _exp) {
			log.fine("DataManager::writeFileStream() Repository Exception adding content");
			throw _exp;
    	} catch (URISyntaxException _exp) {
			String _message = "DataManager.listDownloadURI() " + pdURI.toASCIIString() + " is not a PLANETS Data Registyr URI."; 
			log.fine(_message+": "+_exp.getMessage());;
			throw _exp;
		}
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#createLocalSandbox()
     */
	public URI createLocalSandbox() throws URISyntaxException {
		log.fine("DataManager::createLocalSandbox()");
		return new URI("file:/" + 
					   System.getProperty("jboss.server.data.dir").replace('\\', '/') +
					   properties.getProperty("planets.sandbox.root").replace('\\', '/'));
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#read(java.net.URI)
     */
	@javax.jws.WebMethod()
	@Deprecated
	public String read(URI pdURI) throws SOAPException {
		log.fine("DataManager::read(URI pdURI)");
		try{
			PDURI _parsedURI = new PDURI(pdURI);
			FileHandler _encoder = new FileHandler(jcrManager.readContent(_parsedURI.getDataRegistryPath()));
			log.fine("DataManager::read() getting XML document");
			return _encoder.getXmlDocument();
		} catch (ParserConfigurationException _exp) {
			String _message = "DataManager.read() Encoding exception for UTF8??"; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (PathNotFoundException _exp) {
			String _message = "DataManager.read() Path not found for content"; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (TransformerException _exp) {
			String _message = "DataManager.read() TransformerException"; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (UnsupportedEncodingException _exp) {
			String _message = "DataManager.read() EncodingException"; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (Exception _exp) {
			String _message = "DataManager.store() bytstream failed MD5 check."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		}
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#store(java.net.URI, java.lang.String)
     */
	@Deprecated
	public void store(URI pdURI, String encodedFile) throws SOAPException {
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			FileHandler _handler = new FileHandler(encodedFile);
			ByteArrayInputStream _byteStream = new ByteArrayInputStream(_handler.getDecodedBytes());
			jcrManager.addBinaryContent(_parsedURI.getDataRegistryPath(), _byteStream);
		} catch (LoginException _exp) {
			String _message = "DataManager.store() Repository login error."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} catch (RepositoryException _exp) {
			String _message = "DataManager.store() Repository malfunction."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		// TODO make a proper Exception
		} catch (Exception _exp) {
			String _message = "DataManager.store() bytstream failed MD5 check."; 
			log.fine(_message+": "+_exp.getMessage());
			throw new SOAPException(_message, _exp);
		} 
	}
	
    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#retrieveBinary(java.net.URI)
     */
	@javax.jws.WebMethod()
	public byte[] retrieveBinary(URI pdURI) throws SOAPException
	{
		byte[] _binary = null;
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			InputStream _inStream = this.jcrManager.readContent(_parsedURI.getDataRegistryPath());
			ByteArrayOutputStream _outStream = new ByteArrayOutputStream(1024);
			byte[] _bytes = new byte[512];
			int _readBytes;
			while ((_readBytes = _inStream.read(_bytes)) > 0) {
				_outStream.write(_bytes, 0, _readBytes);
			}
			
			_binary = _outStream.toByteArray();
			_inStream.close();
			_outStream.close();
		}
		catch (Exception _exp) {
			throw new SOAPException(_exp);
		}
		return _binary;
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerLocal#storeBinary(java.net.URI, byte[])
     */
	public void storeBinary(URI pdURI, byte[] binary) throws LoginException, RepositoryException, URISyntaxException {
		log.fine("DataManager:storeBinary(URI pdURI, byte[] binary)");
		ByteArrayInputStream _inStream = new ByteArrayInputStream(binary);
		
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			jcrManager.addBinaryContent(_parsedURI.getDataRegistryPath(), _inStream);
		} catch (LoginException _exp) {
			log.fine("DataManager::writeFileStream() Couldn't log user into jcr");
			throw _exp;
		} catch (RepositoryException _exp) {
			log.fine("DataManager::writeFileStream() Repository Exception adding content");
			throw _exp;
    	} catch (URISyntaxException _exp) {
			String _message = "DataManager.listDownloadURI() " + pdURI.toASCIIString() + " is not a PLANETS Data Registyr URI."; 
			log.fine(_message+": "+_exp.getMessage());;
			throw _exp;
		}
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#findFilesWithNameContaining(java.net.URI, java.lang.String)
     */
	@javax.jws.WebMethod()
	public URI[] findFilesWithNameContaining(URI pdURI, String name) throws SOAPException {
		URI[] _retVal = null;
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			ArrayList<String> _pathList = this.jcrManager.findFilesWithNameContaining(_parsedURI.getDataRegistryPath(), name);
			if (_pathList != null) {
				_retVal = new URI[_pathList.size()];
				int _arrayCount = 0;
				log.fine("Cycling through returned paths, there are " + _pathList.size() + " elements");
				for (String _string : _pathList) {
					_parsedURI.replaceDecodedPath(_string);
					_retVal[_arrayCount++] = _parsedURI.getURI();
				}
			}
		} catch (Exception _exp) {
			throw new SOAPException(_exp);
		}
		return _retVal;
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DataManagerRemote#findFilesWithExtension(java.net.URI, java.lang.String)
     */
	@javax.jws.WebMethod()
	public URI[] findFilesWithExtension(URI pdURI, String ext) throws SOAPException {
		URI[] _retVal = null;
		try {
			PDURI _parsedURI = new PDURI(pdURI);
			ArrayList<String> _pathList = this.jcrManager.findFilesWithExtension(_parsedURI.getDataRegistryPath(), ext);
			if (_pathList != null) {
				_retVal = new URI[_pathList.size()];
				int _arrayCount = 0;
				log.fine("Cycling through returned paths, there are " + _pathList.size() + " elements");
				for (String _string : _pathList) {
					_parsedURI.replaceDecodedPath(_string);
					_retVal[_arrayCount++] = _parsedURI.getURI();
				}
			}
		} catch (Exception _exp) {
			throw new SOAPException(_exp);
		}
		return _retVal;
	}
}