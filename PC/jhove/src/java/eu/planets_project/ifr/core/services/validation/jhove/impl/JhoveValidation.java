package eu.planets_project.ifr.core.services.validation.jhove.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * JHOVE validation service.
 * @author Fabian Steeg
 */
@WebService(name = JhoveValidation.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.validate.Validate")
@Local(Validate.class)
@Remote(Validate.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class JhoveValidation implements Validate, Serializable {
    /***/
    private static final long serialVersionUID = 2127494848765937613L;
    /***/
    static final String NAME = "JhoveValidation";

    /**
     * {@inheritDoc}
     * @see Validate#validate(eu.planets_project.services.datatypes.DigitalObject,
     *      java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    public ValidateResult validate(final DigitalObject digitalObject,
            final URI format, final List<Parameter> parameters) {
        ServiceReport report;
        boolean valid = false;
        if (!JhoveIdentification.supported(format)) {
            report = new ServiceReport(Type.ERROR, Status.SUCCESS,
                    "Unsupported format: " + format);
        } else {
            valid = basicValidateOneBinary(digitalObject, format);
            report = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        }
        ValidateResult result = new ValidateResult.Builder(format, report)
                .ofThisFormat(valid).build();
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                "JHOVE Validation Service", Validate.class.getCanonicalName());
        sd.classname(this.getClass().getCanonicalName());
        sd.description("Validation service using JHOVE (1.4).");
        sd.author("Fabian Steeg");
        sd.tool(Tool.create(null, "JHOVE", "1.4", null,
                "http://hul.harvard.edu/jhove/"));
        sd.logo(URI.create("http://hul.harvard.edu/jhove/jhove-logo.gif"));
        sd.furtherInfo(URI.create("http://hul.harvard.edu/jhove/"));
        sd.inputFormats(JhoveIdentification.inputFormats());
        sd.serviceProvider("The Planets Consortium");
        return sd.build();
    }

    /**
     * @param digitalObject The digital object file to validate
     * @param fmt The pronom URI the binary should be validated against
     * @return Returns true if the given pronom URI describes the given binary
     *         file, else false
     * @see eu.planets_project.services.validate.BasicValidateOneBinary#basicValidateOneBinary(byte[],
     *      java.net.URI)
     */
    private boolean basicValidateOneBinary(final DigitalObject digitalObject,
            final URI fmt) {
        IdentifyResult identify = identify(digitalObject);
        /* And check it it is what we expected: */
        for (URI uri : identify.getTypes()) {
            if (uri != null && uri.equals(fmt)) {
                /* One of the identified types is the one we expected: */
                return true;
            }
        }
        return false;
    }

    private IdentifyResult identify(final DigitalObject digitalObject) {
        /* Identify the binary: */
        JhoveIdentification identification = new JhoveIdentification();
        IdentifyResult identify = identification.identify(digitalObject, null);
        return identify;
    }

}
