/**
 *
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Simple class to build path matrices from. Contains the input and outputs of
 * the path, and allows for parameters for that mapping.
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
    private MigrationPath() {}

    /**
     * Parameterised constructor.
     * @param in The input format
     * @param out The output format
     * @param pars The parameters
     */
    public MigrationPath(URI in, URI out, List<Parameter> pars) {
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
     * @return A copy of the parameters
     */
    public List<Parameter> getParameters() {
        return new ArrayList<Parameter>(parameters);
    }

    /**
     * {@inheritDoc}
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
     * @param inputFormats The allowed input formats
     * @param outputFormats The allowed output formats
     * @return An array of all the paths.
     */
    public static List<MigrationPath> constructPaths(Set<URI> inputFormats,
            Set<URI> outputFormats) {
        if (inputFormats == null || outputFormats == null) {
            return new ArrayList<MigrationPath>();
        } else {
            List<MigrationPath> paths = new ArrayList<MigrationPath>(
                    inputFormats.size() * outputFormats.size());
            for (URI in : inputFormats) {
                for (URI out : outputFormats) {
                    paths.add(new MigrationPath(in, out, null));
                }
            }
            return paths;
        }
    }

    /**
     * Construct an array of migrationpaths, linking all the formats in
     * inputformas to all the formats in outputformats. If either is null or
     * empty, the array will be length 0. All migrationPaths will be with null
     * parameters.
     * @param inputformats The allowed inputformats
     * @param outputFormats The allowed outputformats
     * @param params the parameters for this migration path.
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
}