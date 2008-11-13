package eu.planets_project.ifr.core.wdt.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.ifr.core.wdt.impl.MonitorImpl;
import eu.planets_project.ifr.core.wdt.api.BeanObject;

import junit.framework.TestCase;
import org.junit.BeforeClass;

import org.junit.Test;

//import eu.planets_project.ifr.core.techreg.api.formats.Format;
//import eu.planets_project.services.datatypes.Content;
//import eu.planets_project.services.datatypes.DigitalObject;
//import eu.planets_project.services.datatypes.ServiceDescription;
//import eu.planets_project.services.datatypes.ServiceReport;
//import eu.planets_project.services.migrate.Migrate;
//import eu.planets_project.services.migrate.MigrateResult;
//import eu.planets_project.services.sanselan.SanselanMigrate;
import eu.planets_project.ifr.core.wdt.api.Monitor;
import eu.planets_project.ifr.core.wdt.api.BeanObject;

/**
 * Local and client tests of the digital object migration functionality.
 * 
 * @author Rainer Schmidt
 */
public final class DummyTest extends TestCase {
	
		static Monitor monitor;

    /**
     * Tests Droid identification using a local Droid instance.
     */
    @BeforeClass
    public static void localTests() {
        monitor = ServiceCreator.createTestService(Monitor.QNAME, MonitorImpl.class, "wdt-ifr-wdt-ejb/MonitorImpl?wsdl", /*ServiceCreator.Mode.STANDALONE*/ServiceCreator.Mode.LOCAL);
    }
    
    /**
     * Test the pass-thru migration.
     * @throws IOException 
     */
    @Test
    public void testMonitor() throws Exception {
        try {
        	BeanObject out = monitor.monitor(new BeanObject());
					System.out.println("testing...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
