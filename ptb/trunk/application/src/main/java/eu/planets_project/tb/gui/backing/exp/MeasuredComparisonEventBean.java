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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
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
        Map<String,MeasuredComparisonBean> cmp = new HashMap<String,MeasuredComparisonBean>();
        for( MeasurementImpl m : this.getEvent().getMeasurements() ) {
            if(m.getTarget().getType() == TargetType.DIGITAL_OBJECT_PAIR ) {
                cmp.put( m.getIdentifier(), new MeasuredComparisonBean(m) );
            } else if( m.getTarget().getType() == TargetType.DIGITAL_OBJECT ) {
                MeasuredComparisonBean mcb = cmp.get(m.getIdentifier());
                if( mcb == null ) {
                    mcb = new MeasuredComparisonBean();
                    cmp.put(m.getIdentifier(), mcb);
                }
                if( this.first.equals(m.getTarget().getDigitalObjects().firstElement())) {
                    mcb.measured1 = m;
                } else if( this.second.equals(m.getTarget().getDigitalObjects().firstElement())) {
                    mcb.measured2 = m;
                }
            }
        }
        // Extract:
        List<MeasuredComparisonBean> cms = new ArrayList<MeasuredComparisonBean>(cmp.values());
        return cms;
    }

    public class MeasuredComparisonBean {

        private MeasurementImpl compared;
        private MeasurementImpl measured1;
        private MeasurementImpl measured2;

        /**
         */
        public MeasuredComparisonBean() {
        }
        
        /**
         * @param m
         */
        public MeasuredComparisonBean(MeasurementImpl m) {
            this.compared = m;
        }

        /**
         * @param m
         */
        public MeasuredComparisonBean(MeasurementImpl m1, MeasurementImpl m2) {
            this.measured1 = m1;
            this.measured2 = m1;
        }

        public String getFirstValue() {
            if( measured1 != null ) return measured1.getValue();
            return "-";
        }

        public FormatBean getFirstFormat() {
            if( measured1 != null ) return measured1.getFormat();
            return null;
        }

        public String getSecondValue() {
            if( measured2 != null ) return measured2.getValue();
            return "-";
        }
        
        public FormatBean getSecondFormat() {
            if( measured2 != null ) return measured2.getFormat();
            return null;
        }


        public String getComparison() {
            if( compared != null ) return compared.getValue();
            if( isEqual() ) return "Equal";
            return "Different";
        }
        
        public boolean isEqual() {
//            if( compared != null ) return compared.getValue();
            if( measured1 != null && measured2 != null ) {
                if( measured1.getValue().equals(measured2.getValue()) ) return true;
            }
            return false;
        }
        
        public boolean isFormatProperty() {
            if( measured1 != null ) return measured1.isFormatProperty();
            return false;
        }
        
        public Property getProperty() {
            if( compared != null ) return compared.toProperty();
            if( measured1 != null ) return measured1.toProperty();
            return null;
        }
        
    }

}
