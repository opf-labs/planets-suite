/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;

import eu.planets_project.ifr.core.techreg.api.formats.Format;

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
        if( format.getVersion() != null )
            return format.getSummaryAndVersion();
        return format.getSummary();
    }

    /**
     * @return
     */
    public URI getUri() {
        return format.getTypeURI();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(FormatBean f) {
//        return format.getTypeURI().compareTo( f.getFormat().getTypeURI() );
        return this.getSummary().compareTo( f.getSummary() );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof FormatBean ) {
            FormatBean fb = (FormatBean) obj;
            return format.getTypeURI().equals( fb.getFormat().getTypeURI() );
        } else {
            return super.equals(obj);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return format.getTypeURI().hashCode();
    }
    
}
