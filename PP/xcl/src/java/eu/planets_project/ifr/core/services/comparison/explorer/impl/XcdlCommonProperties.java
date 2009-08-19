package eu.planets_project.ifr.core.services.comparison.explorer.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.services.comparison.explorer.config.ExplorerResultReader;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.CommonProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

/**
 * Service to retrieve common properties of different file formats, given their
 * IDs, based on the XCL Explorer tool.
 * (http://gforge.planets-project.eu/gf/project/xcltools)
 * @author Thomas Kraemer (thomas.kraemer@uni-koeln.de), Fabian Steeg
 *         (fabian.steeg@uni-koeln.de)
 */
@WebService(
        name = XcdlCommonProperties.NAME, 
        serviceName = CommonProperties.NAME, 
        targetNamespace = PlanetsServices.NS, 
        endpointInterface = "eu.planets_project.services.compare.CommonProperties")
@Stateless
public final class XcdlCommonProperties implements CommonProperties {
    static final String NAME = "XcdlCommonProperties";
    private static final Log LOG = LogFactory.getLog(XcdlCommonProperties.class);
    private static final String XCLTOOLS_HOME = System.getenv("XCLTOOLS_HOME") + File.separator;
    private static String XCLEXPLORER_HOME = (XCLTOOLS_HOME
    											+ File.separator
    											+ "XCLExplorer" 
    											+ File.separator).replace(File.separatorChar + File.separator, File.separator);
    private static final String XCLEXPLORER_TOOL = "XCLExplorer";
    private static final String FPMTOOL_OUT = "fpm.fpm";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CommonProperties#union(java.util.List)
     */
    public CompareResult union(final List<URI> formatIds) {
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        StringBuilder builder = new StringBuilder();
        for (URI uri : formatIds) {
            builder.append(registry.getValueFromUri(uri)).append(":");
        }
        String result = basicCompareFormatProperties(builder.toString());
        List<Property> resultProperties = ExplorerResultReader.properties(result);
        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, result);
        return new CompareResult(resultProperties, report);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CommonProperties#intersection(java.util.List)
     */
    public CompareResult intersection(final List<URI> formatIds) {
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        List<List<Property>> propsOfEach = new ArrayList<List<Property>>();
        StringBuilder fullResult = new StringBuilder();
        for (URI uri : formatIds) {
            String result = basicCompareFormatProperties(registry
                    .getValueFromUri(uri)
                    + ":");
            fullResult.append(result).append("\n");
            List<Property> resultProperties = ExplorerResultReader.properties(result);
            propsOfEach.add(resultProperties);
        }
        List<Property> result = intersectionOf(propsOfEach);
        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS,
                fullResult.toString());
        return new CompareResult(result, report);
    }

    private List<Property> intersectionOf(List<List<Property>> propsOfEach) {
        List<Property> result = new ArrayList<Property>(propsOfEach.get(0));
        for (List<Property> list : propsOfEach) {
            result.retainAll(list);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        return ServiceDescription.create(NAME, CommonProperties.class.getCanonicalName())
                .classname(this.getClass().getCanonicalName())
                .description("This service is a wrapper for the XcdlExplorer command line tool (formerly known as FPM-Tool)\n" +
                		"developed at the UzK.\n" +
                		"It returns a List of Properties for a given list of file formats, each specified by a PRONOM ID.\n" +
                		"It's possible to 1) receive a List containing ALL the properties of all input formats ('union')\n" +
                		"or to 2) receive a List containing only the SHARED properties of the input formats ('intersection').")
                .author("Fabian Steeg")
                .serviceProvider("The Planets Consortium").build();
    }

    /**
     * @param parameters The FPM-tool-specific parameter string
     * @return The FPM-tool-specific XML result string
     */
    private String basicCompareFormatProperties(final String parameters) {
        ProcessRunner shell = new ProcessRunner();
        List<String> command = Arrays.asList(XCLEXPLORER_HOME + XCLEXPLORER_TOOL,
                parameters);
        shell.setCommand(command);
        shell.setStartingDir(new File(XCLEXPLORER_HOME));
        LOG.info("XCLTOOLS_HOME = " + XCLTOOLS_HOME);
        LOG.info("XCLExplorer home dir: " + XCLEXPLORER_HOME);
        LOG.info("Running: " + command);
        shell.run();
        String processOutput = shell.getProcessOutputAsString();
        String processError = shell.getProcessErrorAsString();
        LOG.info("Process Output: " + processOutput);
        LOG.error("Process Error: " + processError);
        String result = FileUtils.readTxtFileIntoString(new File(XCLEXPLORER_HOME
                + FPMTOOL_OUT));
        LOG.info("Returning joint file format properties, starts with: "
                + result.substring(0, Math.min(200, result.length())) + "...");
        return result;
    }

}