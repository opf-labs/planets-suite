package eu.planets_project.services.migration.dia.impl;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.datatypes.Parameter;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class CliMigrationPathTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    // final URI sourceFormat = new URI("info:test/lowercase");
    // final URI destinationFormat = new URI("info:test/uppercase");

    /**
     */
    @Test
    public void testSetGetCommandLine() throws Exception {
        final String originalCommandLine = "cat #param1 #tempSource > "
                + "#myInterimFile && tr #param2 #myInterimFile > "
                + "#tempDestination";

        // The first trivial test.
        final CliMigrationPath cliMigrationPath = new CliMigrationPath();
        cliMigrationPath.setCommandLine(originalCommandLine);
        Assert.assertEquals(
                "getCommandLine() did not return the value just set.",
                originalCommandLine, cliMigrationPath.getCommandLine());

        final ArrayList<Parameter> toolParameters = new ArrayList<Parameter>();
        // Options for the 'cat' command
        toolParameters.add(new Parameter("param1", "-n"));
        // Options for the 'tr' command
        toolParameters.add(new Parameter("param2", "'[:lower:]' '[:upper:]'"));

        // TODO: Find a way for passing the file mappings

        final String executableCommandLine = cliMigrationPath
                .getCommandLine(toolParameters, new HashMap<String,String>());

        final String expectedCommandLine = "cat -n #tempSource > "
                + "#myInterimFile && tr '[:lower:]' '[:upper:]' "
                + "#myInterimFile > " + "#tempDestination";

        System.out.println("expected: " + expectedCommandLine + "\n\nactual: " + executableCommandLine);
        Assert.assertEquals("Un-expected output from getCommandLine().",
                expectedCommandLine, executableCommandLine);
    }
}
