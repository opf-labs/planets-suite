/**
 * 
 */
package eu.planets_project.services.migrate;

import java.net.URI;
import java.util.List;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Simple class to build path matricies from.  
 * Contains the input and outputs of the path, and allows for parameters for that mapping.
 * 
 * @author  <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class MigrationPath {
    /**
     * A particular input format.
     */
    public URI inputFormat;
    /**
     * The output format.
     */
    public URI outputFormat;
    /**
     * The parameters that specifically apply to this pathway.
     */
    public List<Parameter> parameters;

    /**
     * 
     */
    public MigrationPath() {
    }
}