/*
 * DigitalObjectTypes.java
 *
 * Created on 12 December 2007, 15:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.tb.api.model.finals;

import java.util.List;

/**
 *
 * @author Brian Aitken
 */
public interface DigitalObjectTypes {
    
    public List<String[]> getAlLDtypes();
    
    public List<String> getAllDtypeIDs();
    
    public List<String> getAllDtypeNames();
    
    public String getDtypeName(String ID);
    
    public String getDtypeID(String name);
      
}
