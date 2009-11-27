/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.simple.impl.util.FileTypeResolver;
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

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Stateless
@WebService(name = SimpleIdentifyService.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
@StreamingAttachment(parseEagerly = true)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class SimpleIdentifyService implements Identify {

    /** The name of the service. */
    public static final String NAME="SimpleIdentifyService";
    
    private static Logger log = Logger.getLogger(SimpleIdentifyService.class.getName());

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Identify.class.getCanonicalName());
        mds.description("A simple identification service that can determine the mime-type of simple (single-file) digital objects. Only looks at the file extension, so can only work on by-reference objects.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject, java.util.List)
     */
    public IdentifyResult identify(DigitalObject dob, List<Parameter> parameters ) {
        // Initialise the result:
        
        // Use this resolver:
        FileTypeResolver ftr;
        try {
            ftr = FileTypeResolver.instantiate();
        } catch (Exception e) {
            e.printStackTrace();
            return this.returnWithErrorMessage("Could not instanciate the file type resolver.");
        }
        // Can only cope if the object is 'simple':
        if (dob.getContent() == null) {
            return this
                    .returnWithErrorMessage("The Content of the DigitalObject should not be NULL.");
        }
        // URL, can deal with this:
        String type = null;
        try {
            type = ftr.getMIMEType(dob.getPermanentUri().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "Nothing checked");

        List<URI> types = new ArrayList<URI>();
        types.add(FormatRegistryFactory.getFormatRegistry().createMimeUri(type));

        return new IdentifyResult(types, IdentifyResult.Method.EXTENSION, rep);
    }
    
    private IdentifyResult returnWithErrorMessage(String message) {
        ServiceReport rep = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, message);
        List<URI> type = null;
        log.error(message);
        return new IdentifyResult(type, null, rep);
    }

}
