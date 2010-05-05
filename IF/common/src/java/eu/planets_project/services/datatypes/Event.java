/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
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
public final class Event implements Serializable {

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

    
    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((agent == null) ? 0 : agent.hashCode());
        result = prime * result + ((datetime == null) ? 0 : datetime.hashCode());
        long temp;
        temp = Double.doubleToLongBits(duration);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((summary == null) ? 0 : summary.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        if (agent == null) {
            if (other.agent != null) {
                return false;
            }
        } else if (!agent.equals(other.agent)) {
            return false;
        }
        if (datetime == null) {
            if (other.datetime != null) {
                return false;
            }
        } else if (!datetime.equals(other.datetime)) {
            return false;
        }
        if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        if (summary == null) {
            if (other.summary != null) {
                return false;
            }
        } else if (!summary.equals(other.summary)) {
            return false;
        }
        return true;
    }
    

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() 
    {
        int agentSize = agent == null ? 0 : 1;
        int propertiesSize = properties == null ? 0 : properties.size();
        return String.format(
        		"Event: summary '%s', datetime '%s', duration '%s'; %s agent, properties '%s'"
        		, summary, datetime, duration, agentSize, propertiesSize);
    }
}
