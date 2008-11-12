package eu.planets_project.services.pc.odftoolkit;


import java.net.URI;
import java.net.URISyntaxException;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
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

    public static final String NAME="ODFValidatorService";
    
    private static Log log = LogFactory.getLog(ODFValidatorService.class);
    
    /* (non-Javadoc)
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription mds = new ServiceDescription(NAME, Validate.class.getCanonicalName());
        mds.setDescription("A validator for ODF file, based on the ODF Toolkit project ODF Validator.");
        mds.setAuthor("Andrew Jackson <Andrew.Jackson@bl.uk>");
        try {
            mds.setFurtherInfo(new URI("http://odftoolkit.org/projects/odftoolkit/pages/ODFValidator"));
        } catch (URISyntaxException e) { }
        mds.setClassname(this.getClass().getCanonicalName());
        return mds;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.validate.Validate#identify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI)
     */
    public ValidateResult validate(DigitalObject dob, URI format) {
        
       return ODFValidatorWrapper.validateODF(dob);
       
    }
       
 }  