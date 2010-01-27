package eu.planets_project.ifr.core.services.fixity.javadigest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;

/**
 * JavaDigest Fixity service.
 * First pass simply creates an MD5 checksum, will implement other supported algorithms
 * via a parameter
 * 
 * @author  Carl Wilson
 */
@Stateless
@WebService(
		name = JavaDigest.NAME, 
		serviceName = Fixity.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.fixity.Fixity")
@StreamingAttachment(parseEagerly = true)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public final class JavaDigest implements Fixity, Serializable {
    /**
	 * 
	 */
	private static Logger log = Logger.getLogger(JavaDigest.class.getName());

	private static final String MD5 = "MD5";

	/** The name of the service / class */
    static final String NAME = "JavaDigest";

    public FixityResult calculateChecksum(DigitalObject digitalObject,
			List<Parameter> parameters) {
    	FixityResult retResult = null;
    	ServiceReport retReport = null;
    	try {
        	// OK let's try to get the digest algorithm 
			MessageDigest messDigest = MessageDigest.getInstance(JavaDigest.MD5);
			
			// Now calc the result, we need the bytes from the object
			// so let's get the stream
			InputStream inStream = digitalObject.getContent().getInputStream();
			
			// OK we'll do this in 1KB chunks
			byte[] dataBytes = new byte[1024];
			
			// First read
			int numRead = inStream.read(dataBytes);

			// Catch the special case of no data in the file
			if (numRead < 1) {
				retReport = new ServiceReport(ServiceReport.Type.ERROR,
						  ServiceReport.Status.SUCCESS,
						  "No data associated with Digital Object");
				// And wrap it in the result
				retResult = new FixityResult(JavaDigest.MD5, null, retReport);
				return retResult;
			}
			
			// Now loop through the rest of the file
			while (numRead > 0) {
				messDigest.update(dataBytes, 0, numRead);
				numRead = inStream.read(dataBytes);
			}

			// OK, success so create the result
			retReport = new ServiceReport(ServiceReport.Type.INFO,
					  ServiceReport.Status.SUCCESS,
					  "Digest calculated successfully");
			// And wrap it in the result
			retResult = new FixityResult(JavaDigest.MD5, messDigest.digest(), null, retReport);

    	} catch (NoSuchAlgorithmException e) {
			// This shouldn't happen at the moment, it supports MD5
			// Create the Error ServiceReport
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
										  ServiceReport.Status.TOOL_ERROR,
										  e.getMessage());
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		} catch (IOException e) {
			// OK, a problem reading the file
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
										  ServiceReport.Status.TOOL_ERROR,
										  e.getMessage());
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		}
		// Return the result
		return retResult;
	}

	public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                "JavaDigest Fixity Service", 
                Fixity.class.getCanonicalName());
        sd.classname(this.getClass().getCanonicalName());
        
        sd.description("Fixity service based on Java " + MessageDigest.class.getName());
        sd.author("Carl Wilson");
        sd.tool(Tool.create(null, "JavaDigest", "1.0", MessageDigest.class.getName(),
                "http://java.sun.com/j2se/1.5.0/docs/api/java/security/MessageDigest.html"));
        sd.furtherInfo(URI.create("http://java.sun.com/j2se/1.5.0/docs/api/java/security/MessageDigest.html"));
        // Taking this out as logo is no longer hosted there, and this is bad practice anyway - should be hosted locally.
        //sd.logo( URI.create("http://droid.sourceforge.net/wiki/skins/snaphouston/droidlogo.gif"));
        sd.serviceProvider("The Planets Consortium.");
        return sd.build();
	}
}
