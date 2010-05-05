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
package eu.planets_project.services.modify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * Result type for modification services.
 * @author <a href="mailto:Peter.Melms@uni-koeln.de">Peter Melms</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class ModifyResult {

    private DigitalObject digitalObject;
    private ServiceReport report;

    /**
     * for JAXB.
     */
    @SuppressWarnings("unused")
    private ModifyResult() {};

    /**
     * @param digitalObject The modified digital object
     * @param report The report for the modification
     */
    public ModifyResult(final DigitalObject digitalObject,
            final ServiceReport report) {
        this.digitalObject = digitalObject;
        this.report = report;
    }

    /**
     * @return The digital object
     */
    public DigitalObject getDigitalObject() {
        return digitalObject;
    }

    /**
     * @return The report
     */
    public ServiceReport getReport() {
        return report;
    }

}
