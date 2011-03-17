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

import java.util.List;

import javax.ejb.Local;

import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;


/**
 * Documentation can be found in IdentifyRequirementsAction
 */
@Local
public interface IIdentifyRequirements extends IWorkflowStep, INodeValidator {

    public byte[] getFile();
    public void setFile(byte[] file);
    public String upload();

    public void addNode(Object object);
    public void addLeaf(Object object);

    public String remove(Object object);

    public String saveLibrary();
    
    public void downloadTree();
    
    public String saveTemp();
    public void downloadAttachment();
    
    // TEMPLATE-METHODS
    // Save
    public String initTemplates();
    public String initFragments();
    public String selectFragmentForSaving(Object object);
    public String selectTreeForSaving();
    public String saveFragmentHere(Object object);
    
    public String cancelFragmentOperation();
    // Insert
    public String selectInsertionTarget(Object object);
    public String insertThisFragment(Object object);
    // Templates
    public String useTemplate(Object object);

    public void setValidator(ITreeValidator validator);

    public void scaleChanged(Scale v); //TreeNode n,String restriction);

    public void attachFile();
    public void removeAttachedFile();

    
    public void convertToNode(Object leaf);
    public void convertToLeaf(Object node);

    public String startExpert();
}
