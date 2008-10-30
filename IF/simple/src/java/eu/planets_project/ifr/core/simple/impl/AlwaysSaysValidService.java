/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import java.net.URI;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
        


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

    public static final String NAME="AlwaysSaysValidService";
    
    private static Log log = LogFactory.getLog(AlwaysSaysValidService.class);
    
    /* (non-Javadoc)
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription mds = new ServiceDescription("A DigitalObject Validation service, that always says yes.", "");
        mds.setDescription("A simple simple example of a Validation service, but one that always says yes, unless the digital object is null.");
        mds.setAuthor("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.setClassname(this.getClass().getCanonicalName());
        mds.setType(Validate.class.getCanonicalName());
        return mds;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.validate.Validate#identify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI)
     */
    public ValidateResult validate(DigitalObject dob, URI format) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.SUCCESS);
        log.info("This service always says yes, unless the digital object is null.");
        if( dob == null ) {
            return  new ValidateResult( ValidateResult.Validity.INVALID, sr );
        } else {
            return  new ValidateResult( ValidateResult.Validity.VALID, sr );
        }
    }

}
