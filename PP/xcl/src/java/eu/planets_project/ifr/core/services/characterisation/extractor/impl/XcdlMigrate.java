package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.*;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;

import javax.ejb.Stateless;
import javax.jws.WebService;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * XCL extractor service based on the Migrate interface.
 * @author melmsp
 * @see XcdlCharacterise
 */
@WebService(name = XcdlMigrate.NAME, serviceName = Migrate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.migrate.Migrate")
@Stateless
public final class XcdlMigrate implements Migrate {

    private static final long serialVersionUID = -8231114442082962651L;

    /**
     * the service name.
     */
    public static final String NAME = "XcdlMigrateExtractor";
    /**
     * output dir.
     */
    public static final String OUT_DIR = NAME.toUpperCase() + "_OUT"
            + File.separator;
    /**
     * the logger.
     */
    public static final PlanetsLogger LOG = PlanetsLogger
            .getLogger(XcdlMigrate.class);
    /**
     * a max file size.
     */
    public static final int MAX_FILE_SIZE = 75420028;

    private MigrationPath[] createMigrationPathwayMatrix(
            List<URI> inputFormats, List<URI> outputFormats) {
        List<MigrationPath> paths = new ArrayList<MigrationPath>();

        for (Iterator<URI> iterator = inputFormats.iterator(); iterator
                .hasNext();) {
            URI input = iterator.next();

            for (Iterator<URI> iterator2 = outputFormats.iterator(); iterator2
                    .hasNext();) {
                URI output = iterator2.next();
                MigrationPath path = new MigrationPath(input, output, null);
                // Debug...
                // System.out.println(path.getInputFormat() + " --> " +
                // path.getOutputFormat());
                paths.add(path);
            }
        }
        return paths.toArray(new MigrationPath[] {});
    }

    /**
     * @param message an optional message on what happened to the service
     * @param e the Exception e which causes the problem
     * @return CharacteriseResult containing a Error-Report
     */
    private MigrateResult returnWithErrorMessage(final String message,
            final Exception e) {
        if (e == null) {
            return new MigrateResult(null, ServiceUtils
                    .createErrorReport(message));
        } else {
            return new MigrateResult(null, ServiceUtils
                    .createExceptionErrorReport(message, e));
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                XcdlMigrate.NAME, Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd
                .description("A wrapper for the Extractor tool developed by UzK (University at Cologne)."
                        + "The tool returns a XCDL file (as byte[]) in which includes all data of the input file in a machine readable way (xml)."
                        + "This XCDL file could be automatically compared by the Comparator tool (UzK) to evaluate how successful a migration has been."
                        + "(Example: migrate a TIFF to PNG. Create a XCDL for the TIFF input file and create a XCDL for the PNG output file"
                        + "and compare them using the Comparator. How much information has been lost?)"
                        + "IMPORTANT NOTE: To receive the Extractor results as a list of properties, in order to enable the comparison"
                        + "of different characterisation tools, please use the XcdlCharacterise Service!");

        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");

        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter normDataFlag = new Parameter(
                "disableNormDataInXCDL",
                "-n",
                null,
                "Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'");
        parameterList.add(normDataFlag);

        Parameter enableRawData = new Parameter("enableRawDataInXCDL", "-r",
                null,
                "Enables the output of RAW Data in XCDL file. Allowed value: '-r'");
        parameterList.add(enableRawData);

        Parameter optionalXCELString = new Parameter(
                "optionalXCELString",
                "the XCEL file as a String",
                null,
                "Could contain an optional XCEL String which is passed to the Extractor tool.\n\r"
                        + "If no XCEL String is passed, the Extractor tool will try to  find the corresponding XCEL himself.");
        parameterList.add(optionalXCELString);

        sd.parameters(parameterList);

        // Migration Paths: List all combinations:
        sd.paths(createMigrationPathwayMatrix(CoreExtractor
                .getSupportedInputFormats(), CoreExtractor
                .getSupportedOutputFormats()));
        sd.serviceProvider("The Planets Consortium");

        return sd.build();
    }

    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {
        System.out.println("Working on file: " + digitalObject.getTitle());
        DigitalObject resultDigOb = null;
        ServiceReport sReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                "OK");
        MigrateResult migrateResult = null;
        String optionalFormatXCEL = null;

        CoreExtractor coreExtractor = new CoreExtractor(XcdlMigrate.NAME, LOG);

        byte[] inputData = FileUtils.writeInputStreamToBinary(digitalObject
                .getContent().read());

        byte[] result = null;

        if (parameters != null) {
            if (parameters.size() != 0) {
                for (Parameter currentParameter : parameters) {
                    String currentName = currentParameter.getName();

                    if (currentName.equalsIgnoreCase("optionalXCELString")) {
                        optionalFormatXCEL = currentParameter.getValue();
                    }
                }

            }
        }

        if (optionalFormatXCEL != null) {
            result = coreExtractor.extractXCDL(inputData, optionalFormatXCEL
                    .getBytes(), parameters);
        } else {
            result = coreExtractor.extractXCDL(inputData, null, parameters);
        }

        int sizeInKB = 0;

        if (result != null) {
            sizeInKB = result.length / 1024;

            // output Files smaller than 10Mb
            if (sizeInKB < MAX_FILE_SIZE) {
                try {
                    resultDigOb = new DigitalObject.Builder(ImmutableContent
                            .byValue(result)).permanentUrl(
                            new URL(PlanetsServices.NS
                                    + "/pserv-pc-xcdlExtractor")).build();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                migrateResult = new MigrateResult(resultDigOb, sReport);
            } else {
                File tmpResult = FileUtils.getTempFile(result, "tmpResult",
                        "tmp");
                try {
                    resultDigOb = new DigitalObject.Builder(ImmutableContent
                            .byReference(tmpResult.toURI().toURL())).build();
                    migrateResult = new MigrateResult(resultDigOb, sReport);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            this.returnWithErrorMessage("ERROR: No XCDL created!", null);
        }

        return migrateResult;
    }

}
