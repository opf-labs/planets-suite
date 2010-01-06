/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.ParameterBuilder;
import eu.planets_project.services.datatypes.MigrationPath;
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
    private static final String SERVICE_PROVIDER = "TestProvider";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	final DocumentLocator documentLocator = new DocumentLocator(
		"GenericWrapperConfigFileExample.xml");

	serviceDescriptionFactory = new ServiceDescriptionFactory(this
		.getClass().getCanonicalName(), SERVICE_PROVIDER,
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

	final String expectedInstructions = "Example: Please install the XYZ "
		+ "tool on the system to\n\t\t\tmake this service work.";
	assertEquals("Un-expected instructions.", expectedInstructions,
		serviceDescription.getInstructions());

	final String expectedLogoURL = "http://www.planets-project.eu/graphics"
		+ "/Planets_Logo.png";
	assertEquals("Un-expected logo URI.", expectedLogoURL,
		serviceDescription.getLogo().toString());

	final String expctedServiceName = "Example: Eggnog migration service.";
	assertEquals("Un-expected service name.", expctedServiceName,
		serviceDescription.getName());

	assertEquals("Un-expected service class name.", this.getClass()
		.getCanonicalName(), serviceDescription.getClassname());

	verifyParameters(serviceDescription.getParameters());

	verifyToolDescription(serviceDescription.getTool());

	assertEquals("Un-expected service version.", "3.141592653589793",
		serviceDescription.getVersion());

	assertEquals("Not testing a service, thus un-expected end-point URL.",
		null, serviceDescription.getEndpoint());

	verifyProperties(serviceDescription.getProperties());

	assertEquals("Un-expected service provider information.",
		SERVICE_PROVIDER, serviceDescription.getServiceProvider());

	assertEquals("Un-expected interface type.",
		"eu.planets_project.services.migrate.Migrate",
		serviceDescription.getType());

//FIXME! Enable when finished!	
//	verifyMigrationPaths(serviceDescription.getPaths());
    }

    /**
     * Verify that <code>planetsMigrationPaths</code> contains the expected
     * migration paths. Assertions will be triggered if that is not the case.
     * 
     * @param planetsMigrationPaths
     *            <code>List</code> of planets migration paths to verify.
     */
    private void verifyMigrationPaths(List<MigrationPath> planetsMigrationPaths)
	    throws Exception {

	final List<MigrationPath> expectedPaths = createTestPaths();

	assertEquals("Un-expected numbar of migration paths.", expectedPaths
		.size(), planetsMigrationPaths.size());

	for (MigrationPath expectedPath : expectedPaths) {
	    boolean pathMatchFound = false;

	    // Run through all the paths and verify that each expected path
	    // exists only once in the list and that its parameters are correct.
	    for (MigrationPath planetsPath : planetsMigrationPaths) {

		if ((expectedPath.getInputFormat().equals(planetsPath
			.getInputFormat()))
			&& (expectedPath.getOutputFormat().equals(planetsPath
				.getOutputFormat()))) {

		    pathMatchFound = true;

		    assertEquals("Wrong parameters for migration path '"
			    + expectedPath + "'", expectedPath.getParameters(),
			    planetsPath.getParameters());
		}
	    }
	}
    }

    private List<MigrationPath> createTestPaths() throws URISyntaxException {

	final List<MigrationPath> paths = new ArrayList<MigrationPath>();

	// Construct all paths with the fmt/16 output format.
	URI outputFormat = new URI("info:pronom/fmt/16");

	URI inputFormat = new URI("info:pronom/x-fmt/91");

	List<Parameter> parameters = new ArrayList<Parameter>();
	Parameter.Builder parameterBuilder = new Parameter.Builder("param1",
		null);
	parameterBuilder.description("Paper size of the migrated object. Valid"
		+ " values are\n\t\t\t\t\t\t\"a4\" and \"legal\"");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("mode", "Normal");
	parameterBuilder.description("Valid options are 'Normal' or 'US'.\n\t"
		+ "\t\t\t\t\tDefaults to 'Normal'.");
	parameters.add(parameterBuilder.build());

	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/406");
	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/407");
	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/408");
	MigrationPath blah = new MigrationPath(inputFormat, outputFormat,
		parameters);
	paths.add(blah);
	paths.add(blah);
	paths.add(blah);

	// paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	// <uri value="info:planets/fmt/ext/lowercase" />
	// <uri value="info:planets/fmt/ext/uppercase" />

	return paths;
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

	final URI expectedID = new URI(
		"http://example-planets-registry.eu/toolident?4385794357");
	final String expectedName = "Example: HandMixer.exe";
	final String expectedVersion = "HandMixer V4.13 by J. Random Hacker.";
	final String expectedDescription = "Example: A useful tool for "
		+ "migrating eggs to eggnog.";
	final URL expectedHomeURL = new URL("http://example.org");

	assertEquals("Un-expected URI in tool description.", expectedID,
		toolDescription.getIdentifier());
	assertEquals("Un-expected tool name in tool description.",
		expectedName, toolDescription.getName());
	assertEquals("Un-expected tool version in tool description.",
		expectedVersion, toolDescription.getVersion());
	assertEquals("Un-expected tool description .", expectedDescription,
		toolDescription.getDescription());
	assertEquals("Un-expected tool homepage URL in tool description.",
		expectedHomeURL, toolDescription.getHomepage());
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
