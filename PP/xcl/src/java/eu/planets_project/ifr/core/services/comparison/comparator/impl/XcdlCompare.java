package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.services.AbstractSampleXclUsage;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.CoreExtractor;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigCreator;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParser;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * XCL Comparator service. Compares image, text or XCDL files wrapped as digital objects.
 * @see {@link AbstractSampleXclUsage}
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService( name = XcdlCompare.NAME, serviceName = Compare.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.compare.Compare" )
@Stateless
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public final class XcdlCompare implements Compare {
    private static Logger log = Logger.getLogger(XcdlCompare.class.getName());
    
    /***/
    static final String NAME = "XcdlCompare";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#compare(eu.planets_project.services.datatypes.DigitalObject[],
     *      eu.planets_project.services.datatypes.DigitalObject)
     */
    public CompareResult compare(final DigitalObject first, final DigitalObject second,
            final List<Parameter> config) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Digital objects to compare must not be null");
        }
        String pcr = null;
        if( config != null) {
        	pcr = new ComparatorConfigCreator(config).getComparatorConfigXml();
        }
        String result = ComparatorWrapper.compare(xcdlFor(first), Arrays.asList(xcdlFor(second)), pcr);
        log.info("Got Result: "+result);
        List<List<Property>> props = propertiesFrom(result);
        return compareResult(props);
    }

    private String xcdlFor(DigitalObject object) {
        // Try using the extractor to create an XCDL for the input file:
        File xcdl = new CoreExtractor(getClass().getName())
                .extractXCDL(object, null, null, null);
        // Return either the extracted XCDL (if it exists) or assume the file is an XCDL:
        String xcdlString = null;
        try {
        	xcdlString = xcdl != null && xcdl.exists() ? read(new DigitalObject.Builder(Content.byReference(xcdl)).build()) : read(object);
        } catch ( IllegalArgumentException e ) {
        	log.severe("ERROR when reading XCDL file. "+e);
        	xcdlString = "";
        }
        return xcdlString;
    }

    /**
     * @param props The 1-level nested result properties
     * @return A compare result object with either top-level properties only or without top-level
     *         properties but embedded results
     */
    static CompareResult compareResult(final List<List<Property>> props) {
        if (props.size() == 1) {
            return new CompareResult(props.get(0), new ServiceReport(Type.INFO, Status.SUCCESS,
                    "Top-level comparison result without embedded results"));
        } else {
            List<CompareResult> embedded = new ArrayList<CompareResult>();
            for (List<Property> list : props) {
                embedded.add(new CompareResult(list, new ServiceReport(Type.INFO, Status.SUCCESS,
                        "Embedded comparison result")));
            }
            return new CompareResult(new ArrayList<Property>(), new ServiceReport(Type.INFO,
                    Status.SUCCESS, "Top-level comparison result with embedded results"), embedded);
        }
    }

    /**
     * @param result The comparator result
     * @return The properties found in the result XML
     */
    private List<List<Property>> propertiesFrom(final String result) {
        File file = FileUtils.writeByteArrayToTempFile(result.getBytes());
        try {
        	return new ResultPropertiesReader(file).getProperties();
        } catch( IllegalArgumentException e ) {
        	log.severe("Could not parse properties from string "+result+"\n "+e);
        	return new ArrayList<List<Property>>();
        }
    }

    /**
     * @param digitalObject The digital objects
     * @return A string representing the content of the digital objects
     */
    private String read(final DigitalObject digitalObject) {
        if (digitalObject == null) {
            throw new IllegalArgumentException("Digital object is null!");
        }
        InputStream stream = digitalObject.getContent().getInputStream();
        String xcdl = new String(FileUtils.writeInputStreamToBinary(stream));
        if (!xcdl.toLowerCase().contains("<xcdl")) {
            throw new IllegalArgumentException("Digital object given is not XCDL: " + xcdl.substring(0,100));
        }
        return stream == null ? null : xcdl;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#describe()
     */
    public ServiceDescription describe() {
        /* TODO: Set input formats to XCDL PUID when there is one. */
        return new ServiceDescription.Builder(NAME, Compare.class.getCanonicalName())
                .classname(this.getClass().getCanonicalName())
                .author("Fabian Steeg")
                .description(
                        "XCDL Comparison Service, which compares two Xcdl files generated by the Extractor tool."
                                + "This services is a wrapper for the Comparator command line tool developed at the UzK."
                                + "The Comparator allows to check how much information has been lost during a migration process."
                                + "To use the Comparator with a list of properties instead of .xcdl files, please use the XcdlCompareProperties service!")
                .logo(URI.create("http://www.planets-project.eu/graphics/Planets_Logo.png"))
                .serviceProvider("The Planets Consortium").inputFormats(
                        ComparatorWrapper.getSupportedInputFormats().toArray(new URI[] {})).build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#convert(eu.planets_project.services.datatypes.DigitalObject)
     */
    public List<Parameter> convert(final DigitalObject configFile) {
        InputStream inputStream = configFile.getContent().getInputStream();
        return new ComparatorConfigParser(inputStream).getProperties();
    }
}
