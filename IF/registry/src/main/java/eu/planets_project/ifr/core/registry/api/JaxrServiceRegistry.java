package eu.planets_project.ifr.core.registry.api;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.wsf.common.ObjectNameFactory;

import eu.planets_project.ifr.core.registry.api.model.BindingList;
import eu.planets_project.ifr.core.registry.api.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceList;
import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * API access to the IF service registry via JAXR.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class JaxrServiceRegistry implements ServiceRegistry,
        ServiceDescriptionRegistry {

    /***/
    private static final String LOG_CONFIG_FILE = "eu/planets_project/ifr/core/registry/servreg-log4j.xml";
    /***/
    private static Log LOG = LogFactory.getLog(JaxrServiceRegistry.class
            .getName());
    /** The managed instances. */
    private static Map<String, JaxrServiceRegistry> instances = new HashMap<String, JaxrServiceRegistry>();
    /** Write access to the actual registry. */
    private BusinessLifeCycleManager blcm;
    /** Query access to the actual registry. */
    private BusinessQueryManager bqm;
    private String password;
    private String username;

    /**
     * We enforce factory usage with a private constructor.
     * @param username The username
     * @param password The password
     */
    private JaxrServiceRegistry(final String username, final String password) {
        this.username = username;
        this.password = password;
        RegistryService service = configure(username, password);
        try {
            this.bqm = service.getBusinessQueryManager();
            this.blcm = service.getBusinessLifeCycleManager();
        } catch (JAXRException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param username The username
     * @param password The password
     * @return Returns an instance for the given credentials
     */
    public static JaxrServiceRegistry getInstance(final String username,
            final String password) {
        JaxrServiceRegistry access = instances.get(username);
        if (access == null) {
            access = new JaxrServiceRegistry(username, password);
            instances.put(username, access);
        }
        return access;
    }

    /** Enumeration of attributes for the registry configuration. */
    private enum Attribute {
        /***/
        INQUIRY_URL("javax.xml.registry.queryManagerURL", System.getProperty(
                "jaxr.query.url", "http://localhost:8080/juddi/inquiry")),
        /***/
        PUBLISH_URL("javax.xml.registry.lifeCycleManagerURL", System
                .getProperty("jaxr.publish.url",
                        "http://localhost:8080/juddi/publish")),
        /***/
        FACTORY_CLASS("javax.xml.registry.ConnectionFactoryClass", "org.apache.ws.scout.registry.ConnectionFactoryImpl"),
        // com.sun.xml.registry.uddi.ConnectionFactoryImpl
        // org.apache.ws.scout.registry.ConnectionFactoryImpl
        /***/
        TRANSPORT_CLASS("juddi.proxy.transportClass", "org.jboss.jaxr.juddi.transport.SaajTransport"),
        /***/
        TAXONOMY_PATH_0("com.sun.xml.registry.userTaxonomyFilenames", System
                .getProperty("jboss.home.dir")
                + "/server/default/conf/registryconcepts.xml"),
        /***/
        TAXONOMY_PATH_1("userTaxonomyFilenames", System
                .getProperty("jboss.home.dir")
                + "/server/default/conf/registryconcepts.xml");
        /***/
        private String key;
        /***/
        private String val;

        /**
         * @param key The key
         * @param val The val
         */
        private Attribute(final String key, final String val) {
            this.key = key;
            this.val = val;
        }
    }

    /***/
    private static Properties properties = null;

    static {
        /* Set up local properties: */
        properties = new Properties();
        for (Attribute a : Attribute.values()) {
            properties.setProperty(a.key, a.val);
            System.setProperty(a.key, a.val);
        }
    }

    /**
     * @param username The username
     * @param password The password
     * @return Returns the registry service, which makes the lifecycle and the
     *         query managers available, or null
     */
    private static RegistryService configure(final String username,
            final String password) {
        initJuddiService();
        RegistryService rs = null;
        try {
            /*
             * Create the connection factory, passing it the configuration
             * properties:
             */
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(properties);
            rs = null;
            if (username != null && password != null) {
                PasswordAuthentication passwdAuth = new PasswordAuthentication(
                        username, password.toCharArray());
                HashSet<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
                creds.add(passwdAuth);
                Connection connection = factory.createConnection();
                connection.setCredentials(creds);
                rs = connection.getRegistryService();
                if (rs.getBusinessLifeCycleManager() != null) {
                    LOG
                            .debug("Both username & pass given, publish api will be accessible via business lifecycle manager");
                }
            } else {
                Connection connection = factory.createConnection();
                rs = connection.getRegistryService();
                LOG
                        .debug("No username & pass given, only inquiry api will be accessible via business query manager");
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * This stuff became necessary after switching to the metro stack and JBoss
     * 4.2.2; it is adopted from the JAXR tests that come with the JBoss metro
     * stack release.
     */
    private static void initJuddiService() {
        try {
            final ObjectName objectName = ObjectNameFactory
                    .create("jboss:service=juddi");
            Hashtable<String, String> table = new Hashtable<String, String>();
            /*
             * TODO this should probably go into the jndi.properties file in
             * server/default/conf, which then needs to be on the classpath
             */
            table.put("java.naming.factory.initial",
                    "org.jnp.interfaces.NamingContextFactory");
            table.put("java.naming.factory.url.pkgs",
                    "org.jboss.naming:org.jnp.interfaces");
            table.put("java.naming.provider.url", "jnp://localhost:1099");
            InitialContext iniCtx = new InitialContext(table);
            MBeanServerConnection server = (MBeanServerConnection) iniCtx
                    .lookup("jmx/invoker/RMIAdaptor");
            server.invoke(objectName, "setCreateOnStart",
                    new Object[] { Boolean.TRUE }, new String[] { Boolean.TYPE
                            .getName() });
            server.invoke(objectName, "stop", null, null);
            server.invoke(objectName, "start", null, null);
        } catch (InstanceNotFoundException e1) {
            e1.printStackTrace();
        } catch (MBeanException e1) {
            e1.printStackTrace();
        } catch (ReflectionException e1) {
            e1.printStackTrace();
        } catch (NamingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @param serviceId The service ID
     * @param classificationString The classification string
     * @return Returns a response containing the result of this operation
     */
    BulkResponse saveFreeClassification(final String serviceId,
            final String classificationString) {
        BulkResponse response = null;
        try {
            Service service = JaxrServiceRegistryHelper.findServiceByKey(
                    serviceId, bqm);
            ServiceTaxonomy stax = new ServiceTaxonomy(blcm, bqm);
            PsService s = new PsService(service.getName().getValue(), service
                    .getDescription().getValue());
            s.setKey(service.getKey().getId());
            stax.setFreeCategory(s, classificationString);
            // FIXME return something meaningful
            return null;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param serviceId The service ID
     * @param classificationId The classification ID (see findTaxonomy for
     *        available categories)
     * @return Returns a response containing the result of this operation
     */
    BulkResponse savePredefinedClassification(final String serviceId,
            final String classificationId) {
        try {
            Service service = JaxrServiceRegistryHelper.findServiceByKey(
                    serviceId, bqm);
            ServiceTaxonomy stax = new ServiceTaxonomy(blcm, bqm);
            PsService s = PsService.of(service);
            stax.addClassification(s, classificationId);
            // FIXME return something meaningful
            return null;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param name The service name
     * @param description The service description
     * @param orgKey The ID of the organization registering the service
     * @return Returns a response containing the result of this operation
     */
    Service createService(final String name, final String description,
            final String orgKey) {
        Service service = null;
        try {
            /* Create the service and set its values: */
            service = blcm.createService(blcm.createInternationalString(name));
            service.setName(blcm.createInternationalString(name));
            service.setDescription(blcm.createInternationalString(description));
            Organization organization = JaxrServiceRegistryHelper
                    .fetchOrganizationByKey(orgKey, bqm);
            if (organization != null) {
                service.setProvidingOrganization(organization);
                organization.addService(service);
            } else {
                throw new IllegalStateException(
                        "Could not fetch organization: " + orgKey);
            }
            BulkResponse br = blcm.saveServices(Arrays.asList(service));
            service.setKey((Key) br.getCollection().iterator().next());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return service;
    }

    /**
     * @param name The name for the binding
     * @param description The description of the binding
     * @param accessUri The binding URI
     * @param isValidateUri The validate URI attribute
     * @param serviceKey The service key
     * @return Returns a response containing the result of this operation
     */
    ServiceBinding createBinding(final String name, final String description,
            final String accessUri, final boolean isValidateUri,
            final String serviceKey) {
        ServiceBinding serviceBinding = null;
        try {
            Service service = JaxrServiceRegistryHelper.findServiceByKey(
                    serviceKey, bqm);
            serviceBinding = blcm.createServiceBinding();
            serviceBinding.setDescription(blcm
                    .createInternationalString(description));
            serviceBinding.setValidateURI(isValidateUri);
            serviceBinding.setName(blcm.createInternationalString(name));
            serviceBinding.setAccessURI(accessUri);
            List<ServiceBinding> bindings = Arrays.asList(serviceBinding);
            service.addServiceBindings(bindings);
            BulkResponse br = blcm.saveServiceBindings(bindings);
            serviceBinding.setKey((Key) br.getCollection().iterator().next());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return serviceBinding;
    }

    /**
     * @return Returns the service taxonomy
     */
    ServiceTaxonomy findTaxonomy() {
        try {
            return new ServiceTaxonomy(blcm, bqm);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param query The query
     * @return Returns a response containing the result of this operation
     */
    BulkResponse findOrganizations(final String query) {
        try {
            BulkResponse br = bqm.findOrganizations(Arrays
                    .asList(FindQualifier.SORT_BY_NAME_DESC), Arrays
                    .asList(query), null, null, null, null);
            fixOrgsServices(br);
            return br;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param queryStr The query string
     * @param categoryId The category ID
     * @return Returns a response containing the result of this operation
     */
    BulkResponse findServices(final String queryStr, final String categoryId) {
        Collection<Service> services;
        ServiceTaxonomy stax;
        try {
            BulkResponse br = bqm.findServices(null, Arrays
                    .asList(FindQualifier.SORT_BY_NAME_ASC), Arrays
                    .asList(queryStr), null, null);
            services = br.getCollection();
            stax = new ServiceTaxonomy(blcm, bqm);
            /*
             * FIXME: actually, we should be giving the keys to the query
             * manager as one of the collections above... but that won't support
             * both our free and predefined classifications.
             */
            List<Service> filtered = new ArrayList<Service>();
            stax.fixClassifications(services);
            if (categoryId.length() != 0) {
                /*
                 * Filter out those that do not match, supporting multiple
                 * comma-separated search strings:
                 */
                List<String> list = Arrays.asList(categoryId.split(","));
                for (Service service : services) {
                    LOG.info("Service selection by category: " + categoryId);
                    if (stax.included(service, list)) {
                        filtered.add(service);
                    }
                }
            } else {
                LOG.info("Service selection by name: " + queryStr);
                filtered = new ArrayList<Service>(services);
            }
            br.getCollection().clear();
            br.getCollection().addAll(filtered);
            return br;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param servKey The service ID
     * @return Returns a response containing the result of this operation
     */
    BulkResponse findBindings(final String servKey) {
        BulkResponse br = null;
        try {
            br = bqm.findServiceBindings(blcm.createKey(servKey), Arrays
                    .asList(FindQualifier.SORT_BY_NAME_DESC), null, null);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return br;
    }

    /**
     * @return Returns all bindings
     */
    BulkResponse findBindings() {
        try {
            BulkResponse response = bqm
                    .getRegistryObjects(LifeCycleManager.SERVICE_BINDING);
            Collection<ServiceBinding> bs = response.getCollection();
            for (ServiceBinding b : bs) {
                findTaxonomy()
                        .fixClassifications(Arrays.asList(b.getService()));
            }
            return response;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Returns all organizations
     */
    BulkResponse findOrganizations() {
        try {
            BulkResponse response = bqm
                    .getRegistryObjects(LifeCycleManager.ORGANIZATION);
            fixOrgsServices(response);
            return response;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param organization The org to delete
     * @return Returns a response
     */
    BulkResponse deleteJaxrOrganization(final Organization organization) {
        BulkResponse response = null;
        try {
            response = blcm.deleteOrganizations(Arrays.asList(organization
                    .getKey()));
            // Before, we had to delete all the org's services here...
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param service The service to delete
     * @return Returns a response
     */
    BulkResponse deleteJaxrService(final Service service) {
        BulkResponse response = null;
        try {
            response = blcm.deleteServices(Arrays.asList(service.getKey()));
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        findTaxonomy().removeService(service);
        return response;
    }

    /**
     * @param serviceBinding The binding to delete
     * @return Retruns a response
     */
    BulkResponse deleteJaxrBinding(final ServiceBinding serviceBinding) {
        BulkResponse response = null;
        try {
            response = blcm.deleteServiceBindings(Arrays.asList(serviceBinding
                    .getKey()));
            serviceBinding.getService().removeServiceBinding(serviceBinding);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param response The response containing orgs to have their services fixed
     */
    void fixOrgsServices(final BulkResponse response) {
        Collection<Organization> orgs;
        try {
            orgs = response.getCollection();
            for (Organization o : orgs) {
                findTaxonomy().fixClassifications(o.getServices());
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param value The string value
     * @return Retruns an internaitonal string for the given string
     */
    InternationalString internationalString(final String value) {
        try {
            return blcm.createInternationalString(value);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param service The existing service to be saved back to the registry
     * @return Returns a response to the operation
     */
    BulkResponse saveService(final Service service) {
        try {
            return blcm.saveServices(Arrays.asList(service));
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#clear(java.lang.String,
     *      java.lang.String)
     */
    public ServiceRegistryMessage clear(final String username,
            final String password) {
        try {
            Collection<Organization> organizations = bqm.getRegistryObjects(
                    LifeCycleManager.ORGANIZATION).getCollection();
            for (Organization organization : organizations) {
                BulkResponse deleteOrganization = deleteJaxrOrganization(organization);
                if (deleteOrganization.getStatus() != JAXRResponse.STATUS_SUCCESS) {
                    return new ServiceRegistryMessage(
                            "Could not delete organization: " + organization);
                }
            }
            // FIXME we should not have orphant services
            Collection<Service> services = bqm.getRegistryObjects(
                    LifeCycleManager.SERVICE).getCollection();
            for (Service service : services) {
                BulkResponse deleteService = deleteJaxrService(service);
                if (deleteService.getStatus() != JAXRResponse.STATUS_SUCCESS) {
                    return new ServiceRegistryMessage(
                            "Could not delete service: " + service);
                }
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return new ServiceRegistryMessage("Cleared registry.");
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#findBindings(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public BindingList findBindings(final String username,
            final String password, final String servKey) {
        String message = "Finding bindings; ";
        BindingList bindingsList = new BindingList();
        BulkResponse br = findBindings(servKey);
        message = JaxrServiceRegistryHelper.check(br, message);
        try {
            if (JaxrServiceRegistryHelper.successful(br)) {
                Collection<ServiceBinding> serviceBindings = br.getCollection();
                for (ServiceBinding serviceBinding : serviceBindings) {
                    PsBinding psBinding = PsBinding.of(serviceBinding);
                    bindingsList.bindings.add(psBinding);
                }
            } else {
                bindingsList.errorMessage = (message);
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        LOG.info(message);
        return bindingsList;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#findOrganizations(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public OrganizationList findOrganizations(final String username,
            final String password, final String query) {
        String message = "Finding organizations; ";
        OrganizationList resultList = new OrganizationList();
        BulkResponse response = findOrganizations(query);
        message = JaxrServiceRegistryHelper.check(response, message);
        try {
            if (JaxrServiceRegistryHelper.successful(response)) {
                Collection<Organization> organizations = response
                        .getCollection();
                for (Organization organization : organizations) {
                    PsOrganization psOrganization = PsOrganization
                            .of(organization);
                    resultList.organizations.add(psOrganization);
                }
            } else {
                resultList.errorMessage = (message);
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        LOG.info(message);
        return resultList;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#findServices(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public ServiceList findServices(final String username,
            final String password, final String query, final String category) {
        String message = "Finding services; ";
        ServiceList psServiceList = new ServiceList();
        BulkResponse br = findServices(query, category);
        message = JaxrServiceRegistryHelper.check(br, message);
        try {
            if (JaxrServiceRegistryHelper.successful(br)) {
                Collection<Service> services = br.getCollection();
                for (Service service : services) {
                    /* Create a PsService for each service in the registry: */
                    PsService pService = PsService.of(service);
                    psServiceList.services.add(pService);
                }
            } else {
                psServiceList.errorMessage = (message);
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        LOG.info(message);
        return psServiceList;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#findTaxonomy(java.lang.String,
     *      java.lang.String)
     */
    public ServiceTaxonomy findTaxonomy(final String username,
            final String password) {
        String message = "Getting taxonomy; ";
        PsSchema planetsServiceScheme = new PsSchema();
        ServiceTaxonomy st = findTaxonomy();
        LOG.info(message);
        return st;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#saveBinding(java.lang.String,
     *      java.lang.String,
     *      eu.planets_project.ifr.core.registry.api.model.PsBinding)
     */
    public ServiceRegistryMessage saveBinding(final String username,
            final String password, final PsBinding binding) {
        String string = "Binding save; ";
        if (binding.getService() == null) {
            throw new NullPointerException(
                    "Service is null; binding must have an associated service;");
        }
        if (binding.getService().getKey() == null) {
            throw new NullPointerException("Service key is null;");
        }
        ServiceRegistryMessage message = new ServiceRegistryMessage();
        ServiceBinding jaxrBinding = binding.toJaxrBinding(blcm);
        Service s = JaxrServiceRegistryHelper.findServiceByKey(binding
                .getService().getKey(), bqm);
        try {
            s.addServiceBinding(jaxrBinding);
            BulkResponse response = blcm.saveServiceBindings(Arrays
                    .asList(jaxrBinding));
            jaxrBinding
                    .setKey((Key) response.getCollection().iterator().next());
            binding.setKey(jaxrBinding.getKey().getId());
            message.registryObject = binding;
            message.message = JaxrServiceRegistryHelper.check(response, string);
            LOG.info(message);
        } catch (JAXRException e1) {
            e1.printStackTrace();
        }
        if (jaxrBinding != null) {
            try {
                message.operands.add(jaxrBinding.getKey().getId());
            } catch (JAXRException e) {
                e.printStackTrace();
            }
            message.message = ("Saved binding with key: " + message.operands);
        } else {
            message.message = ("No success in saving service binding.");
        }
        LOG.info(message.message);
        return message;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#saveFreeClassification(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public ServiceRegistryMessage saveFreeClassification(final String username,
            final String password, final String serviceId,
            final String classificationString) {
        BulkResponse response = saveFreeClassification(serviceId,
                classificationString);
        String message = "Set free classification; ";
        message = JaxrServiceRegistryHelper.check(response, message);
        LOG.info(message);
        return new ServiceRegistryMessage(message);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#saveOrganization(java.lang.String,
     *      java.lang.String,
     *      eu.planets_project.ifr.core.registry.api.model.PsOrganization)
     */
    public ServiceRegistryMessage saveOrganization(final String username,
            final String password, final PsOrganization organization) {
        String message = "Org save; ";
        ServiceRegistryMessage psRegistryMessage = new ServiceRegistryMessage(
                message);
        Organization o = organization.toJaxrOrganization(blcm, bqm);
        try {
            BulkResponse response = blcm.saveOrganizations(Arrays.asList(o));
            String id = ((Key) response.getCollection().iterator().next())
                    .getId();
            organization.setKey(id);
            message = JaxrServiceRegistryHelper.check(response, message);
            psRegistryMessage.message = message;
            psRegistryMessage.operands.add(id);
            psRegistryMessage.registryObject = organization;
            LOG.info(message);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return psRegistryMessage;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#savePredefinedClassification(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public ServiceRegistryMessage savePredefinedClassification(
            final String username, final String password,
            final String serviceId, final String classificationId) {
        BulkResponse response = savePredefinedClassification(serviceId,
                classificationId);
        String message = "Adding classification; ";
        message = JaxrServiceRegistryHelper.check(response, message);
        LOG.info(message);
        return new ServiceRegistryMessage(message);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#saveService(java.lang.String,
     *      java.lang.String,
     *      eu.planets_project.ifr.core.registry.api.model.PsService)
     */
    public ServiceRegistryMessage saveService(final String username,
            final String password, final PsService psService) {
        if (psService.getOrganization() == null) {
            throw new NullPointerException("Service has no organization;");
        }
        if (psService.getOrganization().getKey() == null) {
            throw new NullPointerException("Service organization key is null;");
        }
        String messageText = "Saving service; ";
        ServiceRegistryMessage message = new ServiceRegistryMessage();
        Service service = psService.toJaxrService(blcm, bqm);
        try {
            BulkResponse saveServices = blcm.saveServices(Arrays
                    .asList(service));
            if (saveServices.getStatus() == JAXRResponse.STATUS_SUCCESS) {
                LOG.info("Success: " + saveServices.toString());
            } else {
                throw new IllegalStateException("Failed to save service "
                        + service.getKey() + " with response: "
                        + saveServices.getCollection());
            }
            String id = ((Key) saveServices.getCollection().iterator().next())
                    .getId();
            psService.setKey(id);
            message.registryObject = psService;
        } catch (JAXRException e1) {
            e1.printStackTrace();
        }
        messageText = messageText + (service == null ? "no success" : service);
        if (service != null) {
            try {
                message.operands.add(service.getKey().getId());
            } catch (JAXRException e) {
                e.printStackTrace();
            }
        }
        message.message = (messageText);
        LOG.info(message.message);
        return message;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#deleteBinding(eu.planets_project.ifr.core.registry.api.model.PsBinding)
     */
    public ServiceRegistryMessage deleteBinding(final String username,
            final String password, final PsBinding serviceBinding) {
        try {
            BulkResponse response = blcm.deleteServiceBindings(Arrays
                    .asList(blcm.createKey(serviceBinding.getKey())));
            return new ServiceRegistryMessage(response.getCollection()
                    .toString());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return new ServiceRegistryMessage("Could not delete binding"
                + serviceBinding);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#deleteOrganization(eu.planets_project.ifr.core.registry.api.model.PsOrganization)
     */
    public ServiceRegistryMessage deleteOrganization(final String username,
            final String password, final PsOrganization organization) {
        try {
            BulkResponse response = blcm.deleteOrganizations(Arrays.asList(blcm
                    .createKey(organization.getKey())));
            return new ServiceRegistryMessage(response.getCollection()
                    .toString());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return new ServiceRegistryMessage("Could not delete organization"
                + organization);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#deleteService(eu.planets_project.ifr.core.registry.api.model.PsService)
     */
    public ServiceRegistryMessage deleteService(final String username,
            final String password, final PsService service) {
        try {
            BulkResponse response = blcm.deleteServices(Arrays.asList(blcm
                    .createKey(service.getKey())));
            return new ServiceRegistryMessage(response.getCollection()
                    .toString());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return new ServiceRegistryMessage("Could not delete service" + service);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#find(java.lang.String)
     */
    public List<ServiceDescription> find(final String serviceName) {
        ServiceList found = this.findServices(username, password, serviceName,
                "");
        List<ServiceDescription> descriptions = new ArrayList<ServiceDescription>();
        List<PsService> list = found.services;
        for (PsService psService : list) {
            descriptions.add(ServiceRegistryObjectFactory
                    .descriptionOf(psService));
        }
        return descriptions;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public ServiceRegistryMessage register(final ServiceDescription service) {
        ServiceRegistryObjectFactory factory = new ServiceRegistryObjectFactory(
                username, password, this);
        PsService psService = factory.serviceOf(service);
        ServiceRegistryMessage message = new ServiceRegistryMessage();
        message.registryObject = psService;
        return message;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#clear()
     */
    public ServiceRegistryMessage clear() {
        ServiceRegistryMessage clear = this.clear(username, password);
        return clear;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceRegistry#findServicesForInputFormats(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String[])
     */
    public ServiceList findServicesForInputFormats(String username,
            String password, String type, String... inputFormats) {
        ServiceList services = findServices(username, password, "%",
                type == null ? "" : findTaxonomy().getPsSchema().getId(type));
        List<PsService> result = new ArrayList<PsService>();
        if (inputFormats == null || inputFormats.length == 0) {
            return services;
        }
        for (PsService psService : services.services) {
            List<PsCategory> categories = psService.getCategories();
            for (PsCategory psCategory : categories) {
                String code = psCategory.code;
                String[] split = code.substring(1, code.length() - 1)
                        .split(",");
                List<String> formats = new ArrayList<String>();
                for (String string : split) {
                    formats.add(string.trim());
                }
                if (formats.containsAll(Arrays.asList(inputFormats))) {
                    result.add(psService);
                }
            }
        }
        services.services = result;
        return services;
    }
}
