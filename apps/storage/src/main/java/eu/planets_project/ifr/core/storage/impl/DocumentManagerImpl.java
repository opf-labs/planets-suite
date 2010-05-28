package eu.planets_project.ifr.core.storage.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jboss.annotation.ejb.LocalBinding;
import org.w3c.dom.Document;

import eu.planets_project.ifr.core.storage.api.DocumentManager;
import eu.planets_project.ifr.core.storage.impl.util.JCRManager;

/**
 * Reference Implementation of the DocumentManger Interface built on top of the Apache Jackrabbit JCR.
 * 
 * @author CFwilson
 *
 */
@Stateless(mappedName="data/DocumentManager")
@Local(DocumentManager.class)
@LocalBinding(jndiBinding="planets-project.eu/DocumentManager")
public class DocumentManagerImpl implements DocumentManager {
	// PLANETS logger
	private static Logger log = Logger.getLogger(DocumentManagerImpl.class.getName());
	// Properties file location and holder for the DocumentManager
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
	public DocumentManagerImpl() throws SOAPException {
		try {
			log.fine("DocumentManagerImpl::DocumentManagerImpl()");
			properties = new Properties();
			log.fine("Getting properties");
	       	properties.load(this.getClass().getClassLoader().getResourceAsStream(propPath));
			log.fine("Creating JCRManager");
	       	jcrManager = new JCRManager(properties.getProperty("planets.if.dr.default.jndi"));
		} catch (IOException _exp) {
			String _message = "DocumentManagerImpl::DocumentManagerImpl() Cannot load resources"; 
			log.fine(_message+": "+_exp.getMessage());;
			throw new SOAPException(_message, _exp);
		} catch (NamingException _exp) {
			String _message = "DocumentManagerImpl::DocumentManagerImpl() Cannot connect to Repository";
			log.fine(_message+": "+_exp.getMessage());;
			throw new SOAPException(_message, _exp);
		}
	}

    /**
     * @see eu.planets_project.ifr.core.storage.api.DocumentManager#storeDocument(java.lang.String, org.w3c.dom.Document)
     */
	public URI storeDocument(String name, Document doc) throws IOException, LoginException, RepositoryException, TransformerConfigurationException, TransformerException {
		log.fine("DataManager.storeDocument(String, Document)");
		URI _uri = null;
		String _path =  "/documents/".concat(name);
		File _tempFile = null;
		
		// Open the temp dire and create it if it doesn't exist
		log.fine("getting temp directory");
		String _dirName = this.getTempDir();
		log.fine("opening temp directory");
		File _tempDir = new File(_dirName);
		// make a temp file name
		String[] _nameParts = name.split("/");
		// Now a temp file
		log.fine("creating temp file");
		_tempFile = File.createTempFile(_nameParts[_nameParts.length - 1], "tmp", _tempDir);
		// _tempFile.deleteOnExit();
		// Prepare the source for writing
		log.fine("new source document");
		Source _source = new DOMSource(doc);
		// And a result for the file
		log.fine("doing the resutl transoformer thing");
		Result _result = new StreamResult(_tempFile);
		Transformer _transformer = TransformerFactory.newInstance().newTransformer();
		_transformer.transform(_source, _result);
		// Now get the input stream for import to JCR
		log.fine("sorting the input stream");
		InputStream _inStream = new FileInputStream(_tempFile);
		log.fine("calling improt document view");
		this.jcrManager.importDocumentView(_inStream, _path);

		return _uri;
	}
	
    /**
     * @see eu.planets_project.ifr.core.storage.api.DocumentManager#getDocument(java.lang.String)
     */
	public Document getDocument(String name) {
		Document _doc = null;
		String _path = "/documents/".concat(name);
		File _tempFile = null;
		
		try {
			// Open the temp dire and create it if it doesn't exist
			File _tempDir = new File(this.getTempDir());
			// Now a temp file
			_tempFile = File.createTempFile(name, "tmp", _tempDir);
			// _tempFile.deleteOnExit();
			// Prepare the source for writing
			OutputStream _outStream = new FileOutputStream(_tempFile);
			// Export the document view from the JCR
			this.jcrManager.exportDocumentView(_outStream, _path);
			_outStream.close();
			// Set file as transformation source
			Source _source = new StreamSource(_tempFile);
			// And a result for the the Document
			_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Result _result = new DOMResult(_doc);
			Transformer _transformer = TransformerFactory.newInstance().newTransformer();
			_transformer.transform(_source, _result);
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		} catch (LoginException _exp) {
			throw new RuntimeException(_exp);
		} catch (ParserConfigurationException _exp) {
			throw new RuntimeException(_exp);
		} catch (RepositoryException _exp) {
			throw new RuntimeException(_exp);
		} catch (TransformerConfigurationException _exp) {
			throw new RuntimeException(_exp);
		} catch (TransformerException _exp) {
			throw new RuntimeException(_exp);
		} finally {
			if ((_tempFile != null)&&(_tempFile.isFile())) {
				// _tempFile.delete();
			}
		}
		return _doc;
	}

	private String getTempDir() {
		log.fine("the JBOSS dir IS");
		log.fine(System.getProperty("jboss.server.data.dir"));
		log.fine("the data dir IS");
		log.fine(properties.getProperty("planets.data.temp.root"));
		return System.getProperty("jboss.server.data.dir").replace('\\', '/') +
		   properties.getProperty("planets.data.temp.root").replace('\\', '/');
	}
}
