/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.planets_project.ifr.core.storage.impl.oai;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.services.datatypes.DigitalObject;
import java.net.URI;
import java.net.URISyntaxException;
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
    public void testListAndRetrieve() throws URISyntaxException, DigitalObjectNotFoundException {
        System.out.println("list");
        URI pdURI = null;
        OaiOnbDigitalObjectManagerImpl instance = new OaiOnbDigitalObjectManagerImpl();
        URI[] result = instance.list(pdURI);

        for (URI uri : result) {
            String id = uri.toString();
            System.out.println("id: " + id);
            DigitalObject dio = instance.retrieve(new URI(id));
            System.out.println("title: " + dio.getTitle());
        }
    }
}