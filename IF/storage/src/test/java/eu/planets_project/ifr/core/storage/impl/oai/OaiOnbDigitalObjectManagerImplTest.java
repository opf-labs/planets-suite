/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.impl.oai;

import eu.planets_project.services.datatypes.DigitalObject;
import java.net.URI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author onb
 */
public class OaiOnbDigitalObjectManagerImplTest {

    public OaiOnbDigitalObjectManagerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testStore() throws Exception {
        System.out.println("store");
        URI pdURI = null;
        DigitalObject digitalObject = null;
        OaiOnbDigitalObjectManagerImpl instance = new OaiOnbDigitalObjectManagerImpl();
        instance.store(pdURI, digitalObject);
    }

    @Test
    public void testRetrieve() throws Exception {
        System.out.println("retrieve");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl instance = new OaiOnbDigitalObjectManagerImpl();
        DigitalObject expResult = null;
        DigitalObject result = instance.retrieve(pdURI);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testList() {
        System.out.println("list");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl instance = new OaiOnbDigitalObjectManagerImpl();
        URI[] expResult = null;
        URI[] result = instance.list(pdURI);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

}