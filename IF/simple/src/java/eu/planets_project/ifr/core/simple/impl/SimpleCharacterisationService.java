package eu.planets_project.ifr.core.simple.impl;

import java.io.IOException;
import java.net.URI;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.DetermineProperties;
import eu.planets_project.services.characterise.DeterminePropertiesResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * A simple characterisation service.
 * 
 * This just measures the size of the byte array associated with this Digital Object, in bytes.
 *
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
@Stateless
@Remote(DetermineProperties.class)

@WebService(name = SimpleCharacterisationService.NAME, 
        serviceName = DetermineProperties.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.characterise.DetermineProperties" )
public class SimpleCharacterisationService implements DetermineProperties
{
    private final Log log = LogFactory.getLog(getClass().getName());
    
    /** A unique name for this service. */
    static final String NAME = "SimpleCharacterisationService";
    
    /** The Planets Property ID for the size of the object. */
    public static String MIME_PROP_URI = "planets:pc/basic/bytestream/size";

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription sd = new ServiceDescription( NAME, DetermineProperties.class.getCanonicalName() );
        sd.setDescription("A simple example characterization service, which just measures the size of single-binary digital objects.");
        return sd;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#getMeasurableProperties(java.net.URI)
     */
    public Properties getMeasurableProperties(URI format) {
        Properties props = new Properties();
        props.add(MIME_PROP_URI, null);
        return props;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#measure(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.services.datatypes.Properties, eu.planets_project.services.datatypes.Parameters)
     */
    public DeterminePropertiesResult measure(DigitalObject digitalObject,
            Properties properties, Parameters parameters) {
        log.info("Start...");
        // Set up property list:
        Properties measured = new Properties();
        ServiceReport sr = new ServiceReport();
        // Loop through properties:
        for( Property prop : properties.getProperties() ) {
            log.info("Parsing property = "+prop.getName());
            // Attempt to measure:
            if( prop.getName().equals(MIME_PROP_URI)) {
                if( digitalObject.getContent() != null ) {
                    if( digitalObject.getContent().isByValue() ) {
                        measured.add( MIME_PROP_URI, ""+digitalObject.getContent().getValue().length);
                        log.info("Added for val.");
                    } else {
                        try {
                            measured.add( MIME_PROP_URI, 
                                    ""+digitalObject.getContent().getReference().openStream().available() );
                            log.info("Added for ref.");
                        } catch (IOException e) {
                            sr = ServiceUtils.createExceptionErrorReport("Could not inspect "+digitalObject.getContent().getReference(), e);
                        }
                    }
                }
            }
        }
        return new DeterminePropertiesResult(measured, sr);
    }

}