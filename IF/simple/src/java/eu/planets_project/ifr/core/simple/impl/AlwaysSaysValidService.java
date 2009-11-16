/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import java.net.URI;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@Stateless
@WebService(name = AlwaysSaysValidService.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.validate.Validate")
@StreamingAttachment(parseEagerly = true)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
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
                    .ofThisFormat(true).validInRegardToThisFormat(true).build();
            return result;
        }
    }

}
