package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.Collection;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Class for describing setting values for presets. A preset setting consists of
 * a collection of parameters which constitutes the preset setting, a name and
 * optionally a description with information about the expected effect is when
 * the parameters are applied.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class PresetSetting {

    private final String name;
    private String description;
    private Collection<Parameter> parameters;

    PresetSetting(String name, Collection<Parameter> parameters) {
	this.name = name;
	this.parameters = parameters;
	setDescription("");
    }

    /**
     * Get the name of this <code>PresetSetting</code> instance.
     * 
     * @return <code>String</code> with the name.
     */
    String getName() {
	return name;
    }

    /**
     * Get the <code>Parameter</code> objects constituting this preset setting.
     * 
     * @return <code>Collection</code> of <code>Parameter</code> instances
     *         defining this preset setting.
     */
    Collection<Parameter> getParameters() {
	return parameters;
    }

    /**
     * Get the description for this <code>PresetSetting</code> instance which
     * describes the effect of applying this preset setting.
     * 
     * @return the description
     */
    String getDescription() {
	return description;
    }

    /**
     * Set a description for this <code>PresetSetting</code> instance which
     * describes the effect of applying this preset setting.
     * 
     * @param description
     *            the description text to set
     */
    void setDescription(String description) {
	this.description = description;
    }
}
