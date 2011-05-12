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

import org.jboss.annotation.ejb.Local;

import eu.planets_project.pp.plato.application.ErrorClass;
import eu.planets_project.pp.plato.application.NewsClass;


@Local
public interface IMessages {

    public List<ErrorClass> getErrors();
    public List<NewsClass> getNews();

    public void addErrorMessage(ErrorClass error);
    public void addNewsMessage(NewsClass news);
    
    public void clearErrors();
    public void clearNews();
    public void destroy();
}
