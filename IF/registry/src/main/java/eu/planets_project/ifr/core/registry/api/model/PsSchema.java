package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.registry.JAXRException;

import eu.planets_project.ifr.core.registry.api.jaxb.concepts.JAXRClassificationScheme;
import eu.planets_project.ifr.core.registry.api.jaxb.concepts.JAXRConcept;

/**
 * Planets registry schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PsSchema {
    public String schemaName;
    public String schemaDescription;
    public String schemaId;
    public ArrayList<PsCategory> categories = new ArrayList<PsCategory>();
    public String errorMessage;

    public PsSchema() {
    }

    /**
     * @param jaxrscheme
     * @throws JAXRException
     */
    public PsSchema(JAXRClassificationScheme jaxrscheme) throws JAXRException {
        this.schemaName = jaxrscheme.getName();
        this.schemaDescription = jaxrscheme.getDescription();
        this.schemaId = jaxrscheme.getId();
        this.categories = new ArrayList<PsCategory>();
        Iterator<JAXRConcept> jaxrconcepts = jaxrscheme.getJAXRConcept()
                .iterator();
        for (int h = 0; h < jaxrscheme.getJAXRConcept().size(); h++) {
            PsCategory rootCategory = new PsCategory();
            rootCategory = rootCategory
                    .clone(jaxrconcepts.next(), rootCategory);
            this.categories.add(rootCategory);
        }
        this.categories.trimToSize();
    }

    @Override
    public String toString() {
        return "PsSchema : " + schemaId + " name: " + schemaName + " :  "
                + " descr  " + schemaDescription;
    }

    /**
     * @param name The name of the category (e.g. migration)
     * @return The ID of a classification containing the name, or null
     */
    public String getId(String name) {
        for (PsCategory c : categories) {
            if (c.id.toLowerCase().contains(name)) {
                return c.id;
            }
        }
        return null;
    }
}
