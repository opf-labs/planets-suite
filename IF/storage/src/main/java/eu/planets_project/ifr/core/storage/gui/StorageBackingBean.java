package eu.planets_project.ifr.core.storage.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.DropEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.TreeRowKey;
//import org.richfaces.model.TreeNode;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectDirectoryLister;
import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectReference;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRConstants;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRManager;
import eu.planets_project.services.datatypes.DigitalObject;

import org.richfaces.component.html.HtmlDataTable;
import org.richfaces.component.html.HtmlTree;
import javax.faces.context.FacesContext;

import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Property;


/**
 * This is the controller class for the storage JSF web application.
 */
public class StorageBackingBean {

	public enum DataModelConfiguration {
		PLANETS, PREMIS;
	}

//	private static DataModelConfiguration currentConfiguration = DataModelConfiguration.PLANETS;
	private static String currentConfiguration = "PLANETS";

	private static Logger log = Logger.getLogger(StorageBackingBean.class
			.getName());

	// The Data Registry:
	private static StorageDigitalObjectDirectoryLister dr = new StorageDigitalObjectDirectoryLister();

	// The currently viewed DR entities
	private FileTreeNode[] currentItems;
	
	// The current URI/position in the DR:
	private URI location = null;

	private TreeNodeImpl rootNode = new TreeNodeImpl();
//	private TreeNodeImpl rootNode = null;
	private TreeNodeImpl doRootNode = new TreeNodeImpl();

	private RegistryPropertyImpl rpi = new RegistryPropertyImpl(null);
	private Map<Integer, RegistryPropertyImpl> mapNodes = new HashMap<Integer, RegistryPropertyImpl>();
	private static int currentNodeIndex = 0;
	
	// tree
	private static String ALL_REGISTRIES = "all_registries";
	private String selectedview = null;
	private static String selectedRegistry = ALL_REGISTRIES;


	private Map<String, org.richfaces.model.TreeNode> dndSelNodes = new HashMap<String, org.richfaces.model.TreeNode>();

    private org.richfaces.model.TreeNode selNode;
    
    private static List<String> registryList = new ArrayList<String>();
    
	
	/**
	 * Constructor for the UseBackingBean, this populates the user manager and
	 * user members
	 */
	public StorageBackingBean() {
		log.info("StorageBackingBean()");
	}

	public void selectionChanged(ValueChangeEvent e) {
		log
				.info("StorageBackingBean selectionChanged(comboBox) currentConfiguration: "
						+ currentConfiguration);
		log.info("StorageBackingBean selectionChanged() selectedRegistry: "
				+ selectedRegistry);
		loadTree();
	}

	public List<SelectItem> getDataRegistryOptions() {
		List<SelectItem> res = new ArrayList<SelectItem>();

		res.add(new SelectItem(ALL_REGISTRIES));
		log.info("+++ StorageBackingBean getDataRegistryOptions() dors:dr.list() uri: " + null);
		StorageDigitalObjectReference[] dors = dr.list(null);
		for (int i = 0; i < dors.length; i++) {
			SelectItem si = new SelectItem(dors[i].getLeafname());
			res.add(si);
			registryList.add(dors[i].getLeafname());
		}
		return res;
	}

	public List<SelectItem> getConfigurations() {
		List<SelectItem> res = new ArrayList<SelectItem>();
//		for (DataModelConfiguration c : DataModelConfiguration.values()) {
//			SelectItem si = new SelectItem(c.name());
//			res.add(si);
//		}
		res.add(new SelectItem("PLANETS"));
		res.add(new SelectItem("PREMIS"));
		return res;
	}

	/**
	 * Sends back a list of the DOs under the current URI
	 * 
	 * @return
	 */
	public FileTreeNode[] getList() {
		log.info("StorageBackingBean getList()");
		return this.currentItems;
	}

	
	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	public String getCurrentConfiguration() {
		return currentConfiguration;
	}

//	public DataModelConfiguration getCurrentConfiguration() {
//		return currentConfiguration;
//	}
//
	public void setCurrentConfiguration(
			String currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

//	public void setCurrentConfiguration(
//			DataModelConfiguration currentConfiguration) {
//		this.currentConfiguration = currentConfiguration;
//	}

	public TreeNodeImpl<String> getTreeNode() {
		log.info("StorageBackingBean getTreeNode() rootNode: " + rootNode);		
		loadTree();
		log.info("StorageBackingBean getTreeNode() return rootNode: " + rootNode);		
		return rootNode;
	}

	public TreeNodeImpl<String> getDoTreeNode() {
		if (doRootNode == null) {
			loadDoTree();
		} else {
			checkDoReloadTree();
		}
		return doRootNode;
	}

	private void loadTree() {
		log.info("****** StorageBackingBean loadTree()");		
		TreeViews.standardTraverseTree(new RegistryPropertyImpl(null), rootNode);
	}

	private void loadDoTree() {
		log.info("****** StorageBackingBean loadDoTree()");		
		TreeDoViews.standardTraverseTree(doRootNode, this.getSelTreeNode(), currentConfiguration);
	}

	private void checkDoReloadTree() {
		// check if the filter or tree view has changed
		log.info("StorageBackingBean checkDoReloadTree() selectedRegistry: "
				+ selectedRegistry);

		if (this.treeViewChanged()) {
			this.loadDoTree();
			return;
		}
	}

	/**
	 * Indicates if the selected treeview (rothenberg, standard, etc.) changed
	 * since the tree was updated the last time
	 * 
	 * @return
	 */
	private boolean treeViewChanged() {
		log
		.info("StorageBackingBean treeValueChanged() currentConfiguration: "
				+ currentConfiguration);
		return true;
	}

	public void processValueChange(ValueChangeEvent e) {
		log
				.info("StorageBackingBean processValueChange() currentConfiguration: "
						+ currentConfiguration);
		loadDoTree();
	}

	/**
	 * User selected a leaf node and is interested in its data attributes
	 * 
	 * @param event
	 */
	public void processSelection(NodeSelectedEvent event) {
		log.info("StorageBackingBean processSelection()");
        HtmlTree tree = (HtmlTree) event.getComponent();
        org.richfaces.model.TreeNode currentNode = tree.getTreeNode(tree.getRowKey());
		log.info("StorageBackingBean processSelection() currentNode: " + currentNode.getData() +
				", isLeaf: " + currentNode.isLeaf());
        if (currentNode.isLeaf()){
        	 this.setSelTreeNode(currentNode);
        }  
	}

	public void processDrop(DropEvent dropEvent) {
		log.info("StorageBackingBean processDrop()");
    	// resolve drag source attributes
        UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent.getDraggableSource() : null;
        UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
        TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent.getDragValue() : null;
        org.richfaces.model.TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
        
        log.info("dropped key: " + dragNodeKey);
       
        //add to list of selected properties
        if(draggedNode!=null) {
    		log.info("StorageBackingBean processDrop() dndSelNodes.put key: " + ((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName());
        	this.dndSelNodes.put(((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName(), draggedNode);
        }
	}

	/**
	 * List of all nodes which have been selected via drag and drop
	 * 
	 * @return
	 */
	public List<org.richfaces.model.TreeNode> getSelNodes() {
		log.info("StorageBackingBean getSelNodes()");
		List<org.richfaces.model.TreeNode> ret = new ArrayList<org.richfaces.model.TreeNode>();
		for (org.richfaces.model.TreeNode n : dndSelNodes.values()) {
			ret.add(n);
		}
		return ret;
	}

	/**
	 * Triggers expand/collapse on the tree
	 */
	public Boolean adviseNodeOpened(UITree tree) {
		// root always elapsed
		log.info("StorageBackingBean adviseNodeOpened()");
		return Boolean.TRUE;
	}

	public void processPropertyDblClick(ActionEvent event) {
		log.info("StorageBackingBean processPropertyDblClick()");
		UITreeNode srcNode = (event.getComponent().getParent() instanceof UITreeNode) ? (UITreeNode) event
				.getComponent().getParent()
				: null;
		processAddNodeActionEvent(srcNode);
	}

	private void processAddNodeActionEvent(UITreeNode srcNode) {
		log.info("StorageBackingBean processAddNodeActionEvent()");
    	UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
    	TreeRowKey dragNodeKey = (srcNode.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) srcNode.getDragValue() : null;
    	org.richfaces.model.TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
    	
    	log.info("dropped key: " + dragNodeKey);
    	
    	//add to list of selected properties
    	if(draggedNode!=null) {
    		log.info("StorageBackingBean processAddNodeActionEvent() dndSelNodes.put key: " + ((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName());
        	this.dndSelNodes.put(((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName(), draggedNode);
    	}
	}


	public void processLeafContextMenuAddProperty(ActionEvent event) {
		log.info("StorageBackingBean processLeafContextMenuAddProperty()");
    	UITreeNode srcNode = (event.getComponent().getParent().getParent()instanceof UITreeNode) ? 
    			(UITreeNode) event.getComponent().getParent().getParent() : null;
    	processAddNodeActionEvent(srcNode);
    	}

	HtmlSelectBooleanCheckbox cbx;

	public HtmlSelectBooleanCheckbox getContextMenuEnabled() {
		if (cbx == null) {
			cbx = new HtmlSelectBooleanCheckbox();
			cbx.setId("cbxcontextmenu");
			cbx.setSelected(false);
		}
		return cbx;
	}

	public void setContextMenuEnabled(HtmlSelectBooleanCheckbox cbx) {
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

	
    /**
     * The current selected leaf node within the tree on which information is presented
     * @return
     */
    public org.richfaces.model.TreeNode getSelTreeNode(){
    	return (org.richfaces.model.TreeNode) this.selNode;
    }
    
    public void setSelTreeNode(org.richfaces.model.TreeNode node){
		log.info("StorageBackingBean setSelTreeNode()");
    	this.selNode = new TreeNodeImpl();
		log.info("StorageBackingBean setSelTreeNode() node.getData(): " + node.getData().toString());
    	selNode.setData(node.getData());
    }
    
	/**
	 * removes all selected properties from the list
	 */
	public void removeAllSelectedProps(){
		this.dndSelNodes.clear();
	}
    
    
    public int getSelNodesSize(){
    	return this.dndSelNodes.size();
    }    
    
    HtmlDataTable selPropTable = new HtmlDataTable();
    /**
     * @return the SelPropTable for dropped properties
     */
    public HtmlDataTable getSelPropTable() {
        return this.selPropTable;
    }

    /**
     * @param selPropTable to hold the dropped properties
     */
    public void setSelPropTable(HtmlDataTable selPropTable) {
		log.info("StorageBackingBean setSelPropTable()");
        this.selPropTable = selPropTable;
    }
    
    /**
     * Trigger removal of a for property that has been selected and is rendered in the "selected properties" box
     */
    public void processRemoveSelProp(){
		log.info("StorageBackingBean processRemoveSelProp()");
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
		if(o1!=null){
			log.info("StorageBackingBean processRemoveSelProp() o1: " + (String)o1);
			this.dndSelNodes.remove((String)o1);
		}
    }
    
    
    /**
     * Triggered for properties that have been selected and are rendered in the "selected properties" box
     * Displays their detailed information.
     */
    public void processDetailsForSelProp() {
		log.info("StorageBackingBean processDetailsForSelProp()");
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
		if(o1!=null){
			log.info("StorageBackingBean processDetailsForSelProp() o1: " + (String)o1);
			//the properties of the selTreeNode are rendered in the info box
			this.setSelTreeNode(this.dndSelNodes.get((String)o1));
		}
    }


    public String getNodeTitle() {
		log.info("StorageBackingBean getNodeTitle()");
    	if(this.getSelTreeNode()!=null)
    		if (this.getSelTreeNode() != null && this.getSelTreeNode().getData() != null) {
    			return (((RegistryPropertyImpl)this.getSelTreeNode().getData()).getName());
    		}
    	return "";
    }
    
    
    private static class TreeViews{
    	
    	public static TreeNode standardTraverseTree(RegistryPropertyImpl cl, TreeNode node) {
    		log.info("StorageBackingBean standardTraverseTree() cl.getUri(): " + cl.getUri());
    		StorageDigitalObjectReference[] dors = dr.list(cl.getUri());
    		log.info("StorageBackingBean standardTraverseTree() dors.length: " + dors.length);
    		
            TreeNode childClass = new TreeNodeImpl();
			log.info("StorageBackingBean standardTraverseTree() setData for node.addChild: " + (cl.getHumanReadableName())); 
            childClass.setData(new DummyRegistryProperty(cl.getHumanReadableName()));
            childClass.setParent(node); 
			log.info("#### StorageBackingBean standardTraverseTree() node.addChild cl.uri: " + cl.getUri()); 
			if (dors.length>0) {
	            node.removeChild(cl.getHumanReadableName()); 
				node.addChild(cl.getHumanReadableName(), childClass);
			}

    		for (int i = 0; i < dors.length; i++) {
    			log.info("StorageBackingBean standardTraverseTree() currentConfiguration: " + currentConfiguration + 
    					", dors[i].getLeafname(): " + dors[i].getLeafname() + ", dors[i].getUri(): " + dors[i].getUri());
    			// filter if filter set
    			log.info("StorageBackingBean standardTraverseTree() selectedRegistry: " + selectedRegistry); 
    			boolean allowNodes = false;
				boolean containsRegisrtyName = false;
				if (StorageBackingBean.registryList != null) {
    				for (int idx = 0; idx < StorageBackingBean.registryList.size(); idx++) {
            			if (dors[i].getUri().toString().contains(StorageBackingBean.registryList.get(idx))) {
            				containsRegisrtyName = true;
            			}    					
    				}
				}
				if (!containsRegisrtyName) {
					allowNodes = true;
				}
    				
    			if (selectedRegistry != null && selectedRegistry.length() > 0 && 
    					(dors[i].getUri().toString().contains(selectedRegistry) || selectedRegistry.equals(ALL_REGISTRIES) ||
    							allowNodes)) {
        			log.info("StorageBackingBean standardTraverseTree() process new node creation."); 
	            	try {
	                	TreeNode child = new TreeNodeImpl();
	                    RegistryPropertyImpl registryProperty = new RegistryPropertyImpl(dors[i]);
	                    
	        			log.info("StorageBackingBean standardTraverseTree() child.setData(registryProperty): " + registryProperty +
	        					", rp.getUri: " + registryProperty.getURI()); 
	 	                child.setData(registryProperty);
	 	                log.info("#### StorageBackingBean standardTraverseTree() childClass.addChild registryProperty.getURI(): " + 
	 	                		registryProperty.getURI());
	 	                if (!dors[i].isDirectory()) {
	 	                	childClass.addChild(registryProperty.getURI(), child);
	 	                }
	                    standardTraverseTree(registryProperty, childClass);
	            	} catch (ClassCastException e) {
	            		log.info("Shouldn't happen any more: Filtering error: " + e.getMessage());
	            	}
    			} // filter end
            }
            
            return node;
        }
    	
    }
    
	/**
	 * This method retrieves a digital object from JCR data registry
	 * @param uri
	 * @return digital object
	 */
//	private static DigitalObject retrieveDigitalObjectFromJCR(URI uri)
//	{
//		DigitalObject res = null;
//		
//		try {
//			res = ((DataRegistry) dr.getDataManager(
//				        DataRegistryFactory.createDataRegistryIdFromName(DOJCRConstants.REGISTRY_NAME)))
//				.getDigitalObjectManager(
//						DataRegistryFactory.createDataRegistryIdFromName(DOJCRConstants.REGISTRY_NAME))
//				.retrieve(uri);
//		} catch (Exception u) {
//			log.info("\nError! Unable to retrieve selected digital object!");
//		}
//		
//		return res;
//	}
	
	
	
    /**
     * This class reveals DigitalObject in GUI tree.
     * @author GrafR
     *
     */
    private static class TreeDoViews{
    	
    	private static TreeNode addNode(TreeNode node, String parentName, String value) {
            log.info("StorageBackingBean DoTreeView addNode() parentName: " + parentName + ", value: " + value);
            TreeNodeImpl childNode = new TreeNodeImpl();
            childNode.setData(parentName);
            childNode.setParent(node);
            node.removeChild(parentName + "1");
            node.addChild(parentName + "1", childNode);

            TreeNodeImpl childChildNode1 = new TreeNodeImpl();
            if (value != null) {
	            log.info("#### StorageBackingBean DoTreeView standardTraverseTree() value: " + value);
               childChildNode1.setData(value);
            } else {
	            log.info("StorageBackingBean DoTreeView standardTraverseTree() 2. ");
            }
            childChildNode1.setParent(childNode);
            childNode.addChild(parentName + "1.1", childChildNode1);
            return childNode;
    	}
    	
    	
    	private static TreeNode addNode(TreeNode node, String parentName) {
            log.info("StorageBackingBean DoTreeView addNode() parentName: " + parentName);
            TreeNodeImpl childNode = new TreeNodeImpl();
            childNode.setData(parentName);
            childNode.setParent(node);
            node.removeChild(parentName + "1");
            node.addChild(parentName + "1", childNode);

            return childNode;
    	}

    	
    	private static void describeMetadata(TreeNode childNode, DigitalObject o) {
        	List<Metadata> metadataList = o.getMetadata();   
        	if (metadataList != null) {
        		ListIterator<Metadata> iterMeta = metadataList.listIterator();
    		
                if (metadataList.size() > 0) 
                {
                	TreeNode metadatasNode = addNode(childNode, "metadatalist");
        			while(iterMeta.hasNext())
        			{
                    	TreeNode metadataNode = addNode(metadatasNode, "metadata");
        				Metadata metadataObj = iterMeta.next();
        				try
        				{
                        	if (metadataObj.getType() != null) {
                        		addNode(metadataNode, "type", metadataObj.getType().toString());
                        	}
                        	if (metadataObj.getContent() != null) {
                        		addNode(metadataNode, "content", metadataObj.getContent());
                        	}
                        	if (metadataObj.getName() != null) {
                        		addNode(metadataNode, "name", metadataObj.getName());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "metadataList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}    	
    	}

    	
    	private static void describeSignificantProperties(TreeNode childNode, DigitalObject o) {
        	List<Metadata> metadataList = o.getMetadata();   
        	if (metadataList != null) {
        		ListIterator<Metadata> iterMeta = metadataList.listIterator();
    		
                if (metadataList.size() > 0) 
                {
                	TreeNode metadatasNode = addNode(childNode, "significantProperties");
        			while(iterMeta.hasNext())
        			{
                    	TreeNode metadataNode = addNode(metadatasNode, "significantProperty");
        				Metadata metadataObj = iterMeta.next();
        				try
        				{
                        	if (metadataObj.getType() != null) {
                        		addNode(metadataNode, "type", metadataObj.getType().toString());
                        	}
                        	if (metadataObj.getContent() != null) {
                        		addNode(metadataNode, "value", metadataObj.getContent());
                        	}
                        	if (metadataObj.getName() != null) {
                        		addNode(metadataNode, "extension", metadataObj.getName());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "metadataList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}    	
    	}

    	
    	private static void describeAgent(TreeNode eventNode, Event eventObj) {
        	Agent agentObj = eventObj.getAgent();   
        	if (agentObj != null) {
            	TreeNode agentNode = addNode(eventNode, "agent");
				try
				{
                	if (agentObj.getId() != null) {
                		addNode(agentNode, "id", agentObj.getId());
                	}
                	if (agentObj.getName() != null) {
                		addNode(agentNode, "name", agentObj.getName());
                	}
                	if (agentObj.getType() != null) {
                		addNode(agentNode, "type", agentObj.getType());
                	}
				} catch (Exception e)
				{
					log.log(Level.INFO, "agent error: " + e.getMessage(), e);
				}
        	}    	
    	}


    	private static void describeLinkingAgentId(TreeNode eventNode, Event eventObj) {
        	Agent agentObj = eventObj.getAgent();   
        	if (agentObj != null) {
            	TreeNode agentNode = addNode(eventNode, "linkingAgentIdentifier");
				try
				{
                	TreeNode agentIdNode = addNode(agentNode, "agentIdentifier");
                	if (agentObj.getId() != null) {
                		addNode(agentIdNode, "type", "String");
                		addNode(agentIdNode, "value", agentObj.getId());
                	}
                	if (agentObj.getName() != null) {
                		addNode(agentNode, "agentName", agentObj.getName());
                	}
                	if (agentObj.getType() != null) {
                		addNode(agentNode, "agentType", agentObj.getType());
                	}
				} catch (Exception e)
				{
					log.log(Level.INFO, "agent error: " + e.getMessage(), e);
				}
        	}    	
    	}


    	private static void describeProperties(TreeNode eventNode, Event eventObj) {
        	List<Property> propertyList = eventObj.getProperties();   
        	if (propertyList != null) {
        		ListIterator<Property> iter = propertyList.listIterator();
    		
                if (propertyList.size() > 0) 
                {
                	TreeNode propertiesNode = addNode(eventNode, "properties");
        			while(iter.hasNext())
        			{
                    	TreeNode propertyNode = addNode(propertiesNode, "property");
        				Property propertyObj = iter.next();
        				try
        				{
                        	if (propertyObj.getUri() != null) {
                        		addNode(propertyNode, "uri", propertyObj.getUri().toString());
                        	}
                        	if (propertyObj.getName() != null) {
                        		addNode(propertyNode, "name", propertyObj.getName());
                        	}
                        	if (propertyObj.getValue() != null) {
                        		addNode(propertyNode, "value", propertyObj.getValue());
                        	}
                        	if (propertyObj.getDescription() != null) {
                        		addNode(propertyNode, "description", propertyObj.getDescription());
                        	}
                        	if (propertyObj.getUnit() != null) {
                        		addNode(propertyNode, "unit", propertyObj.getUnit());
                        	}
                        	if (propertyObj.getType() != null) {
                        		addNode(propertyNode, "type", propertyObj.getType());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "propertyList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}
    	}
    	
    	
    	private static void describeLinkingObjIdentifier(TreeNode eventNode, Event eventObj) {
        	List<Property> propertyList = eventObj.getProperties();   
        	if (propertyList != null) {
        		ListIterator<Property> iter = propertyList.listIterator();
    		
                if (propertyList.size() > 0) 
                {
                	TreeNode propertiesNode = addNode(eventNode, "linkingObjIdentifier");
        			while(iter.hasNext())
        			{
                    	TreeNode propertyNode = addNode(propertiesNode, "significantProperty");
        				Property propertyObj = iter.next();
        				try
        				{                        	
                        	if (propertyObj.getType() != null) {
                        		addNode(propertyNode, "type", propertyObj.getType());
                        	}
                        	if (propertyObj.getValue() != null) {
                        		addNode(propertyNode, "value", propertyObj.getValue());
                        	}
                        	if (propertyObj.getUri() != null) {
                        		addNode(propertyNode, "extension", propertyObj.getUri().toString());
                        	}
                        	if (propertyObj.getName() != null) {
                        		addNode(propertyNode, "extension1", propertyObj.getName());
                        	}
                        	if (propertyObj.getDescription() != null) {
                        		addNode(propertyNode, "extension2", propertyObj.getDescription());
                        	}
                        	if (propertyObj.getUnit() != null) {
                        		addNode(propertyNode, "extension3", propertyObj.getUnit());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "propertyList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}
    	}
    	
    	
    	private static void describeEvents(TreeNode childNode, DigitalObject o) {
        	// describe events
        	List<Event> eventList = o.getEvents();   
        	if (eventList != null) {
        		ListIterator<Event> iter = eventList.listIterator();
    		
                if (eventList.size() > 0) 
                {
                	TreeNode eventsNode = addNode(childNode, "events");
        			while(iter.hasNext())
        			{
                    	TreeNode eventNode = addNode(eventsNode, "event");
        				Event eventObj = iter.next();
        				try
        				{
                        	if (eventObj.getDatetime() != null) {
                        		addNode(eventNode, "datetime", eventObj.getDatetime());
                        	}
                        	if (eventObj.getSummary() != null) {
                        		addNode(eventNode, "summary", eventObj.getSummary());
                        	}
                        	if (eventObj.getDuration() >= 0) {
                        		addNode(eventNode, "duration", String.valueOf(eventObj.getDuration()));
                        	}
                        	describeAgent(eventNode, eventObj);
                        	describeProperties(eventNode, eventObj);
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "eventList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}
    	}
    	
    	
    	private static void describeLinkingEventIds(TreeNode childNode, DigitalObject o) {
        	// describe events
        	List<Event> eventList = o.getEvents();   
        	if (eventList != null) {
        		ListIterator<Event> iter = eventList.listIterator();
    		
                if (eventList.size() > 0) 
                {
                	TreeNode eventsNode = addNode(childNode, "linkingEventIdentifiers");
        			while(iter.hasNext())
        			{
                    	TreeNode eventNode = addNode(eventsNode, "linkingEventIdentifier");
        				Event eventObj = iter.next();
        				try
        				{
                        	TreeNode eventIdNode = addNode(eventNode, "eventIdentifier");
    	                	if (eventObj.getSummary() != null) {
    	                		addNode(eventIdNode, "eventIdentifierType", "String");
    	                		addNode(eventIdNode, "eventIdentifierValue", eventObj.getSummary());
    	                	}
                        	if (eventObj.getDatetime() != null) {
                        		addNode(eventNode, "eventDateType", eventObj.getDatetime());
                        	}
                        	if (eventObj.getDuration() >= 0) {
                        		addNode(eventNode, "eventDetail", String.valueOf(eventObj.getDuration()));
                        	}
                        	describeLinkingAgentId(eventNode, eventObj);
                        	describeLinkingObjIdentifier(eventNode, eventObj);
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "eventList error: " + e.getMessage(), e);
        				}
        			}
                }
        	}
    	}
    	
    	
    	public static TreeNode standardTraverseTree(TreeNode node, TreeNode _node, String currentConfiguration) {
	           DigitalObject o = null;
            	
               if (_node != null) {
	                if (_node.getData() != null) {
	                   URI uri = ((RegistryPropertyImpl)_node.getData()).getUri();
	    	            log.info("#### StorageBackingBean DoTreeView standardTraverseTree() uri: " + uri);
						// Special handling for the digital objects from JCR repository
//						if (uri.toString().contains(DOJCRConstants.DOJCR))
//						{
//							o = retrieveDigitalObjectFromJCR(uri);
//						} else {
							try {
							   o = dr.getDataManager(uri).retrieve(uri);
							} catch (Exception e) {
								log.info("StorageBackingBean DoTreeView standardTraverseTree() error: " + e.getMessage());
							}
//						}
	                   if (o != null)
	                   log.info("StorageBackingBean DoTreeView standardTraverseTree() digitalObject: " + o.toString());
	                }
               }

               if (o != null) {
                   log.info("StorageBackingBean DoTreeView standardTraverseTree() o != null currentConfiguration: " + currentConfiguration);
            	   if (currentConfiguration.equals("PLANETS")) {
		                TreeNodeImpl childNode = new TreeNodeImpl();
		                childNode.setData("digitalObject");
		                childNode.setParent(node);
		                node.removeChild("1");
		                node.addChild("1", childNode);
	
	                	if (o.getTitle() != null) {
	                		addNode(childNode, "title", o.getTitle());
	                	}
	                	if (o.getPermanentUri() != null) {
	                		addNode(childNode, "permanentUri", o.getPermanentUri().toString());
	                	}
	                	if (o.getFormat() != null) {
	                		addNode(childNode, "format", o.getFormat().toString());
	                	}
	                	if (o.getManifestationOf() != null) {
	                		addNode(childNode, "manifestationOf", o.getManifestationOf().toString());
	                	}
	                	describeMetadata(childNode, o);
	                	describeEvents(childNode, o);
            	   } else {
		                TreeNodeImpl childNode = new TreeNodeImpl();
		                childNode.setData("premisObject");
		                childNode.setParent(node);
		                node.removeChild("1");
		                node.addChild("1", childNode);
	
                    	TreeNode idNode = addNode(childNode, "objectIdentifier");
	                	if (o.getPermanentUri() != null) {
	                		addNode(idNode, "type", "URI");
	                		addNode(idNode, "value", o.getPermanentUri().toString());
	                	}
	                	if (o.getTitle() != null) {
	                		addNode(childNode, "originalName", o.getTitle());
	                	}
                    	TreeNode charNode = addNode(childNode, "objectCharacteristics");
	                	if (o.getFormat() != null) {
	                		addNode(charNode, "format", o.getFormat().toString());
	                	}
                    	TreeNode relationshipNode = addNode(childNode, "relationship");
                    	TreeNode relObjIdNode = addNode(relationshipNode, "relatedObjectIdentifier");
	                	if (o.getManifestationOf() != null) {
	                		addNode(idNode, "type", "URI");
	                		addNode(idNode, "value", o.getManifestationOf().toString());
	                	}
	                	describeSignificantProperties(childNode, o);
	                	describeLinkingEventIds(childNode, o);            		   
            	   }
                } 
            return node;
        }
    	
    }
    

    public static class DummyRegistryProperty extends RegistryPropertyImpl implements RegistryProperty{

    	protected DummyRegistryProperty(String name){
    		this.name = name;
    		this.uri = uri;
    	}
    	
    	String name;
    	String uri;
		public String getName() {
			return this.name;
		}
		public void setName(String s){
    		this.name = s;
		}
		public String getDataType() {
			return null;
		}
		public void setDataType(String s){
			//
		}
		public String getType() {
			return null;
		}
		
		public String getParentType(){
			return null;
		}
		
		public void setType(String s){
			//
		}
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}
		public String getHumanReadableName() {
			// TODO Auto-generated method stub
			return null;
		}
		public List<String> getIsSameAsNames() {
			// TODO Auto-generated method stub
			return null;
		}
		public Object getRDFProperty(String rdfString) {
			// TODO Auto-generated method stub
			return null;
		}
		public String getURI() {
			return null;
		}
		public String getUnit() {
			// TODO Auto-generated method stub
			return null;
		}
    }
    	
}
