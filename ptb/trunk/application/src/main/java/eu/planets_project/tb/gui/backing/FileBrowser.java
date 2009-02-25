/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.DigitalObjectReference;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DigitalObjectDirectoryLister;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * This class is the backing bean that provides the interface to 
 * the Data Registry, which is currently a mock-up.
 * @author AnJackson
 * 
 */
public class FileBrowser {
    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(FileBrowser.class, "testbed-log4j.xml");
    
    // The Data Registry:
    private DigitalObjectDirectoryLister dr = new DigitalObjectDirectoryLister();

    // The current URI/position in the DR:
    private URI location = null;
    
    // The currently viewed DR entities
    private FileTreeNode[] currentItems;
    
    // The root tree node
    FileTreeNode tn = null;
    
    // The File tree model:
    TreeModel tm;

    /**
     * Constructor to set up the initial tree model.
     */
    public FileBrowser() {
        // Build the tree.
        tn = new FileTreeNode(dr.getRootDigitalObject());
        tn.setType("folder"); 
        tn.setLeaf(false);
        tn.setExpanded(true);

        // Create the tree:
        tm = new TreeModelBase(tn);
        
        // Add child nodes:
        this.getChildItems(tm, tn, dr.list(null), 1);
        
    }
    
    /**
     * Sends back a list of the DOs under the current URI
     * @return
     */
    public FileTreeNode[] getList() {
        return this.currentItems;
    }
    
    public void setDir( FileTreeNode tfn ) {
        // Update the location:
        setLocation(tfn.getUri());
        // Also add childs:
        tfn.setExpanded(true);
        this.getChildItems(tm, tfn, dr.list(getLocation()), 1);
    }
    
    /**
     * @return the location
     */
    public URI getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(URI location) {
        log.debug("Setting location: "+location);
        if( location != null ) this.location = location.normalize();
        DigitalObjectReference[] dobs = dr.list(this.location);
        int fileCount = 0;
        for( DigitalObjectReference dob : dobs ) {
            if( !dob.isDirectory() ) fileCount++;
        }
        //this.currentItems = new FileTreeNode[fileCount];
        // Put directories first.
        this.currentItems = new FileTreeNode[dobs.length];
        int i = 0;
        for( DigitalObjectReference dob : dobs ) {
            if( dob.isDirectory() ) {
                this.currentItems[i] = new FileTreeNode(dob);
                i++;
            }
        }
        for( DigitalObjectReference dob : dobs ) {
            if( !dob.isDirectory() ) {
                this.currentItems[i] = new FileTreeNode(dob);
                i++;
            }
        }
        /*
        if( this.getParentExists() ) {
          this.currentItems = new DigitalObject[listItems.length+1];
          try {
            this.currentItems[0] = new DigitalObject(new URI(this.location+"/.."));
            this.currentItems[0].setDirectory(true);
            this.currentItems[0].setSelectable(false);
          } catch( java.net.URISyntaxException e ) {
              log.error("Failed to create parent URI: " + e );
              this.currentItems = listItems;
          }
          System.arraycopy(listItems, 0, this.currentItems, 1, listItems.length);
        } else {
          this.currentItems = listItems;
        }*/
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
        if( this.location == null ) return this.location;
        return this.location.resolve("..").normalize();
    }
    
    
    /**
     * Backing for the Tomahawk Tree2 I'm using for displaying the filer tree.
     * @return A TreeModel holding the directory structure.
     */
    public TreeModel getFilerTree() {
        return tm;
    }
    
    /**
     * Add the childs...
     * 
     * @param tm
     * @param parent
     * @param dobs
     * @param depth
     */
    private void getChildItems( TreeModel tm, TreeNode parent, DigitalObjectReference[] dobs, int depth ) {
        // Do nothing if there are no comments.
        if( dobs == null ) return;
        if( dobs.length == 0 ) return;
        
        // Iterate over the children:
        for ( DigitalObjectReference dob : dobs ) {
          // Only include directories:
          if( dob.isDirectory() ) {
            // Generate the child node:
            FileTreeNode cnode = new FileTreeNode(dob);
            // Add the child element to the tree:
            List<FileTreeNode> cchilds = (List<FileTreeNode>) parent.getChildren();
            if( ! cchilds.contains(cnode) )
                cchilds.add(cnode);
            // If there are any, add them via recursion:
            if( dob.isDirectory() && depth > 0 ) 
                this.getChildItems(tm, cnode, dr.list( dob.getUri()), depth - 1 );
          }
        }
        
    }

    /**
     * Controller that selects all of the current items.
     */
    public static String selectAll() {
        FileBrowser fb = (FileBrowser) JSFUtil.getManagedObject("FileBrowser");
        for( FileTreeNode dob: fb.getList() ) {
            if( dob.isSelectable() ) dob.setSelected(true);
        }
        return "success";
    }

    /**
     * Controller that de-selects the current items.
     */
    public static String selectNone() {
        FileBrowser fb = (FileBrowser) JSFUtil.getManagedObject("FileBrowser");
        for( FileTreeNode dob: fb.getList() ) {
            if( dob.isSelectable() ) dob.setSelected(false);
        }
        return "success";
    }
    
    /**
     * Controller that adds the currently selected items to the experiment.
     */
    public static String addToExperiment() {
        FileBrowser fb = (FileBrowser) JSFUtil.getManagedObject("FileBrowser");
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) return "failure";
        // Add each of the selected items to the experiment:
        for( FileTreeNode dob: fb.getList() ) {
          // Only include selected items that are eligible:
          if( dob.isSelectable() && dob.isSelected() ) {
            try {
                DataHandler dh = new DataHandlerImpl();
            	String ref = dh.addFromDataRegistry(fb.dr , dob.getUri());
            	//add reference to the new experiment's backing bean
          		expBean.addExperimentInputData(ref);
            } catch( IOException e ) {
              log.error("Failed to add to experiment: "+dob.getUri());
              log.error("Exception: "+e);
            }
          }
        }
        // Clear any selection:
        FileBrowser.selectNone();
        // Return: gotoStage2 in the browse new experiment wizard
        return "goToStage2";
    }
    
    public static String redirectToDataRegistry() {
        FileBrowser fb = (FileBrowser) JSFUtil.getManagedObject("FileBrowser");
/*        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect(fb.getRootUrl().toString());
        } catch( java.io.IOException e ) {
          log.debug("Caught exception on redirectToDataRegistry: " + e );
        }
        */
        return "success";
    }
    
}
