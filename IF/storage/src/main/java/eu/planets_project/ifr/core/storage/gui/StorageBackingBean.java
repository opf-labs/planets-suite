package eu.planets_project.ifr.core.storage.gui;

import java.util.logging.Logger;

import javax.faces.model.SelectItem;


import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNodeImpl;

import eu.planets_project.ifr.core.storage.impl.data.DigitalObjectDirectoryLister;
import eu.planets_project.ifr.core.storage.impl.data.DigitalObjectReference;


/**
 * This is the controller class for the storage JSF web application.
 */
public class StorageBackingBean {
	
	public enum DataModelConfiguration {
		 PLANETS, PREMIS;  
		}
	
	private DataModelConfiguration currentConfiguration = DataModelConfiguration.PLANETS;
	
	private static Logger log = Logger.getLogger(StorageBackingBean.class.getName());

	// The Data Registry:
	private DigitalObjectDirectoryLister dr = new DigitalObjectDirectoryLister();

	// The currently viewed DR entities
	private FileTreeNode[] currentItems;

	// The root tree node
	FileTreeNode tn = null;

	// The File tree model:
	TreeModel tm;

	// The current URI/position in the DR:
	private URI location = null;

	private TreeNodeImpl<String> rootNode = new TreeNodeImpl<String>();

	// tree
	private String selectedview = null;
	private String selectedRegistry = "";

    private Map<String,TreeNode> dndSelNodes = new HashMap<String,TreeNode>();

	// test data to delete
	private List<TreeNodeImpl<String>> stationRootList = new ArrayList<TreeNodeImpl<String>>();


	/**
	 * Constructor for the UseBackingBean, this populates the user manager and user members
	 */
	public StorageBackingBean() {
		log.info("StorageBackingBean()");
		
		// Build the file tree from the DirectoryListener
		tn = new FileTreeNode(dr.getRootDigitalObject());
		tn.setType("folder");
		tn.setLeaf(false);
		tn.setExpanded(true);
		// Create the tree:
		tm = new TreeModelBase(tn);
		// Add child nodes:
		this.getChildItems(tm, tn, dr.list(null), 1);

	}

	
	public void selectionChanged(ValueChangeEvent e)
	{
		log.info("StorageBackingBean selectionChanged(comboBox) selectedRegistry: " + selectedRegistry);		
		log.info("StorageBackingBean selectionChanged(comboBox) currentConfiguration: " + currentConfiguration);	
		loadTree(false);
	}
	
	
	public List<SelectItem> getDataRegistryOptions()
	{
		List<SelectItem> res = new ArrayList<SelectItem>();

		DigitalObjectReference[] dors = dr.list(null);
        for( int i = 0; i < dors.length; i ++ ) {
			SelectItem si = new SelectItem(dors[i].getLeafname());
			res.add(si);
        }
		return res;
	}
	
	
	public List<SelectItem> getConfigurations()
	{
		List<SelectItem> res = new ArrayList<SelectItem>();
		for(DataModelConfiguration c : DataModelConfiguration.values()) {
			SelectItem si = new SelectItem(c.name());
			res.add(si);
			}
		return res;
	}
	
	
	/**
	 * Sends back a list of the DOs under the current URI
	 * 
	 * @return
	 */
	public FileTreeNode[] getList() {
		return this.currentItems;
	}

	/**
	 * Backing for the Tomahawk Tree2 I'm using for displaying the filer tree.
	 * 
	 * @return A TreeModel holding the directory structure.
	 */
	public TreeModel getFilterTree() {
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
	private void getChildItems(TreeModel tm, TreeNode parent,
			DigitalObjectReference[] dobs, int depth) {
		// Do nothing if there are no comments.
		if (dobs == null)
		{
    		log.info("StorageBackingBean getChildItems() dobs = null");		
			return;
		}
		if (dobs.length == 0)
		{
    		log.info("StorageBackingBean getChildItems() dobs.length = 0");		
			return;
		}

		// Iterate over the children:
		for (DigitalObjectReference dob : dobs) {
			// Only include directories:
			if (dob.isDirectory()) {
				// Generate the child node:
				FileTreeNode cnode = new FileTreeNode(dob);
				// Add the child element to the tree:
				List<FileTreeNode> cchilds = (List<FileTreeNode>) parent
						.getChildren();
				if (!cchilds.contains(cnode))
				{
					cchilds.add(cnode);
				}
				// If there are any, add them via recursion:
				if (dob.isDirectory() && depth > 0)
				{
					this.getChildItems(tm, cnode, dr.list(dob.getUri()),
							depth - 1);
				}
			}
		}

	}


	/**
	 * Add the childs...
	 * 
	 * @param tm
	 * @param parent
	 * @param dobs
	 * @param depth
	 */
	private void addChildItems(TreeModel tm, TreeNode parent,
			DigitalObjectReference[] dobs, int depth, TreeNodeImpl<String> rnode) {
		// Do nothing if there are no comments.
		if (dobs == null)
		{
    		log.info("StorageBackingBean addChildItems() dobs = null");		
			return;
		}
		if (dobs.length == 0)
		{
    		log.info("StorageBackingBean addChildItems() dobs.length = 0");		
			return;
		}
		log.info("StorageBackingBean addChildItems() dobs.length: " + dobs.length);		

		int i = 0;
		// Iterate over the children:
		for (DigitalObjectReference dob : dobs) {
    		log.info("StorageBackingBean addChildItems() dob leafname: " + dob.getLeafname());		
    		log.info("StorageBackingBean addChildItems() dob uri: " + dob.getUri());		
			// Only include directories:
			if (dob.isDirectory()) {
	    		log.info("StorageBackingBean addChildItems() dob.isDirectory");		
				// Generate the child node:
				FileTreeNode cnode = new FileTreeNode(dob);
				// Add the child element to the tree:
				List<FileTreeNode> cchilds = (List<FileTreeNode>) parent
						.getChildren();
				if (!cchilds.contains(cnode))
				{
		    		log.info("StorageBackingBean addChildItems() add cnode: " + cnode.getLeafname() + ", i: " + i);		
					cchilds.add(cnode);
					TreeNodeImpl<String> child = new TreeNodeImpl<String>();
				    child.setData(cnode.getLeafname());
					rnode.addChild(i, child);	
					i++;
				}
				// If there are any, add them via recursion:
				if (dob.isDirectory() && depth > 0)
				{
		    		log.info("StorageBackingBean addChildItems() recursiv dr.list(dob.getUri()): " + dob.getUri());		
					this.addChildItems(tm, cnode, dr.list(dob.getUri()),
					depth - 1, rnode);
				}
			}
		}

	}


	public void setDir(FileTreeNode tfn) {
		// Update the location:
		setLocation(tfn.getUri());
		// Also add childs:
		tfn.setExpanded(true);
		log.info("StorageBackingBean setDir() dr.list(location): " + getLocation());		
		this.getChildItems(tm, tfn, dr.list(getLocation()), 1);
	}


	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(URI location) {
		log.info("StorageBackingBean setLocation: " + location);
		if (location != null)
			this.location = location.normalize();
		DigitalObjectReference[] dobs = dr.list(this.location);
		int fileCount = 0;
		for (DigitalObjectReference dob : dobs) {
			if (!dob.isDirectory())
				fileCount++;
		}
		// Put directories first.
		this.currentItems = new FileTreeNode[dobs.length];
		int i = 0;
		for (DigitalObjectReference dob : dobs) {
			if (dob.isDirectory()) {
	    		log.info("StorageBackingBean setLocation() add DIR currentItem dob.toString(): " + dob.toString() + ", i: " + i);		
				this.currentItems[i] = new FileTreeNode(dob);
				i++;
			}
		}
		for (DigitalObjectReference dob : dobs) {
			if (!dob.isDirectory()) {
	    		log.info("StorageBackingBean setLocation() add FILE currentItem dob.toString(): " + dob.toString() + ", i: " + i);		
				this.currentItems[i] = new FileTreeNode(dob);
				i++;
			}
		}
	}

	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	public DataModelConfiguration getCurrentConfiguration() {
		return currentConfiguration;
	}

	public void setCurrentConfiguration(DataModelConfiguration currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

	
    public TreeNodeImpl<String> getTreeNode() {
        if (rootNode == null) {
            loadTree(false);
        }else{
        	checkReloadTree();
        }
        return rootNode;
    }
    

    private void loadTree(boolean applyFilter){
    	rootNode.removeChild(0);
		log.info("****** StorageBackingBean loadTree() selectedRegistry: " + selectedRegistry);		
		log.info("StorageBackingBean loadTree() currentConfiguration: " + currentConfiguration);		
		DigitalObjectReference[] dors = dr.list(null);
		log.info("StorageBackingBean loadTree() dors.length: " + dors.length);		
        for( int i = 0; i < dors.length; i ++ ) {
    		log.info("StorageBackingBean loadTree() dors[i].getLeafname(): " + dors[i].getLeafname());		
    		log.info("StorageBackingBean loadTree() dors[i].getUri(): " + dors[i].getUri());		
    		log.info("StorageBackingBean loadTree() selectedRegistry: " + selectedRegistry);		
        	stationRootList.add(i, new TreeNodeImpl<String>());
			stationRootList.get(i).setData(dors[i].getLeafname());

			addChildItems(tm, tn, dr.list(dors[i].getUri()), 1, stationRootList.get(i));
			rootNode.addChild(i, stationRootList.get(i));
        }
    }
    

    private void checkReloadTree(){
    	//check if the filter or tree view has changed
		log.info("StorageBackingBean checkReloadTree() selectedRegistry: " + selectedRegistry);		

        if(this.treeViewChanged()){
			this.loadTree(false);
			return;
    	}	
    }
    
    
    /**
     * Indicates if the selected treeview (rothenberg, standard, etc.) changed since 
     * the tree was updated the last time
     * @return
     */
    private boolean treeViewChanged(){
    	return true;
    }
    

    public void processValueChange()
    {
		log.info("StorageBackingBean processValueChange() selectedRegistry: " + selectedRegistry);		
		log.info("StorageBackingBean processValueChange() currentConfiguration: " + currentConfiguration);		
    }
    
    
    /**
     * User selected a leaf node and is interested in its data attributes
     * @param event
     */
    public void processSelection(NodeSelectedEvent event) {
		log.info("StorageBackingBean processSelection()");		
    }    

    
    public void processDrop(DropEvent dropEvent) {
    	// resolve drag source attributes
    }
    

    /**
     * List of all nodes which have been selected via drag and drop
     * @return
     */
    public List<TreeNode> getSelNodes(){
    	List<TreeNode> ret = new ArrayList<TreeNode>();
    	for(TreeNode n : dndSelNodes.values()){
    		ret.add(n);
    	}
    	return ret;
    }
    

    /**
	 * Triggers expand/collapse on the tree
     */
    public Boolean adviseNodeOpened(UITree tree) {
        //root always elapsed 
		log.info("StorageBackingBean adviseNodeOpened()");		
        return Boolean.FALSE;
    }
    

    
    public void processPropertyDblClick(ActionEvent event){
		log.info("StorageBackingBean processPropertyDblClick()");		
    	UITreeNode srcNode = (event.getComponent().getParent()instanceof UITreeNode) ? (UITreeNode) event.getComponent().getParent() : null;
    	processAddNodeActionEvent(srcNode);
    }
    
    
    private void processAddNodeActionEvent(UITreeNode srcNode){
		log.info("StorageBackingBean processAddNodeActionEvent()");		
    }
    
        
    public void processLeafContextMenuAddProperty(ActionEvent event){
		log.info("StorageBackingBean processLeafContextMenuAddProperty()");		
    }
    

    HtmlSelectBooleanCheckbox cbx;
    public HtmlSelectBooleanCheckbox getContextMenuEnabled(){
    	if(cbx == null){
    		cbx = new HtmlSelectBooleanCheckbox();
    		cbx.setId("cbxcontextmenu");
    		cbx.setSelected(false);
    	}
    	return cbx;
    }
    

    public void setContextMenuEnabled(HtmlSelectBooleanCheckbox cbx){
		log.info("StorageBackingBean setContextMenuEnabled()");		
    }
    

	public String getSelectedRegistry() {
		return selectedRegistry;
	}

	public void setSelectedRegistry(String selectedRegistry) {
		this.selectedRegistry = selectedRegistry;
	}

	public String getSelectedview() {
		return selectedview;
	}

	public void setSelectedview(String selectedview) {
		this.selectedview = selectedview;
	}
    

}
