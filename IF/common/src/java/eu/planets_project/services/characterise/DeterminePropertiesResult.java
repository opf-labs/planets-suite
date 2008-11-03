/**
 * 
 */
package eu.planets_project.services.characterise;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DeterminePropertiesResult {

    Properties properties;
    
    ServiceReport report;
    
    /**
     * 
     */
    protected DeterminePropertiesResult() { }
    
    /**
     * 
     * @param properties
     * @param report
     */
    public DeterminePropertiesResult( Properties properties, ServiceReport report ) {
        this.properties = properties;
        this.report = report;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }
    
}
