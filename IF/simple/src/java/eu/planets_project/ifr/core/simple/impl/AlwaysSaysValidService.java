/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@Local(Validate.class)
@Remote(Validate.class)
@Stateless
@WebService(name = AlwaysSaysValidService.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.validate.Validate")
public class AlwaysSaysValidService implements Validate {

    /** The service name. */
    public static final String NAME = "AlwaysSaysValidService";

    private static Log log = LogFactory.getLog(AlwaysSaysValidService.class);

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME,
                Validate.class.getCanonicalName());
        mds
                .description("A simple simple example of a Validation service, but one that always says yes, unless the digital object is null.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.validate.Validate#validate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.util.List)
     */
    public ValidateResult validate(DigitalObject dob, URI format,
            List<Parameter> parameters) {
        String message = "This service always says yes, unless the digital object is null.";
        ServiceReport sr = new ServiceReport(Type.INFO, Status.SUCCESS, message);
        log.info(message);
        if (dob == null) {
            return new ValidateResult.Builder(format, sr).build();
        } else {
            ValidateResult result = new ValidateResult.Builder(format, sr)
                    .ofThisFormat(false).build();
            return result;
        }
    }

}
