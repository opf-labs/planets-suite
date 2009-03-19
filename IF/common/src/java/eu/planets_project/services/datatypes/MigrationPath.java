/**
 *
 */
package eu.planets_project.services.datatypes;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Simple class to build path matrices from.  
 *
 * Contains the input and outputs of the path, and allows for parameters for that mapping.
 *
 * @author  <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement(name = "path")
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
     * No arg constructor
     */
    protected MigrationPath() { }

    /**
     * Parameterised constructor
     *
     * @param in
     * @param out
     * @param pars
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
    
    @Override
	public String toString() {
    	return inputFormat + " -> " + outputFormat + "  Parameters: " + parameters;
	}

	/**
     * Construct an array of migrationpaths, linking all the formats in
     * inputformas to all the formats in outputformats. If either is null or
     * empty, the array will be length 0. All migrationPaths will be with
     * null parameters.
     * @param inputformats The allowed inputformats
     * @param outputFormats The allowed outputformats
     * @return An array of all the paths.
     */
    public static MigrationPath[] constructPaths(Set<URI> inputformats, Set<URI> outputFormats){
        if (inputformats == null || outputFormats == null) {
            return new MigrationPath[0];
        } else {
            List<MigrationPath> paths = new ArrayList<MigrationPath>(inputformats.size()*outputFormats.size());
            for (URI in: inputformats){
                for (URI out:outputFormats){
                    paths.add(new MigrationPath(in,out,null));
                }
            }
            return paths.toArray(new MigrationPath[0]);
        }
    }
    
    /**
     * Construct an array of migrationpaths, linking all the formats in
     * inputformas to all the formats in outputformats. If either is null or
     * empty, the array will be length 0. All migrationPaths will be with
     * null parameters.
     * @param inputformats The allowed inputformats
     * @param outputFormats The allowed outputformats
     * @param params the parameters for this migration path.
     * @return An array of all the paths.
     */
    public static MigrationPath[] constructPathsWithParams(Set<URI> inputformats, Set<URI> outputFormats, Parameters params){
        if (inputformats == null || outputFormats == null) {
            return new MigrationPath[0];
        }
        
        if(params==null) {
        	return constructPaths(inputformats, outputFormats);
        }
        
        if(params.getParameters().size()>0) {
        	List<MigrationPath> paths = new ArrayList<MigrationPath>(inputformats.size()*outputFormats.size());
            for (URI in: inputformats){
                for (URI out:outputFormats){
                    paths.add(new MigrationPath(in,out,params.getParameters()));
                }
            }
            return paths.toArray(new MigrationPath[0]);
        }
        else {
        	return constructPaths(inputformats, outputFormats);
        }
        
    }
}