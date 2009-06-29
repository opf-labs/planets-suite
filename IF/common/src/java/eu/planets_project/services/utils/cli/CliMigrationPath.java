package eu.planets_project.services.utils.cli;

import java.net.URI;
import java.util.Set;

/**
 * Migration paths created from a XML config file.
 * @author Asger Blekinge-Rasmussen
 */
public class CliMigrationPath {


    private Set<URI> in;
    private Set<URI> out;
    private String tool;

    /**
     * @param froms The source formats
     * @param tos The target formats
     * @param command The command
     */
    public CliMigrationPath(Set<URI> froms, Set<URI> tos, String command) {
        in  = froms;
        out = tos;
        tool = command;
    }


    /**
     * @return The input formats
     */
    public Set<URI> getIn() {
        return in;
    }

    /**
     * @return The output formats
     */
    public Set<URI> getOut() {
        return out;
    }

    /**
     * @return The tool
     */
    public String getTool() {
        return tool;
    }
}
