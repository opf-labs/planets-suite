package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.InputStream;

/**
 * Representation of digital object content.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlJavaTypeAdapter(DigitalObjectContent.Adapter.class)
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public interface DigitalObjectContent {
    /**
     * @return An input stream for this content; this is either created for the
     *         actual value (if this is value content) or a stream for reading
     *         the reference (if this is a reference content)
     */
    InputStream getInputStream();

    /**
     * @return The checksum for this content
     */
    @XmlElement(name = "checksum", namespace = PlanetsServices.OBJECTS_NS)
    Checksum getChecksum();

    /*
     * TODO This solution to maintain immutability is probably not the best way
     * to go. Maybe we should reconsider our decision to move the checksum from
     * digital object to content, makes things less straightforward...
     */
    /**
     * As checksum calculation is optional, this functionality is supported via
     * this method, not the factory methods. To maintain immutability, this
     * method does not alter this content, but returns a copy with the given
     * checksum set.
     * @param checksum the checksum to set
     * @return A copy of this content, with the given Checksum set
     * @see DigitalObjectContent#getChecksum()
     */
    DigitalObjectContent withChecksum(Checksum checksum);

    /**
     * @return The content length
     */
    long length();

    /*
     * The current solution to be able to pass this into web service methods: We
     * tell JAXB which adapter to use for converting from the interface to the
     * implementation. While this does tightly couple the interface and the
     * implementation, it still allows us to hide the implementation class, i.e.
     * keeping it out of our web service interfaces. Also, for using the IF API
     * outside of a web service stack or JAXB, i.e. as a plain Java library,
     * this is perfectly fine.
     */
    /**
     * Adapter for serialization of Content interface instances.
     */
    static class Adapter extends
            XmlAdapter<ImmutableContent, DigitalObjectContent> {
        public DigitalObjectContent unmarshal(ImmutableContent immutableContent)
                throws Exception {
            return immutableContent;
        }

        public ImmutableContent marshal(DigitalObjectContent content)
                throws Exception {
            return (ImmutableContent) content;
        }
    }
}