/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import eu.planets_project.services.datatypes.Event;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateStreamResponse {
    
    private Event event;
    @XmlMimeType("application/octet-stream")
    private DataHandler stream;

}
