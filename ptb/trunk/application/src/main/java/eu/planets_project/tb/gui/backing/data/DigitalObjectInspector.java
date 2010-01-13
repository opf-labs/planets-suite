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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;
import eu.planets_project.tb.impl.services.wrappers.IdentifyWrapper;

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
     * -Dfile.encoding=UTF8
     * @param dobUri the dobUri to set
     */
    public void setDobUri(String dobUri) {
        dobUri = fixBadEncoding(dobUri);
        this.dobUri = dobUri;
    }
    
    public DigitalObjectTreeNode getDob() {
        if( this.dobUri == null ) return null;
        // Create as a URI:
        URI item = DigitalObjectInspector.uriEncoder(this.dobUri);
        if( item == null ) return null;
        // Lookup and return:
        DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, dsm );
        return itemNode;
    }

    /**
     * TODO Should be able to avoid this hack workaround.
     * How to set the page encoding correctly?
     * <?xml version="1.0" encoding="UTF-8"?>
     * @param in
     * @return
     */
    public static String fixBadEncoding( String in ) { 
        return in;
        /*
        if( in == null ) return in;
        try {
            String out = new String( in.getBytes("ISO-8859-1"), "UTF-8" );
            return out;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return in;
        }
        */
    }
    
    public static URI uriEncoder( String uri ) {
        if( uri == null || uri.length() == 0 ) return null;
        try {
            URI nuri = new URI("planets", uri.replaceFirst("planets:", ""), null);
            return nuri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
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
    /** */
    private String identifyService;

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
    
    private CharacteriseResult runCharacteriseService() {
        log.info("Looking for properties using: "+this.getCharacteriseService());
        // Return nothing if no service is selected:
        if( this.getCharacteriseService() == null ) return null;
        // Run the service:
        try {
            Characterise chr = new CharacteriseWrapper(new URL(this.getCharacteriseService()));
            DigitalObject dob = this.getDob().getDob();
            log.info("Got digital object: "+dob);
            CharacteriseResult cr = chr.characterise( dob, null);
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
    public List<Property> getCharacteriseProperties() {
        CharacteriseResult cr = this.runCharacteriseService();
        if( cr == null ) return null;
        if( cr.getProperties() != null ) {
            log.info("Got properties: "+cr.getProperties().size());
        }
        return cr.getProperties();
    }
    
    /**
     * @return
     */
    public String getCharacteriseServiceReport() {
        CharacteriseResult cr = this.runCharacteriseService();
        if( cr == null ) return null;
        return ""+cr.getReport();
    }

    
    /**
     * @return the identifyService
     */
    public String getIdentifyService() {
        return identifyService;
    }

    /**
     * @param identifyService the identifyService to set
     */
    public void setIdentifyService(String identifyService) {
        this.identifyService = identifyService;
    }
    
    private IdentifyResult runIdentifyService() {
        if( this.getIdentifyService() == null ) return null;
        try {
            Identify id = new IdentifyWrapper(new URL( this.getIdentifyService()));
            IdentifyResult ir = id.identify(this.getDob().getDob(), null);
            return ir;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            e.printStackTrace();
            return null;
        }
    }
    
    public String getIdentifyResult() {
        IdentifyResult ir = this.runIdentifyService();
        if( ir == null ) return null;
        String result = "";
        for( URI type : ir.getTypes() ) {
            result += type+" ";
        }
        return result;
    }
    
    public List<FormatBean> getIdentifyResultList() {
        IdentifyResult ir = this.runIdentifyService();
        if( ir == null ) return null;
        List<FormatBean> fmts = new ArrayList<FormatBean>();
        for( URI type : ir.getTypes() ) {
            FormatBean fb = new FormatBean( ServiceBrowser.fr.getFormatForUri( type ) );
            fmts.add(fb);
        }
        return fmts;
    }
    
    public String getIdentifyServiceReport() {
        IdentifyResult ir = this.runIdentifyService();
        if( ir == null ) return null;
        return ""+ir.getReport();
    }
    

}
