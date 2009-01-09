/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.tb.gui.backing.service.PathwayBean;
import eu.planets_project.tb.impl.services.Service;
import eu.planets_project.tb.impl.services.ServiceRegistryManager;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;


/**
 * TODO Merge in the ServiceTemplates and the IF Service Registry - all endpoints only once, of course.
 * TODO Add in storage of service records.
 * TODO Add in service active/inactive status, and service history information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceBrowser {
    /** */
    private static final Log log = LogFactory.getLog(ServiceBrowser.class);
    /** */
    public static final String CATEGORY = "category";
    public static final String SERVICE = "service";
    public static final String UNKNOWN_SERVICE = "Unrecognised Services";
    
    private List<Service> services;
    
    private TreeNode<ServiceTreeItem> rootNode = null;
    private Service selectedService = null;    
    
    private String nodeTitle;
    
    // Get the format registry:
    public static  FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
    public static Format unknown = fr.getFormatForURI( Format.extensionToURI("unknown") );

    /**
     * 
     */
    public class ServiceTreeItem {
        String type;
        String category;
        Service service;

        /**
         * @param s
         */
        public ServiceTreeItem(Service s) {
            this.type = SERVICE;
            this.service  = s;
        }

        /**
         * @param type
         */
        public ServiceTreeItem(String category) {
            this.type = CATEGORY;
            this.category = category;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @return the category
         */
        public String getCategory() {
            return category;
        }

        /**
         * @return the service
         */
        public Service getService() {
            return service;
        }
        
    }
    
    
    /**
     * 
     * @return
     */
    public List<Service> getAvailableServices() {
        if( services == null )
            services = ServiceRegistryManager.listAvailableServices();
        return services;
    }
    
    /**
     * 
     */
    private void loadTree() {
        rootNode = new TreeNodeImpl<ServiceTreeItem>();
        // For PA services
        TreeNode<ServiceTreeItem> paCat = addCategoryNode("Preservation Action", rootNode );
        addServiceTypeTree("Migrate", "eu.planets_project.services.migrate", paCat );
        // For PC services
        TreeNode<ServiceTreeItem> pcCat = addCategoryNode("Preservation Characterisation", rootNode );
        addServiceTypeTree("Identify", "eu.planets_project.services.identify", pcCat );
        addServiceTypeTree("Validate", "eu.planets_project.services.validate", pcCat );
        addServiceTypeTree("DetermineProperties", "eu.planets_project.services.characterise", pcCat );
        // For unknown/unrecognised services:
        addServiceTypeTree(UNKNOWN_SERVICE, "unknown", rootNode );
    }

    /**
     * 
     * @param category
     * @param parentNode
     * @return
     */
    private TreeNode<ServiceTreeItem> addCategoryNode( String category, TreeNode<ServiceTreeItem> parentNode ) {
        TreeNodeImpl<ServiceTreeItem> categoryNode = new TreeNodeImpl<ServiceTreeItem>();
        categoryNode.setData( new ServiceTreeItem(category) );
        parentNode.addChild(category, categoryNode);
        return categoryNode;
    }
    
    /**
     * 
     * @param type
     * @param typeClass
     * @param node
     */
    private void addServiceTypeTree( String type, String typeClass, TreeNode<ServiceTreeItem> node ) {
        TreeNodeImpl<ServiceTreeItem> categoryNode = new TreeNodeImpl<ServiceTreeItem>();
        categoryNode.setData( new ServiceTreeItem(type) );
        node.addChild(type, categoryNode);
        addNodes(typeClass, categoryNode, this.getAvailableServices());
    }
    
    /**
     * 
     * @param type
     * @param node
     * @param slist
     */
    private void addNodes(String type, TreeNode<ServiceTreeItem> node, List<Service> slist ) {
        for( int i = 0; i < slist.size(); i++ ) {
            Service s = slist.get(i);
            if( s.getType().startsWith(type) ) {
                TreeNodeImpl<ServiceTreeItem> nodeImpl = new TreeNodeImpl<ServiceTreeItem>();
                nodeImpl.setData( new ServiceTreeItem(s) );
                node.addChild(new Integer(i), nodeImpl);
            }
        }
    }
    
    /**
     * 
     * @param event
     */
    public void processSelection(NodeSelectedEvent event) {
        //log.info("Dealing with event: "+event);
        HtmlTree tree = (HtmlTree) event.getComponent();
        ServiceTreeItem currentNode = (ServiceTreeItem) tree.getRowData();
        if( currentNode.getType().equals(SERVICE) ) {
            nodeTitle = currentNode.getService().getName();
            selectedService = currentNode.getService();
        } else if( currentNode.getType().equals(CATEGORY) ) {
            // TODO Summarise the category?
        }
    }
    
    /**
     * This locks the tree into a particular state, and is not in use at present.
     * 
     * @param uitree
     * @return
     */
    public boolean adviseNodeExpanded( UITree uitree ) 
    {
        ServiceTreeItem sti = (ServiceTreeItem) uitree.getTreeNode().getData();
        log.info("Current ServiceTreeItem of type: " + sti.getType() + " : " + sti.getCategory() );
        if( CATEGORY.equals(sti.getType()) ) {
            if( UNKNOWN_SERVICE.equals(sti.getCategory())) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.FALSE;
        }
    }
    
    public TreeNode<ServiceTreeItem> getTreeNode() {
        if (rootNode == null) {
            loadTree();
        }
        
        return rootNode;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }


    /**
     * @return the selectedService
     */
    public Service getSelectedService() {
        return selectedService;
    }
    
    /**
     * @return A list of SelectItems populated with the identify service endpoints.
     */
    public List<SelectItem> getIdentifyServicesSelectList() {
        return createServiceList(Identify.class.getCanonicalName());
    }
    
    /**
     * @return A list of SelectItems populated with the identify service endpoints.
     */
    public List<SelectItem> getMigrateServicesSelectList() {
        return createServiceList(Migrate.class.getCanonicalName());
    }

    /**
     * @param type
     * @return
     */
    private static List<SelectItem> createServiceList( String type ) {
        return mapServicesToSelectList( getListOfServices(type) );
    }

    /**
     * @param type
     * @return
     */
    public static List<ServiceDescription> getListOfServices( String type ) {
        //Instantiate a registry:
        Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());

        //Get all services
        //List<ServiceDescription> identifiers = registry.query(null); //If you pass a ServiceDescription with fields filled in it will query against matches.
        
        // List Identify services:
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, type).build();

        //To register a service
        //Response = registry.register(ServiceDescription)
        
        return registry.query(sdQuery);
    }

    /**
     * 
     * @return
     */
    public List<ServiceDescription> getAllServices() {
        return getListOfServices(null);
    }
    
    /**
     * 
     * @return
     */
    public List<PathwayBean> getAllPathways() {
        List<ServiceDescription> sds = getListOfServices(null);
        List<PathwayBean> paths = new ArrayList<PathwayBean>();
        for( ServiceDescription sd : sds ) {
            for( MigrationPath path : sd.getPaths() ) {
                PathwayBean pb = new PathwayBean( sd.getName(), path.getInputFormat(), path.getOutputFormat() );
                paths.add(pb);
            }
        }
        return paths;
    }
    
    /**
     * @param sdlist
     * @return
     */
    public static List<SelectItem> mapServicesToSelectList( List<ServiceDescription> sdlist ) {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        for( ServiceDescription sd : sdlist ) {
            slist.add( createServiceSelectItem(sd) );
        }
        return slist;
    }

    /**
     * @param sd
     * @return
     */
    private static SelectItem createServiceSelectItem( ServiceDescription sd ) {
        String serviceName = sd.getName();
        serviceName += " (@"+sd.getEndpoint().getHost()+")";
        return new SelectItem( sd.getEndpoint(), serviceName );
    }
    
    /**
     * @param formats
     * @return
     */
    public static List<SelectItem> mapFormatURIsToSelectList( Set<URI> formats ) {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        List<URI> formatList = new ArrayList<URI>(formats);
        Collections.sort(formatList);
        for( URI fmt : formatList ) {
            slist.add( createFormatURISelectItem(fmt) );
        }
        return slist;
    }

    /**
     * @param fmt
     * @return
     */
    private static SelectItem createFormatURISelectItem( URI fmt ) {
        return new SelectItem( fmt.toString(), fmt.toString() );
    }
    
}
