package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.planets_project.services.datatypes.Parameter;

/**
 * This class is a holder for methods for conversion of generic wrapper
 * <code>MigrationPath</code> instances to other similar types.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class MigrationPathConverter {

    /**
     * Convert the generic wrapper <code>MigrationPath</code> instances in a
     * <code>MigrationPaths</code> container to an array of PLANETS
     * <code>MigrationPath</code> instances. During this conversion any presets
     * of the generic wrapper migration paths will be converted to a PLANETS
     * parameters and a list of valid values and their descriptions will be
     * appended to the description of the (preset) parameter.
     * 
     * @param migrationPaths
     *            A <code>MigrationPaths</code> container, containing generic
     *            wrapper <code>MigrationPath</code> instances to convert.
     * @return an array of
     *         <code>eu.planets_project.services.datatypes.MigrationPath</code>
     *         created from the generic wrapper migration paths.
     */
    static eu.planets_project.services.datatypes.MigrationPath[] toPlanetsPaths(
	    MigrationPaths migrationPaths) {

	final Collection<MigrationPath> genericWrapperMigrationPaths = migrationPaths
		.getAllMigrationPaths();

	final ArrayList<eu.planets_project.services.datatypes.MigrationPath> planetsPaths = new ArrayList<eu.planets_project.services.datatypes.MigrationPath>();
	for (MigrationPath migrationPath : genericWrapperMigrationPaths) {

	    final List<Parameter> planetsParameters = new ArrayList<Parameter>();
	    planetsParameters.addAll(migrationPath.getToolParameters());

	    // Add a parameter for each preset (category)
	    final ToolPresets toolPresets = migrationPath.getToolPresets();
	    final Collection<Preset> presets = toolPresets.getAllToolPresets();
	    for (Preset preset : presets) {

		// Create a parameter for each preset which is not assigned any
		// value (i.e. null), however, the default preset parameter will
		// be
		// assigned the name of the default preset setting.
		Parameter.Builder parameterBuilder;
		if (preset.getName().equals(toolPresets.getDefaultPresetID())) {
		    parameterBuilder = new Parameter.Builder(preset.getName(),
			    preset.getDefaultSetting().getName());
		} else {
		    parameterBuilder = new Parameter.Builder(preset.getName(),
			    null);
		}

		// Append a description of the valid values for the preset
		// parameter.
		String usageDescription = "\n\nValid values : Description\n";

		for (PresetSetting presetSetting : preset.getAllSettings()) {

		    usageDescription += "\n" + presetSetting.getName() + " : "
			    + presetSetting.getDescription();
		}

		parameterBuilder.description(preset.getDescription()
			+ usageDescription);

		planetsParameters.add(parameterBuilder.build());
	    }
	    planetsPaths
		    .add(new eu.planets_project.services.datatypes.MigrationPath(
			    migrationPath.getInputFormat(), migrationPath
				    .getOutputFormat(), planetsParameters));
	}

	return planetsPaths
		.toArray(new eu.planets_project.services.datatypes.MigrationPath[planetsPaths
			.size()]);
    }
}
