/**
 * 
 */
package eu.planets_project.services.identify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Types;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class IdentifyResult {

    Types types;
    
    ServiceReport resport;

    /**
     * No-args constructor required by JAXB
     */
    public IdentifyResult() { }

}
