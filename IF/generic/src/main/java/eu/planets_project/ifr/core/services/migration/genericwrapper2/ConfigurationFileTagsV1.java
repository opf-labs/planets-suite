/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2;

/**
 * This class contains constants for each of the tags/XML element names
 * applicable in a generic wrapper configuration file adhering to format version
 * 1.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class ConfigurationFileTagsV1 {

    /**
     * Constant containing the configuration file format version number which
     * the XML tags in this file are valid for.
     */
    static final String CONFIGURATION_FORMAT_VERSION = "1.0";

    /**
     * A constant describing the absolute Xpath to the "serviceDescription"
     * element in the configuration file.
     */
    static final String SERVICE_DESCRIPTION_ELEMENT_XPATH = "//serviceWrapping/serviceDescription";

    /**
     * The XPath to a property element in a properties element. 
     */
    static final String PROPERTIES_PROPERTY_XPATH = "properties/property";
    
    /**
     * The name of a version element.
     */
    static final String VERSION_ELEMENT = "version";

    /**
     * The name of a title element.
     */
    static final String TITLE_ELEMENT = "title";

    /**
     * The name of an instructions element.
     */
    static final String INSTRUCTIONS_ELEMENT = "instructions";

    /**
     * The name of a further info element.
     */
    static final String FURTHER_INFO_ELEMENT = "furtherinfo";

    /**
     * The name of a value element.
     */
    static final String VALUE_ELEMENT = "value";
    
    /**
     * The name of a logo element.
     */
    static final String LOGO_ELEMENT = "logo";

    /**
     * The name of a creator element.
     */
    static final String CREATOR_ELEMENT = "creator";

    /**
     * The name of a identifier element.
     */
    static final String IDENTIFIER_ELEMENT = "identifier";

    /**
     * The name of a "name" element.
     */
    static final String NAME_ELEMENT = "name";

    /**
     * The name of a "home page" element.
     */
    static final String HOME_PAGE_ELEMENT = "homepage";
    
    /**
     * The name of value attributes.
     */
    static final String VALUE_ATTRIBUTE = "value";

    /**
     * The name of unit attributes.
     */
    static final String UNIT_ATTRIBUTE = "unit";
    
    /**
     * The name of ID attributes.
     */
    static final String ID_ATTRIBUTE = "id";
    
    /**
     * The name of type attributes.
     */
    static final String TYPE_ATTRIBUTE = "type";
    
    /**
     * A constant describing the absolute Xpath to a "path" element in the
     * configuration file.
     */
    static final String PATH_ELEMENT_XPATH = "//serviceWrapping/paths/path";

    /**
     * The name of a "tool" element in the service description element.
     */
    static final String TOOL_ELEMENT = "tool";

    /**
     * The name of a "toolparameters" element.
     */
    static final String TOOLPARAMETERS_ELEMENT = "toolparameters";

    /**
     * The name of a "tooloutput" element.
     */
    static final String TOOLOUTPUT_ELEMENT = "tooloutput";

    /**
     * The name of a "toolinput" element.
     */
    static final String TOOLINPUT_ELEMENT = "toolinput";

    /**
     * The name of a "piped" element.
     */
    static final String PIPED_ELEMENT = "piped";

    /**
     * The name of the version attribute.
     */
    static final String VERSION_ATTRIBUTE = "version";

    /**
     * The absolute Xpath to the configuration root element.
     */
    static final String CONFIGURATION_ROOT_ELEMENT_XPATH = "//serviceWrapping";

    /**
     * The name of a URI element.
     */
    static final String URI_ELEMENT = "uri";

    /**
     * The relative Xpath to a parameter element in the command parameters
     * section.
     */
    static final String COMMANDPARAMETER_ELEMENT_XPATH = "commandparameters/parameter";

    /**
     * The name of the command element.
     */
    static final String COMMAND_ELEMENT = "command";

    /**
     * The name of a label attribute.
     */
    static final String LABEL_ATTRIBUTE = "label";

    /**
     * The name of temp. file elements.
     */
    static final String TEMPFILE_ELEMENT = "tempfile";

    /**
     * The name of parameter elements.
     */
    static final String PARAMETER_ELEMENT = "parameter";

    /**
     * The name of a "settings" element, used in preset sections.
     */
    static final String SETTINGS_ELEMENT = "settings";

    /**
     * The name of "description" elements.
     */
    static final String DESCRIPTION_ELEMENT = "description";

    /**
     * The name of "default" attributes.
     */
    static final String DEFAULT_ATTRIBUTE = "default";

    /**
     * The name of "name" attributes.
     */
    static final String NAME_ATTRIBUTE = "name";

    /**
     * The name of "preset" elements.
     */
    static final String PRESET_ELEMENT = "preset";
}
