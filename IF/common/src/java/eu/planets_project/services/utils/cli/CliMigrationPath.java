package eu.planets_project.services.utils.cli;

import java.net.URI;
import java.util.Set;

/**
 * TODO abr forgot to document this class
 */
public class CliMigrationPath {


    private Set<URI> in;
    private Set<URI> out;
    private String tool;

    public CliMigrationPath(Set<URI> froms, Set<URI> tos, String command) {
        in  = froms;
        out = tos;
        tool = command;
    }


    public Set<URI> getIn() {
        return in;
    }

    public Set<URI> getOut() {
        return out;
    }

    public String getTool() {
        return tool;
    }


}
