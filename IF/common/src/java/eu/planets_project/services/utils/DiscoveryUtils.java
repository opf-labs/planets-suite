/**
 * 
 */
package eu.planets_project.services.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.utils.wrappers.BasicMigrateWrapper;
import eu.planets_project.services.validate.Validate;

/**
 * This class provides some utilities for those who wish to consume Planets Service.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DiscoveryUtils {
    
     
    /**
     * Attempts to determine the service description for the given WSDL.
     * 
     * @param wsdlLocation
     * @return
     */
    public static ServiceDescription getServiceDescription( URL wsdlLocation ) {
        PlanetsServiceExplorer se = new PlanetsServiceExplorer(wsdlLocation);
        PlanetsService s = (PlanetsService) createServiceObject(se.getServiceClass(), wsdlLocation);
        if( s == null ) return null;
        return s.describe();
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
        if( se.isDeprecated() ) {
            if( se.getServiceClass().equals(Migrate.class)) {
                return (T) new BasicMigrateWrapper(se);
            } else if( se.getServiceClass().equals(Identify.class)) {
                return null;
            } else if( se.getServiceClass().equals(Validate.class)) {
                return null;
            }
            // Otherwise, unrecognised.
            return null;
        } else {
            service = Service.create(wsdlLocation, se.getQName());
            return (T) service.getPort(serviceClass);
        }
    }
    
    /**
     * 
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String [] args) throws MalformedURLException {
        // If called from the command line, parse the argument as a WSDL URL.
        if( args.length == 1 ) {
            URL wsdl  = new URL(args[0]);
            ServiceDescription sd = DiscoveryUtils.getServiceDescription(wsdl);
            System.out.print(sd.toXml(true));
            return;
        }
        // Otherwise, do a simple test:
        URL wsdls[] = new URL[] { 
                new URL("http://127.0.0.1:8080/pserv-if-simple/AlwaysSaysValidService?wsdl"),
                new URL("http://127.0.0.1:8080/pserv-if-simple/PassThruMigrationService?wsdl"),
                new URL("http://127.0.0.1:8080/pserv-pa-sanselan/SanselanMigrate?wsdl")
                };
        for( URL wsdl : wsdls ) {
            ServiceDescription sd = DiscoveryUtils.getServiceDescription(wsdl);
            System.out.println(" Description: "+sd.toXml(true));
        }
    }
    
}
