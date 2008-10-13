/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * <h2>A Planets Event</h2>
 *
 * Event data, based on the Planets conceptual data model.
 * 
 * <p>
 * If the Event corresponds to the invocation of a Service,
 * then most of this can be filled in by the caller.
 * </p>
 * 
 * <p>
 * TODO Should some space be left in this item for a more extensible system,
 * such as name-value pairs implementing [subject, predicate, object] triples?
 * The subject is clearly the Event, so use 'dc.creator', 'dc.terms.isPartOf', 'planets.process.info' etc?
 * </p>
 * 
 * <p>
 * The ProcessLog should usually be returned by the called Service,
 * and allows the Service to pass back some information about what 
 * happened.
 *</p>
 *
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class Event {
    
    /** 
     * A human-readable description of the event.
     */
    @XmlAttribute String summary;

    /** 
     * The date and time at which this Event began.
     */
    @XmlAttribute String datetime;
    
    /** 
     * The total duration of this event, 
     * i.e. the wall-clock execution time (in seconds)
     * since the start date and time. 
     * 
     * <p>
     * TODO Please not that this is an addition to the CDM. 
     * Rationale: It is useful to record this information for runtime-estimation.
     * Perhaps this is not the place for it though?
     * Only really known to the caller, so maybe this does not belong here.
     * </p>
     */
    @XmlAttribute double duration;

    /** 
     * The Agent that caused this Event. 
     */
    Agent agent;
    
    /**
     * Name-value pairs for extra properties.  
     * This is an expansion point for future functionality.
     */
    @XmlElement List<Property> properties;

    /**
     * 
     */
    public Event() {
        super();
    }
    
}
