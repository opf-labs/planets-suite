/**
 * Copyright (c) 2007, 2008, 2009, 2010 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of
 * the Apache License version 2.0 which accompanies
 * this distribution, and is available at:
 *   http://www.apache.org/licenses/LICENSE-2.0.txt
 * 
 */
package eu.planets_project.ifr.core.servreg.utils.client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.servreg.utils.PlanetsServiceExplorer;
import eu.planets_project.ifr.core.servreg.utils.client.wrappers.IdentifyWrapper;
import eu.planets_project.ifr.core.servreg.utils.client.wrappers.MigrateWrapper;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.FileUtils;

/**
 * A really simple class that allows a Planets Service to be invoked from the command line.
 * 
 * @author AnJackson
 *
 */
public class PlanetsCommand {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        /* FIXME, point to log4j.properties instead of doing this? */
        /*
        java.util.logging.Logger.getLogger("com.sun.xml.ws.model").setLevel(java.util.logging.Level.WARNING); 
        java.util.logging.Logger.getAnonymousLogger().setLevel(java.util.logging.Level.WARNING);
        Logger sunlogger = Logger.getLogger("com.sun.xml.ws.model");
        sunlogger.setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger( com.sun.xml.ws.util.Constants.LoggingDomain).setLevel(java.util.logging.Level.WARNING);
*/        
        /* Lots of info please: */
        java.util.logging.Logger.getAnonymousLogger().setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger( com.sun.xml.ws.util.Constants.LoggingDomain).setLevel(java.util.logging.Level.FINEST);
        //System.setProperty("com.sun.xml.ws.transport.local.LocalTransportPipe.dump","true");
        //System.setProperty("com.sun.xml.ws.util.pipe.StandaloneTubeAssembler.dump","true");
        //System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump","true");
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump","true");

        URL wsdl;
        try {
            wsdl = new URL( args[0] );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        
        PlanetsServiceExplorer pse = new PlanetsServiceExplorer(  wsdl );
        
        System.out.println(".describe(): "+pse.getServiceDescription());

        /* 
         * The different services are invoked in different ways...
         */
        if( pse.getQName().equals( Migrate.QNAME ) ) {
            System.out.println("Is a Migrate service. ");
            Migrate s = new MigrateWrapper(wsdl);
            
            DigitalObject dobIn = new DigitalObject.Builder(Content.byReference( new File(args[1]))).build();
            
            MigrateResult result = s.migrate(dobIn, URI.create(args[2]), URI.create(args[3]), null);
            
            System.out.println("ServiceReport: "+result.getReport());
            
            FileUtils.writeInputStreamToFile( result.getDigitalObject().getContent().getInputStream(), new File("./output" ) );
            
        } else if( pse.getQName().equals( Identify.QNAME ) ) {
            System.out.println("Is an Identify service. ");
            Identify s = new IdentifyWrapper(wsdl);
            
            DigitalObject dobIn = new DigitalObject.Builder(Content.byReference( new File(args[1]))).build();
            
            IdentifyResult result = s.identify(dobIn, null);
                        
            System.out.println("ServiceReport: "+result.getReport());
        }
    }

}
