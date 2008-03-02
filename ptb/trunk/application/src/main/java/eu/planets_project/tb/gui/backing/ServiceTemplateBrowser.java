/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.util.Collection;
import java.util.Iterator;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;

/**
 * backing bean for the servicetemplate and serviceopartion browsing screen in reader mode
 * @author alindley
 * 
 */
public class ServiceTemplateBrowser {
	
	private TreeNode treeData;
	private TreeModelBase TreeModel;
	private String SelectedNode; 
	private String currentNodeID;
	private boolean displayWSDLContent=false;
 
    public ServiceTemplateBrowser() {
    	
    	treeData = new TreeNodeBase("root","Available Service Templates","IDroot",false);
    	initTreeDataFromServiceTemplateRegistry();
    }
    
    
    public TreeNode getTreeData(){
        //TreeNodeBase(type, description, identifier, leaf)
        return treeData;
     }

     public void setTreeData( TreeNode treeData ){
    	 this.treeData = treeData;
    	 }
     public String getSelectedNode(){
    	 return SelectedNode;
    	 }
     public void setSelectedNode(String selectedNode){
    	 SelectedNode = selectedNode;
     }
     
     public TreeModelBase getTreeModel(){
    	 return TreeModel;
    	 }
     public void setTreeModel(TreeModelBase treeModel){
    	 TreeModel = treeModel;
     } 
     
     /**
      * Sets the location where within the tree a command link action happened
     * @param location
     */
    public void setLocation(String currentNodeID){
    	 this.currentNodeID = currentNodeID;
    }
    
    public String getLocation(){
    	return this.currentNodeID;
    }
    
    
    /**
     * Queries the ServiceTemplateRegistry and fills up the tree's root node
     */
    public void initTreeDataFromServiceTemplateRegistry(){
    	ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
    	Collection<TestbedServiceTemplate> templates = registry.getAllServices();
    	if((templates!=null)&&(templates.size()>0)){
    		Iterator<TestbedServiceTemplate> itTemplates = templates.iterator();
    		//iterate over all templates
    		while(itTemplates.hasNext()){
    			//the current template: iterate over all Serviceperations
    			TestbedServiceTemplate template = itTemplates.next();
    			//build new child node containing a serviceTemplate
    			TreeNodeBase nodeServiceTemplate = new TreeNodeBase("TBServiceTemplate",template.getName(),template.getUUID(),false); 
    			
    			//addServiceTemplate properties
    			nodeServiceTemplate.getChildren().add(new TreeNodeBase("Description",template.getDescription(),true));
    			nodeServiceTemplate.getChildren().add(new TreeNodeBase("Endpoint",template.getEndpoint(),true));
    			nodeServiceTemplate.getChildren().add(new TreeNodeBase("WSDLContent",template.getWSDLContent(),true));
    			nodeServiceTemplate.getChildren().add(new TreeNodeBase("UUID",template.getUUID(),true));
    			nodeServiceTemplate.getChildren().add(new TreeNodeBase("DeploymentDate",template.getDeploymentDate().getTime()+"",true));
    			
    			Collection<ServiceTag> tags = template.getAllTags();
    			if((tags!=null)&&(tags.size()>0)){
    				Iterator<ServiceTag> itTags = tags.iterator();
    				while(itTags.hasNext()){
    					ServiceTag tag = itTags.next();
    					TreeNodeBase nodeTag = new TreeNodeBase("Tag",tag.getName(),tag.getName(),false); 
    					nodeTag.getChildren().add(new TreeNodeBase("TagValue",tag.getValue(),true));
    					nodeTag.getChildren().add(new TreeNodeBase("TagDescription",tag.getDescription(),true));
    					nodeTag.getChildren().add(new TreeNodeBase("TagPriority",tag.getPriority()+"",true));
    					
    					//add to serviceTemplate node
    					nodeServiceTemplate.getChildren().add(nodeTag);
    				}
    			}
    			
    			Collection<ServiceOperation> operations = template.getAllServiceOperations();
    			if((operations!=null)&&(operations.size()>0)){
    				//Iterate over all serviceOperations
    				Iterator<ServiceOperation> itOperations = operations.iterator();
    				while(itOperations.hasNext()){
    					ServiceOperation operation = itOperations.next();
    					TreeNodeBase nodeOp = new TreeNodeBase("operation",operation.getName(), operation.getName(), false);
    					//att leaf nodes with properties
    					nodeOp.getChildren().add(new TreeNodeBase("OpDescription",operation.getDescription(),true));
    					nodeOp.getChildren().add(new TreeNodeBase("OutputObjectType",operation.getOutputObjectType(),true));
    					nodeOp.getChildren().add(new TreeNodeBase("ServiceOperationType",operation.getServiceOperationType(),true));
    					nodeOp.getChildren().add(new TreeNodeBase("maxInputFiles",operation.getMaxSupportedInputFiles()+"",true));
    					nodeOp.getChildren().add(new TreeNodeBase("minReqFiles",operation.getMinRequiredInputFiles()+"",true));
    				
    					//add nodeOp to ServiceTemplate
    					nodeServiceTemplate.getChildren().add(nodeOp);
    				}
    			}
    			else{
    				//no operations --> add service as leaf node
    				nodeServiceTemplate.setLeaf(true);
    			}
    			
    			//add the serviceTemplate to the root node of the tree
        		this.treeData.getChildren().add(nodeServiceTemplate);
    		}
    	}
    }
    
    /**
     * Indicates to reload the table's source
     */
    public String reloadDataFromDB(){
    	treeData = new TreeNodeBase("root","Available Service Templates","IDroot",false);
    	initTreeDataFromServiceTemplateRegistry();
    	return "reload-page";
    }

    /**
     * Triggers the display of WSDL content
     */
    public void closeWSDLContent(){
    	this.displayWSDLContent = false;
    }
    
    public void openWSDLContent(){
    	this.displayWSDLContent = true;
    }
    
    public boolean isWSDLContentOpen(){
    	return this.displayWSDLContent;
    }
     
}
