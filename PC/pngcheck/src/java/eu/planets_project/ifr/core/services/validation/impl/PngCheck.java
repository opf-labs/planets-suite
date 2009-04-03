package eu.planets_project.ifr.core.services.validation.impl;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


/**
 * PngCheck validation service.
 * @author Fabian Steeg
 */
@WebService(name = PngCheck.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.validate.Validate")
@Local(Validate.class)
@Remote(Validate.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class PngCheck implements Validate, Serializable {
    /***/
    private static final long serialVersionUID = -596706737946485163L;
    /***/
    static final String NAME = "PngCheck";
    /**
     * A list of pronom URIs describing PNG files; the URI given to this service
     * must be one of these or null.
     */
    private static final List<URI> PNG_PRONOM = Arrays.asList(URI
            .create("info:pronom/fmt/11"), URI.create("info:pronom/fmt/12"),
            URI.create("info:pronom/fmt/13"));
    /***/
    private static final PlanetsLogger LOG = PlanetsLogger
            .getLogger(PngCheck.class);
    /***/
    //private byte[] bytes;

    /**
     * Validates that a file is a PNG using PngCheck.
     * @param tempFile The file to verify being a PNG using PngCheck
     * @param fmt Not required in this service (as it only identifies PNG
     *        files), so can be null; if it is not null and not one of the PNG
     *        pronom URIs however, an IllegalArgumentExcpetion is thrown
     * @return Returns true if the given file is a valid PNG file, else false
     */
    private boolean basicValidateOneBinary(final File tempFile, final URI fmt) {
        /* PngCheck can only validate PNG files: */
        if (fmt != null && !PNG_PRONOM.contains(fmt)) {
            throw new IllegalArgumentException(
                    "PngCheck can only validate PNG (" + PNG_PRONOM
                            + ") files, not " + fmt.toString());
        }
        /* We call pngcheck with that temporary file: */
        List<String> commands = Arrays.asList("pngcheck", tempFile
                .getAbsolutePath());
        ProcessRunner pr = new ProcessRunner(commands);
        LOG.debug("Executing: " + commands);
        pr.run();
        /* Print some debugging info on the call: */
        String output = pr.getProcessOutputAsString();
        LOG.debug("PngCheck call output: " + output);
        LOG.debug("PngCheck call error: " + pr.getProcessErrorAsString());
        return output.contains("OK:");
    }

    /**
     * Method for testing purpose: takes a file name as the only parameter,
     * converts the file into a byte array and calls the actual identification
     * method with that array.
     * @param fileName The local (where the service is running) location of the
     *        PNG file to validate
     * @return Returns true if the file with the given name is a PNG file, else
     *         false
     */
    @WebMethod
    public boolean basicValidateOneBinary(final String fileName) {
        return basicValidateOneBinary(new File(fileName), null);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.validate.Validate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME,
                Validate.class.getCanonicalName());
        sd.classname(this.getClass().getCanonicalName());
        sd.description("Validation service based on PngCheck.");
        sd.author("Fabian Steeg");
        sd.inputFormats(PNG_PRONOM.toArray(new URI[] {}));
        sd.tool(URI.create("http://www.libpng.org/pub/png/apps/pngcheck.html"));
        sd.serviceProvider("The Planets Consortium");
        return sd.build();
    }

    /**
     * {@inheritDoc}
     */
    public ValidateResult validate(final DigitalObject digitalObject,
            final URI format, Parameters parameters) {
        File file = FileUtils.writeInputStreamToTmpFile(digitalObject
                .getContent().read(), "pngcheck-temp", "bin");
        boolean valid = basicValidateOneBinary(file, format);
        ValidateResult result = new ValidateResult.Builder(format, new ServiceReport())
                .ofThisFormat(valid)
                .build();
        return result;
    }
}
