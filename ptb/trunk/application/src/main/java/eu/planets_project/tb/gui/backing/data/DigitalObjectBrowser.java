/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;


import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.event.DropEvent;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;

/**
 * This class is the backing bean that provides the interface to 
 * the Data Registry, which is currently a mock-up.
 * @author AnJackson
 * 
 */
public class DigitalObjectBrowser {
    // A Log for this:
    private static Log log = LogFactory.getLog(DigitalObjectBrowser.class);
    
    // The Data Registry:
    private DigitalObjectRepositoryLister<DigitalObjectTreeNode> dr = null;
    
    private DigitalObjectTreeNode currentDob;
    
    private List<URI> selectedDobs = new ArrayList<URI>();
    
    protected static final String INSPECTOR_PANEL = "dob_pan_inspector";
    protected static final String SELECTION_PANEL = "dob_pan_selection";
    protected static final String CORPORA_PANEL = "dob_pan_corpora";
    private String selectedPanel = "";
    
    // Hard-coded upper limit on automatic select-all size, to avoid (near)infinite loops.
    private static final int SELECT_ALL_MAX_SIZE = 100000;

    /**
     * Constructor to set up the initial tree model.
     */
    public DigitalObjectBrowser() {
        dr = new DigitalObjectRepositoryLister<DigitalObjectTreeNode>(this);
    }

    /**
     * @return The multiManager
     */
    public DigitalObjectMultiManager getDom() {
        return dr.dsm;
    }
    

    /**
     * Sends back a list of the DOs under the current URI
     * @return
     */
    public List<DigitalObjectTreeNode> getList() {
        log.info("Getting list, location: "+dr.getLocation());
        return this.dr;
    }
    
    /**
     * @return
     */
    public  List<DigitalObjectTreeNode> getBreadcrumb() { 
        // NULL if no location is set:
        if( this.getLocation() == null ) return null;
        return this.getBreadcrumb(this.getLocation());
    }

    /**
     * @param location
     * @return
     */
    protected  List<DigitalObjectTreeNode> getBreadcrumb( URI location ) { 
        // Build a list:
        List<DigitalObjectTreeNode> b = new ArrayList<DigitalObjectTreeNode>();
        b.add( this.getRootTreeNode() );
        // Get the path and trim any trailing slash:
        String path = location.getPath().replaceFirst("/$", "");
        log.info("Getting breadcrumb for path " +path);
        // Split and descend...
        String[] parts = path.split("/");
        for( int i = 0; i < parts.length; i++ ) {
            String relative = "./";
            for( int j = 1; j < parts.length-i; j++ ) relative += "../";
            URI newloc = location.resolve(relative);
            if( this.dr.canAccessURI( newloc ) ) {
                log.debug("Adding parent location: "+newloc);
                b.add( new DigitalObjectTreeNode( newloc ) );
            }
        }
        return b;
    }
    
    private DigitalObjectTreeNode getRootTreeNode() {
        DigitalObjectTreeNode root = new DigitalObjectTreeNode(null);
        root.setLeafname("~");
        return root;
    }
    
    public void setDir( DigitalObjectTreeNode tfn ) {
        // Update the location:
        setLocation(tfn.getUri());
        // Also add childs:
        tfn.setExpanded(true);
        // Clear any current selected digital object:
        this.setDob(null);
    }

    /** Define the current digital object */
    public void setDob( DigitalObjectTreeNode tfn ) {
        if( tfn != null ) this.setSelectedPanel(DigitalObjectBrowser.INSPECTOR_PANEL);
        this.currentDob = tfn;
    }
    
    /** Get the currently inspected digital object */
    public DigitalObjectTreeNode getDob() {
        return currentDob;
    }
    
    /**
     * @return the location
     */
    public URI getLocation() {
        return dr.getLocation();
    }
    
    public DigitalObjectTreeNode getLocationDob() {
        if( this.getLocation() != null ) {
            return new DigitalObjectTreeNode(this.getLocation());
        }
        return null;
    }
    
    /**
     * @param location the location to set
     */
    public void setLocation(URI location) {
        log.info("Setting location: "+location);
        dr.setLocation(location);
    }

    /**
     * Check if the current location has a parent:
     * @return
     */
    public boolean getParentExists() {
        return dr.canAccessURI( this.getParentUri() );
    }

    /**
     * Return the string used to denote the parent URI:
     * @return
     */
    public String getParentName() {
        return "..";
    }

    /**
     * Return the parent URI:
     * @return
     */
    public URI getParentUri() {
        if( dr.getLocation() == null ) return null;
        return dr.getLocation().resolve("..").normalize();
    }
    
    /**
     * Controller to go to the parent directory.
     */
    public String gotoParentLocation() {
        this.setLocation(this.getParentUri());
        return "success";
    }
    
    /**
     * @return
     */
    public int getSelectionSize() {
        return this.selectedDobs.size();
    }

    /**
     * @return
     */
    public List<DigitalObjectTreeNode> getSelectedDobs() {
        List<DigitalObjectTreeNode> b = new ArrayList<DigitalObjectTreeNode>();
        for( URI doburi : this.selectedDobs ) {
            b.add( new DigitalObjectTreeNode(doburi) );
        }
        return b;
    }
    
    /**
     * @return
     */
    public List<URI> getSelectedUris() {
        return this.selectedDobs;
    }

    /** */
    public boolean getAllSelected() {
        if( this.getList().size() > SELECT_ALL_MAX_SIZE ) return false;
        for( DigitalObjectTreeNode dob :  getList() ) {
                if( ! this.selectedDobs.contains( dob.getUri() ) ){
                    log.info("Returning false for: "+dob.getUri());
                    return false;
                }
        }
        return true;
    }
    
    /** */
    public void setAllSelected( boolean selected ) {
        // This deliberately does nothing, as the action is handled by toggleSelectAll.
    }

    /** */
    public boolean isListSelectable() {
        if( this.getList().size() > SELECT_ALL_MAX_SIZE ) return false;
        // Otherwise, see if any are selectable...
        for( DigitalObjectTreeNode dob : getList() ) {
            if( dob.isSelectable() ) return true;
        }
        return false;
    }


    /**
     * @param event
     */
    public void addDobByDrop(DropEvent event) {
        setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
        this.addToSelection( (URI) event.getDragValue() );
    }

    /** */
    protected void addToSelection( URI nuri ) {
        // Only add if not already selected:
        if( ! this.selectedDobs.contains(nuri) ) {
            log.info("Adding selection: "+nuri);
            this.selectedDobs.add(0,nuri);
        }
    }

    /** */
    protected void removeFromSelection( URI nuri ) {
        if( this.selectedDobs.contains( nuri ) ) {
            this.selectedDobs.remove(nuri);
        }
    }
    
    /**
     * Controller that clears the selected items.
     * @return
     */
    public String clearSelection() {
        setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
        this.selectedDobs.clear();
        return "success";
    }
    
    /**
     * Controller that selects all of the current items at the current level.
     */
    public static String selectAll() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        fb.setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
        
        if( fb.getList().size() > SELECT_ALL_MAX_SIZE ) return "failure";

        List<DigitalObjectTreeNode> dobs = fb.getList();
        for( int i = 0; i < dobs.size(); i ++ ) {
            if( ! dobs.get(i).isDirectory() ) {
                fb.addToSelection( dobs.get(i).getUri() );
            }
        }
        return "success";
    }

    /**
     * @return
     */
    public static String unselectAll() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        fb.setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
        
        if( fb.getList().size() > SELECT_ALL_MAX_SIZE ) return "failure";
        
        List<DigitalObjectTreeNode> dobs = fb.getList();
        for( int i = 0; i < dobs.size(); i ++ ) {
            if( ! dobs.get(i).isDirectory() ) {
                fb.removeFromSelection( dobs.get(i).getUri() );
            }
        }
        return "success";
    }

    /** */
    public static void toggleSelectAll( ValueChangeEvent event ) {
        Boolean newValue = (Boolean) event.getNewValue();
        if( newValue.booleanValue() == true ) {
            DigitalObjectBrowser.selectAll();
        } else {
            DigitalObjectBrowser.unselectAll();
        }
    }

    /**
     * Controller that de-selects the current items.
     */
    public static String selectNone() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        fb.clearSelection();
        return "success";
    }
    
    /**
     * Controller that resets the DO browser to the 'Home' location.
     */
    public static String goHome() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        fb.setLocation(null);
        return "success";
    }
    
    /**
     * @return the selectedPanel
     */
    public String getSelectedPanel() {
        log.info("Getting panel: "+this.selectedPanel);
        return this.selectedPanel;
    }
    
    public void setSelectedPanelInspector() {
        this.setSelectedPanel(DigitalObjectBrowser.INSPECTOR_PANEL);
    }
    public void setSelectedPanelSelection() {
        this.setSelectedPanel(DigitalObjectBrowser.SELECTION_PANEL);
    }
    public void setSelectedPanelCorpora() {
        this.setSelectedPanel(DigitalObjectBrowser.CORPORA_PANEL);
    }

    /**
     * @param selectedPanel the selectedPanel to set
     */
    public void setSelectedPanel(String selectedPanel) {
        log.info("Setting panel from "+this.selectedPanel+" to "+selectedPanel);
        this.selectedPanel = selectedPanel;
    }

    /** */
    public static void panelSelectEvent( ValueChangeEvent event ) {
        log.info("Got panel event: "+event);
    }

    
    /**
     * Controller that adds the currently selected items to the experiment.
     */
    public static String addToExperiment() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) return "failure";
        // Add each of the selected items to the experiment:
        for( URI uri : fb.getSelectedUris() ) {
            	//add reference to the new experiment's backing bean
          		expBean.addExperimentInputData(uri.toString());
        }
        // Clear any selection:
        fb.clearSelection();
        // Return: gotoStage2 in the browse new experiment wizard
        return "goToStage2";
    }
    
    public static String redirectToDataRegistry() {
        /*
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect(fb.getRootUrl().toString());
        } catch( java.io.IOException e ) {
          log.debug("Caught exception on redirectToDataRegistry: " + e );
        }
        */
        return "success";
    }
    
}
