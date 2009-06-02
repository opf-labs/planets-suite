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
              return format.getSummaryAndVersion();
	    } else {
              return format.getSummary();
            }
        }
	return "";
    }

    /**
     * @return
     */
    public URI getUri() {
        if( format == null ) return null;
        return format.getTypeURI();
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
            if( format != null && format.getTypeURI() != null && fb != null && fb.getFormat() != null ) {
              return format.getTypeURI().equals( fb.getFormat().getTypeURI() );
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
        if( format != null && format.getTypeURI() != null )
            return format.getTypeURI().hashCode();
        return super.hashCode();
    }
    
}
