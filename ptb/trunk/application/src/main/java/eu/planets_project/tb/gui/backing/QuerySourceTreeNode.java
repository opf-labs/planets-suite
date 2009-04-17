package eu.planets_project.tb.gui.backing;

import java.util.List;
import java.util.ArrayList;

import eu.planets_project.tb.impl.data.demo.queryable.QuerySource;
import eu.planets_project.tb.impl.data.demo.queryable.QuerySourceCategory;

import org.apache.myfaces.custom.tree2.TreeNode;

public class QuerySourceTreeNode implements TreeNode {
    static final long serialVersionUID = 72362318283823293l;
    
    /**
     * Type identifiers
     */
    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_QUERYSOURCE = "querysource";
        
    /**
     * Reference to Query Source
     */
    private QuerySource reference = null;
    
    /**
     * Constructor based on query source name & URL.
     * A URL of <code>null</code> indicates a category of sources
     * rather than an individual source.
     */
    public QuerySourceTreeNode(QuerySource reference) {
   		this.reference = reference;
    }
    
    public String getIdentifier() {
    	return this.reference.getName();
    }
    
    public void setIdentifier(String identifier) {
    	// read only
    }
    
    public String getType() {
    	if (this.reference instanceof QuerySourceCategory)
    		return TYPE_CATEGORY;
    	
    	return TYPE_QUERYSOURCE;
    }
    
    public void setType(String type) {
    	//
    }
    
    public boolean isLeaf() {
    	return !(this.reference instanceof QuerySourceCategory);
    }
    
    public void setLeaf(boolean leaf) {
    	//
    }
    
    public String getDescription() {
    	return null;
    }
    
    public void setDescription(String description) {
    	//
    }
    
    public List<TreeNode> getChildren() {
    	ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
    	
    	if (this.reference instanceof QuerySourceCategory) {
    		ArrayList<QuerySource> qSources = ((QuerySourceCategory) this.reference).getChildren();
    		for (int i=0; i<qSources.size(); i++) {
    			nodes.add(new QuerySourceTreeNode(qSources.get(i)));
    		}
    	}
		
		return nodes;
    }
    
    public int getChildCount() {
    	return getChildren().size();
    }
      
    public String getLeafname(){
    	return this.reference.getName();
    }
    
    public QuerySource getQuerySource() {
    	return this.reference;
    }
    
}
