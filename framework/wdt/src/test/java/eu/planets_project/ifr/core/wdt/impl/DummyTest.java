package eu.planets_project.ifr.core.wdt.impl;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.wdt.api.BeanObject;
import eu.planets_project.ifr.core.wdt.api.Monitor;
import eu.planets_project.services.utils.test.ServiceCreator;

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
