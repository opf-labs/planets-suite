/**
 * 
 */
package eu.planets_project.ifr.core.wdt.gui.faces;

import java.net.URI;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wdt.api.data.DigitalObject;

/**
 * @author AnJackson
 *
 */
public class FileTreeNode extends TreeNodeBase implements java.io.Serializable {
    static final long serialVersionUID = 82362318283823293l;
    
    static private PlanetsLogger log = PlanetsLogger.getLogger(FileTreeNode.class);
    
    private DigitalObject dob;
    private String displayName;
    private String owner;
    private String size;
    private String dateAdded;
    private String dateModified;
    private boolean selected;
    private boolean selectable;
    
    /**
     * Constructor based on Digital Object:
     */
    public FileTreeNode( DigitalObject dob ) {
        this.setDob(dob);
    }
    
    /**
     * @return the dob
     */
    public DigitalObject getDob() {
        return dob;
    }
    /**
     * @param dob the dob to set
     */
    public void setDob(DigitalObject dob) {
        this.dob = dob;
        // Pick up configuration from the DO:
        if( this.isDirectory() ) {
            this.setType("folder");
            this.setLeaf(false);
            this.setSelectable(false);
        } else {
            this.setType("file");
            this.setLeaf(true);
            this.setSelectable(true);
        }
        if( dob != null ) this.displayName = dob.getLeafname();
    }
    
    /**
     * @return the underlying URI
     */
    public URI getUri() {
        return dob.getUri();
    }
    
    /**
     * @return the leafname
     */
    public String getLeafname() {
        return dob.getLeafname();
    }
    
    /**
     * @return the directory
     */
    public boolean isDirectory() {
        return dob.isDirectory();
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
     * @return the selectable
     */
    public boolean isSelectable() {
        return selectable;
    }
    
    /**
     * @param selectable the selectable to set
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
 
}
