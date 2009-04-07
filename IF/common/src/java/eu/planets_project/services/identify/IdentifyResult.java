/**
 * 
 */
package eu.planets_project.services.identify;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class IdentifyResult {

    List<URI> types;
    
    /**
     *  The Method enumeration is intended to allow you state what kind of evidence was used to create this identification result.
     */
    public enum Method { 
        
        /** 'Extension' means that this identification was based purely on the filename extension of the supplied digital object. */
        EXTENSION, 
        
        /** 'Metadata' means that this identification was based on other metadata supplied with the digital object, but the objects bytes were not inspected.*/
        METADATA, 
        
        /** 'Magic' means that this identification was based on matching the digital object bytes against a bytestream signature, a.k.a. a 'file Magic number'. In this case, a small set of bytes from the file has been inspected, and in general the inspection does not depend on the file size. */
        MAGIC, 
        
        /** 'Partial Parse' means that this identification was based on a detailed inspection of the digital object bytestreams, and a significant proportion of the objects bytes were used to reach this conclusion. */
        PARTIAL_PARSE,
        
        /** 'Full Parse' means that this identification was based on a detailed inspection of the digital object bytestream, and every single bit was 'touched'. */
        FULL_PARSE, 
        
        /** Means that this identification was based on some other evidence.  Please contact the IF so that more Methods can be added. */
        OTHER 
        
    };
    Method method;
    
    List<Property> properties;
    
    ServiceReport report;

    /**
     * No-args constructor required by JAXB
     */
    public IdentifyResult() { }

    /**
     * @param types The list of matching format URIs, specifying the formats the object is consistent with.
     * @param method The method used to reach this decision.  Should not be set to NULL unless completely unavoidable.
     * @param report The standard service report object.
     * @param properties Any other properties determined by the service.
     */
    public IdentifyResult(List<URI> types, Method method, ServiceReport report, List<Property> properties ) {
        super();
        this.types = types;
        this.method = method;
        this.report = report;
        this.properties = properties;
    }

    /**
     * @param types The list of matching format URIs, specifying the formats the object is consistent with.
     * @param method The method used to reach this decision.  Should not be set to NULL unless completely unavoidable.
     * @param report The standard service report object.
     */
    public IdentifyResult(List<URI> types, Method method, ServiceReport report ) {
        super();
        this.types = types;
        this.method = method;
        this.report = report;
        this.properties = null;
    }

    /**
     * @return the type
     */
    public List<URI> getTypes() {
        return types;
    }
    
    
    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }
    
}
