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
import eu.planets_project.tb.api.data.DigitalObject;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.DataRegistryManagerImpl;
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
    private DataRegistryManagerImpl dr = new DataRegistryManagerImpl();

    // The current URI/position in the DR:
    private URI location = null;
    
    // The currently viewed DR entities
    private FileTreeNode[] currentItems;
    
    public FileBrowser() {
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
        if( location != null ) this.location = location.normalize();
        DigitalObject[] dobs = dr.list(this.location);
        int fileCount = 0;
        for( DigitalObject dob : dobs ) {
            if( !dob.isDirectory() ) fileCount++;
        }
        //this.currentItems = new FileTreeNode[fileCount];
        // Put directories first.
        this.currentItems = new FileTreeNode[dobs.length];
        int i = 0;
        for( DigitalObject dob : dobs ) {
            if( dob.isDirectory() ) {
                this.currentItems[i] = new FileTreeNode(dob);
                i++;
            }
        }
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
        if( this.location == null ) return this.location;
        return this.location.resolve("..").normalize();
    }
    
    
    /**
     * Backing for the Tomahawk Tree2 I'm using for displaying the filer tree.
     * @return A TreeModel holding the directory structure.
     */
    public TreeModel getFilerTree() {
        
        // Build the tree.
        TreeNode tn = new FileTreeNode(dr.getRootDigitalObject());
        tn.setType("folder"); tn.setLeaf(false);

        // Create the tree:
        TreeModel tm = new TreeModelBase(tn);

        // Add child nodes:
        this.getChildItems(tm, tn, dr.list(null));
        
        return tm;
    }
    
    private void getChildItems( TreeModel tm, TreeNode parent, DigitalObject[] dobs ) {
        // Do nothing if there are no comments.
        if( dobs == null ) return;
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
            	File fCopy = helperUploadDataForNewExperimentWizard(fb.dr , dob.getUri());
            	//add reference to the new experiment's backing bean
            	//pre-condition: max. supported number of files has not been yet reached
            	if(expBean.getSelectedServiceOperation().getMaxSupportedInputFiles()>expBean.getExperimentInputData().values().size()){
            		//max. number not reached. Add this file ref
            		expBean.addExperimentInputData(fCopy.getAbsolutePath());
            	}
            } catch( IOException e ) {
              log.error("Failed to add to experiment: "+dob.getUri());
              log.equals("Exception: "+e);
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
    
    
    /**
     * WORK AROUND - TO REMOVE WHEN FIXED
     * Uploading single files to an experiment currently uses the JSF tomahawk inputFileUpload element to upload
     * the data from a user into the testbed's experiment data store 
     * i.e.../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war/planets-testbed/inputdata
     * 
     * Data added via this FileBrowser are local files and must be uploaded as well. As in future the data registry
     * will hand over URLs anyway. Therefore there's a work around currently, just copying the local file input
     * into the Testbed's experiment data repository, which only works if both are located on the same machine.
     * 
     * Restrictions: This currently only works if the IF Server + Testbed application are used on localhost where also 
     * the FileBrowsers data can be accessed locally.
     * @return the copied and renamed File
     */
    //TODO discuss solution for work around
    private static File helperUploadDataForNewExperimentWizard( DataRegistryManagerImpl dr, URI pduri ) throws IOException{
    	//workaround: copy the local FileBrowsers file reference into
    	//the Testbed's experiment data repository
    	DataHandler dh = new DataHandlerImpl();
    	//the input dir of the server where all experiment related files are stored
    	String fileInDir = dh.getFileInDir();
 
    	//if input dir does not yet exist
    	File dir = new File(fileInDir);
        dir.mkdirs();  
        	
        //@see FileUploadBean:
        //create unique filename
        String ext = pduri.getPath().substring(pduri.getPath().lastIndexOf('.'));
    	String mathName = UUID.randomUUID().toString() + ext;
    	dh.setInputFileIndexEntryName(mathName, pduri.toString());
        File fcopy = new File(fileInDir,mathName);
        //copy the renamed file to it's new location
    	dh.copy(dr, pduri , fcopy);
    	
    	return fcopy;
    }
}
