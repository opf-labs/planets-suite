package eu.planets_project.ifr.core.services.characterisation.metadata.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import nz.govt.natlib.meta.FileHarvestSource;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.ConfigurationException;
import nz.govt.natlib.meta.ui.PropsManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;

/**
 * Service wrapping the Metadata Extraction Tool from the National Archive of
 * New Zealand (http://meta-extractor.sourceforge.net/).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Stateless
@StreamingAttachment(parseEagerly = true)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(name = MetadataExtractor.NAME, serviceName = Characterise.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.characterise.Characterise")
public final class MetadataExtractor implements Characterise {
    /***/
    static final String NAME = "MetadataExtractor";
    public static final String NZMEPropertyRoot = "planets:pc/nzme/";

    /**
     * 
     */
    public static URI makePropertyURI( String name) {
        try {
            URI propUri = new URI( NZMEPropertyRoot + name);
            return propUri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The optional format XCEL and parameters are ignored in this
     * implementation (you may pass null). {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject,
     *      java.lang.String, eu.planets_project.services.datatypes.Parameter)
     */
    public CharacteriseResult characterise(final DigitalObject digitalObject,
            final List<Parameter> parameters) {
        InputStream stream = digitalObject.getContent().getInputStream();
        byte[] binary = FileUtils.writeInputStreamToBinary(stream);
        String resultString = basicCharacteriseOneBinary(binary);
        List<Property> props = readProperties(resultString);
        return new CharacteriseResult(props, new ServiceReport(Type.INFO,
                Status.SUCCESS, "OK"));
    }

    /**
     * Property listing is not yet implemented for this class, the resulting
     * list will always be empty. {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
     */
    public List<Property> listProperties(final URI formatURI) {
        ArrayList<Property> result = new ArrayList<Property>();
        /* Get the extensions for the supplied Pronom ID: */
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        Set<String> extensions = registry.getExtensions(formatURI);
        /* Find the corresponding metadata file type: */
        MetadataType[] types = MetadataType.values();
        for (MetadataType metadataType : types) {
            String[] split = metadataType.sample.split("\\.");
            String suffix = split[split.length - 1];
            if (extensions.contains(suffix.toLowerCase())) {
                /* For that, get the extractable properties: */
                List<String> listProperties = listProperties(metadataType);
                for (String string : listProperties) {
                    result.add(new Property(makePropertyURI(string), string,
                            null));
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        /*
         * Gather all supported input formats using the tech reg and the types
         * enum:
         */
        FormatRegistry formatRegistry = FormatRegistryFactory
                .getFormatRegistry();
        List<URI> inputFormats = new ArrayList<URI>();
        MetadataType[] metadataTypes = MetadataType.values();
        for (MetadataType metadataType : metadataTypes) {
            /*
             * We use the sample file extension instead of the mime type, as the
             * latter is file/unknown for many types (it's what the tool returns
             * as a result, used for testing)
             */
            String[] split = metadataType.sample.split("\\.");
            String extension = split[split.length - 1];
            inputFormats.addAll(formatRegistry.getUrisForExtension(extension));
        }
        return new ServiceDescription.Builder(
                "New Zealand Metadata Extractor Service", Characterise.class
                        .getName())
                .author("Fabian Steeg")
                .classname(this.getClass().getName())
                .description(
                        "Metadata extraction service based on the Metadata Extraction Tool of the National Library of New Zealand (patched 3.4GA).")
                .serviceProvider("The Planets Consortium")
                .tool( Tool.create(null, "New Zealand Metadata Extractor", "3.4GA (patched)", null, "http://meta-extractor.sourceforge.net/"))
                .furtherInfo(
                        URI
                                .create("http://sourceforge.net/tracker/index.php?func=detail&aid=2027729&group_id=189407&atid=929202"))
                .inputFormats(inputFormats.toArray(new URI[] {})).build();
    }

    /*------------------------------------------------------------------------*/
    /*-------------------------- package private API -------------------------*/
    /*------------------------------------------------------------------------*/

    /**
     * @param metadataXml The XML string resulting from harvesting, the output
     *        of the NZ metadata extractor
     * @return A list of properties
     */
    static List<Property> readProperties(final String metadataXml) {
        List<Property> properties = new ArrayList<Property>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(new StringReader(metadataXml));
            Element meta = doc.getRootElement().getChild("METADATA");
            for (Object propElem : meta.getChildren()) {
                Element e = (Element) propElem;
                Property p = new Property(makePropertyURI(e.getName()), e.getName(), e.getText());
                properties.add(p);
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * @param type The file type
     * @return A list of attributes extractable for the given type, as defined
     *         in the adapters DTD file
     */
    static List<String> listProperties(final MetadataType type) {
        File adapter = null;
        /*
         * We get the adapter jar from the current thread in order to work in
         * all environments (e.g., when running locally as a test or when
         * running on a server:)
         */
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(type.adapter);
        adapter = FileUtils.writeInputStreamToTmpFile(stream, "adapter", "tmp");
        List<String> props = new ArrayList<String>();
        try {
            /*
             * The NZ metadata extractor has an adapter jar for each supported
             * file format. Inside that, there is a dtd in which the extractable
             * properties for that format are listed. Thus, we iterate over the
             * contents of the jar file, get the dtd, and read the properties
             * defined inside of it:
             */
            JarFile jar = new JarFile(adapter);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith("dtd")) {
                    InputStream inputStream = jar.getInputStream(entry);
                    Scanner s = new Scanner(inputStream);
                    while (s.hasNextLine()) {
                        String nextLine = s.nextLine();
                        /**
                         * A line we care about looks like this:
                         * <p/>
                         * <!ELEMENT COMPRESSION (#PCDATA)>
                         */
                        if (nextLine.startsWith("<!ELEMENT")) {
                            String prop = nextLine.split(" ")[1];
                            props.add(prop);
                        }
                    }
                    /* It's just one DTD file: */
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /*------------------------------------------------------------------------*/
    /*------------------------------- private API ----------------------------*/
    /*------------------------------------------------------------------------*/

    /**
     * @param binary The binary file to characterize
     * @return Returns the proprietary XML result string returned by the
     *         extractor tool
     * @see eu.planets_project.services.characterise.BasicCharacteriseOneBinary#basicCharacteriseOneBinary(byte[])
     */
    private String basicCharacteriseOneBinary(final byte[] binary) {
        if (binary.length == 0) {
            throw new IllegalArgumentException("Binary is empty!");
        }
        File file = FileUtils.writeByteArrayToTempFile(binary);
        /* Create a HarvestSource of the object we want to harvest */
        FileHarvestSource source = new FileHarvestSource(file);
        try {
            /* Get the native Configuration: */
            Configuration c = Config.getInstance().getConfiguration(
                    "Extract in Native form");
            String tempFolder = file.getParent();
            c.setOutputDirectory(tempFolder);
            /* Harvest the file: */
            c.getHarvester().harvest(c, source, new PropsManager());
            /* The resulting file is the original file plus ".xml": */
            File result = new File(c.getOutputDirectory() + File.separator
                    + file.getName() + ".xml");
            result.deleteOnExit();
            return read(result.getAbsolutePath());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param location The location of the text file to read
     * @return Return the content of the file at the specified location
     */
    private static String read(final String location) {
        StringBuilder builder = new StringBuilder();
        Scanner s;
        try {
            s = new Scanner(new File(location));
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append("\n");
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
