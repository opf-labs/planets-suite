package eu.planets_project.ifr.core.registry.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Classification;

import eu.planets_project.ifr.core.registry.api.ServiceTaxonomy;
import eu.planets_project.ifr.core.registry.api.jaxb.concepts.JAXRConcept;

/**
 * Planets service category.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PsCategory implements Comparable<PsCategory> {

    @XmlAttribute
    public String code;
    @XmlAttribute
    public String id;
    @XmlAttribute
    public String name;
    @XmlAttribute
    protected String parent;

    public PsCategory(String name, String value) {
        this.name = name;
        this.code = value;
    }

    public PsCategory() {
    }

    PsCategory(String id) {
        this.id = id;
    }

    protected PsCategory clone(JAXRConcept source, PsCategory dest)
            throws JAXRException {
        dest.id = source.getId();
        dest.name = source.getName();
        dest.parent = source.getParent();
        dest.code = source.getCode();
        return dest;
    }

    /**
     * @param classification The JAXR classification to convert into a Planets
     *        category
     * @return A Planets category corresponding to the given JAXR classification
     */
    public static PsCategory of(Classification classification) {
        try {
            PsCategory cat = new PsCategory(
                    classification.getName().getValue(), classification
                            .getValue());
            if (classification.getKey() != null) {
                cat.id = classification.getKey().getId();
            }
            return cat;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PsCategory
                && this.compareTo(((PsCategory) obj)) == 0;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(PsCategory o) {
        return this.id.compareTo(o.id);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @param blcm The manager
     * @return A JAXR classification corresponding to this Planets category
     */
    public Classification toJaxrClassification(BusinessLifeCycleManager blcm) {
        ServiceTaxonomy stax;
        try {
            stax = new ServiceTaxonomy(blcm, null);
            Classification classification = stax.getClassification(this.id);
            return classification;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }
}
