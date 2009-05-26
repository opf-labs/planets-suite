/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import eu.planets_project.services.datatypes.Event;

/**
 * A stream-based response from migration services.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateStreamResponse {
    
    private Event event;
    @XmlMimeType("application/octet-stream")
    private DataHandler stream;
    /**
     * Create a stream-based migrate response.
     * @param event The event
     * @param stream The strem
     */
    public MigrateStreamResponse(Event event, DataHandler stream){
        this.event=event;
        this.stream=stream;
    }
    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }
    /**
     * @return the stream
     */
    public DataHandler getStream() {
        return stream;
    }
    

}
