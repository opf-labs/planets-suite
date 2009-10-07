package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import eu.planets_project.services.datatypes.Parameter;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * TODO abr forgot to document this class
 */
public class Preset {

    private String name;

    private Map<String, Collection<Parameter>> parameters;

    private String defaultSetting;

    private String description;

    public Preset(String name) {
        this.name = name;
        parameters = new HashMap<String, Collection<Parameter>>();
    }

    public String getName() {
        return name;
    }

    public void addSetting(String settingName,
                           Collection<Parameter> settingParameters) {
        parameters.put(settingName,settingParameters);
    }

    public void setDefaultSetting(String defaultSetting) {
        this.defaultSetting = defaultSetting;
    }

    public Collection<Parameter> getDefaultParameters() {
        return parameters.get(defaultSetting);
    }

    public Collection<Parameter> getParameters(String value) {
        return parameters.get(value);
    }

    public Parameter getAsPlanetsParameter(){

        Parameter.Builder pb = new Parameter.Builder(name,defaultSetting);
        pb.description(description);
        return pb.build();
    }
}
