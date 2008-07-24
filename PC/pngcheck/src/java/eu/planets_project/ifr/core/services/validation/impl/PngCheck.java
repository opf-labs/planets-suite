package eu.planets_project.ifr.core.services.validation.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;

/**
 * PngCheck validation service.
 * 
 * @author Fabian Steeg
 * 
 */
@WebService(name = PngCheck.NAME, serviceName = BasicValidateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicValidateOneBinary.class)
@Remote(BasicValidateOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class PngCheck implements BasicValidateOneBinary, Serializable {
    /***/
    private static final long serialVersionUID = -596706737946485163L;
    /***/
    public static final String NAME = "PngCheck";
    /***/
    public static final QName QNAME = new QName(PlanetsServices.NS,
            BasicValidateOneBinary.NAME);
    /**
     * A list of pronom URIs describing PNG files; the URI given to this service
     * must be one of these or null.
     */
    public static final List<String> PNG_PRONOM = Arrays.asList(
            "info:pronom/fmt/11", "info:pronom/fmt/12", "info:pronom/fmt/13");
    /***/
    private static final PlanetsLogger LOG = PlanetsLogger
            .getLogger(PngCheck.class);
    /***/
    private byte[] bytes;

    /**
     * Validates that a file (represented as a byte array) is a PNG using
     * PngCheck.
     * 
     * @param binary The file to verify being a PNG using PngCheck (as a byte
     *        array)
     * @param fmt Not required in this service (as it only identifies PNG
     *        files), so can be null; if it is not null and not one of the PNG
     *        pronom URIs however, an IllegalArgumentExcpetion is thrown
     * @return Returns true if the given file is a valid PNG file, else false
     */
    @WebMethod(operationName = BasicValidateOneBinary.NAME, action = PlanetsServices.NS
            + "/" + BasicValidateOneBinary.NAME)
    @WebResult(name = BasicValidateOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicValidateOneBinary.NAME, partName = BasicValidateOneBinary.NAME
            + "Result")
    public boolean basicValidateOneBinary(
            @WebParam(name = "binary", targetNamespace = PlanetsServices.NS
                    + "/" + BasicValidateOneBinary.NAME, partName = "binary")
            final byte[] binary,
            @WebParam(name = "fmt", targetNamespace = PlanetsServices.NS + "/"
                    + BasicValidateOneBinary.NAME, partName = "fmt")
            final URI fmt) {
        /* PngCheck can only validate PNG files: */
        if (fmt != null && !PNG_PRONOM.contains(fmt.toString())) {
            throw new IllegalArgumentException(
                    "PngCheck can only validate PNG (" + PNG_PRONOM
                            + ") files, not " + fmt.toString());
        }
        /* We create a temporary file and write the bytes to that file: */
        File tempFile = ByteArrayHelper.write(binary);
        /* Then we call pngcheck with that temporary file: */
        List<String> commands = Arrays.asList("pngcheck", tempFile
                .getAbsolutePath());
        ProcessRunner pr = new ProcessRunner(commands);
        LOG.debug("Executing: " + commands);
        pr.run();
        /* Print some debugging info on the call: */
        String output = pr.getProcessOutputAsString();
        LOG.debug("PngCheck call output: " + output);
        LOG.debug("PngCheck call error: " + pr.getProcessErrorAsString());
        return output.startsWith("OK");
    }

    /**
     * Method for testing purpose: takes a file name as the only parameter,
     * converts the file into a byte array and calls the actual identification
     * method with that array.
     * 
     * @param fileName The local (where the service is running) location of the
     *        PNG file to validate
     * @return Returns true if the file with the given name is a PNG file, else
     *         false
     */
    @WebMethod
    public boolean basicValidateOneBinary(final String fileName) {
        bytes = ByteArrayHelper.read(new File(fileName));
        return basicValidateOneBinary(bytes, null);
    }
}
