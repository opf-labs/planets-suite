/**
 * 
 */
package eu.planets_project.services.validate;

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
public class ValidateResult {

    /**
     * A description of the validity of the object.
     */
    enum Validity { 
        /** 'Invalid' means that the digital object does not appear to be in the proposed format. */
        INVALID, 
        
        /** 'Well Formed' means that the digital object appears to be structurally sound, but the content could not be fully semantically checked or internally verified. */
        WELL_FORMED, 

        /** 'Valid' means that the digital object is not only well formed, but also appears to be semantically valid and self-consistent. */
        VALID 
    };
    private Validity validity;
    
    /**
     * 
     */
    private ServiceReport report;

    /**
     * No-args constructor required by JAXB
     */
    private ValidateResult() {}
    
    /**
     * Creates a new validation result, containing a description of the Validity and a Service Report.
     * 
     * @param validity The Validity of the digital object, against the proposed format.
     * @param report The service report.
     */
    public ValidateResult(Validity validity, ServiceReport report ) {
        this.validity = validity;
        this.report = report;
    }

    /**
     * @return the validity
     */
    public Validity getValidity() {
        return validity;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }
    
    

}
