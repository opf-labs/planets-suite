/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author AnJackson
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MigrateOneBinaryResult {

    public byte[] binary;
    
    public ServiceReport log = new ServiceReport();
    
}
