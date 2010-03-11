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
		+ "wrapping of a fantastic\n            command line tool for migrating an "
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
		+ "tool on the system to\n            make this service work.";
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
		+ " values are\n                        \"a4\" and \"legal\"\n"
		+"                    ");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("mode", "Normal");
	parameterBuilder.description("\n                        \n            "
		+"            Valid options are 'Normal' or 'US'.\n    "
		+ "                    Defaults to 'Normal'." + "\n           "
		+"         \n\nValid values : Description\n\n"
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
	parameterBuilder.description("\n                        \n"
		+ "                        \n"
		+ "                        Parameters for 'cat'\n"
		+ "                        \n"
		+ "                            -A, --show-all\n"
		+ "                                  equivalent to -vET\n"
		+ "                                  \n"
		+ "                            -b, --number-nonblank\n"
		+ "                                  number nonempty output lines\n"
		+ "                                  \n"
		+ "                            -e    equivalent to -vE\n"
		+ "                                  \n"
		+ "                            -E, --show-ends\n"
		+ "                                  display $ at end of each line\n"
		+ "                                  \n"
		+ "                            -n, --number\n"
		+ "                                  number all output lines\n"
		+ "                                  \n"
		+ "                            -s, --squeeze-blank\n"
		+ "                                  suppress repeated empty output lines\n"
		+ "                                  \n"
		+ "                            -t    equivalent to -vT\n"
		+ "                                  \n"
		+ "                            -T, --show-tabs\n"
		+ "                                  display TAB characters as ^I\n"
		+ "                                  \n"
		+ "                            -v, --show-nonprinting\n"
		+ "                                  use ^ and M- notation, except for"
		+ " LFD and TAB\n                    ");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("param2", null);
	parameterBuilder
		.description("\n                        \n"+
			"                        \n"
			+ "                        Command line parameters for the 'tr' command.\n"
			+ "                        \n"
			+ "                           -c, -C, --complement\n"
			+ "                                 first complement SET1\n"
			+ "                                 \n"
			+ "                           -d, --delete\n"
			+ "                                 delete characters in SET1, do not translate\n"
			+ "                                 \n"
			+ "                           -s, --squeeze-repeats\n"
			+ "                                 replace each input sequence of  a  repeated  character  that  is\n"
			+ "                                 listed in SET1 with a single occurrence of that character\n"
			+ "                                 \n"
			+ "                           -t, --truncate-set1\n"
			+ "                                 first truncate SET1 to length of SET2\n"
			+ "                                 \n"
			+ "                           SETs  are  specified  as  strings  of characters.  Most represent them‐\n"
			+ "                           selves.  Interpreted sequences are:\n"
			+ "                           \n"
			+ "                           \\NNN   character with octal value NNN (1 to 3 octal digits)\n"
			+ "                           \n"
			+ "                           \\\\     backslash\n"
			+ "                           \n"
			+ "                           \\a     audible BEL\n"
			+ "                           \n"
			+ "                           \\b     backspace\n"
			+ "                           \n"
			+ "                           \\f     form feed\n"
			+ "                           \n"
			+ "                           \\n     new line\n"
			+ "                           \n"
			+ "                           \\r     return\n"
			+ "                           \n"
			+ "                           \\t     horizontal tab\n"
			+ "                           \n"
			+ "                           \\v     vertical tab\n"
			+ "                                  CHAR1-CHAR2\n"
			+ "                                  all characters from CHAR1 to CHAR2 in ascending order\n"
			+ "                                  \n"
			+ "                           [CHAR*]\n"
			+ "                                  in SET2, copies of CHAR until length of SET1\n"
			+ "                                  \n"
			+ "                           [CHAR*REPEAT]\n"
			+ "                                  REPEAT copies of CHAR, REPEAT octal if starting with 0\n"
			+ "                                  \n"
			+ "                           [:alnum:]\n"
			+ "                                  all letters and digits\n"
			+ "                                  \n"
			+ "                           [:alpha:]\n"
			+ "                                  all letters\n"
			+ "                                  \n"
			+ "                           [:blank:]\n"
			+ "                                  all horizontal whitespace\n"
			+ "                                  \n"
			+ "                           [:cntrl:]\n"
			+ "                                  all control characters\n"
			+ "                                  \n"
			+ "                           [:digit:]\n"
			+ "                                  all digits\n"
			+ "                                  \n"
			+ "                           [:graph:]\n"
			+ "                                  all printable characters, not including space\n"
			+ "                                  \n"
			+ "                           [:lower:]\n"
			+ "                                  all lower case letters\n"
			+ "                                  \n"
			+ "                           [:print:]\n"
			+ "                                  all printable characters, including space\n"
			+ "                                  \n"
			+ "                           [:punct:]\n"
			+ "                                  all punctuation characters\n"
			+ "                                  \n"
			+ "                           [:space:]\n"
			+ "                                  all horizontal or vertical whitespace\n"
			+ "                                  \n"
			+ "                           [:upper:]\n"
			+ "                                  all upper case letters\n"
			+ "                                  \n"
			+ "                           [:xdigit:]\n"
			+ "                                  all hexadecimal digits\n"
			+ "                                  \n"
			+ "                           [=CHAR=]\n"
			+ "                                  all characters which are equivalent to CHAR\n"
			+ "                                  \n"
			+ "                           Translation occurs if -d is not given and both SET1  and  SET2  appear.\n"
			+ "                           -t  may  be  used only when translating.  SET2 is extended to length of\n"
			+ "                           SET1 by repeating its last character as necessary.   Excess  characters\n"
			+ "                           of  SET2  are  ignored.  Only [:lower:] and [:upper:] are guaranteed to\n"
			+ "                           expand in ascending order; used in SET2  while  translating,  they  may\n"
			+ "                           only  be used in pairs to specify case conversion.  -s uses SET1 if not\n"
			+ "                           translating nor deleting; else squeezing uses  SET2  and  occurs  after\n"
			+ "                           translation or deletion.\n"
			+ "                    ");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("mode", "complete");
	parameterBuilder
		.description("\n                        \n                        \n"
			+ "                        Valid options:\n"
			+ "                        \n"
			+ "                        'complete' : Converts from lowercase to uppercase.\n"
			+ "                        'AC-DC'    : Converts As to Ds, thus, AC-DC becomes DC-AC.\n"
			+ "                        'extra'    : Converts from lowercase to uppercase and adds\n"
			+ "                                     a line number to each line.\n"
			+ "                                     \n"
			+ "                        Defaults to 'complete'.\n"
			+ "                    \n\nValid values : Description\n\n"
			+ "complete : Uppercase all text.\n"
			+ "extra : Uppercase all text and add line numbers.\n"
			+ "                        \n"
			+ "AC-DC : Swaps As with Ds. Thus changing AC-DC to DC-AC\n"
			+ "                        ");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("quality", null);
	parameterBuilder
		.description("\n                        \n                        \n"
			+ "                        Quality presets for the test tool.\n"
			+ "                        \n"
			+ "                        Valid options:\n"
			+ "                        \n"
			+ "                        'good' : Converts from lowercase to uppercase.\n"
			+ "                        'better' : Converts As to Ds, thus, AC-DC becomes DC-AC.\n"
			+ "                        'best' : Converts from lowercase to uppercase and adds\n"
			+ "                                 a line number to each line.\n"
			+ "                                 \n"
			+ "                        Defaults to 'good'.\n"
			+ "                        \n" + "                    \n\n"
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
		+ "                        command.\n" + "                        See\n"
		+ "                        'man\n" + "                        cat'.\n" 
		+ "                    ");
	parameters.add(parameterBuilder.build());

	parameterBuilder = new Parameter.Builder("param2", null);
	parameterBuilder.description("Command line parameters for the 'tr'\n"
		+ "                        command.\n"
		+ "                        See\n"
		+ "                        'man\n" 
		+ "                        tr'.\n"
		+ "                    ");
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
		+ " an example property with a value\n                    and "
		+ "a silly value unit specification.";
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
