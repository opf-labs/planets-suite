/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/*
 * DigitalObjectTypesImpl.java
 *
 * Created on 12 December 2007, 16:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.planets_project.tb.impl.model.finals;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Brian Aitken
 */
public class DigitalObjectTypesImpl implements
		eu.planets_project.tb.api.model.finals.DigitalObjectTypes{
    
    private List<String[]> dTypes = new ArrayList<String[]>();
    
    
    /** Creates a new instance of DigitalObjectTypesImpl */
    public DigitalObjectTypesImpl() {
        String[] type = {"d1","Image"};
        dTypes.add(type);
        
        String[] type2 = {"d2","Audio"};
        dTypes.add(type2);
        
        String[] type3 = {"d3","Text"};
        dTypes.add(type3);
        
        String[] type4 = {"d4","Video"};
        dTypes.add(type4);
        
        String[] type5 = {"d5","Database"};
        dTypes.add(type5);
    }

    public List<String[]> getAlLDtypes() {
        
        return this.dTypes;
    }

    public List<String> getAllDtypeIDs() {
        
        List<String> ids = new ArrayList<String>();
        
        for(int i=0;i<dTypes.size();i++)
        {
            String[] id = dTypes.get(i);
            
            ids.add(id[0]);
            
        }
        
        return ids;        
    }

    public List<String> getAllDtypeNames() {
        
        List<String> names = new ArrayList<String>();

        for(int i=0;i<dTypes.size();i++)
        {
            String[] name = dTypes.get(i);
            
            names.add(name[1]);
            
        }
        
        return names;  
    }

    public String getDtypeName(String ID) {
        
        String name = null;
        
        int i = 0;
        
        while(name==null){
            
            String[] entry = dTypes.get(i);
            
            if(entry[0].equals(ID)){
                name = entry[1];
            }
            
            i++;
        }
        
        return name;
    }

    public String getDtypeID(String name) {
        
        String id = null;
 
        int i = 0;
        
        while(id==null){
            
            String[] entry = dTypes.get(i);
            
            if(entry[1].equals(name)){
                id = entry[1];
            }
            
            i++;
        }
        
        return id;
    }
    
}
