package eu.planets_project.ifr.core.storage.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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

import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectDirectoryLister;
import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectReference;
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

	/**
	 * Difines for the configuration combo button
	 * @author GrafR
	 *
	 */
	public enum DataModelConfiguration {
		PLANETS, 
		PREMIS;
	}
	
	/**
	 * Configuration models definition
	 * @author GrafR
	 *
	 */
	public enum ModelConfiguration {
		DIGITAL_OBJECT_MODEL, 
		PREMIS_MODEL;
		public enum PremisModel {
			OBJECT_IDENTIFIER,
			ORIGINAL_NAME,
			OBJECT_CHARACTERISTICS,
			RELATIONSHIP,
			SIGNIFICANT_PROPERTIES,
			LINKING_EVENT_IDENTIFIERS;
			public enum ObjectIdentifier {
				TYPE,
				VALUE
			}
			public enum ObjectCharacteristics {
				FORMAT
			}
			public enum Relationship {
				RELATED_OBJECT_IDENTIFIER; 
				public enum RelatedObjectIdentifier {
					TYPE, 
					VALUE;
				}
			}
			public enum SignificantProperties {
				SIGNIFICANT_PROPERTY; 
				public enum SignificantProperty {
					TYPE, 
					VALUE, 
					EXTENSION;
				}
			}
			public enum LinkingEventIdentifier{
				LINKING_EVENT_IDENTIFIER; 
				public enum EventIdentifier{
					EVENT_IDENTIFIER, 
					EVENT_DATETIME, 
					EVENT_DETAIL,
					LINKING_AGENT_IDENTIFIER,
					LINKING_OBJECT_IDENTIFIER; 
					public enum EvnetId {
						EVENT_IDENTIFIER_TYPE, 
						EVENT_IDENTIFIER_VALUE
					}
					public enum LinkingAgentIdentifier{
						AGENT_IDENTIFIER, 
						AGENT_NAME, 
						AGENT_TYPE;
						public enum AgentIdentifier {
							TYPE,
							VALUE
						}
					}
					public enum LinkingObjectIdentifier{
						SIGNIFICANT_PROPERTY; 
						public enum SignificantProperty {
							TYPE, 
							VALUE, 
							EXTENSION, 
							EXTENSION1, 
							EXTENSION2, 
							EXTENSION3; 
						}
					}
				}
			}
		}

		public enum DigitalObjectModel {
			PLANETS_URI, 
			TITLE,
			FORMAT,
			MANIFESTATION_OF,
		    METADATA_LIST,
		    EVENTS;
			public enum MetadataList {
				METADATA; 
				public enum Metadata {
					TYPE, 
					CONTENT, 
					NAME; 
				}
			}
			public enum EventsList {
				EVENT; 
				public enum Event {
					SUMMARY, 
					DATETIME, 
					DURATION,
					AGENT,
					PROPERTIES; 
					public enum Agent {
						AGENT; 
						public enum AgentObject {
							ID, 
							NAME, 
							TYPE; 
						}
					}
					public enum PropertiesList {
						PROPERTY; 
						public enum Property {
							URI, 
							NAME, 
							VALUE,
							DESCRIPTION,
							UNIT,
							TYPE; 
						}
					}
				}
			}
		}

	}

	private static DataModelConfiguration currentConfiguration = DataModelConfiguration.PLANETS;

	private static Logger log = Logger.getLogger(StorageBackingBean.class
			.getName());

	// The Data Registry:
	private static StorageDigitalObjectDirectoryLister dr = new StorageDigitalObjectDirectoryLister();

	// The currently viewed DR entities
	private FileTreeNode[] currentItems;
	
	// The current URI/position in the DR:
	private URI location = null;

	private TreeNodeImpl rootNode = new TreeNodeImpl();
	private TreeNodeImpl doRootNode = new TreeNodeImpl();
	
	// tree
	private static String ALL_REGISTRIES = "all_registries";
	private static String selectedRegistry = ALL_REGISTRIES;
	private static String selectedRegistryOld = null;
	private static String NODE_ID = "0";
	private static String CHILD_NODE_ID = "1.1";

	private Map<String, TreeNode> dndSelNodes = new HashMap<String, TreeNode>();

    private TreeNode selNode;
    
    private static List<String> registryList = new ArrayList<String>();
    
	
	/**
	 * Constructor for the UseBackingBean, this populates the user manager and
	 * user members
	 */
	public StorageBackingBean() {
		log.info("StorageBackingBean()");
		selectedRegistry = ALL_REGISTRIES;
		loadTree();
	}

	/**
	 * Selection in the registry combo box was changed.
	 * @param e
	 */
	public void selectionChanged(ValueChangeEvent e) {
		log
				.info("StorageBackingBean selectionChanged(comboBox) currentConfiguration: "
						+ currentConfiguration);
		log.info("StorageBackingBean selectionChanged() selectedRegistry: "
				+ selectedRegistry);
		loadTree();
	}

	/**
	 * This method initializes registry combo box.
	 * @return
	 */
	public List<SelectItem> getDataRegistryOptions() {
		List<SelectItem> res = new ArrayList<SelectItem>();

		res.add(new SelectItem(ALL_REGISTRIES));
		log.info("+++ StorageBackingBean getDataRegistryOptions() dors:dr.list() uri: " + null);
		StorageDigitalObjectReference[] dors = dr.list(null);
		for (int i = 0; i < dors.length; i++) {
			SelectItem si = new SelectItem(dors[i].getLeafname());
			res.add(si);
			log.info("+++ StorageBackingBean getDataRegistryOptions() add registry: " + dors[i].getLeafname());
			registryList.add(dors[i].getLeafname());
		}
		return res;
	}

	/**
	 * This method initializes configurations combo box.
	 * @return
	 */
	public List<SelectItem> getConfigurations() {
		List<SelectItem> res = new ArrayList<SelectItem>();
		for (DataModelConfiguration c : DataModelConfiguration.values()) {
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
		log.info("StorageBackingBean getList()");
		return this.currentItems;
	}

	
	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	/**
	 * This method returns current cofiguration value.
	 * @return
	 */
	public DataModelConfiguration getCurrentConfiguration() {
		return currentConfiguration;
	}

	/**
	 * This method sets current cofiguration
	 * @param currentConfiguration
	 */
	public void setCurrentConfiguration(
			DataModelConfiguration currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

	/**
	 * This method returns current tree node for registry tree.
	 * @return
	 */
	public TreeNodeImpl<String> getTreeNode() {
//		log.info("StorageBackingBean getTreeNode() rootNode: " + rootNode);		
		if (rootNode == null) {
			loadTree();
		} else {
			checkReloadTree();
		}
//		log.info("StorageBackingBean getTreeNode() return rootNode: " + rootNode);		
		return rootNode;
	}

	/**
	 * This method returns current tree node for object representation tree.
	 * @return
	 */
	public TreeNodeImpl<String> getDoTreeNode() {
		loadDoTree();
		return doRootNode;
	}

	/**
	 * This method fills registry tree with data.
	 */
	private void loadTree() {
		log.info("****** StorageBackingBean loadTree()");	
		selectedRegistryOld = selectedRegistry;
		TreeViews.standardTraverseTree(new RegistryPropertyImpl(null), rootNode);
	}

	/**
	 * This method fills object tree with data.
	 */
	private void loadDoTree() {
		log.info("****** StorageBackingBean loadDoTree()");		
		TreeDoViews.standardTraverseTree(doRootNode, this.getSelTreeNode(), currentConfiguration);
	}

	/**
	 * This method checks if registry tree should be reloaded.
	 */
	private void checkReloadTree() {
		// check if the filter or tree view has changed
//		log.info("StorageBackingBean checkReloadTree() selectedRegistry: "
//				+ selectedRegistry);	
		if (this.treeViewChanged()) {
			this.loadTree();
		}
	}

	/**
	 * Indicates if the selected registry was changed
	 * since the tree was updated the last time.
	 */
	private boolean treeViewChanged() {
//		log
//		.info("StorageBackingBean treeValueChanged() currentConfiguration: "
//				+ currentConfiguration);
//		log
//		.info("StorageBackingBean treeValueChanged() selectedRegistry: "
//				+ selectedRegistry + ", selectedRegistryOld: " + selectedRegistryOld);
    	if(selectedRegistry.equalsIgnoreCase(selectedRegistryOld)){
    		return false;
    	}
		return true;
	}

	/**
	 * This method is called if object configuration was changed in configurations
	 * combo box.
	 * @param e
	 */
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
//		log.info("StorageBackingBean processSelection()");
        HtmlTree tree = (HtmlTree) event.getComponent();
        TreeNode currentNode = tree.getTreeNode(tree.getRowKey());
		log.info("StorageBackingBean processSelection() currentNode: " + currentNode.getData() +
				", isLeaf: " + currentNode.isLeaf());
        if (currentNode.isLeaf()){
        	 this.setSelTreeNode(currentNode);
        }  
	}

	public void processDrop(DropEvent dropEvent) {
//		log.info("StorageBackingBean processDrop()");
    	// resolve drag source attributes
        UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent.getDraggableSource() : null;
        UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
        TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent.getDragValue() : null;
        TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
        
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
	public List<TreeNode> getSelNodes() {
		log.info("StorageBackingBean getSelNodes()");
		List<TreeNode> ret = new ArrayList<TreeNode>();
		for (TreeNode n : dndSelNodes.values()) {
			ret.add(n);
		}
		return ret;
	}

	/**
	 * Triggers expand/collapse on the tree
	 */
	public Boolean adviseNodeOpened(UITree tree) {
		// root always elapsed
//		log.info("StorageBackingBean adviseNodeOpened()");
		return Boolean.TRUE;
	}

	public void processPropertyDblClick(ActionEvent event) {
//		log.info("StorageBackingBean processPropertyDblClick()");
		UITreeNode srcNode = (event.getComponent().getParent() instanceof UITreeNode) ? (UITreeNode) event
				.getComponent().getParent()
				: null;
		processAddNodeActionEvent(srcNode);
	}

	private void processAddNodeActionEvent(UITreeNode srcNode) {
//		log.info("StorageBackingBean processAddNodeActionEvent()");
    	UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
    	TreeRowKey dragNodeKey = (srcNode.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) srcNode.getDragValue() : null;
    	TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
    	
    	log.info("dropped key: " + dragNodeKey);
    	
    	//add to list of selected properties
    	if(draggedNode!=null) {
    		log.info("StorageBackingBean processAddNodeActionEvent() dndSelNodes.put key: " + ((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName());
        	this.dndSelNodes.put(((RegistryPropertyImpl) draggedNode.getData()).getHumanReadableName(), draggedNode);
    	}
	}


	public void processLeafContextMenuAddProperty(ActionEvent event) {
//		log.info("StorageBackingBean processLeafContextMenuAddProperty()");
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
//		log.info("StorageBackingBean setContextMenuEnabled()");
	}

	public String getSelectedRegistry() {
		return selectedRegistry;
	}

	public void setSelectedRegistry(String selectedRegistry) {
		this.selectedRegistry = selectedRegistry;
	}

    /**
     * The current selected leaf node within the tree on which information is presented
     * @return
     */
    public TreeNode getSelTreeNode(){
    	return (TreeNode) this.selNode;
    }
    
    public void setSelTreeNode(TreeNode node){
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
//		log.info("StorageBackingBean processRemoveSelProp()");
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
//		log.info("StorageBackingBean processDetailsForSelProp()");
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
		if(o1!=null){
			log.info("StorageBackingBean processDetailsForSelProp() o1: " + (String)o1);
			//the properties of the selTreeNode are rendered in the info box
			this.setSelTreeNode(this.dndSelNodes.get((String)o1));
		}
    }


    public String getNodeTitle() {
//		log.info("StorageBackingBean getNodeTitle()");
    	if(this.getSelTreeNode()!=null)
    		if (this.getSelTreeNode() != null && this.getSelTreeNode().getData() != null) {
    			return (((RegistryPropertyImpl)this.getSelTreeNode().getData()).getName());
    		}
    	return "";
    }
    
    
    /**
     * This class is used for registries tree presentation
     * @author GrafR
     *
     */
    private static class TreeViews{
    	
    	/**
    	 * This method fills the tree nodes with data
    	 * @param cl The root presentation object
    	 * @param node The root node
    	 * @return Nodes filled with data
    	 */
    	public static TreeNode standardTraverseTree(RegistryPropertyImpl cl, TreeNode node) {
    		log.info("StorageBackingBean standardTraverseTree() cl.getUri(): " + cl.getUri());
    		StorageDigitalObjectReference[] dors = dr.list(cl.getUri());
    		log.info("StorageBackingBean standardTraverseTree() dors.length: " + dors.length);
    		
            TreeNode childClass = new TreeNodeImpl();
//			log.info("StorageBackingBean standardTraverseTree() setData for node.addChild: " + (cl.getHumanReadableName())); 
            childClass.setData(new DummyRegistryProperty(cl.getHumanReadableName()));
            childClass.setParent(node); 
			if (dors.length>0) {
	            node.removeChild(cl.getHumanReadableName()); 
				node.addChild(cl.getHumanReadableName(), childClass);
			}

    		for (int i = 0; i < dors.length; i++) {
//    			log.info("StorageBackingBean standardTraverseTree() currentConfiguration: " + currentConfiguration + 
//    					", dors[i].getLeafname(): " + dors[i].getLeafname() + ", dors[i].getUri(): " + dors[i].getUri());
    			// filter if filter set
//    			log.info("StorageBackingBean standardTraverseTree() selectedRegistry: " + selectedRegistry); 
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
//        			log.info("StorageBackingBean standardTraverseTree() process new node creation."); 
	            	try {
	                	TreeNode child = new TreeNodeImpl();
	                    RegistryPropertyImpl registryProperty = new RegistryPropertyImpl(dors[i]);
	                    
//	        			log.info("StorageBackingBean standardTraverseTree() child.setData(registryProperty): " + registryProperty +
//	        					", rp.getUri: " + registryProperty.getURI()); 
	 	                child.setData(registryProperty);
//	 	                log.info("#### StorageBackingBean standardTraverseTree() childClass.addChild registryProperty.getURI(): " + 
//	 	                		registryProperty.getURI());
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
     * This class reveals DigitalObject in GUI tree depending on current configuration view.
     * @author GrafR
     *
     */
    private static class TreeDoViews{
    	
    	/**
    	 * This method adds child node to the parent node
    	 * @param node The parent node
    	 * @param parentName The parent name
    	 * @param value The value of the child
    	 * @return The child node
    	 */
    	private static TreeNode addNode(TreeNode node, String parentName, String value) {
//            log.info("StorageBackingBean DoTreeView addNode() parentName: " + parentName + ", value: " + value);
    		TreeNodeImpl childNode = (TreeNodeImpl) addNode(node, parentName);
            TreeNodeImpl childChildNode1 = new TreeNodeImpl();
            if (value != null) {
//	            log.info("#### StorageBackingBean DoTreeView standardTraverseTree() value: " + value);
               childChildNode1.setData(value);
            } else {
	            log.info("StorageBackingBean DoTreeView standardTraverseTree() 2. ");
            }
            childChildNode1.setParent(childNode);
            childNode.addChild(parentName + CHILD_NODE_ID, childChildNode1);
            return childNode;
    	}
    	
    	
    	/**
    	 * This method adds child node without value - only directory
    	 * @param node The parent node
    	 * @param parentName The parent name
    	 * @return The child node
    	 */
    	private static TreeNode addNodeExt(TreeNode node, String parentName, int index) {
//            log.info("StorageBackingBean DoTreeView addNode() parentName: " + parentName);
            TreeNodeImpl childNode = new TreeNodeImpl();
            childNode.setData(parentName);
            childNode.setParent(node);
            String idxStr = NODE_ID;
            if (index >= 0) {
            	idxStr = Integer.toString(index);
            }
            node.removeChild(parentName + idxStr);
            node.addChild(parentName + idxStr, childNode);

            return childNode;
    	}

    	
    	/**
    	 * This method adds child node without value - only directory
    	 * @param node The parent node
    	 * @param parentName The parent name
    	 * @return The child node
    	 */
    	private static TreeNode addNode(TreeNode node, String parentName) {
//            log.info("StorageBackingBean DoTreeView addNode() parentName: " + parentName);
    		return addNodeExt(node, parentName, 0);
    	}

    	
    	/**
    	 * This method adds metadata to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeMetadata(TreeNode childNode, DigitalObject o) {
        	List<Metadata> metadataList = o.getMetadata();   
        	if (metadataList != null) {
        		ListIterator<Metadata> iterMeta = metadataList.listIterator();
    		
                if (metadataList.size() > 0) 
                {
                	TreeNode metadatasNode = addNode(childNode, ModelConfiguration.DigitalObjectModel.METADATA_LIST.name());
                	int index = 0;
        			while(iterMeta.hasNext())
        			{
                    	TreeNode metadataNode = addNodeExt(metadatasNode, 
                    		ModelConfiguration.DigitalObjectModel.MetadataList.METADATA.name(), index);
        				Metadata metadataObj = iterMeta.next();
        				try
        				{
                        	if (metadataObj.getType() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.DigitalObjectModel.MetadataList.Metadata.TYPE.name(), metadataObj.getType().toString());
                        	}
                        	if (metadataObj.getContent() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.DigitalObjectModel.MetadataList.Metadata.CONTENT.name(), metadataObj.getContent());
                        	}
                        	if (metadataObj.getName() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.DigitalObjectModel.MetadataList.Metadata.NAME.name(), metadataObj.getName());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "metadataList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}    	
    	}

    	
    	/**
    	 * This method adds significant properties to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeSignificantProperties(TreeNode childNode, DigitalObject o) {
        	List<Metadata> metadataList = o.getMetadata();   
        	if (metadataList != null) {
        		ListIterator<Metadata> iterMeta = metadataList.listIterator();
    		
                if (metadataList.size() > 0) 
                {
                	TreeNode metadatasNode = addNode(childNode, ModelConfiguration.PremisModel.SIGNIFICANT_PROPERTIES.name());
                	int index = 0;
        			while(iterMeta.hasNext())
        			{
                    	TreeNode metadataNode = addNodeExt(metadatasNode, 
                    		ModelConfiguration.PremisModel.SignificantProperties.SIGNIFICANT_PROPERTY.name(), index);
        				Metadata metadataObj = iterMeta.next();
        				try
        				{
                        	if (metadataObj.getType() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.PremisModel.SignificantProperties.SignificantProperty.TYPE.name(), metadataObj.getType().toString());
                        	}
                        	if (metadataObj.getContent() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.PremisModel.SignificantProperties.SignificantProperty.VALUE.name(), metadataObj.getContent());
                        	}
                        	if (metadataObj.getName() != null) {
                        		addNode(metadataNode, 
                        			ModelConfiguration.PremisModel.SignificantProperties.SignificantProperty.EXTENSION.name(), metadataObj.getName());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "metadataList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}    	
    	}

    	
    	/**
    	 * This method adds agent to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeAgent(TreeNode eventNode, Event eventObj) {
        	Agent agentObj = eventObj.getAgent();   
        	if (agentObj != null) {
            	TreeNode agentNode = addNode(eventNode, ModelConfiguration.DigitalObjectModel.EventsList.Event.AGENT.name());
				try
				{
                	if (agentObj.getId() != null) {
                		addNode(agentNode, 
                			ModelConfiguration.DigitalObjectModel.EventsList.Event.Agent.AgentObject.ID.name(), agentObj.getId());
                	}
                	if (agentObj.getName() != null) {
                		addNode(agentNode, 
                			ModelConfiguration.DigitalObjectModel.EventsList.Event.Agent.AgentObject.NAME.name(), agentObj.getName());
                	}
                	if (agentObj.getType() != null) {
                		addNode(agentNode, 
                			ModelConfiguration.DigitalObjectModel.EventsList.Event.Agent.AgentObject.TYPE.name(), agentObj.getType());
                	}
				} catch (Exception e)
				{
					log.log(Level.INFO, "agent error: " + e.getMessage(), e);
				}
        	}    	
    	}


    	/**
    	 * This method adds linking agent to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeLinkingAgentId(TreeNode eventNode, Event eventObj) {
        	Agent agentObj = eventObj.getAgent();   
        	if (agentObj != null) {
            	TreeNode agentNode = addNode(eventNode, 
            			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LINKING_AGENT_IDENTIFIER.name());
				try
				{
                	TreeNode agentIdNode = addNode(agentNode, 
                			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingAgentIdentifier.AGENT_IDENTIFIER.name());
                	if (agentObj.getId() != null) {
                		addNode(agentIdNode, ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingAgentIdentifier.AgentIdentifier.TYPE.name(), "String");
                		addNode(agentIdNode, ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingAgentIdentifier.AgentIdentifier.VALUE.name(), agentObj.getId());
                	}
                	if (agentObj.getName() != null) {
                		addNode(agentNode, 
                			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingAgentIdentifier.AGENT_NAME.name(), agentObj.getName());
                	}
                	if (agentObj.getType() != null) {
                		addNode(agentNode, 
                			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingAgentIdentifier.AGENT_TYPE.name(), agentObj.getType());
                	}
				} catch (Exception e)
				{
					log.log(Level.INFO, "agent error: " + e.getMessage(), e);
				}
        	}    	
    	}


    	/**
    	 * This method adds properties to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeProperties(TreeNode eventNode, Event eventObj) {
        	List<Property> propertyList = eventObj.getProperties();   
        	if (propertyList != null) {
        		ListIterator<Property> iter = propertyList.listIterator();
    		
                if (propertyList.size() > 0) 
                {
                	TreeNode propertiesNode = addNode(eventNode, 
                			ModelConfiguration.DigitalObjectModel.EventsList.Event.PROPERTIES.name());
                	int index = 0;
                	while(iter.hasNext())
        			{
                    	TreeNode propertyNode = addNodeExt(propertiesNode, 
                    			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.PROPERTY.name(), index);
        				Property propertyObj = iter.next();
        				try
        				{
                        	if (propertyObj.getUri() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.URI.name(), propertyObj.getUri().toString());
                        	}
                        	if (propertyObj.getName() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.NAME.name(), propertyObj.getName());
                        	}
                        	if (propertyObj.getValue() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.VALUE.name(), propertyObj.getValue());
                        	}
                        	if (propertyObj.getDescription() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.DESCRIPTION.name(), propertyObj.getDescription());
                        	}
                        	if (propertyObj.getUnit() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.UNIT.name(), propertyObj.getUnit());
                        	}
                        	if (propertyObj.getType() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.DigitalObjectModel.EventsList.Event.PropertiesList.Property.TYPE.name(), propertyObj.getType());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "propertyList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}
    	}
    	
    	
    	/**
    	 * This method adds object identifier to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeLinkingObjIdentifier(TreeNode eventNode, Event eventObj) {
        	List<Property> propertyList = eventObj.getProperties();   
        	if (propertyList != null) {
        		ListIterator<Property> iter = propertyList.listIterator();
    		
                if (propertyList.size() > 0) 
                {
                	TreeNode propertiesNode = addNode(eventNode, 
                			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LINKING_OBJECT_IDENTIFIER.name());
                	int index = 0;
                	while(iter.hasNext())
        			{
                    	TreeNode propertyNode = addNodeExt(propertiesNode, 
                    			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SIGNIFICANT_PROPERTY.name(),
                    			index);
        				Property propertyObj = iter.next();
        				try
        				{                        	
                        	if (propertyObj.getType() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.TYPE.name(), propertyObj.getType());
                        	}
                        	if (propertyObj.getValue() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.VALUE.name(), propertyObj.getValue());
                        	}
                        	if (propertyObj.getUri() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.EXTENSION.name(), propertyObj.getUri().toString());
                        	}
                        	if (propertyObj.getName() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.EXTENSION1.name(), propertyObj.getName());
                        	}
                        	if (propertyObj.getDescription() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.EXTENSION2.name(), propertyObj.getDescription());
                        	}
                        	if (propertyObj.getUnit() != null) {
                        		addNode(propertyNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.LinkingObjectIdentifier.SignificantProperty.EXTENSION3.name(), propertyObj.getUnit());
                        	}
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "propertyList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}
    	}
    	
    	
    	/**
    	 * This method adds events to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeEvents(TreeNode childNode, DigitalObject o) {
        	// describe events
        	List<Event> eventList = o.getEvents();   
        	if (eventList != null) {
        		ListIterator<Event> iter = eventList.listIterator();
    		
                if (eventList.size() > 0) 
                {
                	TreeNode eventsNode = addNode(childNode, ModelConfiguration.DigitalObjectModel.EVENTS.name());
                	int index = 0;
        			while(iter.hasNext())
        			{
                    	TreeNode eventNode = addNodeExt(
                    			eventsNode, ModelConfiguration.DigitalObjectModel.EventsList.EVENT.name(), index);
        				Event eventObj = iter.next();
        				try
        				{
                        	if (eventObj.getDatetime() != null) {
                        		addNode(eventNode, ModelConfiguration.DigitalObjectModel.EventsList.Event.DATETIME.name(), eventObj.getDatetime());
                        	}
                        	if (eventObj.getSummary() != null) {
                        		addNode(eventNode, ModelConfiguration.DigitalObjectModel.EventsList.Event.SUMMARY.name(), eventObj.getSummary());
                        	}
                        	if (eventObj.getDuration() >= 0) {
                        		addNode(eventNode, ModelConfiguration.DigitalObjectModel.EventsList.Event.DURATION.name(), String.valueOf(eventObj.getDuration()));
                        	}
                        	describeAgent(eventNode, eventObj);
                        	describeProperties(eventNode, eventObj);
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "eventList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}
    	}
    	
    	
    	/**
    	 * This method adds linking events to the object view tree.
    	 * @param childNode The node containing added data
    	 * @param o The digital object
    	 */
    	private static void describeLinkingEventIds(TreeNode childNode, DigitalObject o) {
        	// describe events
        	List<Event> eventList = o.getEvents();   
        	if (eventList != null) {
        		ListIterator<Event> iter = eventList.listIterator();
    		
                if (eventList.size() > 0) 
                {
                	TreeNode eventsNode = addNode(childNode, ModelConfiguration.PremisModel.LINKING_EVENT_IDENTIFIERS.name());
                	int index = 0;
        			while(iter.hasNext())
        			{
                    	TreeNode eventNode = addNodeExt(eventsNode, 
                    			ModelConfiguration.PremisModel.LinkingEventIdentifier.LINKING_EVENT_IDENTIFIER.name(), 
                    			index);
        				Event eventObj = iter.next();
        				try
        				{
                        	TreeNode eventIdNode = addNode(eventNode, 
                        			ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.EVENT_IDENTIFIER.name());
    	                	if (eventObj.getSummary() != null) {
    	                		addNode(eventIdNode, 
    	                				ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.EvnetId.EVENT_IDENTIFIER_TYPE.name(), "String");
    	                		addNode(eventIdNode, 
    	                				ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.EvnetId.EVENT_IDENTIFIER_VALUE.name(), eventObj.getSummary());
    	                	}
                        	if (eventObj.getDatetime() != null) {
                        		addNode(eventNode, 
                        				ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.EVENT_DATETIME.name(), eventObj.getDatetime());
                        	}
                        	if (eventObj.getDuration() >= 0) {
                        		addNode(eventNode, 
                        				ModelConfiguration.PremisModel.LinkingEventIdentifier.EventIdentifier.EVENT_DETAIL.name(), String.valueOf(eventObj.getDuration()));
                        	}
                        	describeLinkingAgentId(eventNode, eventObj);
                        	describeLinkingObjIdentifier(eventNode, eventObj);
        				} catch (Exception e)
        				{
        					log.log(Level.INFO, "eventList error: " + e.getMessage(), e);
        				}
        				index++;
        			}
                }
        	}
    	}
    	
    	
    	/**
    	 * This method fills the tree nodes with data
    	 * @param cl The root presentation object
    	 * @param node The root node
    	 * @param _node Selected node
    	 * @param currentConfiguration Presentation view 
    	 * @return Nodes filled with data
    	 * @return
    	 */
    	public static TreeNode standardTraverseTree(
    			TreeNode node, TreeNode _node, DataModelConfiguration currentConfiguration) {
	           DigitalObject o = null;
            	
               if (_node != null) {
	                if (_node.getData() != null) {
	                   URI uri = ((RegistryPropertyImpl)_node.getData()).getUri();
	    	            log.info("#### StorageBackingBean DoTreeView standardTraverseTree() uri: " + uri);
						try {
						   o = dr.getDataManager(uri).retrieve(uri);
						} catch (Exception e) {
							log.info("StorageBackingBean DoTreeView standardTraverseTree() error: " + e.getMessage());
						}
	                   if (o != null)
	                   log.info("StorageBackingBean DoTreeView standardTraverseTree() digitalObject: " + o.toString());
	                }
               }

               if (o != null) {
                   log.info("StorageBackingBean DoTreeView standardTraverseTree() o != null currentConfiguration: " + currentConfiguration);
            	   if (currentConfiguration.equals(DataModelConfiguration.PLANETS)) {
		                TreeNodeImpl childNode = new TreeNodeImpl();
		                childNode.setData(ModelConfiguration.DIGITAL_OBJECT_MODEL.name());
		                childNode.setParent(node);
		                node.removeChild(NODE_ID);
		                node.addChild(NODE_ID, childNode);
	
	                	if (o.getTitle() != null) {
	                		addNode(childNode, ModelConfiguration.DigitalObjectModel.TITLE.name(), o.getTitle());
	                	}
	                	if (o.getPermanentUri() != null) {
	                		addNode(childNode, ModelConfiguration.DigitalObjectModel.PLANETS_URI.name(), 
	                				o.getPermanentUri().toString());
	                	}
	                	if (o.getFormat() != null) {
	                		addNode(childNode, ModelConfiguration.DigitalObjectModel.FORMAT.name(), 
	                				o.getFormat().toString());
	                	}
	                	if (o.getManifestationOf() != null) {
	                		addNode(childNode, ModelConfiguration.DigitalObjectModel.MANIFESTATION_OF.name(), 
	                				o.getManifestationOf().toString());
	                	}
	                	describeMetadata(childNode, o);
	                	describeEvents(childNode, o);
            	   } else {
		                TreeNodeImpl childNode = new TreeNodeImpl();
		                childNode.setData(ModelConfiguration.PREMIS_MODEL.name());
		                childNode.setParent(node);
		                node.removeChild(NODE_ID);
		                node.addChild(NODE_ID, childNode);
	
                    	TreeNode idNode = addNode(childNode, ModelConfiguration.PremisModel.OBJECT_IDENTIFIER.name());
	                	if (o.getPermanentUri() != null) {
	                		addNode(idNode, ModelConfiguration.PremisModel.ObjectIdentifier.TYPE.name(), "URI");
	                		addNode(idNode, ModelConfiguration.PremisModel.ObjectIdentifier.VALUE.name(), 
	                				o.getPermanentUri().toString());
	                	}
	                	if (o.getTitle() != null) {
	                		addNode(childNode, ModelConfiguration.PremisModel.ORIGINAL_NAME.name(), o.getTitle());
	                	}
                    	TreeNode charNode = addNode(childNode, ModelConfiguration.PremisModel.OBJECT_CHARACTERISTICS.name());
	                	if (o.getFormat() != null) {
	                		addNode(charNode, ModelConfiguration.PremisModel.ObjectCharacteristics.FORMAT.name(), 
	                				o.getFormat().toString());
	                	}
                    	TreeNode relationshipNode = addNode(childNode, ModelConfiguration.PremisModel.RELATIONSHIP.name());
                    	TreeNode relObjIdNode = addNode(relationshipNode, 
                    			ModelConfiguration.PremisModel.Relationship.RELATED_OBJECT_IDENTIFIER.name());
	                	if (o.getManifestationOf() != null) {
	                		addNode(idNode, 
	                				ModelConfiguration.PremisModel.Relationship.RelatedObjectIdentifier.TYPE.name(), "URI");
	                		addNode(idNode, ModelConfiguration.PremisModel.Relationship.RelatedObjectIdentifier.VALUE.name(), 
	                				o.getManifestationOf().toString());
	                	}
	                	describeSignificantProperties(childNode, o);
	                	describeLinkingEventIds(childNode, o);            		   
            	   }
                } 
            return node;
        }
    	
    }
    

    /**
     * This class is used for GUI tree presentation.
     * @author GrafR
     *
     */
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
