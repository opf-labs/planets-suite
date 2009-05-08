/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.richfaces.event.DropEvent;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * This class is the backing bean that provides the interface to 
 * the Data Registry, which is currently a mock-up.
 * @author AnJackson
 * 
 */
public class DigitalObjectBrowser {
    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectBrowser.class);
    
    // The Data Registry:
    private DigitalObjectRepositoryLister<DigitalObjectTreeNode> dr = null;
    
    private DigitalObjectTreeNode currentDob;
    
    private List<URI> selectedDobs = new ArrayList<URI>();

    /**
     * Constructor to set up the initial tree model.
     */
    public DigitalObjectBrowser() {
        dr = new DigitalObjectRepositoryLister<DigitalObjectTreeNode>(this);
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


    /**
     * @param event
     */
    public void addDobByDrop(DropEvent event) {
        this.addToSelection( (URI) event.getDragValue() );
    }

    /** */
    private void addToSelection( URI nuri ) {
        // Only add if not already selected:
        if( ! this.selectedDobs.contains(nuri) ) {
            log.info("Adding selection: "+nuri);
            this.selectedDobs.add(0,nuri);
        }
    }

    /** */
    private void removeFromSelection( URI nuri ) {
        if( this.selectedDobs.contains( nuri ) ) {
            this.selectedDobs.remove(nuri);
        }
    }
    
    /**
     * Controller that clears the selected items.
     * @return
     */
    public String clearSelection() {
        this.selectedDobs.clear();
        return "success";
    }
    
    /**
     * Controller that selects all of the current items at the current level.
     */
    public static String selectAll() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        List<DigitalObjectTreeNode> dobs = fb.getList();
        for( int i = 0; i < dobs.size(); i ++ ) {
            if( dobs.get(i).isLeaf() ) {
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
        List<DigitalObjectTreeNode> dobs = fb.getList();
        for( int i = 0; i < dobs.size(); i ++ ) {
            if( dobs.get(i).isLeaf() ) {
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
