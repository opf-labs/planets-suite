package eu.planets_project.ifr.core.registry.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Planets registry object.
 */
public class PsRegistryObject {

    protected String key;
    protected String name;
    protected String description;
    protected List<String> exceptionMessages = new ArrayList<String>();

    protected List<PsCategory> categories = new ArrayList<PsCategory>();

    public PsRegistryObject() {
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format(
                        "Registry Object with name '%s', key '%s', description '%s', %s categories, %s exceptions",
                        name, key, description, categories, exceptionMessages);
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the exceptionMessages
     */
    public List<String> getExceptionMessages() {
        return exceptionMessages;
    }

    /**
     * @param exceptionMessages the exceptionMessages to set
     */
    public void setExceptionMessages(List<String> exceptionMessages) {
        this.exceptionMessages = exceptionMessages;
    }

    /**
     * @return the categories
     */
    public List<PsCategory> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<PsCategory> categories) {
        this.categories = categories;
    }
}
