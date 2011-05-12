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

@Local
public interface IAdmin {
    public void throwException();
    public void clearErrors();
    public void clearNews();
    public void destroy();
    public void addNews();
    public String clearData();
    public String clearAllData();
    public String clearKB();
    public String clearDataUsingProjectID();
    public String unlockAll();
    public String unlockUsingProjectID();
    public String resetPublicLibraries();
    public String deleteUserLibraries();
    public boolean check();

    public String exportAllProjectsToZip();
    
    public String exportPrivateTemplates();
    public String exportAllTemplates();
    public String cleanupValues();
    public String refresh();
    
    public void munchMem(int mb);
    public void releaseMem();    
}
