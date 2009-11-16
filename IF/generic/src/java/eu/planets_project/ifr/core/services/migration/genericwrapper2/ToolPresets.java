/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class ToolPresets {

    private Map<String, Preset> presets;
    private String defaultPresetID;

    public ToolPresets() {
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
    public Collection<String> getToolPresetNames() {
	return presets.keySet();
    }

    public Collection<Preset> getAllToolPresets() {
	return presets.values();
    }

    /**
     * Set the ID of the default preset category to apply if no preset or
     * parameters are specified by the user of this migration path.
     * 
     * @param defaultPreset
     *            the ID of the default preset category.
     */
    public void setDefaultPresetName(String defaultPreset) {
	this.defaultPresetID = defaultPreset;
    }

    /**
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
