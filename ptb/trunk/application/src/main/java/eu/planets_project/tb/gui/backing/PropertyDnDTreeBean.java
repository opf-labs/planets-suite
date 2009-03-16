package eu.planets_project.tb.gui.backing;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.tree.DefaultMutableTreeNode;
import org.richfaces.component.UIDragSupport;
import org.richfaces.component.UITreeNode;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.DropEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.TreeRowKey;

import sun.util.logging.resources.logging;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.impl.model.ontology.OntologyHandlerImpl;
import eu.planets_project.tb.impl.model.ontology.OntologyPropertyImpl;

/**
 * This bean renders a given OWL ontology model in a tree structure and is backing
 * the page property_tree_browser.xhtml
 *  - implements multiple selectable view representations of the ontology
 *  - It allows selecting properties by drag and drop of its node elements
 *  - searching the tree by full-text query
 *  - returns a standard TreeNode representation of the ontology
 *  - node data contain information in form of a Testbed: OntologyProperty object
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 09.03.2009
 *
 */
public class PropertyDnDTreeBean{
    
	//the ontology model the bean extract it's information from
	private OWLModel owlModel;
	//customizing tree view by filter and multiple views on the tree
	private static String filterTreeString = null;
	private static String filterTreeStringOld = "";
	private ArrayList<SelectItem> viewsSelectItems = new ArrayList<SelectItem>();
	private String selectedview = null;
	private TreeNode rootNode = null;
	private String rootNodeName = "";
    private TreeNode selNode;
    //structure: Map<ID,TreeNode>
    private Map<String,TreeNode> dndSelNodes = new HashMap<String,TreeNode>();
    private static final String VIEW_STANDARD = "standard";
    private static final String VIEW_ROTHENBERG = "rothenberg";
    
    private static Log log = LogFactory.getLog(PropertyDnDTreeBean.class);
    
    //requires a no-arg constructor
    public PropertyDnDTreeBean(){
    	fillAvailableViews(new String[]{VIEW_STANDARD,VIEW_ROTHENBERG});
    	loadOwlOntology();
    }
    
    private void loadOwlOntology(){
    	OntologyHandlerImpl ontoHandler = OntologyHandlerImpl.getInstance();
		this.owlModel = ontoHandler.getOWLModel();
		OWLNamedClass startClass = owlModel.getOWLNamedClass("XCLOntology1:specificationPropertyNames");
		log.debug("loaded ontology."+startClass.getPrefixedName());
    }

    
    private void loadTree(boolean applyFilter){
    	
    	this.filterTreeStringOld = this.getFilterTreeString();
    	rootNode = new TreeNodeImpl();

    	if(this.selectedview.equals(VIEW_STANDARD)){
    			OWLNamedClass startClass = owlModel.getOWLNamedClass("XCLOntology1:specificationPropertyNames");
    			this.rootNodeName = startClass.getLocalName();
    			TreeViews.standardTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    	}
    	if(this.selectedview.equals(VIEW_ROTHENBERG)){
    		//TODO add rothenbergTraverseTree
    		OWLNamedClass startClass = owlModel.getOWLNamedClass("XCLOntology1:specificationPropertyNames");
    		this.rootNodeName = startClass.getLocalName();
    		TreeViews.standardTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    	}
    }
    
    public String getFilterTreeString(){
    	return filterTreeString;
    }
    
    public void setFilterTreeString(String s){
    	if(s.equals("")){
    		filterTreeString = null;
    		this.loadTree(false);
    	}else{
    		filterTreeString = s;
    	}
    }
    
    /**
     * User selected a leaf node and is interested in its data attributes
     * @param event
     */
    public void processSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        TreeNode currentNode = tree.getTreeNode(tree.getRowKey());
        if (currentNode.isLeaf()){
        	 this.setSelTreeNode(currentNode);
        }     
    }
    
    /**
     * Triggered for properties that have been selected and are rendered in the "selected properties" box
     * Displays their detailed information.
     */
    public void processDetailsForSelProp() {
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
		if(o1!=null){
			//the properties of the selTreeNode are rendered in the info box
			this.setSelTreeNode(this.dndSelNodes.get((String)o1));
		}
    }
    
    /**
     * Trigger removal of a for property that has been selected and is rendered in the "selected properties" box
     */
    public void processRemoveSelProp(){
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
		if(o1!=null){
			this.dndSelNodes.remove((String)o1);
		}
    }
    
    public void processDrop(DropEvent dropEvent) {
    	// resolve drag source attributes
        UITreeNode srcNode = (dropEvent.getDraggableSource() instanceof UITreeNode) ? (UITreeNode) dropEvent.getDraggableSource() : null;
        UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
        TreeRowKey dragNodeKey = (dropEvent.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) dropEvent.getDragValue() : null;
        TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
        
        log.debug("dropped key: "+dragNodeKey);
       
        //add to list of selected properties
        if(draggedNode!=null)
        	this.dndSelNodes.put(((OntologyProperty)draggedNode.getData()).getURI(),draggedNode);
    }
    
    public void processPropertyDblClick(ActionEvent event){
    	UITreeNode srcNode = (event.getComponent().getParent()instanceof UITreeNode) ? (UITreeNode) event.getComponent().getParent() : null;
    	UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
    	TreeRowKey dragNodeKey = (srcNode.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) srcNode.getDragValue() : null;
    	TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
    	
    	log.debug("dropped key: "+dragNodeKey);
    	
    	//add to list of selected properties
    	if(draggedNode!=null)
        	this.dndSelNodes.put(((OntologyProperty)draggedNode.getData()).getURI(),draggedNode);
    }
    
    private boolean filterStringChanged(){
    	String filter = this.getFilterTreeString();
    	if(filter.equalsIgnoreCase(this.filterTreeStringOld)){
    		return false;
    	}
    	return true;
    }
    
    public TreeNode getTreeNode() {
        if (rootNode == null) {
            loadTree(false);
        }else{
        	if(this.getFilterTreeString()!=null){
        		if(filterStringChanged()){
        			loadTree(true);
        		}
        	}
        }
        return rootNode;
    }
    

    @Deprecated
    public void setNodeTitle(String nodeTitle) {
       //
    }
    
    @Deprecated
    public String getNodeTitle() {
    	if(this.getSelTreeNode()!=null)
    		return (((OntologyProperty)this.getSelTreeNode().getData()).getName());
    	return "";
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
     * The current selected leaf node within the tree on which information is presented
     * @return
     */
    public TreeNode getSelTreeNode(){
    	return this.selNode;
    }
    
    public void setSelTreeNode(TreeNode node){
    	this.selNode = new TreeNodeImpl();
    	selNode.setData(node.getData());
    }
    
    boolean bCollapstree = true;  
    public void processCollapsExpandTree(){
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("collapsexpand");
		if(o1!=null){
			String s = (String)o1;
			if(s.equals("collaps"))
				bCollapstree = true;
			if(s.equals("expand"))
				bCollapstree = false;
		}
    }
    
    public void setCollapsTree(boolean b){}
    
    public boolean getCollapsTree(){
    	return bCollapstree;
    }
    
    /**
	 * Triggers expand/collapse on the tree
     */
    public Boolean adviseNodeOpened(UITree tree) {
        //root always elapsed 
    	if(((OntologyProperty)tree.getRowData()).getName().equals(this.rootNodeName)){return Boolean.TRUE;}
    	
    	//for all other nodes check if collapse/expand was chosen
    	if (!bCollapstree)
                return Boolean.TRUE;        
        return Boolean.FALSE;
    }
    
	/**
	 * Gets the SelectItems of all available views on the tree
	 */
	public List<SelectItem> getAvailableViews(){
		return this.viewsSelectItems;       
	}
	
	public void setAvailableViews(List<SelectItem> itemList){}
	
	public void fillAvailableViews(String[] views){
		this.viewsSelectItems.clear();
		boolean bFirst = true;
		for(String view : views){
			SelectItem viewSelectItem = new SelectItem(view);                       
			this.viewsSelectItems.add(viewSelectItem);
			if(bFirst){
				this.setSelectedViewItem((String)viewSelectItem.getValue());
				bFirst = false;
			}
		}
	}
	
	public String getSelectedViewItem(){
		return this.selectedview;
	}
	
	public void setSelectedViewItem(String value){
		this.selectedview = value;
	}       
    

    private static class TreeViews{
    	
    	public static TreeNode standardTraverseTree(OWLNamedClass cl, List stack, TreeNode node, boolean applyfilter) {

    		Collection<OWLIndividual> instances = cl.getInstances(false);    
            //adding a new category - isn't backed by any data, not even name??
            TreeNode childClass = new TreeNodeImpl();
            String instanceCountText = instances.size()>0 ? " ("+instances.size()+")" : "";
            childClass.setData(new DummyOntologyProperty(cl.getLocalName()+instanceCountText));
            //addChild(key, nodeImpl
            node.addChild(cl.getURI(),childClass);
            
            if (instances.size() > 0) {
                for (Iterator<OWLIndividual> jt = instances.iterator(); jt.hasNext();) {
                	OWLIndividual individual = (OWLIndividual) jt.next();    
                	TreeNode child = new TreeNodeImpl();
                    OntologyProperty ontologyProperty = new OntologyPropertyImpl(individual);
                    
                    boolean bMatchesFilter = true;
 	                if(applyfilter)
 	                	bMatchesFilter =  ontologyProperty.getName().toLowerCase().contains(filterTreeString.toLowerCase());
                    if(bMatchesFilter){
	 	                child.setData(ontologyProperty);
	                    childClass.addChild(ontologyProperty.getURI(), child);
	                }
                }
            }
            if (!stack.contains(cl)) {
                for (java.util.Iterator<OWLNamedClass> it = cl.getSubclasses(false).iterator(); it.hasNext();) {
                    OWLNamedClass subClass = (OWLNamedClass) it.next();
                    stack.add(cl);
                    standardTraverseTree(subClass, stack, childClass,applyfilter);
                    stack.remove(cl);
                }
            }

            //remove nodes that don't apply the given filter
            if(applyfilter){
            	Iterator it = childClass.getChildren();
	        	if(it.hasNext()){
	        		//ok node still has other leaf elements - keep it
	        	}
	        	else{
	        		node.removeChild(cl.getURI());
	        	}
            }
            
            return node;
        }
    }
    
    /**
     * used as data model of property-classes, as they are not of type OWLIndividual
     * only the .getName method is usable as no OWLIndividual is set.
     * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
     * @since 09.03.2009
     *
     */
    public static class DummyOntologyProperty extends OntologyPropertyImpl implements OntologyProperty{

    	protected DummyOntologyProperty(String name){
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
		public void setType(String s){
			//
		}
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}
		public List<OWLNamedClass> getIsSameAs() {
			// TODO Auto-generated method stub
			return null;
		}
		public List<String> getIsSameAsNames() {
			// TODO Auto-generated method stub
			return null;
		}
		public OWLIndividual getOWLIndividual() {
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
