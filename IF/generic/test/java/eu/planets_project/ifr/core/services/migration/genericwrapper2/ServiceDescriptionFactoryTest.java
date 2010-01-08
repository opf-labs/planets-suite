/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;

/**
 * This test class verifies that the
 * <code>{@link ServiceDescriptionFactory}</code> class is capable of correctly
 * instantiating a <code>ServiceDescription</code> instance from a generic
 * wrapper configuration file.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class ServiceDescriptionFactoryTest {

    private static final String CONFIG_FILE_NAME = "GenericWrapperConfigFileExample.xml";
    private ServiceDescriptionFactory serviceDescriptionFactory;
    private static final String SERVICE_PROVIDER = "TestProvider";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	final DocumentLocator documentLocator = new DocumentLocator(
		CONFIG_FILE_NAME);

	serviceDescriptionFactory = new ServiceDescriptionFactory(this
		.getClass().getCanonicalName(), SERVICE_PROVIDER,
		documentLocator.getDocument());
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.ServiceDescriptionFactory#getServiceDescription(org.w3c.dom.Document, java.util.List, java.lang.String)}
     * . This test will create a <code>ServiceDescription</code> instance, using
     * the <code>ServiceDescriptionFactory</code> with the configuration file
     * specified by <code>{@link CONFIG_FILE_NAME}</code>, and verify that its
     * contents is correct.
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

	assertEquals("Un-expected input formats.", getExpectedInputFormats(),
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

	verifyMigrationPaths(serviceDescription.getPaths());
    }

    /**
     * Create a set of input format URIs which is expected to be found in the
     * <code>ServiceDescription</code> instance being tested.
     * 
     * @return a <code>Set</code> of format URIs to compare with the ones in the
     *         <code>ServiceDescription</code> instance being tested.
     * @throws URISyntaxException
     *             if any of the hard-coded format URIs instantiated in this
     *             method are erroneous.
     */
    private Set<URI> getExpectedInputFormats() throws URISyntaxException {
	final Set<URI> expectedInputFormats = new HashSet<URI>();
	expectedInputFormats.add(new URI("info:pronom/x-fmt/408"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/lowercase"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/407"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/406"));
	expectedInputFormats.add(new URI("info:planets/fmt/ext/foo"));
	expectedInputFormats.add(new URI("info:pronom/x-fmt/91"));
	return expectedInputFormats;
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
	    for (MigrationPath actualPath : planetsMigrationPaths) {

		if ((expectedPath.getInputFormat().equals(actualPath
			.getInputFormat()))
			&& (expectedPath.getOutputFormat().equals(actualPath
				.getOutputFormat()))) {

		    assertFalse("Multiple matches found for expected path: "
			    + expectedPath, pathMatchFound);

		    pathMatchFound = true;

		    // It is important to test the number of parameters
		    // explicitly as the following comparison is not able to
		    // detect redundantly defined parameters.
		    assertEquals(
			    "Un-expected number of parameters defined for "
				    + "path: " + actualPath, expectedPath
				    .getParameters().size(), actualPath
				    .getParameters().size());

		    // Sending the parameter lists through a HashSet avoids
		    // failure due to any differences in the order of the
		    // expected and actual parameters. However, this also causes
		    // loss of any (accidentally/erroneous) redundantly defined
		    // parameters in the configuration.
		    final Set<Parameter> expectedParameters = new HashSet<Parameter>(
			    expectedPath.getParameters());
		    final Set<Parameter> actualParameters = new HashSet<Parameter>(
			    actualPath.getParameters());
		    assertEquals("Wrong parameters for migration path '"
			    + actualPath + "'", expectedParameters,
			    actualParameters);
		}
	    }
	}
    }

    /**
     * Create a list containing <code>MigrationPath</code> instances which are
     * expected to be equal to the paths generated from the configuration file.
     * 
     * @return a list of reference <code>MigrationPath</code> instances.
     * @throws URISyntaxException
     *             if a developer has made an error in the hard-coded URIs.
     */
    private List<MigrationPath> createTestPaths() throws URISyntaxException {

	final List<MigrationPath> paths = new ArrayList<MigrationPath>();

	// Construct all paths with the fmt/16 output format.
	URI outputFormat = new URI("info:pronom/fmt/16");

	URI inputFormat = new URI("info:pronom/x-fmt/91");

	List<Parameter> parameters = new ArrayList<Parameter>();
	Parameter.Builder parameterBuilder = new Parameter.Builder("param1",
		null);
	parameterBuilder.description("Paper size of the migrated object. Valid"
		+ " values are\n\t\t\t\t\t\t\"a4\" and \"legal\"\n\t\t\t\t\t");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("mode", "Normal");
	parameterBuilder.description("\n\t\t\t\t\t\t\n\t\t\t\t\t\t"
		+ "Valid options are 'Normal' or 'US'.\n\t"
		+ "\t\t\t\t\tDefaults to 'Normal'." + "\n\t\t\t\t\t\n"
		+ "\nValid values : Description\n\n"
		+ "US : Migrate to US legal paper format.\n"
		+ "Normal : Migrate to the normal A4 paper format.");
	parameters.add(parameterBuilder.build());

	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/406");
	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/407");
	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	inputFormat = new URI("info:pronom/x-fmt/408");
	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	// Construct a path for migration from lowercase to uppercase.
	inputFormat = new URI("info:planets/fmt/ext/lowercase");
	outputFormat = new URI("info:planets/fmt/ext/uppercase");

	parameters = new ArrayList<Parameter>();

	parameterBuilder = new Parameter.Builder("param1", null);
	parameterBuilder.description("\n\t\t\t\t\t\t\n\t\t\t\t\t\n"
		+ "\t\t\t\t\t\tParameters for 'cat'\n" + "\t\t\t\t\t\t\n"
		+ "\t\t\t\t\t\t\t-A, --show-all\n"
		+ "\t\t\t\t\t\t\t\t  equivalent to -vET\n" + "\t\t\t\t\t\n"
		+ "\t\t\t\t\t\t\t-b, --number-nonblank\n"
		+ "\t\t\t\t\t\t\t\t  number nonempty output lines\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-e    equivalent to -vE\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-E, --show-ends\n"
		+ "\t\t\t\t\t\t\t\t  display $ at end of each line\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-n, --number\n"
		+ "\t\t\t\t\t\t\t\t  number all output lines\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-s, --squeeze-blank\n"
		+ "\t\t\t\t\t\t\t\t  suppress repeated empty output lines\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-t    equivalent to -vT\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-T, --show-tabs\n"
		+ "\t\t\t\t\t\t\t\t  display TAB characters as ^I\n"
		+ "\t\t\t\t\t\n" + "\t\t\t\t\t\t\t-v, --show-nonprinting\n"
		+ "\t\t\t\t\t\t\t\t  use ^ and M- notation, except for"
		+ " LFD and TAB\n\t\t\t\t\t");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("param2", null);
	parameterBuilder
		.description("\n\t\t\t\t\t\t\n\t\t\t\t\t\n"
			+ "\t\t\t\t\t\tCommand line parameters for the 'tr' command.\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   -c, -C, --complement\n"
			+ "\t\t\t\t\t\t\t\t first complement SET1\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   -d, --delete\n"
			+ "\t\t\t\t\t\t\t\t delete characters in SET1, do not translate\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   -s, --squeeze-repeats\n"
			+ "\t\t\t\t\t\t\t\t replace each input sequence of  a  repeated  character  that  is\n"
			+ "\t\t\t\t\t\t\t\t listed in SET1 with a single occurrence of that character\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   -t, --truncate-set1\n"
			+ "\t\t\t\t\t\t\t\t first truncate SET1 to length of SET2\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   SETs  are  specified  as  strings  of characters.  Most represent them‐\n"
			+ "\t\t\t\t\t\t   selves.  Interpreted sequences are:\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\NNN   character with octal value NNN (1 to 3 octal digits)\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\\\     backslash\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\a     audible BEL\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\b     backspace\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\f     form feed\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\n     new line\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\r     return\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\t     horizontal tab\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   \\v     vertical tab\n"
			+ "\t\t\t\t\t\t\t\t  CHAR1-CHAR2\n"
			+ "\t\t\t\t\t\t\t\t  all characters from CHAR1 to CHAR2 in ascending order\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [CHAR*]\n"
			+ "\t\t\t\t\t\t\t\t  in SET2, copies of CHAR until length of SET1\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [CHAR*REPEAT]\n"
			+ "\t\t\t\t\t\t\t\t  REPEAT copies of CHAR, REPEAT octal if starting with 0\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:alnum:]\n"
			+ "\t\t\t\t\t\t\t\t  all letters and digits\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:alpha:]\n"
			+ "\t\t\t\t\t\t\t\t  all letters\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:blank:]\n"
			+ "\t\t\t\t\t\t\t\t  all horizontal whitespace\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:cntrl:]\n"
			+ "\t\t\t\t\t\t\t\t  all control characters\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:digit:]\n"
			+ "\t\t\t\t\t\t\t\t  all digits\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:graph:]\n"
			+ "\t\t\t\t\t\t\t\t  all printable characters, not including space\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:lower:]\n"
			+ "\t\t\t\t\t\t\t\t  all lower case letters\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:print:]\n"
			+ "\t\t\t\t\t\t\t\t  all printable characters, including space\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:punct:]\n"
			+ "\t\t\t\t\t\t\t\t  all punctuation characters\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:space:]\n"
			+ "\t\t\t\t\t\t\t\t  all horizontal or vertical whitespace\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:upper:]\n"
			+ "\t\t\t\t\t\t\t\t  all upper case letters\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [:xdigit:]\n"
			+ "\t\t\t\t\t\t\t\t  all hexadecimal digits\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   [=CHAR=]\n"
			+ "\t\t\t\t\t\t\t\t  all characters which are equivalent to CHAR\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t   Translation occurs if -d is not given and both SET1  and  SET2  appear.\n"
			+ "\t\t\t\t\t\t   -t  may  be  used only when translating.  SET2 is extended to length of\n"
			+ "\t\t\t\t\t\t   SET1 by repeating its last character as necessary.   Excess  characters\n"
			+ "\t\t\t\t\t\t   of  SET2  are  ignored.  Only [:lower:] and [:upper:] are guaranteed to\n"
			+ "\t\t\t\t\t\t   expand in ascending order; used in SET2  while  translating,  they  may\n"
			+ "\t\t\t\t\t\t   only  be used in pairs to specify case conversion.  -s uses SET1 if not\n"
			+ "\t\t\t\t\t\t   translating nor deleting; else squeezing uses  SET2  and  occurs  after\n"
			+ "\t\t\t\t\t\t   translation or deletion.\n"
			+ "\t\t\t\t\t");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("mode", "complete");
	parameterBuilder
		.description("\n\t\t\t\t\t\t\n\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\tValid options:\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t'complete' : Converts from lowercase to uppercase.\n"
			+ "\t\t\t\t\t\t'AC-DC' : Converts As to Ds, thus, AC-DC becomes DC-AC.\n"
			+ "\t\t\t\t\t\t'extra' : Converts from lowercase to uppercase and adds\n"
			+ "\t\t\t\t\t\t\t\t  a line number to each line.\n\n"
			+ "\t\t\t\t\t\tDefaults to 'complete'.\n"
			+ "\t\t\t\t\t\n\nValid values : Description\n\n"
			+ "complete : Uppercase all text.\n"
			+ "extra : Uppercase all text and add line numbers.\n"
			+ "\t\t\t\t\t\t\n"
			+ "AC-DC : Swaps As with Ds. Thus changing AC-DC to DC-AC\n"
			+ "\t\t\t\t\t\t");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("quality", null);
	parameterBuilder
		.description("\n\t\t\t\t\t\t\n\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\tValid options:\n"
			+ "\t\t\t\t\t\t\n"
			+ "\t\t\t\t\t\t'good' : Converts from lowercase to uppercase.\n"
			+ "\t\t\t\t\t\t'better' : Converts As to Ds, thus, AC-DC becomes DC-AC.\n"
			+ "\t\t\t\t\t\t'best' : Converts from lowercase to uppercase and adds\n"
			+ "\t\t\t\t\t\t\t\t a line number to each line.\n\n"
			+ "\t\t\t\t\t\tDefaults to 'good'.\n"
			+ "\t\t\t\t\t\t\n" + "\t\t\t\t\t\n\n"
			+ "Valid values : Description\n\n"
			+ "better : AC-DC to DC-AC\n"
			+ "best : Uppercase all and add line numbers.\n"
			+ "good : Uppercase all.");

	parameters.add(parameterBuilder.build());

	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	// Construct a path for migration from lowercase to uppercase.
	inputFormat = new URI("info:planets/fmt/ext/foo");
	outputFormat = new URI("info:planets/fmt/ext/bar");

	parameters = new ArrayList<Parameter>();

	parameterBuilder = new Parameter.Builder("param1", null);
	parameterBuilder.description("Command line parameters for the 'cat'\n"
		+ "\t\t\t\t\t\tcommand.\n" + "\t\t\t\t\t\tSee\n"
		+ "\t\t\t\t\t\t'man\n" + "\t\t\t\t\t\tcat'.\n" + "\t\t\t\t");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("param2", null);
	parameterBuilder.description("Command line parameters for the 'tr'\n"
		+ "\t\t\t\t\t\tcommand.\n" + "\t\t\t\t\t\tSee\n"
		+ "\t\t\t\t\t\t'man\n" + "\t\t\t\t\t\ttr'.\n" + "\t\t\t\t");
	parameters.add(parameterBuilder.build());

	paths.add(new MigrationPath(inputFormat, outputFormat, parameters));

	return paths;
    }

    /**
     * Verify that the contents of the given list of properties, generated from
     * the example configuration file, is correct. If that is not the case then
     * JUnit assertions will be triggered.
     * 
     * @param properties
     *            a list of <code>Property</code> instances generated from the
     *            example configuration file.
     * @throws URISyntaxException
     *             if a developer has made an error in the hard-coded ID URI of
     *             the tested property.
     */
    private void verifyProperties(List<Property> properties)
	    throws URISyntaxException {

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

    /**
     * Verify that the tool description, created from the example configuration
     * file, is correct. If that is not the case then JUnit assertions will be
     * triggered.
     * 
     * @param toolDescription
     *            a <code>Tool</code> instance created from the information in
     *            the example configuration file.
     * @throws URISyntaxException
     *             if a developer has made an error in the hard-coded URI in
     *             this test.
     * @throws MalformedURLException
     *             if a developer has made an error in the hard-coded URL in
     *             this test.
     */
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
