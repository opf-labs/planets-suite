package eu.planets_project.ifr.core.registry.utils;

import java.net.URL;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:andrew.jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DiscoveryUtils {
    /** */
    private static final Log log = LogFactory.getLog(DiscoveryUtils.class);
     
    /**
     * Attempts to determine the service description for the given WSDL.
     * 
     * @param wsdlLocation
     * @return
     */
    public static ServiceDescription getServiceDescription( URL wsdlLocation ) {
        try {
        	PlanetsServiceExplorer se = new PlanetsServiceExplorer(wsdlLocation);
            PlanetsService s = (PlanetsService) createServiceObject(se.getServiceClass(), wsdlLocation);
            if( s == null ) return null;
            ServiceDescription sd = s.describe();
        return sd;
        } catch( Exception e ) {
            log.error("Runtime exception while inspecting WSDL: "+wsdlLocation+" : "+e);
            return null;
        }
    }
    
    /**
     * A generic method that can be used as a short-cut for instanciating Planets services.
     * 
     * e.g. Migrate m = DiscoveryUtils.createServiceClass( Migrate.class, wsdlLocation);
     * 
     * If the given WSDL points to one of the older 'Basic' service forms, it will be wrapped up 
     * to present the new API and hide the old one.
     * 
     * @param <T> Any recognised service class (extends PlanetsService).
     * @param serviceClass The class of the Planets service to instanciate, e.g. Identify.class
     * @param wsdlLocation The location of the WSDL.
     * @return A new instance of the the given class, wrapping the referenced service.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createServiceObject( Class<T> serviceClass, URL wsdlLocation ) {
        PlanetsServiceExplorer se = new PlanetsServiceExplorer(wsdlLocation);
        Service service;
        if( serviceClass == null || wsdlLocation == null ) return null;
        service = Service.create(wsdlLocation, se.getQName());
        return (T) service.getPort(serviceClass);
    }
    
}
