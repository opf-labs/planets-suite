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
import java.util.List;

import eu.planets_project.ifr.core.techreg.formats.Format;

/**
 * @author AnJackson
 *
 */
public class ServiceRecordsByFormatBean {
    
    Format fmt;
    
    List<ServiceRecordBean> asInput = new ArrayList<ServiceRecordBean>();
    List<ServiceRecordBean> asOutput = new ArrayList<ServiceRecordBean>();
    /**
     * @param fmt
     * @param asInput
     * @param asOutput
     */
    public ServiceRecordsByFormatBean( Format fmt ) {
        super();
        this.fmt = fmt;
    }

    /**
     * @param srv
     */
    public void addAsInputService( ServiceRecordBean srv ) {
        this.asInput.add(srv);
    }
    
    /**
     * @param srv
     */
    public void addAsOutputService( ServiceRecordBean srv ) {
        this.asOutput.add(srv);
    }
    
    /**
     * @return the fmt
     */
    public Format getFormat() {
        return fmt;
    }
    
    /**
     * @return the summary with the version number appended.
     */
    public String getSummaryAndVersion() {
        if (fmt.getVersion() != null)
            return fmt.getSummary() + " " + fmt.getVersion();
        return fmt.getSummary();
    }
    
    /**
     * @return the asInput
     */
    public List<ServiceRecordBean> getAsInput() {
        return asInput;
    }
    /**
     * @return the asOutput
     */
    public List<ServiceRecordBean> getAsOutput() {
        return asOutput;
    }
    

    
}
