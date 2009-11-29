package eu.planets_project.ifr.core.services.validation.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * PngCheck validation service.
 * @author Fabian Steeg
 */
@WebService(name = PngCheck.NAME, serviceName = Validate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.validate.Validate")
@StreamingAttachment(parseEagerly = true)
@Stateless
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
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
    private static final Logger log = Logger.getLogger(PngCheck.class.getName());

    /***/
    // private byte[] bytes;
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
        log.fine("Executing: " + commands);
        pr.run();
        /* Print some debugging info on the call: */
        String output = pr.getProcessOutputAsString();
        log.fine("PngCheck call output: " + output);
        log.fine("PngCheck call error: " + pr.getProcessErrorAsString());
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
        sd.tool(Tool.create(null, "PngCheck", null, null,
                "http://www.libpng.org/pub/png/apps/pngcheck.html"));
        sd.serviceProvider("The Planets Consortium");
        return sd.build();
    }

    /**
     * {@inheritDoc}
     */
    public ValidateResult validate(final DigitalObject digitalObject,
            final URI format, final List<Parameter> parameters) {
        File file = FileUtils.writeInputStreamToTmpFile(digitalObject
                .getContent().read(), "pngcheck-temp", "bin");
        boolean valid = basicValidateOneBinary(file, format);
        ValidateResult result = new ValidateResult.Builder(format,
                new ServiceReport(Type.INFO, Status.SUCCESS, "OK"))
                .ofThisFormat(valid).validInRegardToThisFormat(valid).build();
        return result;
    }
}
