package eu.planets_project.services.pc.odftoolkit;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
     
import odfvalidator.ODFValidatorWrapper;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(name = ODFValidatorService.NAME, 
        serviceName = Validate.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.validate.Validate" )
public class ODFValidatorService implements Validate {

    /**
     * the service name
     */
    public static final String NAME="ODFValidatorService";
    
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ODFValidatorService.class);
    
    /**
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Validate.class.getCanonicalName());
        mds.description("A validator for ODF file, based on the ODF Toolkit project ODF Validator.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        try {
            mds.furtherInfo(new URI("http://odftoolkit.org/projects/odftoolkit/pages/ODFValidator"));
        } catch (URISyntaxException e) { }
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

    /**
     * Returns null every time, no matter what is input.
     * @see eu.planets_project.services.validate.Validate#validate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI)
     */
    public ValidateResult validate(DigitalObject dob, URI format, List<Parameter> parameters) {
        
       return ODFValidatorWrapper.validateODF(dob,format);
       
    }
       
 }  