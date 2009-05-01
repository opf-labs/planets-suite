/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

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
    private DigitalObjectRepositoryLister<DigitalObjectTreeNode> dr = new DigitalObjectRepositoryLister<DigitalObjectTreeNode>();

    /**
     * Constructor to set up the initial tree model.
     */
    public DigitalObjectBrowser() {
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
        List<DigitalObjectTreeNode> b = new ArrayList<DigitalObjectTreeNode>();
        if( this.getLocation() == null ) return b;
        log.info("Getting breadcrumb for:" +this.getLocation());
        // Split and descend...
        String[] parts = this.getLocation().getPath().split("/");
        for( int i = 0; i < parts.length; i++ ) {
            String relative = "./";
            for( int j = 1; j < parts.length-i; j++ ) relative += "../";
            URI newloc = this.getLocation().resolve(relative);
            log.info("Adding parent location: "+newloc);
            b.add( new DigitalObjectTreeNode( newloc ) );
        }
        return b;
    }
    
    public void setDir( DigitalObjectTreeNode tfn ) {
        // Update the location:
        setLocation(tfn.getUri());
        // Also add childs:
        tfn.setExpanded(true);
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
        DigitalObjectBrowser fb = (DigitalObjectBrowser) JSFUtil.getManagedObject("DobBrowser");
/*        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect(fb.getRootUrl().toString());
        } catch( java.io.IOException e ) {
          log.debug("Caught exception on redirectToDataRegistry: " + e );
        }
        */
        return "success";
    }
    
}
