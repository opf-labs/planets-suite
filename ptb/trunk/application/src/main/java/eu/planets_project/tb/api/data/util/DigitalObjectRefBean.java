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
import java.net.URI;

import javax.activation.MimetypesFileTypeMap;

import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectRefBean {
    String name;
    URI download;
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
    public DigitalObjectRefBean(String name, URI downloadUri, URI domUri, DigitalObject dob ) {
        this.name = name;
        this.download = downloadUri;
        this.domUri = domUri;
        this.dob = dob;
    }
    
    /**
     * @param name2
     * @param createDownloadUri
     * @param file
     */
    public DigitalObjectRefBean(String name, URI downloadUri, File file) {
        this.name = name;
        this.download = downloadUri;
        this.file = file;
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
        return download;
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
        
        // Digital Object Case:
        if( this.dob != null ) 
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
   
}
