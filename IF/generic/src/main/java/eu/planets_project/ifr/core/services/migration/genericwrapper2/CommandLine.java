package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.Collections;
import java.util.List;

/**
 * Data carrier for a command (line) and its parameters.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class CommandLine {

    /**
     * An unprocessed command line string which may contain labels to be
     * substituted with parameter values.
     */
    private String command;

    /**
     * A list of parameters (i.e. labels) applied in the command line string.
     * That is, the labels that must be replaced with proper values in order to
     * make the command line string executable.
     */
    private List<String> parameters;

    /**
     * Create a <code>CommandLine</code> instance holding a command line string
     * which may contain labels which must be substituted with proper values in
     * order to produce an executable command line.
     * 
     * @param command
     *            a command line string which may contain labels.
     * @param parameters
     *            a list of parameters (i.e. labels) which must be searched for
     *            in the command line string and replaced with proper values in
     *            order to make i executable.
     */
    CommandLine(String command, List<String> parameters) {
	this.command = command;
	this.parameters = Collections.unmodifiableList(parameters);
    }

    /**
     * Get the unprocessed command line string.
     * 
     * @return the command line string.
     */
    String getCommand() {
	return new String(command);
    }

    /**
     * Get all the parameters (i.e. labels) used in the command line string.
     * These labels must be located in the command line string and replaced with
     * proper values in order to make it executable.
     * <p/>
     * <p/>
     * The returned <code>Parameter</code> instances have no value specified
     * they merely serve as a check list of parameters in the command line
     * string which must be replaced with proper values.
     * 
     * @return <code>List</code> containing an <code>Parameter</code> instance
     *         for each parameter that must be defined in order be able to
     *         produce an executable command line from the string returned by
     *         <code>{@link getCommand()} .
     */
    List<String> getParameters() {
	return parameters;
    }
}
