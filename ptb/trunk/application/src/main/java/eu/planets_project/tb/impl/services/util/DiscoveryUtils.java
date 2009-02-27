/**
 * 
 */
package eu.planets_project.tb.impl.services.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.validate.BasicValidateOneBinary;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.view.CreateView;
import eu.planets_project.tb.impl.services.wrappers.MigrateWrapper;

/**
 * This class provides some utilities for those who wish to consume Planets Service.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
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
        if( canBeWrapped(se.getQName()) ) {
            
            if( getServiceWrapperClass(se.getQName()).equals(Migrate.class)) {
                log.info("Wrapping up to Migrate: "+wsdlLocation);
                return (T) new MigrateWrapper(se);
                
            } else if( getServiceWrapperClass(se.getQName()).equals(Identify.class)) {
                log.info("Wrapping up to Identify: "+wsdlLocation);
                return null;
                
            } else if( getServiceWrapperClass(se.getQName()).equals(Validate.class)) {
                log.info("Wrapping up to Validate: "+wsdlLocation);
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
     * @param qName
     * @return
     */
    public static boolean canBeWrapped( QName qName ) {
        if( qName.getLocalPart().startsWith("Basic")) return true;
        if( qName.equals(IdentifyOneBinary.QNAME)) return true;
        return false;
    }
    
    
    /**
     * Determine the high-level service class for this service type.
     * e.g. the Basic forms are wrapped up in higher level forms.
     * @return
     */
    public static Class<? extends PlanetsService> getServiceWrapperClass( QName qName ) {

        // Unqualified services cannot be dealt with:
        if (qName == null)
            return null;

        // Determine class of service:
        if (       qName.equals(Migrate.QNAME) 
                || qName.equals(BasicMigrateOneBinary.QNAME)) {
            return Migrate.class;
            
        } else if (qName.equals(Identify.QNAME)
                ||  qName.equals(IdentifyOneBinary.QNAME)
                ||  qName.equals(BasicIdentifyOneBinary.QNAME)) {
            return Identify.class;
            
        } else if (qName.equals(Validate.QNAME)
                ||  qName.equals(BasicValidateOneBinary.QNAME)) {
            return Validate.class;
            
        } else if (qName.equals(Characterise.QNAME) ) {
            return Characterise.class;
            
        } else if (qName.equals(CreateView.QNAME) ) {
            return CreateView.class;
            
        }

        // Otherwise, this is and unrecognised service:
        return null;
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
            System.out.print(sd.toXmlFormatted());
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
            System.out.println(" Description: "+sd.toXmlFormatted());
        }
    }
    
}
