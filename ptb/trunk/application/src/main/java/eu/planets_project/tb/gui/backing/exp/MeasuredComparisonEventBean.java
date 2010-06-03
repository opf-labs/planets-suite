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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.exp.view.MeasuredComparisonBean;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;

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
