/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.DigitalObject;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DataRegistryManagerImpl;

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
    // TODO This should be the real thing, provided by the If as an EJB.
    private DataRegistryManagerImpl dr = new DataRegistryManagerImpl();

    // The current URI/position in the DR:
    private URI location;
    
    // The currently viewed DR entities
    private FileTreeNode[] currentItems;
    
    public FileBrowser() {
        this.setLocation(dr.getDataRegistryUri());
    }
    
    /**
     * Display the root URI of the DR file system:
     * @return The root URI of the Data Registry.
     */
    public URL getRootUrl() {
        try {
          return dr.getDataRegistryUri().toURL();
        } catch( java.net.MalformedURLException e ) {
          return null;
        }
    }
    
    /**
     * Sends back a list of the DOs under the current URI
     * @return
     */
    public FileTreeNode[] getList() {
        return this.currentItems;
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
        this.location = location.normalize();
        DigitalObject[] dobs = dr.list(this.location);
        int fileCount = 0;
        for( DigitalObject dob : dobs ) {
            if( !dob.isDirectory() ) fileCount++;
        }
        this.currentItems = new FileTreeNode[fileCount];
        int i = 0;
        for( DigitalObject dob : dobs ) {
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
        return this.location.resolve("..").normalize();
    }
    
    
    /**
     * Backing for the Tomahawk Tree2 I'm using for displaying the filer tree.
     * @return A TreeModel holding the directory structure.
     */
    public TreeModel getFilerTree() {
        
        // Build the tree.
        TreeNode tn = new FileTreeNode(new DigitalObject(dr.getDataRegistryUri()));
        tn.setType("folder"); tn.setLeaf(false);

        // Create the tree:
        TreeModel tm = new TreeModelBase(tn);

        // Add child nodes:
        this.getChildItems(tm, tn, dr.list(dr.getDataRegistryUri()));
        
        return tm;
    }
    
    private void getChildItems( TreeModel tm, TreeNode parent, DigitalObject[] dobs ) {
        // Do nothing if there are no comments.
        if( dobs.length == 0 ) return;
        
        // Iterate over the children:
        for ( DigitalObject dob : dobs ) {
          // Only include directories:
          if( dob.isDirectory() ) {
            // Generate the child node:
            FileTreeNode cnode = new FileTreeNode(dob);
            // Add the child element to the tree:
            List<FileTreeNode> cchilds = (List<FileTreeNode>) parent.getChildren();
            cchilds.add(cnode);
            // If there are any, add them via recursion:
            if( dob.isDirectory() ) 
                this.getChildItems(tm, cnode, dr.list(dob.getUri()));
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
              String fileRef = new File(dob.getUri()).getCanonicalPath();
              expBean.addExperimentInputData(fileRef);
            } catch( IOException e ) {
              log.error("Failed to add to experiment: "+dob.getUri());
              log.equals("Exception: "+e);
            }
          }
        }
        // Clear any selection:
        FileBrowser.selectNone();
        // Return:
        return "goToStage2";
    }
    
    public static String redirectToDataRegistry() {
        FileBrowser fb = (FileBrowser) JSFUtil.getManagedObject("FileBrowser");
        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect(fb.getRootUrl().toString());
        } catch( java.io.IOException e ) {
          log.debug("Caught exception on redirectToDataRegistry: " + e );
        }
        return "success";
    }
}
