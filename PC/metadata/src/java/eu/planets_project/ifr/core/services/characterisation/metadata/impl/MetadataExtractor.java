package eu.planets_project.ifr.core.services.characterisation.metadata.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import nz.govt.natlib.meta.FileHarvestSource;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.ConfigurationException;
import nz.govt.natlib.meta.ui.PropsManager;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;

/**
 * Service wrapping the Metadata Extraction Tool from the National Archive of
 * New Zealand (http://meta-extractor.sourceforge.net/).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Stateless
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(name = MetadataExtractor.NAME, serviceName = Characterise.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.characterise.Characterise")
public final class MetadataExtractor implements Characterise {
    /***/
    static final String NAME = "MetadataExtractor";

    /**
     * The optional format XCEL and parameters are ignored in this
     * implementation (you may pass null). {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject,
     *      java.lang.String, eu.planets_project.services.datatypes.Parameters)
     */
    public CharacteriseResult characterise(final DigitalObject digitalObject,
            final String optionalFormatXCEL, final Parameters parameters) {
        InputStream stream = digitalObject.getContent().read();
        byte[] binary = FileUtils.writeInputStreamToBinary(stream);
        String resultString = basicCharacteriseOneBinary(binary);
        DigitalObject result = new DigitalObject.Builder(Content
                .byValue(resultString.getBytes())).build();
        return new CharacteriseResult(result, new ServiceReport());
    }

    /**
     * Property listing is not yet implemented for this class, the resulting
     * list will always be empty. {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
     */
    public List<FileFormatProperty> listProperties(final URI formatURI) {
        // TODO implement property listing
        return new ArrayList<FileFormatProperty>();
    }

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
        File file = ByteArrayHelper.write(binary);
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

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription.Builder(
                "New Zealand Metadata Extractor Service", Characterise.class
                        .getName())
                .author("Fabian Steeg")
                .classname(this.getClass().getName())
                .description(
                        "Metadata extraction service based on the Metadata Extraction Tool of the National Library of New Zealand (patched 3.4GA).")
                .serviceProvider("The Planets Consortium")
                .tool(URI.create("http://meta-extractor.sourceforge.net/"))
                .furtherInfo(
                        URI
                                .create("http://sourceforge.net/tracker/index.php?func=detail&aid=2027729&group_id=189407&atid=929202"))
                .build();
    }
}
