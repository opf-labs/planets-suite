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

import eu.planets_project.pp.plato.model.tree.Leaf;

@Local
public interface IEvaluateExperiments extends IWorkflowStep {

    public String select(Object ob);


    public void approve();

    public void setTreeFromRecordAltern(Object record1,Object record2);

    public void evaluateAll();
    public void evaluate(Leaf leaf);
    public void init();
}
