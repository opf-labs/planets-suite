/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    
    private URI currentDob;
    
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
        // Build a list:
        List<DigitalObjectTreeNode> b = new ArrayList<DigitalObjectTreeNode>();
        b.add( this.getRootTreeNode() );
        // Get the path and trim any trailing slash:
        String path = this.getLocation().getPath().replaceFirst("/$", "");
        log.info("Getting breadcrumb for path " +path);
        // Split and descend...
        String[] parts = path.split("/");
        for( int i = 0; i < parts.length; i++ ) {
            String relative = "./";
            for( int j = 1; j < parts.length-i; j++ ) relative += "../";
            URI newloc = this.getLocation().resolve(relative);
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
        if( tfn == null ) {
            this.currentDob = null;
        } else {
            this.currentDob = tfn.getUri();
        }
    }
    
    /** Get the currently inspected digital object */
    public DigitalObjectTreeNode getDob() {
        if( currentDob != null ) {
            return new DigitalObjectTreeNode(this.currentDob);
        }
        return null;
    }
    
    /**
     * @return the location
     */
    public URI getLocation() {
        return dr.getLocation();
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
        URI nuri = (URI) event.getDragValue();
        // Only add if not already selected:
        if( ! this.selectedDobs.contains(nuri) ) {
          log.info("Adding selection: "+nuri);
          this.selectedDobs.add(0,nuri);
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
     * Controller that selects all of the current items.
     */
    public static String selectAll() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        for( DigitalObjectTreeNode dob: fb.getList() ) {
            if( dob.isSelectable() ) dob.setSelected(true);
        }
        return "success";
    }

    /**
     * Controller that de-selects the current items.
     */
    public static String selectNone() {
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
        for( DigitalObjectTreeNode dob: fb.getList() ) {
            if( dob.isSelectable() ) dob.setSelected(false);
        }
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
        for( DigitalObjectTreeNode dob: fb.getList() ) {
          // Only include selected items that are eligible:
          if( dob.isSelectable() && dob.isSelected() ) {
              /*
            try {
                DataHandler dh = new DataHandlerImpl();
                // FIXME: Add this method
//            	String ref = dh.addFromDataRegistry(fb.dr , dob.getUri());
            	//add reference to the new experiment's backing bean
          		expBean.addExperimentInputData(ref);
            } catch( IOException e ) {
              log.error("Failed to add to experiment: "+dob.getUri());
              log.error("Exception: "+e);
            }
            */
          }
        }
        // Clear any selection:
        DigitalObjectBrowser.selectNone();
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
