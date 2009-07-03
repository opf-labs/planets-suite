package eu.planets_project.services.migration.dia.impl;

import java.net.URI;
import java.util.List;
import java.util.Set;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Migration paths created from a XML config file.
 * @author Asger Blekinge-Rasmussen
 */
public class CliMigrationPath {


    private Set<URI> in;
    private Set<URI> out;
    private String tool;
    //TODO: Should also contain tool parameters

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


    /**
     * @return
     */
    public boolean useTempSourceFile() {
	// TODO Auto-generated method stub
	return false;
    }


    /**
     * @return
     */
    public boolean useTempDestinationFile() {
	// TODO Auto-generated method stub
	return false;
    }


    /**
     * @param toolParameters
     * @return
     */
    public String getConmmandLine(List<Parameter> toolParameters) {
	// TODO Auto-generated method stub
	return "ls -la";
    }


    /**
     * @return
     */
    public URI getSourceFormat() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @return
     */
    public URI getDestinationFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return "CliMigrationPath: " + in + " -> " + out + " Command: " + tool;
    }
}
