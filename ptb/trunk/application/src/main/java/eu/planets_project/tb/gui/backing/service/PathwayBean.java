/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PathwayBean {

    private String name;
    private URI inputFormat;
    private URI outputFormat;

    /**
     * @param name
     * @param inputFormat
     * @param outputFormat
     */
    public PathwayBean(String name, URI inputFormat, URI outputFormat) {
        this.name = name;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the inputFormat
     */
    public URI getInputFormat() {
        return inputFormat;
    }

    /**
     * @return the outputFormat
     */
    public URI getOutputFormat() {
        return outputFormat;
    }
   
}
