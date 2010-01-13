package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * FIXME! Doc!
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class ToolPresets {

    private Map<String, Preset> presets;
    private String defaultPresetID;

    ToolPresets() {
	presets = new HashMap<String, Preset>();
    }

    /**
     * Get the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @return ID of the default preset category
     */
    String getDefaultPresetID() {
	return defaultPresetID;
    }

    /**
     * Get the names of all the available presets (ie. categories) for this
     * migration path.
     * 
     * @return <code>Collection</code> containing the names/IDs of all the
     *         available preset categories.
     */
    Collection<String> getToolPresetNames() {
	return presets.keySet();
    }

    //FIXME! Doc!
    Collection<Preset> getAllToolPresets() {
	return presets.values();
    }

    /**
     * Set the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @param defaultPreset
     *            the ID of the default preset category.
     */
    void setDefaultPresetName(String defaultPreset) {
	this.defaultPresetID = defaultPreset;
    }

    /**
     * FIXME! Fix doc!
     * Set the presets for this <code>MigrationPathImpl</code> instance.
     * 
     * @param toolPresets
     *            Collection of presets to set.
     */
    void setToolPresets(Collection<Preset> toolPresets) {

	presets = new HashMap<String, Preset>();
	for (Preset preset : toolPresets) {
	    presets.put(preset.getName(), preset);
	}
    }

    Preset getPreset(String presetName) {
	return presets.get(presetName);
    }

}
