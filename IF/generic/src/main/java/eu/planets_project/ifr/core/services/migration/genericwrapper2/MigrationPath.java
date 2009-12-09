package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Public accessible interface for migration paths.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public interface MigrationPath {

    /**
     * Get the input profile for the migration tool which contains information
     * about how the object to be migrated should be passed on to the tool.
     * 
     * @return <code>ToolIOProfile</code> instance containing the input profile.
     */
    ToolIOProfile getToolInputProfile();

    /**
     * Get the output profile for the migration tool which contains information
     * about how to get the migration output back from the tool and how it
     * should be delivered to the should be passed on to the tool.
     * 
     * @return <code>ToolIOProfile</code> instance containing the input profile.
     */
    ToolIOProfile getToolOutputProfile();

    /**
     * FIXME! Revisit documentation!
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
    CommandLine getCommandLine();

    /**
     * Get the destination format <code>URI</code> of this migration path.
     * 
     * @return <code>URI</code> identifying the destination format of this
     *         migration path.
     */
    URI getDestinationFormat();

    /**
     * Get the source format <code>URI</code> of this migration path.
     * 
     * @return <code>URI</code> identifying the source format of this migration
     *         path.
     */
    URI getSourceFormat();

    /**
     * Get a map defining the relationship between the identifiers in the
     * command line that should be substituted with file names of temporary
     * files with the actual names of these. However, not all labels (keys in
     * the map) are guaranteed to be associated with a file name, thus the
     * caller of this method will have to add these mappings before passing them
     * on to the {@link getCommandLine} method.
     * 
     * @return a map containing a paring of temp. file labels and optionally a
     *         file name.
     */
    Map<String, String> getTempFileDeclarations();

    /**
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
    Collection<Parameter> getToolParameters();

    /**
     * Get the presets for this <code>MigrationPathImpl</code> instance.
     * 
     * @return <code>ToolPresets</code> instance describing various parameter
     *         presets that can be applied with the tool used by this migration
     *         path.
     */
    public ToolPresets getToolPresets();

    // TODO: Consider killing this method. It makes this interface
    // swiss-armyknifish
    /*
     * FIXME! KILL, KILL. KILL it would be more suitable to put this in the
     * generic wrapper class.
     * eu.planets_project.services.datatypes.MigrationPath getAsPlanetsPath();
     */

}