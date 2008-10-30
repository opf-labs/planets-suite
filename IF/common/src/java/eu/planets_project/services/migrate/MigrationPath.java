/**
 * 
 */
package eu.planets_project.services.migrate;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Simple class to build path matricies from.  
 * Contains the input and outputs of the path, and allows for parameters for that mapping.
 * 
 * @author  <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class MigrationPath {
    /**
     * A particular input format.
     */
    URI inputFormat;
    /**
     * The output format.
     */
    URI outputFormat;
    /**
     * The parameters that specifically apply to this pathway.
     */
    List<Parameter> parameters;

    /**
     * 
     */
    @SuppressWarnings("unused")
    private MigrationPath() {
    }
    
    /**
     * 
     */
    public MigrationPath(URI in, URI out, List<Parameter> pars ) {
        this.inputFormat = in;
        this.outputFormat = out;
        this.parameters = pars;
    }

    /**
     * @return the inputFormat
     */
    public URI getInputFormat() {
        return inputFormat;
    }

    /**
     * @return the outputFormat
     */
    public URI getOutputFormat() {
        return outputFormat;
    }

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
    
}