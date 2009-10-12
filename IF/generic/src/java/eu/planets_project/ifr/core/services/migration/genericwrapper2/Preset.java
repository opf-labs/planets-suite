package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import eu.planets_project.services.datatypes.Parameter;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * Data carrier for tool presets from the generic wrapper configuration.
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class Preset {

    private String name;

    private Map<String, Collection<Parameter>> settings;

    private String defaultSetting;

    private String description;

    public Preset(String name) {
        this.name = name;
        settings = new HashMap<String, Collection<Parameter>>();
    }

    public String getName() {
        return name;
    }

    public void addSetting(String settingName,
                           Collection<Parameter> settingParameters) {
        settings.put(settingName,settingParameters);
    }

    public void setDefaultSetting(String defaultSetting) {
        this.defaultSetting = defaultSetting;
    }

    public Collection<Parameter> getDefaultParameters() {
        return settings.get(defaultSetting);
    }

    public Collection<Parameter> getParameters(String value) {
        return settings.get(value);
    }

    public Parameter getAsPlanetsParameter(){

        Parameter.Builder pb = new Parameter.Builder(name,defaultSetting);
        pb.description(description);
        return pb.build();
    }
}
