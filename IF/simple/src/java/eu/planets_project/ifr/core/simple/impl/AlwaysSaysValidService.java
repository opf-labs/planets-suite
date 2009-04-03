/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import java.net.URI;
        


/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Local(Validate.class)
@Remote(Validate.class)
@Stateless
@WebService(name = AlwaysSaysValidService.NAME, 
        serviceName = Validate.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.validate.Validate" )
        
public class AlwaysSaysValidService implements Validate {

    /** The service name */
    public static final String NAME="AlwaysSaysValidService";
    
    private static Log log = LogFactory.getLog(AlwaysSaysValidService.class);
    
    /**
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Validate.class.getCanonicalName());
        mds.description("A simple simple example of a Validation service, but one that always says yes, unless the digital object is null.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

    public ValidateResult validate(DigitalObject dob, URI format, Parameters parameters) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.SUCCESS);
        log.info("This service always says yes, unless the digital object is null.");
        if( dob == null ) {

            return  new ValidateResult.Builder(format, sr).build();
        } else {
            ValidateResult result = new ValidateResult.Builder(format, sr)
                    .ofThisFormat(false)
                    .build();
            return result;
        }
    }

}
