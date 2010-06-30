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
package eu.planets_project.tb.gui.backing.exp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MeasurementEventBean implements Comparable<Object> {
    /** */
    @SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(MeasurementEventBean.class);

    /** */
    protected boolean selected = true;
    
    /** */
    protected boolean odd = false;

    /** */
    private MeasurementEventImpl event;
    
    public MeasurementEventBean( MeasurementEventImpl event ) {
        this.event = event;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the odd
     */
    public boolean isOdd() {
        return odd;
    }

    /**
     * @param odd the odd to set
     */
    public void setOdd(boolean odd) {
        this.odd = odd;
    }

    /**
     * @return the event
     */
    public MeasurementEventImpl getEvent() {
        return event;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        if( arg0 instanceof MeasurementEventBean ) {
            MeasurementEventBean other = (MeasurementEventBean) arg0;
            if( this.event != null && other != null ) 
                return this.event.compareTo( other.event );
        }
        return 0;
    }
    
}
