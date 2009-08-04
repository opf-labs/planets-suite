package eu.planets_project.services.migration.dia.impl;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.migration.dia.impl.genericwrapper.MigrationPath;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

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
        final MigrationPath migrationPath = new MigrationPath();
        migrationPath.setCommandLine(originalCommandLine);
        Assert.assertEquals(
                "getCommandLine() did not return the value just set.",
                originalCommandLine, migrationPath.getCommandLine());

        final ArrayList<Parameter> toolParameters = new ArrayList<Parameter>();
        // Options for the 'cat' command
        toolParameters.add(new Parameter("param1", "-n"));
        // Options for the 'tr' command
        toolParameters.add(new Parameter("param2", "'[:lower:]' '[:upper:]'"));

        HashMap<String,String> tempFileMap = new HashMap<String,String>();
        tempFileMap.put("tempSource", "random-source-name");
        tempFileMap.put("tempDestination", "random-destination-name");
        tempFileMap.put("myInterimFile", "random-temp-file-name");
        final String executableCommandLine = migrationPath
                .getCommandLine(toolParameters);

        final String expectedCommandLine = "cat -n random-source-name > "
                + "random-temp-file-name && tr '[:lower:]' '[:upper:]' "
                + "random-temp-file-name > " + "random-destination-name";

        System.out.println("expected: " + expectedCommandLine + "\n\nactual: " + executableCommandLine);
        Assert.assertEquals("Un-expected output from getCommandLine().",
                expectedCommandLine, executableCommandLine);
    }
}
