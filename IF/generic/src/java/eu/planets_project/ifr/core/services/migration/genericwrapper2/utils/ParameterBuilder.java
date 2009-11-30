/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Utility class for building <code>Parameter</code> objects from various
 * sources of data.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class ParameterBuilder {

    /**
     * Build a list of <code>Parameter</code> instances from the key - value
     * pairs in <code>configuration</code>.
     * 
     * @param configuration
     *            <code>Configuration</code> containing key - value pairs to
     *            convert to <code>Parameter</code> instances.
     * @return A list of <code>Parameter</code> instances generated from the
     *         contents of <code>configuration</code>.
     */
    @SuppressWarnings("unchecked")
    public static List<Parameter> buid(Configuration configuration) {

	final List<Parameter> parameterList = new ArrayList<Parameter>();
	final Iterator keyIterator = configuration.getKeys();
	while (keyIterator.hasNext()) {

	    final String key = (String) keyIterator.next();
	    final String value = configuration.getString(key);
	    final Parameter.Builder parameterBuilder = new Parameter.Builder(
		    key, value);

	    parameterList.add(parameterBuilder.build());
	}
	return parameterList;
    }
}
