package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.datatypes.Parameter;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class MigrationPathImplTest {

    /**
     * @throws Exception
     */
    // FIXME! This test needs a check-up - partially disabled.
    @Test
    public void testSetGetCommandLine() throws Exception {

	// TODO: This test does not need all these details. The test of command
	// line construction should go elsewhere.
	final String commandParameter = "cat #param1 #tempSource > "
		+ "#myInterimFile && tr #param2 #myInterimFile > "
		+ "#tempDestination";

	final ArrayList<String> commandLineParameters = new ArrayList<String>();
	commandLineParameters.add("-c");
	commandLineParameters.add(commandParameter);

	// The first trivial test.
	final MigrationPathImpl migrationPath = new MigrationPathImpl();
	final CommandLine commandLine = new CommandLine("/bin/sh",
		commandLineParameters);
	migrationPath.setCommandLine(commandLine);

	Assert.assertEquals(commandLine, migrationPath.getCommandLine());

	final ArrayList<Parameter> toolParameters = new ArrayList<Parameter>();
	// Options for the 'cat' command
	toolParameters.add(new Parameter("param1", "-n"));
	// Options for the 'tr' command
	toolParameters.add(new Parameter("param2", "'[:lower:]' '[:upper:]'"));

	migrationPath.addTempFilesDeclaration("myInterimFile",
		"/random-temp-file-name");

	final String executableCommandLine = migrationPath.getCommandLine().getParameters().get(2);

	final String expectedCommandLine = "cat -n /random-source-name > "
		+ "/random-temp-file-name && tr '[:lower:]' '[:upper:]' "
		+ "/random-temp-file-name > " + "/random-destination-name";

	Assert.assertEquals("Un-expected output from getCommandLine().",
		expectedCommandLine, executableCommandLine);
    }
}
