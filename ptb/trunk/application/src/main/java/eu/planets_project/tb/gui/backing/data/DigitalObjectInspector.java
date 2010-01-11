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

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectInspector {
    
    static private Log log = LogFactory.getLog(DigitalObjectInspector.class);
    
    // The data sources are managed here:
    DigitalObjectMultiManager dsm = new DigitalObjectMultiManager();
    
    private String dobUri;

    /**
     * @return the dobUri
     */
    public String getDobUri() {
        return dobUri;
    }

    /**
     * @param dobUri the dobUri to set
     */
    public void setDobUri(String dobUri) {
        this.dobUri = dobUri;
    }
    
    public DigitalObjectTreeNode getDob() {
        if( this.dobUri == null ) return null;
        // Create as a URI:
        URI item;
        try {
            item = new URI(this.dobUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        // Lookup and return:
        DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, dsm );
        return itemNode;
    }
    
    /* -------------------- Additional code for deeper inspection --------------------- */
    
    /**
     * 
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

    /**
     * @return
     */
    public List<Property> getCharacteriseProperties() {
        log.info("Looking for properties using: "+this.getCharacteriseService());
        // Return nothing if no service is selected:
        if( this.getCharacteriseService() == null ) return null;
        // Run the service:
        try {
            Characterise chr = new CharacteriseWrapper(new URL(this.getCharacteriseService()));
            DigitalObject dob = this.getDob().getDob();
            log.info("Got digital object: "+dob);
            CharacteriseResult cr = chr.characterise( dob, null);
            log.info("Got report: "+cr.getReport());
            if( cr.getProperties() != null ) {
                log.info("Got properties: "+cr.getProperties().size());
            }
            return cr.getProperties();
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            e.printStackTrace();
            return null;
        }
    }
    

}
