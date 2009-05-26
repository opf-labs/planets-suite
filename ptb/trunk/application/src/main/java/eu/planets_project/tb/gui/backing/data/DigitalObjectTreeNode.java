/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectTreeNode extends TreeNodeBase implements java.io.Serializable {
    static final long serialVersionUID = 82362318283823293l;
    
    static private PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectTreeNode.class);
    
    private DigitalObject dob;
    private URI uri;
    private String leafname;
    private String owner;
    private String size;
    private String dateAdded;
    private String dateModified;
    private boolean selectable;
    private boolean expanded = false;
    
    private DataHandler dh = DataHandlerImpl.findDataHandler();
    
    /**
     * Constructor based on Digital Object:
     */
    public DigitalObjectTreeNode( URI uri, DigitalObject dob ) {
        log.info("Creating bean for Digital Object at: "+uri);
        this.setUri(uri);
        this.dob = dob;
        this.setType("file");
        this.setLeaf(true);
        this.setSelectable(true);
        DigitalObjectContent con = dob.getContent();
        this.size = ""+con.length();
    }
    
    public DigitalObjectTreeNode( URI uri ) {
        this.setUri(uri);
        this.dob = null;
        this.setType("folder");
        this.setLeaf(false);
        this.setSelectable(false);
        this.size = "-";
    }

    public DigitalObjectTreeNode() {
        this.uri = null;
    }

    /** */
    private void setUri( URI uri ) {
        this.uri = uri;
        if( this.uri != null ) {
            this.leafname = uri.getPath();
            if( this.leafname != null ) {
                String[] parts = this.leafname.split("/");
                if( parts != null && parts.length > 0 )
                    this.leafname = parts[parts.length-1];
            }
        }
        else {
            this.leafname = "/";
        }
    }
    
    /**
     * @return the dob
     */
    public DigitalObject getDob() {
        return dob;
    }

    /**
     * @return a TB download URI:
     */
    public URI getDownloadUri() {
        try {
            return dh.get(this.getUri().toString()).getDownloadUri();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the underlying URI
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return
     */
    public List<DigitalObjectTreeNode> getParents() {
        DigitalObjectBrowser db = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        return db.getBreadcrumb(this.getUri());
    }
    
    /**
     * @return the leafname
     */
    public String getLeafname() {
        return this.leafname;
    }
    
    /**
     * @param string
     */
    protected void setLeafname(String leafname) {
        this.leafname = leafname;
    }
 
    /**
     * @return the size of the object.
     */
    public String getSize() {
        return size;
    }
    
    /**
     * @return the directory
     */
    public boolean isDirectory() {
        return (dob == null);
    }
    
    /**
     * @return the selected
     */
    public boolean isSelected() {
        DigitalObjectBrowser db = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        if( db.getSelectedUris().contains(this.getUri()) ) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        DigitalObjectBrowser db = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        if( selected == true ) {
            db.addToSelection(this.getUri());
        } else {
            db.removeFromSelection(this.getUri());
        }
        log.info("Setting 'Selected' to: "+selected);
        db.setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
    }

    /**
     * 
     */
    public void deselectThis() {
        this.setSelected(false);
    }
    
    /**
     * 
     */
    public void selectThis() {
        this.setSelected(true);
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

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dob == null) ? 0 : dob.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DigitalObjectTreeNode other = (DigitalObjectTreeNode) obj;
        if (dob == null) {
            if (other.dob != null)
                return false;
        } else if (!dob.equals(other.dob))
            return false;
        return true;
    }

    
}
