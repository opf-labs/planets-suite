package eu.planets_project.ifr.core.wdt.impl.registry;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.logging.Log;

import com.ibm.wsdl.xml.WSDLReaderImpl;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryObjectFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceList;

/**
 * backing bean for service registry gui components acesses the service registry
 * web service.
 * @author Rainer Schmidt, ARC
 */
public class WorkflowServiceRegistry {
    private static final String PASSWORD = "provider";
    private static final String USERNAME = "provider";
    private Log logger = PlanetsLogger.getLogger(this.getClass(),
            "resources/log/wdt-log4j.xml");
    private ServiceRegistry registry = null;

    /**
     * 
     */
    public WorkflowServiceRegistry() {
        try {
            registry = ServiceRegistryFactory.getInstance();
        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
    }

    /**
     * looks up services registry based on service properties.
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
                    List<PsBinding> psBindings = registry.findBindings(
                            USERNAME, PASSWORD, psService.getKey()).bindings;
                    logger.debug("found serviceID: " + psService.getKey()
                            + " #categories: "
                            + psService.getCategories().size() + " #bindings"
                            + psBindings.size());

                    for (PsBinding psBinding : psBindings) {
                        String uri = psBinding.getAccessURI();
                        logger.debug("found binding: " + uri
                                + " for service id: " + service.getId());
                        service.setEndpoint(uri);
                        WSDLReader wsdlReader = new WSDLReaderImpl();
                        Definition serviceDef = wsdlReader.readWSDL(uri);
                        service.setQName(serviceDef.getQName());
                        serviceList.add(service);
                        logger.debug("WDT added: " + service);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
    }

    /**
     * Method to programmatically ad a set of default services.
     */
    public void registerServices() {
        try {
            PsOrganization org = null;
            OrganizationList orgList = registry.findOrganizations(USERNAME,
                    PASSWORD, "%");
            List<PsOrganization> orgs = orgList.organizations;

            // find Organization
            for (int k = 0; k < orgs.size(); k++) {
                org = orgs.get(k);
            }
            // None? Create one!
            if (orgs.size() == 0) {
                org = new ServiceRegistryObjectFactory(USERNAME, PASSWORD,
                        registry).createOrganization("Planets",
                        "Planets WDT service owner", "IF",
                        "info@planets-project.eu");
            }

            logger.error("found org key: " + org.getKey());

            // Register Services with Taxonomy Categories

            // image magick

            register(org, "MagicJpg2Tiff@localhost", "jpg-migrate-tiff",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-jmagick/JpgToTiffConverter?wsdl");

            register(org, "MagicPng2Tiff@localhost", "png-migrate-tiff",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-jmagick/PngToTiffConverter?wsdl");

            register(org, "MagicTiff2Png@localhost", "tiff-migrate-png",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-jmagick/TiffToPngConverter?wsdl");

            register(org, "MagicJpg2Png@localhost", "jpg-migrate-png",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-jmagick/JpgToPngConverter?wsdl");

            // cmd-line

            register(org, "CmdPs2Pdf@localhost", "ps-migrate-pdf", "Migrate",
                    "http://localhost:8080/pserv-pa-ps2pdf/Ps2PdfBasicMigration?wsdl");

            register(org, "MagicIdentification@localhost", "indentify-image",
                    "Identify",
                    "http://localhost:8080/pserv-pa-jmagick/ImageIdentificationService?wsdl");

            // xena

            register(org, "XenaDoc2ODF@localhost", "doc-migrate-odf",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-xena/DocToODFXena?wsdl");

            register(org, "XenaODF2PDF@localhost", "odf-migrate-pdf",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-xena/ODFToPDFXena?wsdl");

            // droid

            register(org, "Droid@localhost", "identify-file", "Identify",
                    "http://localhost:8080/pserv-pc-droid/Droid?wsdl");

            // release 1

            register(
                    org,
                    "SimpleCharacterization@localhost",
                    "identify-file",
                    "Identify",
                    "http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl");

            register(org, "Doc2OpenXML@localhost", "doc-migrate-docx",
                    "Migrate",
                    "http://localhost:8080/pserv-pa-openxml/BasicOpenXMLMigration?wsdl");

            register(org, "XCLExtractor2Bin@localhost", "pdf-extract-xcdl",
                    "Characterise",
                    "http://localhost:8080/pserv-pc-extractor/Extractor2Binary?wsdl");

            register(
                    org,
                    "XCLComparator@localhost",
                    "compare XCL files",
                    "Compare",
                    "http://localhost:8080/pserv-pp-comparator/ComparatorBasicCompareTwoXcdlValues?wsdl");
        } catch (Exception e) {
            logger.error("Error testing registry: ", e);
        }
    }

    private void register(PsOrganization organization, String name,
            String description, String type, String endpoint) {
        ServiceRegistryObjectFactory objectFactory = new ServiceRegistryObjectFactory(
                USERNAME, PASSWORD, registry);
        PsService service = objectFactory.createService(name, description,
                organization, type);
        objectFactory.createBinding("local_binding", endpoint, service);

    }
}
