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
/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;

import eu.planets_project.ifr.core.techreg.formats.Format;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class FormatBean implements Comparable<FormatBean> {

    private Format format;
    private boolean selected = false;
    private boolean enabled = true;

    /**
     * @param format
     */
    public FormatBean( Format format ) {
        this.format = format;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }
    
    /**
     * @return
     */
    public String getSummary() {
        if( format != null ) {
            if( format.getVersion() != null ) {
              return format.getSummary() + " " + format.getVersion();
	    } else {
              return format.getSummary();
            }
        }
	return "";
    }
    
    /**
     * @return
     */
    public String getBriefName() {
        if( format != null ) {
            String briefname = format.getSummary();
            if( format.getExtensions() != null &&
                    format.getExtensions().size() > 0 ) {
                briefname = format.getExtensions().iterator().next().toUpperCase();
            }
            if( format.getVersion() != null ) {
                briefname += " " + format.getVersion();
            }
            return briefname;
        }
        return "";
    }

    /**
     * @return
     */
    public URI getUri() {
        if( format == null ) return null;
        return format.getUri();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(FormatBean f) {
        if(f == null || f.getSummary() == null ){ 
            return -1; 
        } 
        //        return format.getTypeURI().compareTo( f.getFormat().getTypeURI() );
        if( this.getSummary() != null ) {
            return this.getSummary().compareTo( f.getSummary() );
        } else {
            return this.getUri().compareTo(f.getUri());
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof FormatBean ) {
            FormatBean fb = (FormatBean) obj;
            if( format != null && format.getUri() != null && fb != null && fb.getFormat() != null ) {
              return format.getUri().equals( fb.getFormat().getUri() );
            } else {
              return super.equals(obj);
            }
        } else {
            return super.equals(obj);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if( format != null && format.getUri() != null )
            return format.getUri().hashCode();
        return super.hashCode();
    }
    
}
