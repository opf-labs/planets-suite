package eu.planets_project.services.file;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
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
    private static Logger log = Logger.getLogger(FileIdentify.class.getName());

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

    /**
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject, java.util.List)
     */
    public IdentifyResult identify(DigitalObject digitalObject, List<Parameter> parameters) {

        // Can only cope if the object is 'simple', i.e. we need a byte sequence
        if(digitalObject.getContent() == null) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.", 1);
        }

        // Now check that windows, we won't work on none windows at the moment
        if (!FileServiceUtilities.isWindows()) {
            return this.returnWithErrorMessage("OS detected not windows based, this service only runs on windows.", 1);
        }

        // Finally check that the cygwin file command cannot be found
        if (!FileServiceUtilities.isCygwinFileDetected()) {
            return this.returnWithErrorMessage("Cygwin file.exe not found at location given in cygwin.file.location property.", 1);
        }

        // write digital object to temporary file
        File tmpInFile = DigitalObjectUtils.toFile(digitalObject);

        // Right we'll need to create a suitable command line
		String[] commands = new String[] {FileServiceUtilities.getFileLocation(),
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
        if (mime.indexOf(FileServiceUtilities.getConfiguration().getString("cygwin.message.nofile")) != -1) {
        	FileIdentify.log.fine("File failed to find an error");
        	return this.returnWithErrorMessage(mime, 1);
        }
        
        // Create the service report
        ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        List<URI> types = new ArrayList<URI>();
        URI mimeURI = FormatRegistryFactory.getFormatRegistry().createMimeUri(mime.split(";")[0]);
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
    private IdentifyResult returnWithErrorMessage(String message, @SuppressWarnings("unused") int errorState) {
    	// Create and empty service report and a null type list
        List<URI> type = null;
        // Log the message
        FileIdentify.log.severe(message);
        // Set the error state and message in the service report
        ServiceReport rep = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, message);
        // Return a new IdentifyResult created from the ServiceReport and the null types
        return new IdentifyResult(type, null, rep);
    }
}
