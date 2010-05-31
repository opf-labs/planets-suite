package eu.planets_project.ifr.core.services.characterisation.fits.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import org.apache.commons.lang.NotImplementedException;

import com.sun.xml.ws.developer.StreamingAttachment;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Chracterisation service using the FITS tool.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Stateless
@MTOM
@StreamingAttachment( parseEagerly = true, memoryThreshold = ServiceUtils.JAXWS_SIZE_THRESHOLD )
@WebService(
        name = FitsCharacterise.NAME,
        serviceName = Characterise.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.characterise.Characterise" )
public final class FitsCharacterise implements Characterise {

    static final String NAME = "FitsCharacterise";

    static final String FITS = System.getenv("FITS_HOME");
    private Fits fits = null;

    /**
     * Instantiates the service. Checks if the environment is set up correctly.
     */
    public FitsCharacterise() {
        if (FITS == null) {
            throw new IllegalStateException(
                    "Please set up a FITS_HOME environment variable pointing to the FITS directory");
        }
        try {
            fits = new Fits(FITS);
        } catch (FitsConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription.Builder(this.getClass().getSimpleName(),
                Characterise.class.getName()).classname(this.getClass().getName())
                .serviceProvider("Open Planets Foundation").author("Fabian Steeg").build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject,
     *      java.util.List)
     */
    public CharacteriseResult characterise(final DigitalObject digitalObject,
            final List<Parameter> parameters) {
        List<Property> props = new ArrayList<Property>();
        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        try {
            FitsOutput output = fits.examine(DigitalObjectUtils.toFile(digitalObject));
            props.addAll(properties("file", output.getFileInfoElements()));
            props.addAll(properties("tech", output.getTechMetadataElements()));
            // TODO add full XML as String
        } catch (FitsException e) {
            report = ServiceUtils.createErrorReport("Exception during FITS characterisation: "
                    + e.getMessage());
            e.printStackTrace();
        }
        return new CharacteriseResult(props, report);
    }

    private List<Property> properties(final String type, final List<FitsMetadataElement> elements) {
        if (elements == null) {
            return Collections.emptyList();
        }
        List<Property> result = new ArrayList<Property>();
        for (FitsMetadataElement element : elements) {
            result.add(new Property.Builder(URI.create("planets:/pc/fits/" + element.getName()))
                    .name(element.getName())
                    .value(element.getValue())
                    .type(type)
                    .description(
                            String.format("By %s %s", element.getReportingToolName(),
                                    element.getReportingToolVersion())).build());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
     */
    public List<Property> listProperties(final URI formatURI) {
        throw new NotImplementedException();
    }

}
