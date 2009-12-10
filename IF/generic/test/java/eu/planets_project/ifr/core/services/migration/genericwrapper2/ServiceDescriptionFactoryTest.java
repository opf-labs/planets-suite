/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class ServiceDescriptionFactoryTest {

    ServiceDescriptionFactory serviceDescriptionFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	final DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");

	serviceDescriptionFactory = new ServiceDescriptionFactory(
		documentLocator.getDocument());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.ServiceDescriptionFactory#getServiceDescription(org.w3c.dom.Document, java.util.List, java.lang.String)}
     * .
     */
    @Test
    public void testGetServiceDescription() {
	fail("Not yet implemented");
    }

}
