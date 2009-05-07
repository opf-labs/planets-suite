package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlParser;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.ifr.core.services.comparison.explorer.impl.XcdlCommonProperties;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * XCL extractor service based on the Characterise interface.
 * @author Peter Melms, Fabian Steeg
 * @see XcdlMigrate
 */
@WebService(name = XcdlCharacterise.NAME, serviceName = Characterise.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.characterise.Characterise")
@Stateless
public final class XcdlCharacterise implements Characterise, Serializable {

    private static final long serialVersionUID = -8537596616209516979L;

    /**
     * the service name.
     */
    public static final String NAME = "XcdlCharacteriseExtractor";
    /**
     * output dir.
     */
    public static final String OUT_DIR = NAME.toUpperCase() + "_OUT"
            + File.separator;
    /**
     * the logger.
     */
    public static final PlanetsLogger LOG = PlanetsLogger
            .getLogger(XcdlCharacterise.class);
    /**
     * a max file size.
     */
    public static final int MAX_FILE_SIZE = 10240;

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject,
     *      eu.planets_project.services.datatypes.Parameter)
     */
    public CharacteriseResult characterise(final DigitalObject digitalObject,
            final List<Parameter> parameters) {

        ServiceReport sReport = new ServiceReport(Type.INFO, Status.SUCCESS,
                "OK");
        CharacteriseResult characteriseResult = null;
        String optionalFormatXCEL = null;

        CoreExtractor coreExtractor = new CoreExtractor(XcdlCharacterise.NAME,
                LOG);

        byte[] inputData = FileUtils.writeInputStreamToBinary(digitalObject
                .getContent().read());

        byte[] result = null;

        if (parameters != null) {
            if (parameters.size() > 0) {
                for (Parameter parameter : parameters) {
                    String name = parameter.getName();
                    if (name.equalsIgnoreCase("optionalXCELString")) {
                        optionalFormatXCEL = parameter.getValue();
                        break;
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

        if (result != null) {
            File outputFile = FileUtils.writeByteArrayToTempFile(result);
            List<Property> properties = new XcdlParser(outputFile)
                    .getProperties();
            characteriseResult = new CharacteriseResult(properties, sReport);
        } else {
            this.returnWithErrorMessage("ERROR: No XCDL created!", null);
        }

        return characteriseResult;
    }

    /**
     * @param message an optional message on what happened to the service
     * @param e the Exception e which causes the problem
     * @return CharacteriseResult containing a Error-Report
     */
    private CharacteriseResult returnWithErrorMessage(final String message,
            final Exception e) {
        if (e == null) {
            return new CharacteriseResult(new ArrayList<Property>(),
                    ServiceUtils.createErrorReport(message));
        } else {
            return new CharacteriseResult(new ArrayList<Property>(),
                    ServiceUtils.createExceptionErrorReport(message, e));
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                XcdlCharacterise.NAME, Characterise.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd
                .description("Another Wrapper for the Extractor tool developed at the UzK. This Wrapper uses the Extractor\n"
                        + "to read all relevant properties from an input file. The Extractor output (.xcdl) is parsed and \n"
                        + "returned as a a List of Properties to enable the comparison of results delivered by different Characterisation tools.\n"
                        + "IMPORTANT NOTE: To receive the .xcdl file, please use the XcdlMigrate service!");
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

        sd.parameters(parameterList);

        sd.inputFormats(CoreExtractor.getSupportedInputFormats().toArray(
                new URI[] {}));
        sd.serviceProvider("The Planets Consortium");

        return sd.build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
     */
    public List<Property> listProperties(final URI formatURI) {
        XcdlCommonProperties commonProperties = new XcdlCommonProperties();
        CompareResult result = commonProperties.union(Arrays.asList(formatURI));
        List<Property> list = result.getProperties();
        /*
         * Starting here, this is a temporary workaround to match the output of
         * the FpmCommonProperties to the FileFormatProperty class (in the
         * future, this method will return elements of the same type as
         * FpmCommonProperties returns):
         */
        List<Property> resultProps = new ArrayList<Property>();
        for (Property prop : list) {
            Property fileFormatProperty = new Property(XcdlProperties
                    .makePropertyURI(prop.getType(), prop.getName()), prop
                    .getName(), null);
            resultProps.add(fileFormatProperty);
        }
        return resultProps;
    }
}
