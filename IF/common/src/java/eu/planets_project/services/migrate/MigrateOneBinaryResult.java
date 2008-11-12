/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
@Deprecated
public class MigrateOneBinaryResult {

    private byte[] binary;
    
    private ServiceReport report;

    /**
     * For JAXB:
     */
    protected MigrateOneBinaryResult() {}

    /**
     * @param binary
     * @param report
     */
    public MigrateOneBinaryResult(byte[] binary, ServiceReport report) {
        super();
        this.binary = binary;
        this.report = report;
    }

    /**
     * @return the binary
     */
    public byte[] getBinary() {
        return binary;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }
    
}
