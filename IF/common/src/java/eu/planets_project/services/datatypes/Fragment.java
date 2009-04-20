package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A digital object fragment.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public final class Fragment {
    /** The fragment ID. */
    @XmlAttribute
    private String id;

    /** No-arg constructor for JAXB. Client should not use this. */
    @SuppressWarnings("unused")
    private Fragment() {}

    /**
     * @param id The ID
     */
    public Fragment(final String id) {
        this.id = id;
    }

    /**
     * @return The ID
     */
    public String getId() {
        return id;
    }
}
