/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.action.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ejb.Local;

import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.tree.TemplateTree;


@Local
public interface IProjectImport {
    public void destroy();

    public int importAllProjectsFromDir(File dir);
    
    /**
     * Reads and returns projects from the file specified by <code>fileName</code>
     */
    public List<Plan> importProjects(String fileName)throws IOException, SAXException;

    /**
     * Reads and returns projects from the given InputStream (required by TestDataLoader)
     */
    public List<Plan> importProjects(InputStream in) throws IOException, SAXException;
    
    public void storeTemplatesInLibrary(byte[] xmlData) throws SAXException, IOException;

    /**
     * Reads and returns templates from the given byte array.
     */
    public List<TemplateTree> importTemplates(byte[] in) throws IOException, SAXException;
    
    public List<String> getAppliedTransformations();
}