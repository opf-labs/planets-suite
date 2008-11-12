package eu.planets_project.ifr.core.wdt.impl.registry;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsRegistryObject;
import eu.planets_project.ifr.core.registry.api.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;

public class RegistryClient {

    private Log logger = PlanetsLogger.getLogger(this.getClass());

    private static String USER = "provider";
    private static String PWD = "provider";

    private ServiceRegistry registry = null;
    private QName qName = new QName(
            "http://planets-project.eu/ifr/core/registry",
            "ServiceRegistryManager");
    private URL endpoint = null;
    public String[] categoryIds = null;
    PsOrganization org = null;

    public RegistryClient(URL endpoint) {
        this.endpoint = endpoint;
        initialize();
    }

    public void initialize() {

        Service service = Service.create(this.endpoint, new QName(
                ServiceRegistry.NS, ServiceRegistry.NAME));
        registry = service.getPort(ServiceRegistry.class);

        // ServiceRegistryManager_Service locator = new
        // ServiceRegistryManager_Service(this.endpoint, this.qName);
        // registry = locator.getServiceRegistryManagerPort();
        String orgId = null;

        // find available categories
        PsSchema schema = registry.findTaxonomy(USER, PWD).getPsSchema();
        List<PsCategory> categories = schema.categories;
        String taxonomyId = schema.schemaId;
        logger.error("Schema ID: " + taxonomyId);

        categoryIds = new String[categories.size()];

        // populate category array
        for (int t = 0; t < categories.size(); t++) {
            PsCategory category = categories.get(t);
            String catCode = category.code;
            categoryIds[t] = category.id;
            logger.error("found category code: " + catCode + " id: "
                    + categoryIds[t]);
        }

        // init organization
        OrganizationList orgList = registry.findOrganizations(USER, PWD, "%");
        List<PsOrganization> orgs = orgList.organizations;

        // find organization
        for (int k = 0; k < orgs.size(); k++) {
            org = orgs.get(k);
            PsRegistryObject obj = (PsRegistryObject) org;
            orgId = obj.getKey();
            logger.debug("found org key: " + orgId);
        }
    }

    /**
     * Method to add a service to the registry
     * 
     * @return serviceId
     */
    public String registerService(String name, String dsc) throws Exception {

        String serviceId = null;
        PsService service = new PsService();

        service.setName(name);
        service.setDescription(dsc);
        service.setOrganization((org));

        ServiceRegistryMessage rMsg = registry.saveService(USER, PWD, service);

        // String msg = rMsg.getMessage();
        // logger.error("saved service message: "+msg);
        List<String> operands = rMsg.operands;
        // find ServiceId
        for (int y = 0; y < operands.size(); y++) {
            serviceId = operands.get(y);
            // logger.error("saved service key: "+serviceId);
        }
        return serviceId;
    }

    /**
     * Method to add a binding to the registry
     */
    public void registerBinding(String serviceId, String name, String dsc,
            String url) throws Exception {
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID is null;");
        }
        PsService service = new PsService();
        service.setKey(serviceId);
        // create Binding
        PsBinding binding = new PsBinding();
        binding.setService(service);
        binding.setName(name);
        binding.setDescription(dsc);
        binding.setAccessURI(url);
        // logger.error("register binding for servicee key: "+binding.getService(
        // ).getKey());
        ServiceRegistryMessage rMsg = registry.saveBinding(USER, PWD, binding);
    }

    /**
     * Method to add a category to a service
     * 
     * @param category see categoryIds planetsservice[0], characterisation[1],
     *        emulation[2], migration[3], identification[4], validation[5],
     *        metadataextraction[6], composite[7], comparison[8]
     */
    public void addCategory(String serviceId, String categoryId)
            throws Exception {
        registry.savePredefinedClassification(USER, PWD, serviceId, categoryId);
    }

    public QName getQName() {
        return qName;
    }

    public void setQName(QName qName) {
        this.qName = qName;
    }

    public URL getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URL endpoint) {
        this.endpoint = endpoint;
    }
}