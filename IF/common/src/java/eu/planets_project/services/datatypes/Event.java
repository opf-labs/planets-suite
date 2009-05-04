/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * <h2>A Planets Event</h2> Event data, based on the Planets conceptual data
 * model.
 * <p>
 * If the Event corresponds to the invocation of a Service, then most of this
 * can be filled in by the caller.
 * </p>
 * <p>
 * TODO Should some space be left in this item for a more extensible system,
 * such as name-value pairs implementing [subject, predicate, object] triples?
 * The subject is clearly the Event, so use 'dc.creator', 'dc.terms.isPartOf',
 * 'planets.process.info' etc?
 * </p>
 * <p>
 * The ProcessLog should usually be returned by the called Service, and allows
 * the Service to pass back some information about what happened.
 *</p>
 *<p>
 * Instances of this class are immutable and so can be shared freely.
 *</p>
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Event {

    /**
     * A human-readable description of the event.
     */
    @XmlAttribute
    private String summary;

    /**
     * The date and time at which this Event began.
     */
    @XmlAttribute
    private String datetime;

    /**
     * The total duration of this event, i.e. the wall-clock execution time (in
     * seconds) since the start date and time.
     * <p>
     * TODO Please not that this is an addition to the CDM. Rationale: It is
     * useful to record this information for runtime-estimation. Perhaps this is
     * not the place for it though? Only really known to the caller, so maybe
     * this does not belong here.
     * </p>
     */
    @XmlAttribute
    private double duration;

    /**
     * The Agent that caused this Event.
     */
    @XmlElement(namespace = PlanetsServices.OBJECTS_NS)
    private Agent agent;

    /**
     * Name-value pairs for extra properties. This is an expansion point for
     * future functionality.
     */
    @XmlElement(name = "property", namespace = PlanetsServices.DATATYPES_NS)
    private List<Property> properties;

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Event() {}

    /**
     * @param summary The event summary
     * @param datetime The event date
     * @param duration The event duration
     * @param agent The event agent
     * @param properties The event properties
     */
    public Event(final String summary, final String datetime,
            final Double duration, final Agent agent,
            final List<Property> properties) {
        this.summary = summary;
        this.datetime = datetime;
        this.duration = duration;
        this.agent = agent;
        this.properties = properties;
    }

    /**
     * @return The event summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return The event date
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @return The event duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @return The event agent
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * @return The event properties, as an unmodifiable defensive copy.
     */
    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }
}
