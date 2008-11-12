package eu.planets_project.ifr.core.wdt.impl.registry;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.logging.Log;

import com.ibm.wsdl.xml.WSDLReaderImpl;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.model.PsRegistryObject;
import eu.planets_project.ifr.core.registry.api.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.model.PsService;
import eu.planets_project.ifr.core.registry.api.model.ServiceList;

/**
 * backing bean for service registry gui components acesses the service registry
 * web service
 * 
 * @author Rainer Schmidt, ARC
 */
public class WorkflowServiceRegistry {
    // @WebServiceRef(wsdlLocation=
    // "http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"
    // )
    // ServiceRegistryManager_Service service;
    // does not inject...

    private static final String PASSWORD = "provider";
    private static final String USERNAME = "provider";
    private Log logger = PlanetsLogger.getLogger(this.getClass(),
            "resources/log/wdt-log4j.xml");
    private ServiceRegistry registry = null;
    private URL registryUrl = null;
    private List<Service> dummyServices = null;
    private RegistryClient regWriter = null;

    public WorkflowServiceRegistry() {
        // locate service registry
        try {
            registryUrl = new URL(
                    "http://localhost:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl");
            // registryUrl = new URL(
            // "http://planetarium.hki.uni-koeln.de:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl"
            // ;
            // ServiceRegistryManager_Service locator = new
            // ServiceRegistryManager_Service(
            // registryUrl, new QName(
            // "http://planets-project.eu/ifr/core/registry",
            // "ServiceRegistryManager"));
            registry = ServiceRegistryFactory.getInstance();// locator.
            // getServiceRegistryManagerPort
            // ();
            regWriter = new RegistryClient(registryUrl);

        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
    }

    /**
     * sends a query string to the service registry
     */
    public List<Service> lookupServices(String query) {

        List<Service> serviceList = new ArrayList<Service>();

        try {
            return dummyServices;
        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
        return null;
    }

    /**
     * looks up services registry based on service properties
     */
    public synchronized List<Service> lookupServices(Service dsc) {

        List<Service> serviceList = new ArrayList<Service>();

        try {

            // TODO retrieve endpoint and qname from wsdl (wsdl4j?)
            logger.error("Searching for services of category: "
                    + dsc.getCategory());

            // -> hand over dsc
            ServiceList serviceList_ = registry.findServices(USERNAME,
                    PASSWORD, "%", dsc.getCategory());
            logger.debug("registry returned list:" + serviceList_);
            List<PsService> psServiceList = serviceList_.services;

            logger.debug("Found " + psServiceList.size()
                    + "Services in Registry");

            for (PsService psService : psServiceList) {
                try {
                    Service service = new Service();
                    setServiceParams(psService, service);
                    // ignore cats: String[] categories =
                    // psService.getCategory().toArray(new String[0]);
                    List<PsBinding> psBindings = registry.findBindings(
                            USERNAME, PASSWORD, psService.getKey()).bindings;
                    logger.debug("found serviceID: " + psService.getKey()
                            + " #categories: " + psService.getCategories().size()
                            + " #bindings" + psBindings.size());

                    for (PsBinding psBinding : psBindings) {
                        String uri = psBinding.getAccessURI();
                        logger.debug("found binding: " + uri
                                + " for service id: " + service.getId());
                        service.setEndpoint(uri);
                        WSDLReader wsdlReader = new WSDLReaderImpl();
                        Definition serviceDef = wsdlReader.readWSDL(uri);
                        service.setQName(serviceDef.getQName());
                        // logger.debug("qname: "+serviceDef.getQName());
                        // String qName =
                        // serviceDef.getQName().getNamespaceURI();
                        serviceList.add(service);
                        logger.debug("WDT added: " + service);
                    }
                } catch (Exception e) {
                    logger.debug("could not initialize service" + e);
                }
            }

            return serviceList;
            // return dummyServices

        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
        return null;
    }

    private void setServiceParams(PsService psService, Service service) {
        service.setId(psService.getKey());
        service.setDescription(psService.getDescription());
        service.setName(psService.getName());
        // service.setCategories(new ArrayList( psService.getCategoryId() ));
        // targetNamespace
    }

    /**
     * Method to programmatically ad a set of default services
     */
    public void registerServices() {
        try {
            // --
            String orgId = null;
            PsOrganization org = null;
            String serviceId = null;
            String[] catIds = null;

            PsSchema schema = registry.findTaxonomy(USERNAME, PASSWORD)
                    .getPsSchema();
            List<PsCategory> categories = schema.categories;

            String taxonomyId = schema.schemaId;
            logger.error("Schema ID: " + taxonomyId);

            // planetsservice[0], characterisation[1], emulation[2],
            // migration[3], identification[4],
            // validation[5], metadataextraction[6], composite[7], comparison[8]
            catIds = new String[categories.size()];

            // find Taxonomies
            for (int t = 0; t < categories.size(); t++) {
                PsCategory category = categories.get(t);
                String catCode = category.code;
                catIds[t] = category.id;
                logger
                        .error("found cat Code: " + catCode + " id: "
                                + catIds[t]);
            }

            OrganizationList orgList = registry.findOrganizations(USERNAME,
                    PASSWORD, "%");
            // logger.error("Orglist: "+orgList);

            List<PsOrganization> orgs = orgList.organizations;

            // find Organization
            for (int k = 0; k < orgs.size(); k++) {
                org = orgs.get(k);
                PsRegistryObject obj = (PsRegistryObject) org;
                orgId = obj.getKey();
                logger.error("found org key: " + orgId);
            }

            // Register Services with Taxonomy Categories

            // image magick
            serviceId = regWriter.registerService("MagicJpg2Tiff@localhost",
                    "jpg-migrate-tiff");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService("MagicPng2Tiff@localhost",
                    "png-migrate-tiff");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-jmagickconverter-ejb/PngToTiffConverter?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService("MagicTiff2Png@localhost",
                    "tiff-migrate-png");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-jmagickconverter-ejb/TiffToPngConverter?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService("MagicJpg2Png@localhost",
                    "jpg-migrate-png");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-jmagickconverter-ejb/JpgToPngConverter?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            // cmd-line
            serviceId = regWriter.registerService("CmdPs2Pdf@localhost",
                    "ps-migrate-pdf");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-ps2pdf-ejb/Ps2PdfBasicMigration?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService(
                    "MagicIdentification@localhost", "indentify-image");
            regWriter
                    .registerBinding(
                            serviceId,
                            "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-jmagickconverter-ejb/ImageIdentificationService?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[4]);

            // xena
            serviceId = regWriter.registerService("XenaDoc2ODF@localhost",
                    "doc-migrate-odf");
            regWriter.registerBinding(serviceId, "local_binding",
                    "binding_desc",
                    "http://localhost:8080/pserv-pa-xena/DocToODFXena?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService("XenaODF2PDF@localhost",
                    "odf-migrate-pdf");
            regWriter.registerBinding(serviceId, "local_binding",
                    "binding_desc",
                    "http://localhost:8080/pserv-pa-xena/ODFToPDFXena?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            // droid
            serviceId = regWriter.registerService("Droid@localhost",
                    "identify-file");
            regWriter.registerBinding(serviceId, "local_binding",
                    "binding_desc",
                    "http://localhost:8080/pserv-pc-droid/Droid?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[4]);

            // release 1
            serviceId = regWriter.registerService(
                    "SimpleCharacterization@localhost", "identify-file");
            regWriter
                    .registerBinding(
                            serviceId,
                            "local_binding",
                            "binding_desc",
                            "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[4]);

            serviceId = regWriter.registerService("Doc2OpenXML@localhost",
                    "doc-migrate-docx");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[3]);

            serviceId = regWriter.registerService("XCLExtractor2URI@localhost",
                    "pdf-extract-xcdl");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/pserv-pc-extractor/Extractor2URI?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[6]);

            serviceId = regWriter.registerService("XCLExtractor2Bin@localhost",
                    "pdf-extract-xcdl");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/pserv-pc-extractor/Extractor2Binary?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[6]);

            serviceId = regWriter.registerService("XCLComparator@localhost",
                    "compare XCL files");
            regWriter
                    .registerBinding(serviceId, "local_binding",
                            "binding_desc",
                            "http://localhost:8080/pserv-pp-comparator/Comparator?wsdl");
            regWriter.addCategory(serviceId, regWriter.categoryIds[8]);

        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
    }

    public RegistryClient getRegistryWriter() {
        return this.regWriter;
    }
}

/*
 * public String registerService(PsService service, String name, String dsc,
 * PsOrganization org) throws Exception { String serviceId = null;
 * 
 * service.setName(name); service.setDescription(dsc);
 * service.setOrganization(org);
 * 
 * PsRegistryMessage rMsg = registry.saveService("provider", "provider",
 * service);
 * 
 * //String msg = rMsg.getMessage();
 * //logger.error("saved service message: "+msg); List<String> operands =
 * rMsg.getOperands(); //find ServiceId for(int y = 0; y < operands.size(); y++)
 * { serviceId = operands.get(y);
 * //logger.error("saved service key: "+serviceId); } return serviceId; }
 * 
 * public void registerBinding(PsService service, String name, String dsc,
 * String url) throws Exception { //create Binding PsBinding binding = new
 * PsBinding(); binding.setService(service); binding.setName(name);
 * binding.setDescription(dsc); binding.setAccessuri(url);
 * //logger.error("register binding for servicee key: "
 * +binding.getService().getKey()); PsRegistryMessage rMsg =
 * registry.saveServiceBinding("provider","provider",binding); }
 */

/*
 * PsService service = new PsService(); serviceId = registerService(service,
 * "SimpleCharacterisation@localhost", "Characterizes by Mime Types", org);
 * logger.debug("registering service: "+
 * "name: SimpleCharacterisation@rainer.arc.at" +
 * "dsc: Characterizes by Mime Types" + "org:"+org.getKey());
 * logger.debug("received service id: "+ serviceId); service.setKey(serviceId);
 * registerBinding(service, "local.binding1", "binding.dsc",
 * "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl"
 * );logger.debug("registering binding for service: "+service.getKey()+
 * "name: local.binding1" + "dsc: binding.dsc" +
 * "uri: http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl"
 * ); PsRegistryMessage rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[1]);
 * logger
 * .debug("adding classification to service: "+serviceId+" category: "+catIds
 * [1]);
 * 
 * service = new PsService(); serviceId = registerService(service,
 * "Tiff2Jpeg@localhost", "Converts tiff format to jpeg", org);
 * logger.debug("registering service: "+ "name: Tiff2Jpeg@rainer.arc.at" +
 * "dsc: Converts tiff format to jpeg" + "org:"+org.getKey());
 * logger.debug("received service id: "+ serviceId); service.setKey(serviceId);
 * registerBinding(service, "local.binding2", "binding.dsc",
 * "http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl");
 * logger.debug("registering binding for service: "
 * +service.getKey()+"name: local.binding2" + "dsc: binding.dsc" +
 * "uri: http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl"); rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[3]);
 * logger
 * .debug("adding classification to service: "+serviceId+" category: "+catIds
 * [3]);
 * 
 * service = new PsService(); serviceId = registerService(service,
 * "Jpeg2Tiff@level1.at", "Converts jpg format to tiff", org);
 * logger.debug("registering service: "+ "name: Jpeg2Tiff@level1.at" +
 * "dsc: Converts jpg format to tiff" + "org:"+org.getKey());
 * logger.debug("received service id: "+ serviceId); service.setKey(serviceId);
 * registerBinding(service, "local.binding3", "binding.dsc",
 * "http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl");
 * logger.debug("registering binding for service: "+service.getKey()+
 * "name: local.binding3" + "dsc: binding.dsc" +
 * "uri: http://localhost:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl"
 * ); rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[3]);
 * logger
 * .debug("adding classification to service: "+serviceId+" category: "+catIds
 * [3]);
 * 
 * //service = new PsService(); //serviceId = registerService(service,
 * "Doc2OpenXML@localhost", "Converts doc format to docx", org);
 * //service.setKey(serviceId); //registerBinding(service, "local.binding",
 * "binding.dsc",
 * "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl"); //rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[3]);
 * 
 * //service = new PsService(); //serviceId = registerService(service,
 * "DataManager@localhost", "Data manager", org); //service.setKey(serviceId);
 * //registerBinding(service, "local.binding", "binding.dsc",
 * "http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl"); //rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[3]);
 * 
 * //service = new PsService(); //serviceId = registerService(service,
 * "ReportGenerator@localhost", "Report generator", org);
 * //service.setKey(serviceId); //registerBinding(service, "local.binding",
 * "binding.dsc",
 * "http://localhost:8080/ReportGenerationService/ReportGenerationService?wsdl"
 * ); //rMsg =
 * registry.addClassificationTo("provider","provider",serviceId,catIds[3]);
 */

/*
 * public void createDummyServices() {
 * 
 * //some dummy services dummyServices = new ArrayList<Service>();
 * 
 * QName simpleCharQname = new
 * QName("http://services.planets-project.eu/ifr/characterisation",
 * "SimpleCharacterisationService") ; dummyServices.add( new Service("#fex#1",
 * "SimpleCharacterisation@localhost", "someTargetNamespace",
 * "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl"
 * , "Characterizes Mime Type", simpleCharQname) );
 * 
 * QName tiff2jpegQname = new QName("http://tiff2jpg.planets.bl.uk/",
 * "Tiff2JpegActionService"); dummyServices.add( new Service("#fex#2",
 * "Tiff2Jpeg@localhost", "someTargetNamespace",
 * "http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl",
 * "Converts tiff Format to jpeg", tiff2jpegQname) );
 * 
 * QName openXMLQname = new
 * QName("http://planets-project.eu/ifr/core/services/migration",
 * "OpenXMLMigration"); dummyServices.add( new Service("#fex#3",
 * "Doc2OpenXML@localhost", "someTargetNamespace",
 * "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl",
 * "Converts doc format to docx", openXMLQname) );
 * 
 * QName dataManagerQname = new
 * QName("http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl",
 * "DataManager"); dummyServices.add( new Service("#fex#4",
 * "DataManager@localhost", "someTargetNamespace",
 * "http://localhost:8080/storage-ifr-storage-ejb/DataManager?wsdl",
 * "Data manager",dataManagerQname) );
 * 
 * QName repGeneratorQname = new
 * QName("http://services.planets-project.eu/ifr/reporting",
 * "ReportGenerationService"); dummyServices.add( new Service("#fex#5",
 * "ReportGenerator@localhost", "someTargetNamespace",
 * "http://localhost:8080/ReportGenerationService/ReportGenerationService?wsdl",
 * "Report generator", repGeneratorQname) ); }
 */

