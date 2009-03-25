package eu.planets_project.services.file;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.file.util.FileServiceSetup;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

/**
 * Class that implements an eu.planets_project.services.Identify interface.  It wraps the Cygwin
 * file utility, identifying passed digital objects and returning a Planets mimetype Format URI.  
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
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

	/** The service name */
    public static final String NAME = "FileIdentify";

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

    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject, java.util.List)
     */
    public IdentifyResult identify(DigitalObject digitalObject, List<Parameter> parameters) {

        // Can only cope if the object is 'simple', i.e. we need a byte sequence
        if(digitalObject.getContent() == null) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.", 1);
        }

        // Now check that windows, we won't work on none windows at the moment
        if (!FileServiceSetup.isWindows()) {
            return this.returnWithErrorMessage("OS detected not windows based, this service only runs on windows.", 1);
        }

        // Finally check that the cygwin file command cannot be found
        if (!FileServiceSetup.isCygwinFileDetected()) {
            return this.returnWithErrorMessage("Cygwin file.exe not found at location given in cygwin.file.location property.", 1);
        }

        // Get binary data from digital object
        byte[] binary = FileUtils.writeInputStreamToBinary(digitalObject.getContent().read());
       
        // write binary array to temporary file
        File tmpInFile = FileUtils.writeByteArrayToTempFile(binary);

        // Right we'll need to create a suitable command line
        String[] commands = new String[] {FileServiceSetup.getProperties().getProperty("cygwin.file.location"),
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
        if (mime.indexOf(FileServiceSetup.getProperties().getProperty("cygwin.message.nofile")) != -1) {
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
