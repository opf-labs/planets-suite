//
//package eu.planets_project.ifr.core.registry.api.jaxb;
//
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.annotation.XmlElementDecl;
//import javax.xml.bind.annotation.XmlRegistry;
//import javax.xml.namespace.QName;
//
//import eu.planets_project.ifr.core.registry.api.BindingList;
//import eu.planets_project.ifr.core.registry.api.OrganizationList;
//import eu.planets_project.ifr.core.registry.api.PsBinding;
//import eu.planets_project.ifr.core.registry.api.PsCategory;
//import eu.planets_project.ifr.core.registry.api.PsOrganization;
//import eu.planets_project.ifr.core.registry.api.PsRegistryObject;
//import eu.planets_project.ifr.core.registry.api.PsSchema;
//import eu.planets_project.ifr.core.registry.api.PsService;
//import eu.planets_project.ifr.core.registry.api.ServiceList;
//import eu.planets_project.ifr.core.registry.api.ServiceRegistryMessage;
//
//
///**
// * This object contains factory methods for each 
// * Java content interface and Java element interface 
// * generated in the eu.planets_project.ifr.core.common.registry package. 
// * <p>An ObjectFactory allows you to programatically 
// * construct new instances of the Java representation 
// * for XML content. The Java representation of XML 
// * content can consist of schema derived interfaces 
// * and classes representing the binding of schema 
// * type definitions, element declarations and model 
// * groups.  Factory methods for each of these are 
// * provided in this class.
// * 
// */
//@XmlRegistry
//public class ObjectFactory {
//
//    private final static QName _Psservices_QNAME = new QName("http://planets-project.eu/ifr/core/registry", "psservices");
//    private final static QName _JAXRException_QNAME = new QName("http://planets-project.eu/ifr/core/registry", "JAXRException");
//    private final static QName _JAXRClassificationScheme_QNAME = new QName("http://planets-project.eu/ifr/core/registry", "JAXRClassificationScheme");
//
//    /**
//     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.planets_project.ifr.core.common.registry
//     * 
//     */
//    public ObjectFactory() {
//    }
//
//    /**
//     * Create an instance of {@link OrganizationList }
//     * 
//     */
//    public OrganizationList createOrganizationList() {
//        return new OrganizationList();
//    }
//
//    /**
//     * Create an instance of {@link JAXRException }
//     * 
//     */
//    public JAXRException createJAXRException() {
//        return new JAXRException();
//    }
//
//    /**
//     * Create an instance of {@link ServiceList }
//     * 
//     */
//    public ServiceList createServiceList() {
//        return new ServiceList();
//    }
//
//    /**
//     * Create an instance of {@link BindingList }
//     * 
//     */
//    public BindingList createBindingList() {
//        return new BindingList();
//    }
//
//    /**
//     * Create an instance of {@link PsCategory }
//     * 
//     */
//    public PsCategory createPsCategory() {
//        return new PsCategory();
//    }
//
//    /**
//     * Create an instance of {@link PsSchema }
//     * 
//     */
//    public PsSchema createPsSchema() {
//        return new PsSchema();
//    }
//
//    /**
//     * Create an instance of {@link PsOrganization }
//     * 
//     */
//    public PsOrganization createPsOrganization() {
//        return new PsOrganization();
//    }
//
//    /**
//     * Create an instance of {@link PsService }
//     * 
//     */
//    public PsService createPsService() {
//        return new PsService();
//    }
//
//    /**
//     * Create an instance of {@link PsRegistryObject }
//     * 
//     */
//    public PsRegistryObject createPsRegistryObject() {
//        return new PsRegistryObject();
//    }
//
//    /**
//     * Create an instance of {@link ServiceRegistryMessage }
//     * 
//     */
//    public ServiceRegistryMessage createPsRegistryMessage() {
//        return new ServiceRegistryMessage();
//    }
//
//    /**
//     * Create an instance of {@link PsBinding }
//     * 
//     */
//    public PsBinding createPsBinding() {
//        return new PsBinding();
//    }
//
//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
//     * 
//     */
//    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/registry", name = "psservices")
//    public JAXBElement<Object> createPsservices(Object value) {
//        return new JAXBElement<Object>(_Psservices_QNAME, Object.class, null, value);
//    }
//
//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link JAXRException }{@code >}}
//     * 
//     */
//    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/registry", name = "JAXRException")
//    public JAXBElement<JAXRException> createJAXRException(JAXRException value) {
//        return new JAXBElement<JAXRException>(_JAXRException_QNAME, JAXRException.class, null, value);
//    }
//
//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link PsSchema }{@code >}}
//     * 
//     */
//    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/registry", name = "JAXRClassificationScheme")
//    public JAXBElement<PsSchema> createJAXRClassificationScheme(PsSchema value) {
//        return new JAXBElement<PsSchema>(_JAXRClassificationScheme_QNAME, PsSchema.class, null, value);
//    }
//
//}
