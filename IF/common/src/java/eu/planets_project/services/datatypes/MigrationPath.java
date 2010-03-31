/**
 *
 */
package eu.planets_project.services.datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.PlanetsServices;

/**
 * Simple class to build path matrices from. Contains the input and outputs of
 * the path, and allows for parameters for that mapping.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlType(name = "path", namespace = PlanetsServices.SERVICES_NS)
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class MigrationPath {
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
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private MigrationPath() {
        
        // Make sure that parameters never is null.
        parameters = new ArrayList<Parameter>();
    }

    /**
     * Parameterised constructor.
     * 
     * @param in
     *            The input format
     * @param out
     *            The output format
     * @param pars
     *            The parameters
     */
    public MigrationPath(URI in, URI out, List<Parameter> pars) {
        this.inputFormat = in;
        this.outputFormat = out;

        // Make sure that parameters never is null.
        if (pars != null) {
            this.parameters = pars;
        } else {
            parameters = new ArrayList<Parameter>();
        }
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
     * @return A copy of the parameters
     */
    public List<Parameter> getParameters() {
        return new ArrayList<Parameter>(parameters);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return inputFormat + " -> " + outputFormat + "  Parameters: "
                + parameters;
    }

    /**
     * Construct an array of migration paths, linking all the formats in input
     * formats to all the formats in output formats. If either is null or empty,
     * the array will be length 0. All migrationPaths will be with null
     * parameters.
     * 
     * @param inputFormats
     *            The allowed input formats
     * @param outputFormats
     *            The allowed output formats
     * @return An array of all the paths.
     */
    public static List<MigrationPath> constructPaths(Set<URI> inputFormats,
            Set<URI> outputFormats) {
        List<MigrationPath> paths = null;
        if (inputFormats == null || outputFormats == null) {
            paths = new ArrayList<MigrationPath>(); // empty list
        } else {
            paths = new ArrayList<MigrationPath>(inputFormats.size()
                    * outputFormats.size());
            for (URI in : inputFormats) {
                for (URI out : outputFormats) {
                    if (in != null && out != null) {
                        paths.add(new MigrationPath(in, out, null));
                    }
                }
            }
        }
        return paths;
    }

    /**
     * Construct an array of migrationpaths, linking all the formats in
     * inputformas to all the formats in outputformats. If either is null or
     * empty, the array will be length 0. All migrationPaths will be with null
     * parameters.
     * 
     * @param inputformats
     *            The allowed inputformats
     * @param outputFormats
     *            The allowed outputformats
     * @param params
     *            the parameters for this migration path.
     * @return An array of all the paths.
     */
    public static List<MigrationPath> constructPathsWithParams(
            Set<URI> inputformats, Set<URI> outputFormats,
            List<Parameter> params) {
        if (inputformats == null || outputFormats == null) {
            return new ArrayList<MigrationPath>();
        }

        if (params == null) {
            return constructPaths(inputformats, outputFormats);
        }

        if (params.size() > 0) {
            List<MigrationPath> paths = new ArrayList<MigrationPath>(
                    inputformats.size() * outputFormats.size());
            for (URI in : inputformats) {
                for (URI out : outputFormats) {
                    paths.add(new MigrationPath(in, out, params));
                }
            }
            return paths;
        } else {
            return constructPaths(inputformats, outputFormats);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((inputFormat == null) ? 0 : inputFormat.hashCode());
        result = prime * result
                + ((outputFormat == null) ? 0 : outputFormat.hashCode());
        result = prime * result
                + ((parameters == null) ? 0 : parameters.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MigrationPath other = (MigrationPath) obj;
        if (inputFormat == null) {
            if (other.inputFormat != null)
                return false;
        } else if (!inputFormat.equals(other.inputFormat))
            return false;
        if (outputFormat == null) {
            if (other.outputFormat != null)
                return false;
        } else if (!outputFormat.equals(other.outputFormat))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        return true;
    }
}