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

import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryDefinition;

@Local
public interface IDefineAlternatives extends IWorkflowStep {

    public String create();

    public String select();

    public void editAlternative();

    public String removeAlternative();

    public String askRemoveAlternative();
    
    public int getAllowRemove();
    
    public String showPreservationServices(Object registry);

    public List<PreservationActionDefinition> queryRegistry(FormatInfo formatInfo,
            PreservationActionRegistryDefinition registry) 
            throws PlatoServiceException;
    public String createAlternativesForPreservationActions();    
}
