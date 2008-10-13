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

    boolean valid;
    
    ServiceReport resport;

    /**
     * No-args constructor required by JAXB
     */
    public ValidateResult() { }

}
