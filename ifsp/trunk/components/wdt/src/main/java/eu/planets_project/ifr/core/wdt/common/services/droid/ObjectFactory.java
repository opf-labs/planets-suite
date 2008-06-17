
package eu.planets_project.ifr.core.wdt.common.services.droid;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.planets_project.ifr.core.wdt.common.services.droid package. 
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

    private final static QName _IdentifyBytesResponse_QNAME = new QName("http://planets-project.eu/services/Droid", "identifyBytesResponse");
    private final static QName _IdentifyFileResponse_QNAME = new QName("http://planets-project.eu/services", "identifyFileResponse");
    private final static QName _IdentifyFile_QNAME = new QName("http://planets-project.eu/services", "identifyFile");
    private final static QName _IdentifyBytes_QNAME = new QName("http://planets-project.eu/services", "identifyBytes");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.planets_project.ifr.core.wdt.common.services.droid
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Types }
     * 
     */
    public Types createTypes() {
        return new Types();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Types }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/services/Droid", name = "identifyBytesResponse")
    public JAXBElement<Types> createIdentifyBytesResponse(Types value) {
        return new JAXBElement<Types>(_IdentifyBytesResponse_QNAME, Types.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Types }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/services", name = "identifyFileResponse")
    public JAXBElement<Types> createIdentifyFileResponse(Types value) {
        return new JAXBElement<Types>(_IdentifyFileResponse_QNAME, Types.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/services", name = "identifyFile")
    public JAXBElement<String> createIdentifyFile(String value) {
        return new JAXBElement<String>(_IdentifyFile_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/services", name = "identifyBytes")
    public JAXBElement<byte[]> createIdentifyBytes(byte[] value) {
        return new JAXBElement<byte[]>(_IdentifyBytes_QNAME, byte[].class, null, ((byte[]) value));
    }

}
