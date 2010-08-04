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

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.SESSION)
@Name("resultTreeHelper")
public class ResultTreeHelperBean implements Serializable {

    private static final long serialVersionUID = 686300796774311162L;

    
    private final static Logger log = Logger.getLogger(ResultTreeHelperBean.class);


    @Out
    private BooleanCapsule showAllAlternatives = new BooleanCapsule(false);


    public String switchDisplayAllAlternatives() {
        this.showAllAlternatives.setBool(!this.showAllAlternatives.isBool());
        return null;
    }

   

}
