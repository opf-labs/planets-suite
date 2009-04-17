/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
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
        return srbs.get(0);
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
