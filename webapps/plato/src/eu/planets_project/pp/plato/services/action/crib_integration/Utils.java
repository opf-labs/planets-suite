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
package eu.planets_project.pp.plato.services.action.crib_integration;

import eu.planets_project.pp.plato.model.FormatInfo;

public class Utils {
    /**
     * Uses the PRONOM format-name and version to build a CRiB-Id with pattern: "<format-name>, <version>"
     * 
     * @param formatInfo
     * @return CRiB-Id equivalent to the PRONOM Id  
     */
    public static String makeCRiBId(FormatInfo formatInfo){
        return formatInfo.getName() + ", version " + formatInfo.getVersion();
    }

}
