package eu.planets_project.services.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.ProcessRunner;

/**
 * @author CFWilson
 *
 */
@Local(Identify.class)
@Remote(Identify.class)
@Stateless

@WebService(name = FileIdentify.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
public class FileIdentify implements Identify {
	/** The logger */
    private static Log _log = LogFactory.getLog(FileIdentify.class);

    /** Properties file location and Properties holder */
	private static final String PROPERTIES_PATH = "eu/planets_project/services/file/FileIdentify.properties";
	private Properties _properties = null;

	/** The service name */
    public static final String NAME = "FileIdentify";

    /**
     * No arg constructor, just loads the properties
     */
    public FileIdentify() {
    	this.loadProperties(FileIdentify.PROPERTIES_PATH);
    }
    
    /**
     * Second constructor for testing, allows an alternative set of properties to be read in
     * DO NOT use this in practise, the no arg constructor is fine
     * @param propPath 
     * 		An alternative properties file
     */
    public FileIdentify(String propPath) {
    	this.loadProperties(propPath);
    }
    
	/**
	 * @see eu.planets_project.services.identify.Identify#describe()
	 */
	public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Identify.class.getCanonicalName());
        mds.description("A DigitalObject Identification Service based on the cygwin File.exe program.");
        mds.author("Carl Wilson <Carl.Wilson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
	}

	/**
	 * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject)
	 */
	public IdentifyResult identify(DigitalObject digitalObject) {
		// First check that the properties aren't null
		// If they are then we can't get going so return a bad initialisation message
		if (this._properties == null) {
			return this.returnWithErrorMessage("Error Reading properties file", 1);
		}

        // Can only cope if the object is 'simple', i.e. we need a byte sequence
        if(digitalObject.getContent() == null) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.", 1);
        }

        // Get binary data from digital object
        byte[] binary = digitalObject.getContent().getValue();
       
        // write binary array to temporary file
        File tmpInFile = ByteArrayHelper.write(binary);

        // Right we'll need to create a suitable command line
        String[] commands = new String[] {this._properties.getProperty("cygwin.file.location"),
        								  "-i",
        								  "-b",
        								  tmpInFile.getAbsolutePath()};

        // Now a process runner to try our magic command
        ProcessRunner runner = new ProcessRunner();
        runner.setCommand(Arrays.asList(commands));
        runner.run();
        
        // Check the process runner for problems
        int retCode = runner.getReturnCode();
        if (retCode != 0) {
        	// Something's gone wrong so return an error response
        	return this.returnWithErrorMessage(runner.getProcessErrorAsString(), retCode);
        }
        // Get the MIME type from the process output 
        String mime = runner.getProcessOutputAsString().trim();
        // Let's check that it found the file, this should never happen but who knows
        if (mime.indexOf(this._properties.getProperty("cygwin.message.nofile")) != -1) {
        	FileIdentify._log.debug("File failed to find an error");
        	return this.returnWithErrorMessage(mime, 1);
        }
        
        // Create the service report
        ServiceReport rep = new ServiceReport();
        rep.setErrorState(0);
        List<URI> types = new ArrayList<URI>();
        URI mimeURI = Format.mimeToURI(mime);
        types.add(mimeURI);
        return new IdentifyResult(types, IdentifyResult.Method.MAGIC, rep);
	}

	//======================================================================
	// PRIVATE METHODS
	//======================================================================

	/**
	 * Load the properties from the supplied path
	 * @param propPath
	 * 		The path to the properties file
	 */
	private void loadProperties(String propPath) {
		try {
			// Create a new properties object and load the properties
			_properties = new Properties();
	       	_properties.load(this.getClass().getClassLoader().getResourceAsStream(propPath));
		} catch (IOException exp) {
			// Hopefully this won't happen, it's unrecoverable if it does
			// We'll log it and then set _propertes to null
			FileIdentify._log.debug("IOException processing properties file", exp);
			_properties = null;
		}
	}

	/**
	 * Method to create the IdentifyResult with an error message, used when things go wrong
	 * @param message
	 * 		The error message for the ServiceReport
	 * @return
	 * 		The IdentifyResult, correctly populated
	 */
    private IdentifyResult returnWithErrorMessage(String message, int errorState) {
    	// Create and empty service report and a null type list
        ServiceReport rep = new ServiceReport();
        List<URI> type = null;
        // Log the message
        FileIdentify._log.error(message);
        // Set the error state and message in the service report
        rep.setErrorState(errorState);
        rep.setError(message);
        // Return a new IdentifyResult created from the ServiceReport and the null types
        return new IdentifyResult(type, rep);
    }
}
