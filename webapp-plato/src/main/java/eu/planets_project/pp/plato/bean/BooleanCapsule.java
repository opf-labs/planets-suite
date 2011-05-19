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

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.workflow.SetImportanceFactorsAction;

/**

 * This boolean capsule stores the value of fields like the
 * "balance Weights"  in {@link SetImportanceFactorsAction}.
 * A Boolean Capsule is needed because java.lang.Boolean 
 * is a POJO (the poor thing) and can therefore not be used
 * as a managed-bean. 
 * @author Christoph Becker
 * @see SetImportanceFactorsAction 
 */
@Stateful
@Scope(ScopeType.SESSION)
public class BooleanCapsule implements IBooleanCapsule {
    /**
     * 
     */
    private static final long serialVersionUID = -7044502998249299831L;
 
    /**
     * Boolean Value to store actual value
     */
    private boolean bool;
    
    /**
     * default constructor sets the value to <code>true</code>
     */
    public BooleanCapsule() {
        this(true);
    }
    
    /**
     * sets the value
     * @param bool initialisation value 
     */
    public BooleanCapsule(boolean bool) {
        this.bool = bool;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    @Destroy
    @Remove
    public void destroy() {
    }

}
