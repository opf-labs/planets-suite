/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.context.FacesContext;
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
import eu.planets_project.services.view.CreateView;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.gui.backing.service.PathwayBean;
import eu.planets_project.tb.gui.backing.service.ServiceRecordBean;
import eu.planets_project.tb.gui.backing.service.ServiceRecordsByNameBean;
import eu.planets_project.tb.gui.backing.service.ServiceRecordsByFormatBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.persistency.ServiceRecordPersistencyImpl;
import eu.planets_project.tb.impl.services.Service;
import eu.planets_project.tb.impl.services.ServiceRegistryManager;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.MatchingMode;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;


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
    private FormatBean selectedInputFormat = null;
    private FormatBean selectedOutputFormat = null;
    private ServiceRecordBean selectedServiceRecord = null;
    
    private String nodeTitle;
    
    // Get the format registry:
    public static FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
//    public static Format unknown = fr.getFormatForURI( Format.extensionToURI("unknown") );

    //Instantiate a registry:
    Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
    
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
     * @return A list of Create View services
     */
    public List<SelectItem> getCreateViewServicesSelectList() {
        return createServiceList(CreateView.class.getCanonicalName());
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
    private List<SelectItem> createServiceList( String type ) {
        return mapServicesToSelectList( getListOfServices(type) );
    }
    
    /**
     * @return Looks up the service registry, cached in ServiceBrowser in Session scope.
     */
    private static Registry instanciateServiceRegistry() {
        return PersistentRegistry.getInstance(CoreRegistry.getInstance());
    }

    /**
     * @param type
     * @return
     */
    public List<ServiceDescription> getListOfServices( String type ) {

        // If no type specified, return all:
        if( type == null) {
            return registry.query(null);
        }
        
        // List particular services:
        log.info("Looking for services of type: "+type);
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, type).build();
        
        // This is the list:
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
        List<ServiceDescription> sds = this.listAllMigrationServices();
        List<PathwayBean> paths = new ArrayList<PathwayBean>();
        for( ServiceDescription sd : sds ) {
            for( MigrationPath path : sd.getPaths() ) {
                ServiceRecordBean srb = new ServiceRecordBean(sd);
                FormatBean in = new FormatBean( ServiceBrowser.fr.getFormatForURI( path.getInputFormat() ) );
                FormatBean out = new FormatBean( ServiceBrowser.fr.getFormatForURI( path.getOutputFormat() ) );
                PathwayBean pb = new PathwayBean( srb, in, out );
                paths.add(pb);
            }
        }
        return paths;
    }
    
    /**
     * @return A list of all the formats that the available migration services can take.
     */
    public List<FormatBean> getMigrationInputFormatBeans() {
        // Look up the enabled formats:
        String endpoint = null;
        if( this.getSelectedServiceRecord() != null ) {
            endpoint = this.getSelectedServiceRecord().getEndpoint().toString();
        }
        String out = null;
        if( this.getSelectedOutputFormat() != null ) {
            out = this.getSelectedOutputFormat().getUri().toString();
        }
        Set<Format> formats =  this.getMigrationInputFormats( endpoint, out );        
            
        // Now build the full output:
        Set<FormatBean> fmts = new HashSet<FormatBean>();
        for( Format f : this.getMigrationInputFormats(null, null) ) {
            FormatBean formatBean = new FormatBean(f);
            // Make this modify if selected:
            if( formatBean.equals( this.getSelectedInputFormat() ) ) formatBean.setSelected(true);
            // Modify if enabled:
            if( ( this.getSelectedInputFormat() != null  && ! this.getSelectedInputFormat().equals(formatBean) )
                    || ! formats.contains( f ) ) formatBean.setEnabled(false);
            
            fmts.add( formatBean );
        }
        ArrayList<FormatBean> list = new ArrayList<FormatBean>(fmts);
        Collections.sort(list);
        return list;
    }
    
    /**
     * @param endpoint
     * @param outputFormat
     * @return
     */
    public Set<Format> getMigrationInputFormats(String endpoint, String outputFormat ) {
        log.info("IN: getMigrationInputFormats");
        Set<Format> formats = new HashSet<Format>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            for( MigrationPath path : sd.getPaths() ) {
                if( ( endpoint == null ) || endpoint.equals(sd.getEndpoint().toString()) ) {
                    if( ( outputFormat == null ) || outputFormat.equals(path.getOutputFormat().toString()) ) {
                        Format fmt = fr.getFormatForURI( path.getInputFormat() );
                        formats.add(fmt);
                    }
                }
            }
        }
        log.info("OUT: getMigrationInputFormats");
        return formats;
    }

    /**
     * @param endpoint
     * @param inputFormat
     * @return
     */
    public Set<Format> getMigrationOutputFormats(String endpoint, String inputFormat ) {
        log.info("IN: getMigrationOutputFormats");
        Set<Format> formats = new HashSet<Format>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            for( MigrationPath path : sd.getPaths() ) {
                if( ( endpoint == null ) || endpoint.equals(sd.getEndpoint().toString()) ) {
                    if( ( inputFormat == null ) || inputFormat.equals(path.getInputFormat().toString()) ) {
                        Format fmt = fr.getFormatForURI( path.getOutputFormat() );
                        formats.add(fmt);
                    }
                }
            }
        }
        log.info("OUT: getMigrationOutputFormats");
        return formats;
    }

    /**
     * @return A list of all the output formats that the available migration services can create.
     */
    public List<FormatBean> getMigrationOutputFormatBeans() {
        // Look up the enabled formats:
        String endpoint = null;
        if( this.getSelectedServiceRecord() != null ) {
            endpoint = this.getSelectedServiceRecord().getEndpoint().toString();
        }
        String in = null;
        if( this.getSelectedInputFormat() != null ) {
            in = this.getSelectedInputFormat().getUri().toString();
        }
        Set<Format> formats = this.getMigrationOutputFormats( endpoint, in );
        
        // Now build the full output:
        Set<FormatBean> fmts = new HashSet<FormatBean>();
        for( Format f : this.getMigrationOutputFormats(null, null) ) {
            FormatBean formatBean = new FormatBean(f);
            // Modify if selected:
            if( formatBean.equals( this.getSelectedOutputFormat() ) ) formatBean.setSelected(true);
            // Modify if enabled:
            if( ( this.getSelectedOutputFormat() != null && ! this.getSelectedOutputFormat().equals(formatBean) ) 
                    || ! formats.contains( f ) ) formatBean.setEnabled(false);
            
            fmts.add( formatBean );
        }
        ArrayList<FormatBean> list = new ArrayList<FormatBean>(fmts);
        Collections.sort(list);
        return list;
    }

    /**
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public List<ServiceDescription> getMigrationServices( String inputFormat, String outputFormat ) {
        List<ServiceDescription> sdl = new Vector<ServiceDescription>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            boolean addThis = false;
            for( MigrationPath path : sd.getPaths() ) {
                if( ( inputFormat == null ) || inputFormat.equals(path.getInputFormat().toString()) ) {
                    if( ( outputFormat == null ) || outputFormat.equals(path.getOutputFormat().toString()) ) {
                        addThis = true;
                    }
                }
            }
            if( addThis ) sdl.add(sd);
        }
        return sdl;
    }
    
    /**
     * @return
     */
    public List<ServiceRecordBean> getMigrationServiceBeans() {
        List<ServiceRecordBean> srbs = new ArrayList<ServiceRecordBean>();
        for( ServiceDescription sd : this.getMigrationServices() ) {
            ServiceRecordBean srb = new ServiceRecordBean(sd);
            // Check if this service is selected:
            if( this.getSelectedServiceRecord() != null && 
                    sd.getEndpoint().equals( this.getSelectedServiceRecord().getEndpoint() )) {
                srb.setSelected(true);
            }
            // Check if this service is compatible with the input and output formats:
            List<ServiceDescription> migrationServices = this.getSelectableMigrationServices();
            if( ( this.getSelectedServiceRecord() != null && ! this.getSelectedServiceRecord().equals(srb) ) 
                    || ! migrationServices.contains(sd) ) {
                srb.setEnabled(false);
            }
            srbs.add(srb);
        }
        return srbs;
    }

    /**
     * @return
     */
    private List<ServiceDescription> getSelectableMigrationServices() {
        String in = null;
        if( this.getSelectedInputFormat() != null ) {
            in = this.getSelectedInputFormat().getUri().toString();
        }
        String out = null;
        if( this.getSelectedOutputFormat() != null ) {
            out =  this.getSelectedOutputFormat().getUri().toString();
        }
        return this.getMigrationServices( in, out );
    }

    /**
     * @return
     */
    public List<ServiceDescription> getMigrationServices() {
        return this.getMigrationServices(null,null);
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
    
    public static List<SelectItem> mapFormatsToSelectList( Set<Format> formats ) {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        List<FormatBean> fbList = new ArrayList<FormatBean>();
        
        // Use the FormatBean to ensure consistent sorting.
        for( Format fmt : formats ) {
            fbList.add(new FormatBean(fmt));
        }
        Collections.sort(fbList);
        
        // Now map:
        for( FormatBean fb : fbList ) {
            slist.add( createFormatSelectItem(fb.getFormat()) );
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
    
    /**
     * @param fmt
     * @return
     */
    private static SelectItem createFormatSelectItem( Format fmt ) {
        return new SelectItem( fmt.getTypeURI().toString(), fmt.getSummaryAndVersion() );
    }
    
    /**
     * @param string
     * @return
     */
    public static ServiceRecordImpl createServiceRecordFromEndpoint(long eid, URL endpoint, Calendar date) {
        if( endpoint == null ) return null;
        
        ServiceRecordImpl sr = null;
        
        Registry registry = instanciateServiceRegistry();
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, null).endpoint(endpoint).build();
        
        List<ServiceDescription> result = registry.query(sdQuery);
        log.info("Got matching results: "+result);
        
        if( result != null && result.size() > 0 ) {
            ServiceDescription sd = result.get(0);
            sr = ServiceRecordImpl.createServiceRecordFromDescription(eid, sd, date);
        }
        
        return sr;
    }


    /**
     * 
     * @return
     */
    public String getExperimentServiceRecordFixLog() {
        log.info("Looking through the experiments...");
        long start = System.currentTimeMillis();
        
        ExperimentPersistencyRemote ep = ExperimentPersistencyImpl.getInstance();
        Collection<Experiment> allExps = ep.queryAllExperiments();
        log.debug("Found "+allExps.size()+" experiment(s).");
        
        // Loop through, looking for missing service records.
        for( Experiment exp: allExps) {
            log.info("Looking at experiment: "+exp.getExperimentSetup().getBasicProperties().getExperimentName());
            ExperimentExecutable executable = exp.getExperimentExecutable();
            if( executable != null && executable.getBatchExecutionRecords() != null ) {
                for( BatchExecutionRecordImpl batch: executable.getBatchExecutionRecords() )  {
                    for( ExecutionRecordImpl run : batch.getRuns() ) {
                        for( ExecutionStageRecordImpl stage : run.getStages() ) {
                            log.info("Looking at stage: " + stage.getStage());
                            ServiceRecordImpl sr = stage.getServiceRecord();
                            if( sr != null ) {
                                log.info("Got old service name: " + sr.getServiceName() );
                                log.info("Got old service endpoint: " + sr.getEndpoint() );
                                log.info("Looking to patch in new service record... "+sr.getId());
                                if( sr.getServiceDescription() != null && sr.getId() <= 0 ) {
                                    log.info("Got old service desc name: " + sr.getServiceDescription().getName() );
                                    ServiceRecordImpl newSR = ServiceRecordImpl.createServiceRecordFromDescription(exp.getEntityID(), sr.getServiceDescription(), exp.getStartDate());
                                    stage.setServiceRecord(newSR);
                                    // FIXME Removed this for now - ep.updateExperiment(exp);
                                }
                                log.info("Got old service host: " + sr.getHost() );
                                // FIXME Go through and check parameters are consistent?
                            } else {
                                log.info("Got service record = null!");
                            }
                        }
                    }
                }
            }
        }
        long finish = System.currentTimeMillis();
        log.info("Done looking: in " + (finish-start)/1000.0 + "s");
        return "success";
    }

    /**
     * @return
     */
    public List<ServiceRecordsByNameBean> getAllServiceRecordsByName() {
        HashMap<String,ServiceRecordsByNameBean> sbn = new HashMap<String,ServiceRecordsByNameBean>();
        
        // Get all the known, unique service records.
        List<ServiceRecordBean> records = this.getAllServicesAndRecords();

        // Aggregate those into a list of new service-by-name:
        for( ServiceRecordBean srb : records ) {
            if( sbn.containsKey(srb.getName()) ) {
                // Add this SRB to the content:
                sbn.get(srb.getName()).addServiceRecord(srb);
            } else {
                sbn.put(srb.getName(), new ServiceRecordsByNameBean(srb) );
            }
        }
        
        return new ArrayList<ServiceRecordsByNameBean>( sbn.values() );
    }

    /**
     * @return
     */
    public List<ServiceRecordsByFormatBean> getAllServiceRecordsByFormat() {
        HashMap<URI,ServiceRecordsByFormatBean> sbn = new HashMap<URI,ServiceRecordsByFormatBean>();

        // Get all the known, unique service records.
        List<ServiceRecordBean> records = this.getAllServicesAndRecords();

        // Aggregate those into a list of new service-by-name:
        for( ServiceRecordBean srb : records ) {
            if( srb.getInputs() != null ) {
                for( URI fmt : srb.getInputs() ) {
                    if( sbn.get(fmt) == null ) {
                        sbn.put(fmt, new ServiceRecordsByFormatBean( fr.getFormatForURI(fmt) ) );
                    }
                    sbn.get(fmt).addAsInputService(srb);
                }
            }
            if( srb.getOutputs() != null ) {
                for( URI fmt : srb.getOutputs() ) {
                    if( sbn.get(fmt) == null ) {
                        sbn.put(fmt, new ServiceRecordsByFormatBean( fr.getFormatForURI(fmt) ) );
                    }
                    sbn.get(fmt).addAsOutputService(srb);
                }
            }
        }

        return new ArrayList<ServiceRecordsByFormatBean>( sbn.values() );
    }
    
    /**
     * 
     * @return
     */
    public List<ServiceRecordBean> getAllServicesAndRecords() {
        // Use a hash map to build up the list.
        HashMap<String,ServiceRecordBean> serviceMap = new HashMap<String,ServiceRecordBean>();
        // Get the historical service records:
        ServiceRecordPersistencyRemote srp = ServiceRecordPersistencyImpl.getInstance();
        for( ServiceRecordImpl sr : srp.getAllServiceRecords() ) {
            log.info("Putting service record: "+sr.getServiceName()+" : '"+sr.getServiceHash()+"'");
            serviceMap.put(sr.getServiceHash(), new ServiceRecordBean(sr) );
        }

        // Now get the active services and patch these records in:
        List<ServiceDescription> serviceList = getListOfServices(null);
        //log.info("Query result: "+serviceList);
        if( serviceList != null ) log.info("Matched services = "+serviceList.size());
        for( ServiceDescription sd : serviceList ) {
            if( serviceMap.containsKey(""+sd.hashCode()) ) {
                log.info("Updating bean for service: "+sd.getName()+" : '"+sd.hashCode()+"'");
                serviceMap.get(""+sd.hashCode()).setServiceDescription(sd);
            } else {
                serviceMap.put(""+sd.hashCode(), new ServiceRecordBean(sd) );
                log.info("Putting in service: "+sd.getName()+" : '"+sd.hashCode()+"'");
            }
        }

        return new ArrayList<ServiceRecordBean>(serviceMap.values());
    }
    
    
    /**
     * @param the selectedInputFormat
     */
    public void setSelectedInputFormat(FormatBean fb) {
        this.selectedInputFormat = fb;
    }

    /**
     * @return the selectedOutputFormat
     */
    public FormatBean getSelectedOutputFormat() {
        return selectedOutputFormat;
    }

    /**
     * @param selectedOutputFormat the selectedOutputFormat to set
     */
    public void setSelectedOutputFormat(FormatBean selectedOutputFormat) {
        this.selectedOutputFormat = selectedOutputFormat;
    }

    /**
     * @return the selectedInputFormat
     */
    public FormatBean getSelectedInputFormat() {
        return selectedInputFormat;
    }

    /**
     * @return the selectedServiceRecord
     */
    public ServiceRecordBean getSelectedServiceRecord() {
        return selectedServiceRecord;
    }

    /**
     * @param selectedServiceRecord the selectedServiceRecord to set
     */
    public void setSelectedServiceRecord(ServiceRecordBean selectedServiceRecord) {
        this.selectedServiceRecord = selectedServiceRecord;
    }


    /* ------------------------------------------------------------------------------------ */

    // FIXME Cache this stuff automatically, in getListOfServices method.
   

    /** Name to store the look-up tables under. */
    private final static String MIGRATE_SD_CACHE_NAME = "CacheMigrationServicesCache";
    
    /**
     * @return A list of all the migration services (cached in request-scope).
     */
    @SuppressWarnings("unchecked")
    private List<ServiceDescription> listAllMigrationServices() {
        Map<String,Object> reqmap =
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        
        // Lookup or re-build:
        List<ServiceDescription> migrators = (List<ServiceDescription>) reqmap.get(MIGRATE_SD_CACHE_NAME);
        if( migrators == null ) {
            log.info("Refreshing list of migration services...");
            migrators = getListOfServices(Migrate.class.getCanonicalName());
            reqmap.put(MIGRATE_SD_CACHE_NAME, migrators);
            log.info("Refreshed.");
        }
        return migrators;
    }



}
