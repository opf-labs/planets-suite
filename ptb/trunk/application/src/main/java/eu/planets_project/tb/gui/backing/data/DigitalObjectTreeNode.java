/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.XcdlCorpusDigitalObjectManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.List;


import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

/**
 * 
 * URGENT Cache the DigitalObjectRefBean instead of the DOB explicitly?
 * URGENT Push the property lookups (and others) down to the RefBean instead of here?
 * 
 * @author AnJackson
 *
 */
public class DigitalObjectTreeNode extends TreeNodeBase implements java.io.Serializable {
    static final long serialVersionUID = 82362318283823293l;
    
    static private Log log = LogFactory.getLog(DigitalObjectTreeNode.class);
    
    private DataRegistry dataReg;
    private DigitalObject dob_cache = null;
    private URI uri;
    private String leafname;
    private String owner;
    private String dateAdded;
    private String dateModified;
    private boolean selectable;
    private boolean expanded = false;
    
    private DataHandler dh = DataHandlerImpl.findDataHandler();
    
    /**
     * Constructor based on Digital Object:
     */
    public DigitalObjectTreeNode( URI uri, DataRegistry dataReg ) {
        log.debug("Creating bean for Digital Object at: " + uri);
        this.setUri(uri);
        this.dataReg = dataReg;
        this.setType("file");
        this.setLeaf(true);
        this.setSelectable(true);
    }
    
    public DigitalObjectTreeNode( URI uri ) {
        this.setUri(uri);
        this.dataReg = null;
        this.setType("folder");
        this.setLeaf(false);
        this.setSelectable(false);
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
        if( dataReg == null ) return null;
        if( dob_cache == null ) {
            try {
            	log.info("Looking for Digital Object at " + this.getUri());
                this.dob_cache = this.dataReg.retrieve(getUri());
            } catch (DigitalObjectNotFoundException e) {
                log.error("Could not locate DOB: " + this.getUri());
                return null;
            }
        }
        return dob_cache;
    }

    /**
     * @return a TB download URI:
     */
    public String getDownloadUri() {
        if( this.getUri() == null ) return null;
        try {
            URI duri = dh.get(this.getUri().toString()).getDownloadUri();
            log.debug("Returning download location: "+duri);
            if( duri == null ) return null;
            return duri.toASCIIString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
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
    public String getUriString() { 
        if( uri == null ) return null;
        return uri.toASCIIString();
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
    public long getSize() {
        if( dataReg == null ) return -1;

        DigitalObject dob = this.getDob();
        if( dob == null ) return -1;
        if( dob.getContent() == null ) return -1;
        return dob.getContent().length();
    }
    
    /**
     * Look for properties attached to this DOB.
     * @return Any properties that this framework understands. NULL if there are none.
     */
    public List<Property> getProperties() {
        if( XcdlCorpusDigitalObjectManagerImpl.hasXcdlPropertied( this.getDob() ) ) {
            return XcdlCorpusDigitalObjectManagerImpl.getXcdlProperties(this.getDob());
        }
        return null;
    }
    
    /**
     * @return
     */
    public String getMimeType() {
        String mimetype = null;
        
        // Based only on URI:
        if( getUri() != null ) 
            mimetype =  new MimetypesFileTypeMap().getContentType(getUri().getPath());

        // Return this if it worked.
        if( mimetype != null ) return mimetype;
        
        // Otherwise, inspect content of the Digital Object: Title:
        if( getDob() != null && getDob().getTitle() != null ) 
            mimetype = new MimetypesFileTypeMap().getContentType(getDob().getTitle());
        
       return mimetype;
    }
    
    /**
     * @return true if this entity can be displayed as a thumbnail.
     */
    public boolean isThumbnailable() {
        if( 
                "image/jpeg".equals(this.getMimeType()) ||
                "image/gif".equals(this.getMimeType()) ||
                "image/png".equals(this.getMimeType()) 
                ) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public String getThumbnailUri() {
        try {
            String duri = dh.get(this.getUri().toString()).getThumbnailUri().toASCIIString();
            log.debug("Returning thumbnail location: "+duri);
            return duri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
     
    /**
     * @return the directory
     */
    public boolean isDirectory() {
        return (dataReg == null);
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
        log.debug("Setting 'Selected' to: "+selected);
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
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
        DigitalObjectTreeNode other = (DigitalObjectTreeNode) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }
    
}
