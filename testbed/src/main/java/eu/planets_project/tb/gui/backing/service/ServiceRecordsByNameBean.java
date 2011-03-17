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
package eu.planets_project.tb.gui.backing.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author AnJackson
 *
 */
public class ServiceRecordsByNameBean {
    
    String name;
    
    List<ServiceRecordBean> srbs = new ArrayList<ServiceRecordBean>();
    
    HashSet<Long> experimentIds = new HashSet<Long>();

    /**
     * @param srb
     */
    public ServiceRecordsByNameBean(ServiceRecordBean srb) {
        this.addServiceRecord(srb);
        this.name = srb.getName();
    }

    /**
     * @param srb
     */
    public void addServiceRecord(ServiceRecordBean srb) {
        // Add this SRB to the set filed under this name.
        srbs.add(srb);
        // Add any experiment Ids to the list:
        if( srb.getServiceRecord() != null ) {
            for( Long eid: srb.getServiceRecord().getExperimentIds() ) {
                experimentIds.add(eid);
            }
        }
    }

    /**
     * @return
     */
    public ServiceRecordBean getNameRecord() {
        // This should get an 'active' one if possible.
        for( ServiceRecordBean sr : srbs ) {
            if( sr.isActive() ) {
                return sr;
            }
        }
        // Otherwise, return the one at the end of the list.
        return srbs.get(srbs.size()-1);
    }

    /**
     * @return
     */
    public int getNumberOfExperiments() {
        return experimentIds.size();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
