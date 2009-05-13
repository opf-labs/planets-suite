package eu.planets_project.ifr.core.storage.impl.oai;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.services.datatypes.DigitalObject;
import java.net.URI;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author georg
 */
public class OaiOnbDigitalObjectManagerImpl_OAI4JTest {

    public OaiOnbDigitalObjectManagerImpl_OAI4JTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of store method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
//    @Test
    public void testStore() throws Exception {
        System.out.println("store");
        URI pdURI = null;
        DigitalObject digitalObject = null;
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        instance.store(pdURI, digitalObject);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isWritable method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
    @Test
    public void testIsWritable() {
        System.out.println("isWritable");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        boolean expResult = false;
        boolean result = instance.isWritable(pdURI);
        assertEquals(expResult, result);
    }

    /**
     * Test of list method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
    @Test
    public void testList_URI() throws DigitalObjectNotFoundException {
        System.out.println("list");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        List<URI> result = instance.list(pdURI);
        for (URI uri : result) {
            String id = uri.toString();
            System.out.println("id: " + id);
            DigitalObject dio = instance.retrieve(uri);
            if (dio != null) {

                System.out.println("title: " + dio.getTitle());
                System.out.println("uri: " + dio.getPermanentUri());

            }
            System.out.println("---");

        }
    }

    /**
     * Test of retrieve method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
//    @Test
    public void testRetrieve() throws Exception {
        System.out.println("retrieve");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        DigitalObject expResult = null;
        DigitalObject result = instance.retrieve(pdURI);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQueryTypes method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
//    @Test
    public void testGetQueryTypes() {
        System.out.println("getQueryTypes");
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        List<Class<? extends Query>> expResult = null;
        List<Class<? extends Query>> result = instance.getQueryTypes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class OaiOnbDigitalObjectManagerImpl_OAI4J.
     */
//    @Test
    public void testList_URI_Query() throws Exception {
        System.out.println("list");
        URI pdURI = null;
        Query q = null;
        OaiOnbDigitalObjectManagerImpl_OAI4J instance = new OaiOnbDigitalObjectManagerImpl_OAI4J();
        List<URI> expResult = null;
        List<URI> result = instance.list(pdURI, q);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}