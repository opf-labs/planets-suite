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

package eu.planets_project.pp.plato.bean;

import java.io.Serializable;

import javax.ejb.Local;

/**
 * @see BooleanCapsule
 * @author cbu
 */
@Local
public interface IBooleanCapsule extends Serializable {

    public boolean isBool();
    public void setBool(boolean bool);

    public void destroy();
}
