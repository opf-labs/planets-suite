/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.utils.cli;

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
     * @return Not yet implemented
     */
    public boolean useTempSourceFile() {
	// TODO Auto-generated method stub
	return false;
    }


    /**
     * @return Not yet implemented
     */
    public boolean useTempDestinationFile() {
	// TODO Auto-generated method stub
	return false;
    }


    /**
     * @param toolParameters
     * @return The command line
     */
    public String getConmmandLine(List<Parameter> toolParameters) {
	// TODO Auto-generated method stub
	return "ls -la";
    }
}
