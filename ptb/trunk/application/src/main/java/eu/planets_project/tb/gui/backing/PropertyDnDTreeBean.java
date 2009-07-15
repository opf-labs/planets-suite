package eu.planets_project.tb.gui.backing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.ajax4jsf.component.UIRepeat;
import org.ajax4jsf.component.html.HtmlAjaxCommandButton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.checkbox.HtmlCheckbox;
import org.richfaces.component.UITreeNode;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlDataTable;
import org.richfaces.component.html.HtmlInplaceSelect;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.DropEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.TreeRowKey;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
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
	private String selectedviewOld = "";
	private TreeNode rootNode = null;
	private String rootNodeName = "";
    private TreeNode selNode;
    //structure: Map<ID,TreeNode>
    private Map<String,TreeNode> dndSelNodes = new HashMap<String,TreeNode>();
    private static final String VIEW_STANDARD = "standard";
    private static final String VIEW_TBEXTENDED = "extended";
    private static final String VIEW_ROTHENBERG = "rothenberg";
    
    private static Log log = LogFactory.getLog(PropertyDnDTreeBean.class);

    // Make this session-scoped, for speed:
    OntologyHandlerImpl ontoHandler = OntologyHandlerImpl.getInstance();

    //requires a no-arg constructor
    public PropertyDnDTreeBean(){
    	fillAvailableViews(new String[]{VIEW_STANDARD,VIEW_TBEXTENDED,VIEW_ROTHENBERG});
    	loadOwlOntology();
    }
    
    private void loadOwlOntology(){
		this.owlModel = ontoHandler.getOWLModel();
		OWLNamedClass startClass = owlModel.getOWLNamedClass("XCLOntology:specificationPropertyNames");
		log.debug("loaded ontology."+startClass.getPrefixedName());
    }

    
    private void loadTree(boolean applyFilter){
    	
    	this.filterTreeStringOld = this.getFilterTreeString();
    	this.selectedviewOld = this.getSelectedViewItem();
    	
    	rootNode = new TreeNodeImpl();

    	if(this.selectedview.equals(VIEW_STANDARD)){
    			OWLNamedClass startClass = owlModel.getOWLNamedClass("XCLOntology:specificationPropertyNames");
    			this.rootNodeName = startClass.getLocalName();
    			TreeViews.standardTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    	}
    	if(this.selectedview.equals(VIEW_ROTHENBERG)){
    		OWLNamedClass startClass = owlModel.getOWLNamedClass("Testbed:RothenbergCategories");
    		this.rootNodeName = startClass.getLocalName();
    		TreeViews.standardTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    		//TreeViews.rothenbergTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    	}
    	if(this.selectedview.equals(VIEW_TBEXTENDED)){
    		OWLNamedClass startClass = owlModel.getOWLNamedClass("Testbed:TestbedProperties");
    		this.rootNodeName = startClass.getLocalName();
    		TreeViews.standardTraverseTree(startClass, new Vector(), rootNode, applyFilter);
    	}
    }
    
    public String getFilterTreeString(){
    	return filterTreeString;
    }
    
    public void setFilterTreeString(String s){
    	/*if(s.equals("")){
    		filterTreeString = null;
    	}else{
    		filterTreeString = s;
    	}*/
    	filterTreeString = s;
    	this.checkReloadTree();
    }
    
    private void checkReloadTree(){
    	//check if the filter or tree view has changed
    	if(this.filterStringChanged() || this.treeViewChanged()){
    		//check if the filter shall be applied
    		if(this.filterStringSet()){
    			this.loadTree(true);
    			return;
    		}
    		else{
    			this.loadTree(false);
    			return;
    		}
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
    	this.cbx = cbx;
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
    	processAddNodeActionEvent(srcNode);
    }
    
    /**
     * TODO: There's a bug in
     * I have a rich:tree with drag-n-drop support and an attached rich:contextMenu to the nestet rich:treeNode. if you click right on a node, the contextMenu will open. After click on the menuItem the cursor changes to the dragIndicator Icon and you drag/drop the element.	
	 * The correct behavior should be that nothing happens in terms of drag-n-drop after click on a contextMenu in a tree. 
     * https://cloud.prod.atl2.jboss.com:8443/jira/browse/RF-2516;jsessionid=146683782496939F59F8D6156B35AA9F?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aall-tabpanel
     * --> fixed, need to update version of RichFaces
     * @param event
     */
    public void processLeafContextMenuAddProperty(ActionEvent event){
    	UITreeNode srcNode = (event.getComponent().getParent().getParent()instanceof UITreeNode) ? (UITreeNode) event.getComponent().getParent().getParent() : null;
    	processAddNodeActionEvent(srcNode);
    }
    
    private void processAddNodeActionEvent(UITreeNode srcNode){
    	UITree srcTree = srcNode != null ? srcNode.getUITree() : null;
    	TreeRowKey dragNodeKey = (srcNode.getDragValue() instanceof TreeRowKey) ? (TreeRowKey) srcNode.getDragValue() : null;
    	TreeNode draggedNode = dragNodeKey != null ? srcTree.getTreeNode(dragNodeKey) : null;
    	
    	log.debug("dropped key: "+dragNodeKey);
    	
    	//add to list of selected properties
    	if(draggedNode!=null)
        	this.dndSelNodes.put(((OntologyProperty)draggedNode.getData()).getURI(),draggedNode);
    }
    
    
	/**
	 * removes all selected properties from the list
	 */
	public void removeAllSelectedProps(){
		this.dndSelNodes.clear();
	}
    
    /**
     * Indicates if the filter string changed since the last time the tree was updated
     * @return
     */
    private boolean filterStringChanged(){
    	String filter = this.getFilterTreeString();
    	if(filter==null||filter.equalsIgnoreCase(this.filterTreeStringOld)){
    		return false;
    	}
    	return true;
    }
    
    /**
     * Indicates if a filter string has been set and shall be used
     * @return
     */
    private boolean filterStringSet(){
    	if(getFilterTreeString()==null||getFilterTreeString().equals("")){
    		return false;
    	}
    	return true;
    }
    
    /**
     * Indicates if the selected treeview (rothenberg, standard, etc.) changed since 
     * the tree was updated the last time
     * @return
     */
    private boolean treeViewChanged(){
    	String treeview = this.getSelectedViewItem();
    	if(treeview.equalsIgnoreCase(this.selectedviewOld)){
    		return false;
    	}
    	return true;
    }
    
    public TreeNode getTreeNode() {
        if (rootNode == null) {
            loadTree(false);
        }else{
        	checkReloadTree();
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
    
    public int getSelNodesSize(){
    	return this.dndSelNodes.size();
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
    	if(((OntologyProperty)tree.getRowData()).getName().startsWith(this.rootNodeName)){return Boolean.TRUE;}
    	
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
	
	/********************if experiment-mode****************************/
	private UIRepeat repeater;
	public void setRepeater(UIRepeat repeater) {
        this.repeater = repeater;
    }

    public UIRepeat getRepeater() {
        return repeater;
    }
	
    private List<String> selStagePropList = new ArrayList<String>();
    public List getSelStagePropList(){
    	return selStagePropList;
    }
    
    public int getSelStagePropListSize(){
    	return selStagePropList.size();
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
        this.selPropTable = selPropTable;
    }
   
    HtmlInplaceSelect stageProp;
	public HtmlInplaceSelect getStageProp() {
		return stageProp;
	}

	public void setStageProp(HtmlInplaceSelect stageProp) {
		this.stageProp = stageProp;
	}
	
	//Structure: HashMap<PropertyID, HashMap<Stagename,sel true/false>>
	HashMap<String,HashMap<String,String>> stageProps = new HashMap<String, HashMap<String,String>>();
    
    public void selStagePropChange(ActionEvent e){
    	String selPropertyID = null, selStageName = null, selValue = null;
    	FacesContext context = FacesContext.getCurrentInstance();
    	Object o1 = context.getExternalContext().getRequestParameterMap().get("selPropID");
        if(o1!=null)
        	selPropertyID = (String)o1;
        Object o2 = context.getExternalContext().getRequestParameterMap().get("selPropStageName");
        if(o2!=null)
        	selStageName = (String)o2;
        
        Object source =  e.getComponent();
        if(source instanceof HtmlAjaxCommandButton ){
        	HtmlAjaxCommandButton  sel = (HtmlAjaxCommandButton )source;
        	selValue = (String)sel.getValue();
        }
        
        if(selPropertyID==null||selStageName==null||selValue==null)
        	return;
        
        //all required parameters received - now store the updates
        HashMap<String,String> selection;
        if(!stageProps.containsKey(selPropertyID)){
        	selection = new HashMap<String,String>();
        }
        else{
        	selection = stageProps.get(selPropertyID);
        }
        selection.put(selStageName, selValue);
        stageProps.put(selPropertyID, selection);  
    }
    
    /**
     * contains the information which propertyID was selected for which stage
     * @return
     */
    public Map<String,HashMap<String,String>> getStageSelectedState(){
		return this.stageProps;
	}
    
    /**
     * Takes the selected properties and returns their data
     * @return
     */
    public List<OntologyProperty> getSelectedOntologyProperties(){
    	List<OntologyProperty> ret = new ArrayList<OntologyProperty>();
    	List<TreeNode> nodes = this.getSelNodes();
    	for(TreeNode node : nodes){
    		ret.add((OntologyProperty)node.getData());
    	}
    	return ret;
    }
    
	
	/********************end experiment-mode***********************/
    

    private static class TreeViews{
    	
    	public static TreeNode standardTraverseTree(OWLNamedClass cl, List stack, TreeNode node, boolean applyfilter) {
    		Collection<RDFIndividual> instances = cl.getInstances(false); 

            //adding a new category - isn't backed by any data, not even name??
            TreeNode childClass = new TreeNodeImpl();
            String instanceCountText = instances.size()>0 ? " ("+instances.size()+")" : "";
            childClass.setData(new DummyOntologyProperty(cl.getLocalName()+instanceCountText));
            //addChild(key, nodeImpl
            node.addChild(cl.getURI(),childClass);

            if (instances.size() > 0) {
                for (Iterator<RDFIndividual> jt = instances.iterator(); jt.hasNext();) {
                	try{
	                	RDFIndividual individual = (RDFIndividual)jt.next();
	                	//OWLIndividual individual = (OWLIndividual) jt.next();    
	                	TreeNode child = new TreeNodeImpl();
	                    OntologyProperty ontologyProperty = new OntologyPropertyImpl(individual);
	                    
	                    boolean bMatchesFilter = true;
	 	                if(applyfilter){
	 	                	boolean b1 = ontologyProperty.getName().toLowerCase().contains(filterTreeString.toLowerCase());
	 	                	boolean b2 = ontologyProperty.getHumanReadableName().toLowerCase().contains(filterTreeString.toLowerCase());
	 	                	bMatchesFilter =  b1||b2;
	 	                }
	 	                if(bMatchesFilter){
		 	                child.setData(ontologyProperty);
		                    childClass.addChild(ontologyProperty.getURI(), child);
		                }
                	}catch(ClassCastException e){
                		log.debug("Shouldn't happen any more: Filtering out RDFIndividual");
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
		public List<RDFIndividual> getIsSameAs() {
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
