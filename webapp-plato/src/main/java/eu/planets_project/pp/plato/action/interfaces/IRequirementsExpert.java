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

import javax.ejb.Local;

import org.richfaces.event.NodeSelectedEvent;

import eu.planets_project.pp.plato.validators.INodeValidator;


/**
 * Documentation can be found in IdentifyRequirementsAction
 */
@Local
public interface IRequirementsExpert extends IWorkflowStep, INodeValidator {

    public void addNode(Object object);
    public void addLeaf(Object object);

    public void removeNode(Object object);

    public void downloadTree();
    
    public void convertToNode(Object leaf);
    public void convertToLeaf(Object node);

    public void addLibraryRequirement(Object object);
    public void addCriterion(Object object);
    public void removeLibraryNode(Object object);
    
    public void applyMeasuremntInfo();
    public void useLibraryFragment();
    
    public void processLibSelection(NodeSelectedEvent event);
    public void processReqSelection(NodeSelectedEvent event);
    
    public String saveLibrary();
    public String saveRequirements();
}
