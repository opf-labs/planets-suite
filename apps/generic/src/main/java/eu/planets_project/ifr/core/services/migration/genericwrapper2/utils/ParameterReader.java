package eu.planets_project.ifr.core.services.migration.genericwrapper2.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Utility class for reading the value of <code>Parameter</code> instances in a
 * <code>List&lt;Parameter&gt;</code> instance.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class ParameterReader {

    /**
     * Map associating parameter names with <code>Parameter</code> instances.
     */
    private Map<String, Parameter> parameterMap;

    /**
     * Create an instance which reads parameter values from
     * <code>parameters</code>.
     * 
     * @param parameters
     *            A list of parameters to read parameter values from.
     */
    public ParameterReader(List<Parameter> parameters) {

	this.parameterMap = new HashMap<String, Parameter>();
	for (Parameter parameter : parameters) {
	    this.parameterMap.put(parameter.getName(), parameter);
	}
    }

    /**
     * Read a boolean parameter value from the parameter with the name specified
     * by <code>parameterName</code>. If the parameter is undefined in the
     * parameter list then <code>defaultValue</code> is returned.
     * 
     * @param parameterName
     *            Name of the <code>Parameter</code> instance to fetch the value
     *            of.
     * @param defaultValue
     *            The default value to return if the parameter was not found in
     *            the parameter list.
     * @return The value of the parameter or the default value, if the parameter
     *         was not found.
     */
    public boolean getBooleanParameter(String parameterName,
	    boolean defaultValue) {

	try {
	    return getBooleanParameter(parameterName);
	} catch (Exception e) {
	    // No matter what, just return the default value.
	    return defaultValue;
	}
    }

    /**
     * Read a boolean parameter value from the parameter with the name specified
     * by <code>parameterName</code>. If the parameter is undefined then an
     * exception is thrown.
     * 
     * @param parameterName
     *            Name of the <code>Parameter</code> instance to fetch the value
     *            of.
     * 
     * @return The boolean value of the parameter with the name specified by
     *         <code>parameterName</code>.
     * @throws NoSuchElementException
     *             if the parameter does not exist in the list held by this
     *             <code>ParameterReader</code> instance.
     */
    public boolean getBooleanParameter(String parameterName) {

	final String parameterValue = this.parameterMap.get(parameterName)
		.getValue();
	if (parameterValue == null) {
	    throw new NoSuchElementException(String.format("Could not find a "
		    + "parameter with the name '%s'", parameterName));
	}

	return Boolean.parseBoolean(parameterValue);
    }
}
