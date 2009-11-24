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
package eu.planets_project.tb.api.data.util;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author AnJackson
 * TODO Remove a lot of reproduction of code and logic, c.f. DigitalObjectTreeNode etc etc.
 */
public class DigitalObjectRefBean {
    // A logger:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectRefBean.class);
    
    String name;
    String id;
    URI domUri;
    DigitalObject dob;
    File file;
    
    /**
     * 
     * @param name
     * @param downloadUri
     * @param domUri
     * @param dob
     */
    public DigitalObjectRefBean(String name, String id, URI domUri, DigitalObject dob ) {
        this.name = name;
        this.id = id;
        this.domUri = domUri;
        this.dob = dob;
    }
    
    /**
     * @param name2
     * @param createDownloadUri
     * @param file
     */
    public DigitalObjectRefBean(String name, String id, File file) {
        this.name = name;
        this.id = id;
        this.file = file;
        this.dob = new DigitalObject.Builder( Content.byReference( file ) ).title(name).build();
        //this.dob = new DigitalObject.Builder( Content.byValue( file ) ).title(name).build();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the download
     */
    public URI getDownloadUri() {
        return createDownloadUri(id, "/reader/download.jsp");
    }

    /**
     * @return
     */
    public URI getThumbnailUri() {
        return createDownloadUri(id, "/reader/thumbnail.jsp");
    }
    
    /**
     * 
     * @return
     */
    public URI getDomUri() {
        return domUri;
        
    }
    
    /**
     * 
     * @return
     */
    public DigitalObject getDigitalObject() {
        return dob;
        
    }
   

    /**
     * @return
     */
    public InputStream getContentAsStream() {
        // File Case:
        if( file != null )
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        // Digital Object case:
        if( this.getDigitalObject() != null && this.getDigitalObject().getContent() != null ) 
            return this.getDigitalObject().getContent().read();
        return null;
        
    }

    /**
     * Note that this only uses the extension to determine the mime-type.
     * 
     * @return The mime-type of this entity.
     */
    public String getMimeType() {
        String mimetype = null;
        
        // File Case:
        if( this.file != null ) 
            mimetype = new MimetypesFileTypeMap().getContentType(file);
        
        // Based only on URI:
        if( this.domUri != null ) 
            mimetype = new MimetypesFileTypeMap().getContentType(this.domUri.getPath());
        
        // Digital Object Case:
        if( this.dob != null && dob.getTitle() != null ) 
            mimetype = new MimetypesFileTypeMap().getContentType(dob.getTitle());
        
       return mimetype;
    }

    /**
     * @return 
     */
    public long getSize() {
        // File Case:
        if( this.file != null ) return this.file.length();
        // Digital Object Case:
        if( this.getDigitalObject() == null ) return -1;
        if( this.getDigitalObject().getContent() == null ) return -1;
        if( this.getDigitalObject().getContent().length() > 0  ) {
            return this.getDigitalObject().getContent().length();
        } else {
            try {
                return this.getDigitalObject().getContent().read().available();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    /* -------------------------------------------------------------------------------------------------- */

    private URI createDownloadUri( String id, String prefix ) {        
        // Define the download URI:
        log.debug("Creating the download URL.");
        String context = "/testbed";
        if( FacesContext.getCurrentInstance() != null ) {
            HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            context = req.getContextPath();
        }
        URI download = null;
        try {
            download = new URI( "https", 
                    PlanetsServerConfig.getHostname()+":"+PlanetsServerConfig.getSSLPort(), 
                    context+prefix,"fid="+URLEncoder.encode( id, "UTF-8") , null);
            /* This can be used if the above is causing problems
            download = new URI( null, null, 
                    context+"/reader/download.jsp","fid="+id, null);
                    */
        } catch (URISyntaxException e) {
            e.printStackTrace();
            download = null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            download = null;
        }
        log.debug("Created download URI: "+download);
        return download;
    }
}
