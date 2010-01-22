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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.impl.DataRegistryImpl;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;
import eu.planets_project.tb.impl.services.wrappers.ComparePropertiesWrapper;
import eu.planets_project.tb.impl.services.wrappers.CompareWrapper;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectCompare {
    
    static private Log log = LogFactory.getLog(DigitalObjectCompare.class);
    
    // The data sources are managed here:
    DataRegistry dataRegistry = DataRegistryImpl.getInstance();
    
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
        dobUri1 = DigitalObjectInspector.fixBadEncoding(dobUri1);
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
        dobUri2 = DigitalObjectInspector.fixBadEncoding(dobUri2);
        this.dobUri2 = dobUri2;
    }
    
    /**
     * @param dobUri
     * @return
     */
    static public DigitalObjectTreeNode lookupDob( String dobUri ) {
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
        DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, DataRegistryImpl.getInstance() );
        return itemNode;
    }
    
    public DigitalObjectTreeNode getDob1() {
        return lookupDob(this.dobUri1);
    }
    
    public DigitalObjectTreeNode getDob2() {
        return lookupDob(this.dobUri2);
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
    private String characteriseServiceException;
    private String characteriseServiceStackTrace;

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
            this.characteriseServiceException = null;
            this.characteriseServiceStackTrace = null;
            return cr;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            this.characteriseServiceException = e.toString();
            this.characteriseServiceStackTrace = this.stackTraceToString(e);
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
    
    /**
     * @return the characteriseServiceException
     */
    public String getCharacteriseServiceException() {
        return characteriseServiceException;
    }

    /**
     * @return the characteriseServiceStackTrace
     */
    public String getCharacteriseServiceStackTrace() {
        return characteriseServiceStackTrace;
    }

    /** */
    private String compareService;
    private String compareServiceException;
    private String compareServiceStackTrace;

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
            this.compareServiceException = null;
            this.compareServiceStackTrace = null;
            return cr;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            this.compareServiceException = e.toString();
            this.compareServiceStackTrace = this.stackTraceToString(e);
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

    /**
     * @return the compareServiceException
     */
    public String getCompareServiceException() {
        return compareServiceException;
    }

    /**
     * @return the compareServiceStackTrace
     */
    public String getCompareServiceStackTrace() {
        return compareServiceStackTrace;
    }

    /** */
    private String compareExtractedService;
    private String compareExtractedServiceException;
    private String compareExtractedServiceStackTrace;

    /**
     * @return the compareExtractedService
     */
    public String getCompareExtractedService() {
        return compareExtractedService;
    }

    /**
     * @param compareExtractedService the compareExtractedService to set
     */
    public void setCompareExtractedService(String compareExtractedService) {
        this.compareExtractedService = compareExtractedService;
    }
    
    /**
     * @return
     */
    public List<SelectItem> getComparePropertiesServiceList() {
        log.info("IN: getComparePropertiesServiceList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
/*
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
*/
        List<ServiceDescription> sdl = sb.getComparePropertiesServices();

        return ServiceBrowser.mapServicesToSelectList( sdl );
    }
    
    protected String stackTraceToString( Exception e ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    private CompareResult runComparePropertiesService() {
        if( this.compareExtractedService == null ) return null;
        try {
            CompareProperties chr = new ComparePropertiesWrapper(new URL(this.getCompareExtractedService()));
            CompareResult cr = chr.compare( this.runCharacteriseService(getDob1()), this.runCharacteriseService(getDob2()), null);
            this.compareExtractedServiceException = null;
            this.compareExtractedServiceStackTrace = null;
            return cr;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            this.compareExtractedServiceException = e.toString();
            this.compareExtractedServiceStackTrace = this.stackTraceToString(e);
            return null;
        }
     }
    /**
     * @return
     */
    public List<Property> getCompareExtractedProperties() {
        CompareResult cr = this.runComparePropertiesService();
        if( cr == null ) return null;
        if( cr.getProperties() != null ) {
            log.info("Got properties: "+cr.getProperties().size());
        }
        return cr.getProperties();
    }
    
    /**
     * @return
     */
    public String getCompareExtractedServiceReport() {
        CompareResult cr = this.runComparePropertiesService();
        if( cr == null ) return null;
        return ""+cr.getReport();
    }

    /**
     * @return the compareExtractedServiceException
     */
    public String getCompareExtractedServiceException() {
        return compareExtractedServiceException;
    }

    /**
     * @return the compareExtractedServiceStackTrace
     */
    public String getCompareExtractedServiceStackTrace() {
        return compareExtractedServiceStackTrace;
    }

}
