/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
class CommandLine {

    private String command;
    private List<String> parameters;

    CommandLine(String command, List<String> parameters) {
	this.command = command;
	this.parameters = Collections.unmodifiableList(parameters);
    }

    String getCommand() {
	return command;
    }

    /**
     * FIXME! Revisit documentation! Substitution should be handled by a utility class.
     * 
     * Get the command line with the parameter identifiers substituted with the
     * parameters specified by <code>toolParameters</code>, and any tempfiles
     * replaces with their absolute location.
     * 
     * The command line should be ready to feed into the processrunner.
     * 
     * Note that all the temp files must have been initialised with File objects
     * by this time, as the absolute location of these files are replaced into
     * the command line
     * 
     * @param toolParameters
     *            the parameters to the tool
     * @return String containing the processed command line, ready for
     *         execution.
     * @throws MigrationException
     *             if not all necessary parameters, or temporary files were
     *             defined in order to substitute all the identifiers in the
     *             command line.
     */
//    ArrayList<String> getParameters(List<Parameter> parametersToSubstitute) {
//	return null;
//    }

    /**
     * FIXME! Revisit documentation....
     * 
     * Get all the parameters that must be initialised in order to execute the
     * command line. The returned <code>Parameter</code> instances have no value
     * specified, thus, their values must be initialised prior calling the
     * {@link #getCommandLine} method to obtain the actual command line to
     * execute.
     * 
     * @return <code>Collection</code> containing an <code>Parameter</code>
     *         instance for each parameter that must be specified in order to
     *         execute the command line of this migration path.
     */
    List<String> getToolParameters() {
	return parameters;
    }

}
