/**
 * 
 */
package eu.planets_project.services.fixity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author CFWilson
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FixityResult {

	private String algorithmId;
	private byte[] digestValue;
    private List<Property> properties;
    private ServiceReport report;

    /**
     * No-arg constructor required by JAXB
     */
    @SuppressWarnings("unused")
	private FixityResult(){/** NULL & private to prevent no arg construction */};

    /**
     * @param algorithmId
     * 			the java.lang.String identifier of the digest algorithm used
     * @param digestValue
     * 			the byte[] digest value
     * @param properties
     * 			any service properties to add to the report
     * @param report
     * 			the service report
     */
    public FixityResult(String algorithmId, byte[] digestValue,
    					List<Property> properties, ServiceReport report) {
    	this.algorithmId = algorithmId;
    	this.digestValue = digestValue.clone();
    	this.properties = properties;
    	this.report = report;
    }
    
    /**
     * @param algorithmId
     * 			the java.lang.String identifier of the digest algorithm used
     * @param properties
     * 			any service properties to add to the report
     * @param report
     * 			the service report
     */
    public FixityResult(String algorithmId,	List<Property> properties, ServiceReport report) {
    	this.algorithmId = algorithmId;
    	this.digestValue = null;
    	this.properties = properties;
    	this.report = report;
    }

    /**
     * @param properties
     * 			any service properties to add to the report
     * @param report
     * 			the service report
     */
    public FixityResult(List<Property> properties, ServiceReport report) {
    	this.algorithmId = null;
    	this.digestValue = null;
    	this.properties = properties;
    	this.report = report;
    }

    /**
     * @param report
     * 			the service report
     */
    public FixityResult(ServiceReport report) {
    	this.algorithmId = null;
    	this.digestValue = null;
    	this.properties = null;
    	this.report = report;
    }

    /**
     * @return the java.lang.String algorithm ID
     */
    public String getAlgorithmId() {
    	return this.algorithmId;
    }
    
    /**
     * @return the digest value as a java.lang.String
     */
    public String getDigestValueAsString() {
    	String result = "";
    	
    	for (byte aByte : this.digestValue) {
    		result += aByte;
    	}
    	
    	return result;
    }
    
    /**
     * @return the digest values as a byte array
     */
    public byte[] getDigestValue() {
    	return this.digestValue;
    }
    
    /**
     * @return the an unmodifiable java.util.List<eu.planets_project.services.datatypes.Property>
     * 		   of properties associated with the FixityResult 
     */
    public List<Property> getProperties() {
        return this.properties == null ?
        		// If propertie
        		Collections.unmodifiableList(new ArrayList<Property>()) : 
       			Collections.unmodifiableList(this.properties);
    }
    
    /**
     * @return the eu.planets_project.services.datatypes.ServiceReport report
     */
    public ServiceReport getReport() {
    	return this.report;
    }
}

