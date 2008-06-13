
package eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PlanetsException_QNAME = new QName("http://planets-project.eu/ifr/migration", "PlanetsException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PlanetsException }
     * 
     */
    public PlanetsException createPlanetsException() {
        return new PlanetsException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PlanetsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/migration", name = "PlanetsException")
    public JAXBElement<PlanetsException> createPlanetsException(PlanetsException value) {
        return new JAXBElement<PlanetsException>(_PlanetsException_QNAME, PlanetsException.class, null, value);
    }

}
