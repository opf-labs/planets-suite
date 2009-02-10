/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PathwayBean {

    private ServiceRecordBean srb;
    private FormatBean inputFormat;
    private FormatBean outputFormat;

    /**
     * @param name
     * @param inputFormat
     * @param outputFormat
     */
    public PathwayBean(ServiceRecordBean srb, FormatBean inputFormat, FormatBean outputFormat) {
        this.srb = srb;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    /**
     * @return the name
     */
    public ServiceRecordBean getServiceRecord() {
        return srb;
    }

    /**
     * @return the inputFormat
     */
    public FormatBean getInputFormat() {
        return inputFormat;
    }

    /**
     * @return the outputFormat
     */
    public FormatBean getOutputFormat() {
        return outputFormat;
    }
   
}
