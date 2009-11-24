/*
 * DataRegistry.java
 *
 * Created on 02 July 2007, 08:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.api;

import eu.planets_project.ifr.core.storage.api.ObjectReference;
import eu.planets_project.ifr.core.storage.api.Value;

import java.io.File;

public interface DataManagerRemoteInterface {
    
//    File load(ObjectReference path);
    
    String saveFile(String userName, String password, String registryName, String fileRef);

    String saveWorkflowFile(String userName, String password, String workflow, String registryName, String fileRef);

    String saveTaskFile(String userName, String password, String workflow, String task, String registryName, String fileRef);

//    void setProperty(ObjectReference path, String property, Value value);
//    
//    String[] getPropertyNames(ObjectReference path);
//    
//    Value getProperty(ObjectReference path, String property);
//
//    void addXMLProperties(ObjectReference path, String xmlDocument);
//    
//    String getXMLProperties(ObjectReference path);
}
