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

package eu.planets_project.pp.plato.services.characterisation;

import java.io.Serializable;

import eu.planets_project.pp.plato.model.FormatInfo;

public interface FormatIdentificationService extends Serializable {
    /**
     * Attempts to identify the given file.
     *  
     * @param data
     * @param filename
     * @return A {@link FormatInfo} object if the file was identified definitely, else <code>null</code>. 
     */
    FormatInfo detectFormat(byte[] data, String filename)throws Exception;
    
    /**
     * Attempts to identify the given file.
     *  
     * @param data
     * @param filename
     * @return A {@link FormatIdentification} object which contains detailed information about the outcome. 
     */
    FormatIdentification identifyFormat(byte[] data, String filename)throws Exception; 
}
