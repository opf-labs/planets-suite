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
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;
import eu.planets_project.tb.impl.services.wrappers.CompareWrapper;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectCompare {
    
    static private Log log = LogFactory.getLog(DigitalObjectCompare.class);
    
    // The data sources are managed here:
    DigitalObjectMultiManager dsm = new DigitalObjectMultiManager();
    
    private String dobUri1;
    private String dobUri2;


    /**
     * @return the dob1
     */
    public String getDobUri1() {
        return dobUri1;
    }

    /**
     * @param dobUri the dobUri to set
     */
    public void setDobUri1(String dobUri1) {
        this.dobUri1 = dobUri1;
    }
    
    /**
     * @return the dob1
     */
    public String getDobUri2() {
        return dobUri2;
    }

    /**
     * @param dobUri the dobUri to set
     */
    public void setDobUri2(String dobUri2) {
        this.dobUri2 = dobUri2;
    }
    
    protected DigitalObjectTreeNode lookupDob( String dobUri ) {
        if( dobUri == null ) return null;
        // Create as a URI:
        URI item;
        try {
            item = new URI(dobUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        // Lookup and return:
        DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, dsm );
        return itemNode;
    }
    
    public DigitalObjectTreeNode getDob1() {
        return this.lookupDob(this.dobUri1);
    }
    
    public DigitalObjectTreeNode getDob2() {
        return this.lookupDob(this.dobUri2);
    }
    
    /* -------------------- Additional code for deeper inspection --------------------- */
    
    /**
     * @return
     */
    public List<SelectItem> getCharacteriseServiceList() {
        log.info("IN: getCharacteriseServiceList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
/*
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
*/
        List<ServiceDescription> sdl = sb.getCharacteriseServices();

        return ServiceBrowser.mapServicesToSelectList( sdl );
    }

    /** */
    private String characteriseService;

    /**
     * @return the characteriseService
     */
    public String getCharacteriseService() {
        return characteriseService;
    }

    /**
     * @param characteriseService the characteriseService to set
     */
    public void setCharacteriseService(String characteriseService) {
        this.characteriseService = characteriseService;
    }
    
    private CharacteriseResult runCharacteriseService( DigitalObjectTreeNode dob ) {
        log.info("Looking for properties using: "+this.getCharacteriseService());
        // Return nothing if no service is selected:
        if( this.getCharacteriseService() == null ) return null;
        // Run the service:
        try {
            Characterise chr = new CharacteriseWrapper(new URL(this.getCharacteriseService()));
            CharacteriseResult cr = chr.characterise( dob.getDob(), null);
            return cr;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return
     */
    public List<Property> getCharacterise1Properties() {
        CharacteriseResult cr = this.runCharacteriseService(this.getDob1());
        if( cr == null ) return null;
        if( cr.getProperties() != null ) {
            log.info("Got properties: "+cr.getProperties().size());
        }
        return cr.getProperties();
    }
    
    /**
     * @return
     */
    public String getCharacterise2ServiceReport() {
        CharacteriseResult cr = this.runCharacteriseService(this.getDob2());
        if( cr == null ) return null;
        return ""+cr.getReport();
    }

    /**
     * @return
     */
    public List<Property> getCharacterise2Properties() {
        CharacteriseResult cr = this.runCharacteriseService(this.getDob2());
        if( cr == null ) return null;
        if( cr.getProperties() != null ) {
            log.info("Got properties: "+cr.getProperties().size());
        }
        return cr.getProperties();
    }
    
    /**
     * @return
     */
    public String getCharacterise1ServiceReport() {
        CharacteriseResult cr = this.runCharacteriseService(this.getDob1());
        if( cr == null ) return null;
        return ""+cr.getReport();
    }

    /**
     * @return
     */
    public List<SelectItem> getCompareServiceList() {
        log.info("IN: getCompareServiceList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
/*
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
*/
        List<ServiceDescription> sdl = sb.getCompareServices();

        return ServiceBrowser.mapServicesToSelectList( sdl );
    }

    /** */
    private String compareService;

    /**
     * @return the compareService
     */
    public String getCompareService() {
        return compareService;
    }

    /**
     * @param compareService the compareService to set
     */
    public void setCompareService(String compareService) {
        this.compareService = compareService;
    }
    
    private CompareResult runCompareService() {
        log.info("Looking for properties using: "+this.getCompareService());
        // Return nothing if no service is selected:
        if( this.getCompareService() == null ) return null;
        // Run the service:
        try {
            Compare chr = new CompareWrapper(new URL(this.getCompareService()));
            CompareResult cr = chr.compare( this.getDob1().getDob(), this.getDob2().getDob(), null);
            return cr;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return
     */
    public List<Property> getCompareProperties() {
        CompareResult cr = this.runCompareService();
        if( cr == null ) return null;
        if( cr.getProperties() != null ) {
            log.info("Got properties: "+cr.getProperties().size());
        }
        return cr.getProperties();
    }
    
    /**
     * @return
     */
    public String getCompareServiceReport() {
        CompareResult cr = this.runCompareService();
        if( cr == null ) return null;
        return ""+cr.getReport();
    }
}
