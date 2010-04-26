package eu.planets_project.ifr.core.services.migration.genericwrapper1;

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

    /**
     * @param name
     */
    public Preset(String name) {
        this.name = name;
        parameters = new HashMap<String, Collection<Parameter>>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param settingName
     * @param settingParameters
     */
    public void addSetting(String settingName,
                           Collection<Parameter> settingParameters) {
        parameters.put(settingName,settingParameters);
    }

    /**
     * @param defaultSetting
     */
    public void setDefaultSetting(String defaultSetting) {
        this.defaultSetting = defaultSetting;
    }

    /**
     * @return the default Parameters
     */
    public Collection<Parameter> getDefaultParameters() {
        return parameters.get(defaultSetting);
    }

    /**
     * @param value
     * @return the Parameters
     */
    public Collection<Parameter> getParameters(String value) {
        return parameters.get(value);
    }

    /**
     * @return a planets parameter
     */
    public Parameter getAsPlanetsParameter(){

        Parameter.Builder pb = new Parameter.Builder(name,defaultSetting);
        pb.description(description);
        return pb.build();
    }
}
