package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingAttachment;

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
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * XCL extractor service based on the Characterise interface.
 * @author Peter Melms, Fabian Steeg
 * @see XcdlMigrate
 */
@WebService(name = XcdlCharacterise.NAME, serviceName = Characterise.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.characterise.Characterise")
@Stateless
@StreamingAttachment(parseEagerly = true)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public final class XcdlCharacterise implements Characterise, Serializable {

    private static final long serialVersionUID = -8537596616209516979L;

    /**
     * the service name.
     */
    public static final String NAME = "XcdlCharacteriseExtractor";
    /**
     * output dir.
     */
    public static final String OUT_DIR = NAME.toUpperCase() + "_OUT" + File.separator;

    public static final File XCDL_CHARACTERISE_TMP = FileUtils.createFolderInWorkFolder(FileUtils
            .getPlanetsTmpStoreFolder(), NAME + "_TMP");

    /**
     * the logger.
     */
    public static final Log LOG = LogFactory.getLog(XcdlCharacterise.class);
    /**
     * a max file size.
     */
    public static final int MAX_FILE_SIZE = 10240;

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject,
     *      eu.planets_project.services.datatypes.Parameter)
     */
    public CharacteriseResult characterise(final DigitalObject digitalObject, final List<Parameter> parameters) {
        String optionalFormatXCEL = null;
        CoreExtractor coreExtractor = new CoreExtractor(XcdlCharacterise.NAME, LOG);
        File xcelFile = new File(XCDL_CHARACTERISE_TMP, FileUtils.randomizeFileName("xcel_input.xml"));
        File result = null;

        if (parameters != null && parameters.size() != 0) {
            for (Parameter currentParameter : parameters) {
                String currentName = currentParameter.getName();
                if (currentName.equalsIgnoreCase("optionalXCELString")) {
                    optionalFormatXCEL = currentParameter.getValue();
                    FileUtils.writeStringToFile(optionalFormatXCEL, xcelFile);
                    break;
                }
            }
        }

        if (optionalFormatXCEL != null) {
            result = coreExtractor.extractXCDL(digitalObject, null, xcelFile, parameters);
        } else {
            result = coreExtractor.extractXCDL(digitalObject, null, null, parameters);
        }

        if (result != null && result.exists()) {
            try {
                return new XcdlParser(new FileReader(result)).getCharacteriseResult();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return returnWithError();
            }
        } else {
            return returnWithError();
        }
    }

    /**
     * @return
     */
    private CharacteriseResult returnWithError() {
        return this.returnWithErrorMessage("ERROR: No XCDL created!", null);
    }

    /**
     * @param message an optional message on what happened to the service
     * @param e the Exception e which causes the problem
     * @return CharacteriseResult containing a Error-Report
     */
    private CharacteriseResult returnWithErrorMessage(final String message, final Exception e) {
        if (e == null) {
            return new CharacteriseResult(new ArrayList<Property>(), ServiceUtils.createErrorReport(message));
        } else {
            return new CharacteriseResult(new ArrayList<Property>(), ServiceUtils
                    .createExceptionErrorReport(message, e));
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(XcdlCharacterise.NAME, Characterise.class
                .getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd
                .description("Another Wrapper for the Extractor tool developed at the UzK. This Wrapper uses the Extractor\n"
                        + "to read all relevant properties from an input file. The Extractor output (.xcdl) is parsed and \n"
                        + "returned as a a List of Properties to enable the comparison of results delivered by different Characterisation tools.\n"
                        + "IMPORTANT NOTE: To receive the .xcdl file, please use the XcdlMigrate service!");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");

        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter normDataFlag = new Parameter.Builder("disableNormDataInXCDL", "-n").description(
                "Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'").build();
        parameterList.add(normDataFlag);

        Parameter enableRawData = new Parameter.Builder("enableRawDataInXCDL", "-r").description(
                "Enables the output of RAW Data in XCDL file. Allowed value: '-r'").build();
        parameterList.add(enableRawData);

        sd.parameters(parameterList);
        sd.logo(URI.create("http://www.planets-project.eu/graphics/Planets_Logo.png"));
        sd.inputFormats(CoreExtractor.getSupportedInputFormats().toArray(new URI[] {}));
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
         * Starting here, this is a temporary workaround to match the output of the FpmCommonProperties to the
         * FileFormatProperty class (in the future, this method will return elements of the same type as
         * FpmCommonProperties returns):
         */
        List<Property> resultProps = new ArrayList<Property>();
        for (Property prop : list) {
            Property fileFormatProperty = new Property(XcdlProperties.makePropertyURI(prop.getName()), prop.getName(),
                    null);
            resultProps.add(fileFormatProperty);
        }
        return resultProps;
    }
}
