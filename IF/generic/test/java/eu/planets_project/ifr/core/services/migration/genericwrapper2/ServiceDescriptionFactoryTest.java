/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class ServiceDescriptionFactoryTest {

    private ServiceDescriptionFactory serviceDescriptionFactory;
    private static final String TOOL_ID = "DummyTestID";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	final DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");

	serviceDescriptionFactory = new ServiceDescriptionFactory(TOOL_ID,
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
    public void testGetServiceDescription() throws Exception {
	final ServiceDescription serviceDescription = serviceDescriptionFactory
		.getServiceDescription();

	assertNotNull(serviceDescription);
	assertEquals("Un-expected author (creator) information.",
		"\"Easter Bunny <easter.bunny@bunny.net>\"", serviceDescription
			.getAuthor());

	final String expectedDescription = "Example description of a service "
		+ "wrapping of a fantastic\n\t\t\tcommand line tool for migrating an "
		+ "egg to eggnog.";
	assertEquals("Un-expected description.", expectedDescription,
		serviceDescription.getDescription());
	// serviceDescription.getEndpoint();
	// serviceDescription.getFurtherInfo();
	// serviceDescription.getIdentifier();
	// serviceDescription.getInputFormats();
	// serviceDescription.getInstructions();
	// serviceDescription.getLogo();
	// serviceDescription.getName();
	// serviceDescription.getParameters();
	// serviceDescription.getProperties();
	// serviceDescription.getServiceProvider();
	// serviceDescription.getTool();
	// serviceDescription.getType();
	assertEquals("Un-expected service version.", "3.141592653589793",
		serviceDescription.getVersion());
    }
}
