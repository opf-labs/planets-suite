/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
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
