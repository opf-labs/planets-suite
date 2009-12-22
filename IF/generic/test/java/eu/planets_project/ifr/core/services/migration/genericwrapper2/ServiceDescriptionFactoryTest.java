/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;

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

	final URI expectedFurtherInfoURI = new URI("http://example.org");
	assertEquals("Un-expected text returned by getFurtherInfo().",
		expectedFurtherInfoURI, serviceDescription.getFurtherInfo());

	assertEquals("Un-expected identifier.", "Example_custom_identifier",
		serviceDescription.getIdentifier());

	final Set<URI> expectedInputFormats = new HashSet<URI>();
	expectedInputFormats.add(new URI("info:pronom/x-fmt/408"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/lowercase"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/407"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/406"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/foo"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/91"));

	assertEquals("Un-expected input formats.", expectedInputFormats,
		new HashSet<URI>(serviceDescription.getInputFormats()));

	assertEquals("Un-expected instructions.",
		"Example: Please install the XYZ tool on the system to\n\t\t\t"
			+ "make this service work.", serviceDescription
			.getInstructions());

	assertEquals("Un-expected logo URI.",
		"http://www.planets-project.eu/graphics/Planets_Logo.png",
		serviceDescription.getLogo().toString());

	assertEquals("Un-expected service name.",
		"Example: Eggnog migration service.", serviceDescription
			.getName());

	verifyParameters(serviceDescription.getParameters());

	verifyToolDescription(serviceDescription.getTool());

	assertEquals("Un-expected service version.", "3.141592653589793",
		serviceDescription.getVersion());

	assertEquals("Un-expected end-point URL.", new URL(
		"http://FIXME! put the correct URL here!"), serviceDescription
		.getEndpoint());

	verifyProperties(serviceDescription.getProperties());
	// TODO: Yet to be finished.... Clarify where this information is
	// supposed to come from...
	//	  
	// assertEquals("Un-expected service provider information.", "",
	// serviceDescription.getServiceProvider());
	//	  
	// assertEquals("Un-expected service interface type.", "",
	// serviceDescription.getType());

	System.out
		.println("@%^@^%@%^ GNARF>>> " + serviceDescription.getTool());

    }

    private void verifyProperties(List<Property> properties) throws Exception {

	assertEquals(
		"Un-expected number of properties in service description.", 1,
		properties.size());

	final Property property = properties.get(0);
	final String expectedDescription = "This is an optional description of"
		+ " an example property with a value\n\t\t\t\t\tand a silly value "
		+ "unit specification.";
	assertEquals("Un-expected property description.", expectedDescription,
		property.getDescription());
	assertEquals("Un-expected property name.", "exampleName", property
		.getName());
	assertEquals("Un-expected property type.", "myOptionalExampleType",
		property.getType());
	assertEquals("Un-expected property ID.", new URI("info:exampleID"),
		property.getUri());
	assertEquals("Un-expected property value unit.", "cubic gram", property
		.getUnit());
	assertEquals("Un-expected ", "exampleValue with a ridiculous unit.",
		property.getValue());
    }

    private void verifyToolDescription(Tool toolDescription)
	    throws URISyntaxException, MalformedURLException {

	final URI id = new URI(
		"http://example-planets-registry.eu/toolident?4385794357");
	final String name = "Example: HandMixer.exe";
	final String version = "HandMixer V4.13 by J. Random Hacker.";
	final String description = "Example: A useful tool for migrating eggs "
		+ "to eggnog.";
	final URL homeURL = new URL("http://example.org");

	final Tool expectedToolDescription = new Tool(id, name, version,
		description, homeURL);

	assertEquals("Un-expected URI in tool description.",
		expectedToolDescription.getIdentifier(), toolDescription
			.getIdentifier());
	assertEquals("Un-expected tool name in tool description.",
		expectedToolDescription.getName(), toolDescription.getName());
	assertEquals("Un-expected tool version in tool description.",
		expectedToolDescription.getVersion(), toolDescription
			.getVersion());
	assertEquals("Un-expected tool description .", expectedToolDescription
		.getDescription(), toolDescription.getDescription());
	assertEquals("Un-expected tool homepage URL in tool description.",
		expectedToolDescription.getHomepage(), toolDescription
			.getHomepage());
    }

    /**
     * Verify that the parameters specified by <code>parameters</code> have the
     * expected names and values. If that is not the case then JUnit assertions
     * will be triggered.
     * 
     * @param parameters
     *            <code>List</code> of parameters to verify.
     */
    private void verifyParameters(List<Parameter> parameters) {

	final HashMap<String, String> expectedParameters = new HashMap<String, String>();
	expectedParameters.put("param1", null);
	expectedParameters.put("param2", null);
	expectedParameters.put("mode", null);
	expectedParameters.put("quality", null);

	for (Parameter parameter : parameters) {

	    assertTrue("Un-expected parameter in the parameters list of the "
		    + "service description: " + parameter.getName(),
		    expectedParameters.containsKey(parameter.getName()));

	    assertEquals("Un-expected value for parameter '"
		    + parameter.getName() + "'.", expectedParameters
		    .get(parameter.getName()), parameter.getValue());
	}
    }
}
