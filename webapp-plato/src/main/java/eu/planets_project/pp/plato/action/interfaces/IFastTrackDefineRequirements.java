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

import eu.planets_project.pp.plato.validators.INodeValidator;

@Local
public interface IFastTrackDefineRequirements extends IWorkflowStep, INodeValidator {

    public void startFastTrackEvaluation();
    
//    public String upload();

//    public String removeRecord();

//    public void download(Object object);

//    public void identifyFormat(Object object);
//
//    public void changeFormat();
//
//    public void selectFormat();
//
//    public String characteriseJHoveTree(Object object);
//    public String characteriseFits(Object object);
//
//    public void extractObjectProperties();    
//    
//    public void addNode(Object object);
//    public void addLeaf(Object object);
//    
//    public String remove(Object object);    
    
    public void useFastTrackTemplate();
}
