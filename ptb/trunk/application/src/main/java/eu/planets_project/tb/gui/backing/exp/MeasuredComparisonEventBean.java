/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.view.MeasuredComparisonBean;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget.TargetType;

/**
 * @author anj
 *
 */
public class MeasuredComparisonEventBean extends MeasurementEventBean {

    private String first;
    private String second;


    /**
     * @param event
     */
    public MeasuredComparisonEventBean(MeasurementEventImpl event, String first, String second ) {
        super(event);
        // Also store the DOB identities:
        this.first = first;
        this.second = second;
    }

    /** */
    private static final Log log = LogFactory.getLog(MeasuredComparisonEventBean.class);


    /**
     * @return the measurements in this event, as a set of comparisons between two objects
     */
    public List<MeasuredComparisonBean> getComparisons() {
        return MeasuredComparisonBean.createFromEvents( first, second, null, getEvent() );
    }


}
