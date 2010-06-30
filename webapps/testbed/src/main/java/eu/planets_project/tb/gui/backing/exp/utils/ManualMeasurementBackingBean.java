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
package eu.planets_project.tb.gui.backing.exp.utils;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.properties.ManuallyMeasuredProperty;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.properties.ManuallyMeasuredPropertyHandlerImpl;

/**
 * @author Andrew.Jackson@bl.uk
 */
public class ManualMeasurementBackingBean {
    static private Log log = LogFactory.getLog(ManualMeasurementBackingBean.class);
    
    private String newManProp;
    private String addManPropName;
    private String addManPropDesc;

    /**
     * @return the newManProp
     */
    public String getNewManProp() {
        return newManProp;
    }
    
    public boolean isManualPropertySelected() {
        if( this.getManualProperty() == null ) return false;
        return true;
    }
    
    public ManuallyMeasuredProperty getManualProperty() {
        for( ManuallyMeasuredProperty mp : getManuallyMeasuredProperties() ) {
            if( mp.getURI().equals( this.newManProp ) ) {
                return mp;
            }
        }
        return null;
    }

    /**
     * @param newManProp the newManProp to set
     */
    public void setNewManProp(String newManProp) {
        this.newManProp = newManProp;
    }

    /**
     * @return the addManPropName
     */
    public String getAddManPropName() {
        return addManPropName;
    }

    /**
     * @param addManPropName the addManPropName to set
     */
    public void setAddManPropName(String addManPropName) {
        this.addManPropName = addManPropName;
    }

    /**
     * @return the addManPropDesc
     */
    public String getAddManPropDesc() {
        return addManPropDesc;
    }

    /**
     * @param addManPropDesc the addManPropDesc to set
     */
    public void setAddManPropDesc(String addManPropDesc) {
        this.addManPropDesc = addManPropDesc;
    }

    /**
     * 
     */
    public void createNewManualMeasurementProperty() {
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        if( this.getAddManPropName() == null || this.getAddManPropName().trim().equals("") ) {
            log.error("Could not create new manual property: No name.");
            return;
        }
        if( this.getAddManPropDesc() == null || this.getAddManPropDesc().trim().equals("") ) {
            log.error("Could not create new manual property: No description.");
            return;
        }
        ManuallyMeasuredProperty mp = ManuallyMeasuredPropertyHandlerImpl.createUserProperty(
                user.getUserid(), this.getAddManPropName(), this.getAddManPropDesc() );
        // And persist it:
        ManuallyMeasuredPropertyHandlerImpl mm = ManuallyMeasuredPropertyHandlerImpl.getInstance();
        mm.addManualUserProperty(user.getUserid(), mp );
        log.info("Created user property: "+mp+" for user: "+user.getUserid());
        this.newManProp = mp.getURI();
        // And clear the fields.
        this.setAddManPropName("");
        this.setAddManPropDesc("");
    }
    
    /**
     * 
     */
    public void removeManualMeasurementProperty() {
        ManuallyMeasuredPropertyHandlerImpl mm = ManuallyMeasuredPropertyHandlerImpl.getInstance();
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        mm.removeManualUserProperty(user.getUserid(), newManProp );
        this.newManProp = null;
    }
    
    /**
     */
    public void updateManualMeasurement() {
        log.info("Updating manual measurement.");
        ExperimentInspector.persistExperiment();
    }

    /**
     * @return
     */
    public List<SelectItem> getAllManualMeasurementProperties() {
        // Build select list:
        List<SelectItem> mpsl = new ArrayList<SelectItem>();
        for( ManuallyMeasuredProperty mp : getManuallyMeasuredProperties() ) {
            mpsl.add(new SelectItem(mp.getURI(), mp.getName()) );
        }
        log.info("Returning "+mpsl.size()+" properties.");
        return mpsl;
    }
    
    public static List<ManuallyMeasuredProperty> getManuallyMeasuredProperties() {
        ManuallyMeasuredPropertyHandlerImpl mm = ManuallyMeasuredPropertyHandlerImpl.getInstance();
        UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
        List<ManuallyMeasuredProperty> mps = mm.loadAllManualProperties(user.getUserid());
        return mps;
    }
    
    public void updateManualMeasurment(){
        //TODO method missing!!
    }

}
