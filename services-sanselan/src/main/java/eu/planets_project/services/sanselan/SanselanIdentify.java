/**
 * 
 */
package eu.planets_project.services.sanselan;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Local(Identify.class)
@Remote(Identify.class)
@Stateless

@WebService(name = SanselanIdentify.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
        
public class SanselanIdentify implements Identify {
    
    /** The service name */
    public static final String NAME = "SanselanIdentify";

    private static Logger log = Logger.getLogger(SanselanIdentify.class.getName());

    /**
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Identify.class.getCanonicalName());
        mds.description("A DigitalObject Identification Service based on the Sanselan library.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.tool(Tool.create(null, "Apache Sanselan", "0.94", null, "http://incubator.apache.org/sanselan/"));
        mds.furtherInfo(URI.create("http://incubator.apache.org/sanselan/"));
        mds.classname(this.getClass().getCanonicalName());
        mds.version("0.1");
        
        ImageFormat[] fmts = ImageFormat.getAllFormats();
        List<URI> ifs = new ArrayList<URI>();
        for( ImageFormat i : fmts ) {
            /**
             *  See {@link http://incubator.apache.org/sanselan/site/formatsupport.html} for details of supported formats.
             */
            if( ! i.extension.equalsIgnoreCase("unknown") ) {
                ifs.add(FormatRegistryFactory.getFormatRegistry()
                        .createExtensionUri(i.extension));
            }
        }
        mds.inputFormats(ifs.toArray(new URI[]{}));

        return mds.build();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject, java.util.List)
     */
    public IdentifyResult identify( DigitalObject dob, List<Parameter> parameters ) {
        
        // Initialise the result:
        ImageInfo imageInfo = null;
        
        // Can only cope if the object is 'simple':
        if( dob.getContent() == null ) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.");
        }
        try {
            imageInfo = Sanselan.getImageInfo(dob.getContent().getInputStream(), 
                    ""+dob.getPermanentUri());
        } catch (ImageReadException e) {
            return this
                    .returnWithErrorMessage("Could not read the image: " + e);
        } catch (IOException e) {
            return this
                    .returnWithErrorMessage("IOException reading the image: "
                            + e);
        }
        if( imageInfo == null || imageInfo.getFormat() == null )
            return this.returnWithErrorMessage("Could not understand the image.");
        
        String type = imageInfo.getFormat().extension;
        ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        
        List<URI> types = new ArrayList<URI>();
        URI typeURI = FormatRegistryFactory.getFormatRegistry().createExtensionUri(type);
        types.add(typeURI);
        return new IdentifyResult(types, IdentifyResult.Method.PARTIAL_PARSE, rep);
        
    }
    
    /**
     * 
     * @param message
     * @return
     */
    private IdentifyResult returnWithErrorMessage(String message) {
        ServiceReport rep = ServiceUtils.createErrorReport(message);
        List<URI> type = null;
        log.severe(message);
        return new IdentifyResult(type, null, rep);
    }

}
