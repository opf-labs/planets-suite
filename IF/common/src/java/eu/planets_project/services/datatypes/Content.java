package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.InputStream;

@XmlJavaTypeAdapter(Content.Adapter.class)
@XmlType(namespace = PlanetsServices.OBJECTS_NS)
public interface Content {
    /**
     * @return An input stream for this content; this is either created for
     *         the actual value (if this is value content) or a stream for
     *         reading the reference (if this is a reference content)
     */
    public InputStream read();

    @XmlElement(name="checksum",namespace = PlanetsServices.OBJECTS_NS)
    public Checksum getChecksum();

    public void setChecksum(Checksum checksum);

    public long length();

    /*
    * The current solution to be able to pass this into web service
    * methods: We tell JAXB which adapter to use for converting from the
    * interface to the implementation. While this does tightly couple the
    * interface and the implementation, it still allows us to hide the
    * implementation class, e.g. keeping it out of our web service
    * interfaces. Also, for using the IF API outside of a web service stack
    * or JAXB, i.e. as a plain Java library, this is perfectly fine.
    */
    /**
     * Adapter for serialization of Content interface
     * instances.
     */
    static class Adapter
            extends
            XmlAdapter<ImmutableContent, Content> {
        public Content unmarshal(ImmutableContent immutableContent) throws Exception {
            return immutableContent;
        }

        public ImmutableContent marshal(Content content) throws Exception {
            return (ImmutableContent) content;
        }
    }
}