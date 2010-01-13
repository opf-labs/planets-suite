package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data carrier for tool presets from the generic wrapper configuration. A
 * preset may contain one or more settings which again contains a collection of
 * parameters. Thus, one could have e.g. a quality preset with three settings;
 * Good, Better and Best. Each of these settings then holds the necessary
 * parameters which must applied in order to achieve the quality described by
 * the preset setting.
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class Preset {

	private String name;

	private Map<String, PresetSetting> settings;

	private String defaultSettingName;

	private String description;

	Preset(String name, Collection<PresetSetting> settings,
			String defaultSettingName) {
		this.name = name;
		this.defaultSettingName = defaultSettingName;
		this.settings = new HashMap<String, PresetSetting>();
		for (PresetSetting presetSetting : settings) {
			this.settings.put(presetSetting.getName(), presetSetting);
		}
	}

	String getName() {
		return name;
	}

	PresetSetting getDefaultSetting() {
		return settings.get(defaultSettingName);
	}

	Collection<PresetSetting> getAllSettings() {
		return settings.values();
	}

	Set<String> getAllSettingNames() {
		return settings.keySet();
	}

	PresetSetting getSetting(String settingName) {
		return settings.get(settingName);
	}

	/**
	 * Get the description for this <code>Preset</code> describing the effect of
	 * applying its different preset settings.
	 * 
	 * @return <code>String</code> containing the description
	 */
	String getDescription() {
		return description;
	}

	/**
	 * Set the description for this <code>Preset</code> describing the effect of
	 * applying its different preset settings.
	 * 
	 * @param description
	 *            <code>String</code> containing the description text.
	 */
	void setDescription(String description) {
		this.description = description;
	}
}
