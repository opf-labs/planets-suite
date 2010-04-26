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
package eu.planets_project.tb.gui.backing.exp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.compare.PropertyComparison.Equivalence;
import eu.planets_project.tb.gui.backing.data.DigitalObjectCompare;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl.EquivalenceStatement;

/**
 * 
 * @author Andrew.Jackson@bl.uk
 *
 */
public class MeasuredComparisonBean 
                    implements Comparable<MeasuredComparisonBean> {
    
    static private Log log = LogFactory.getLog(MeasuredComparisonBean.class);
    

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

    /**{
     * @param m
     */
    public MeasuredComparisonBean(MeasurementImpl m1, MeasurementImpl m2) {
        this.setFirstMeasured(m1);
        this.setSecondMeasured(m1);
    }

    public String getFirstValue() {
        Property p = this.getFirstMeasured();
        if( p == null ) return "";
        String value = p.getValue();
        if( value.length() > 200 ) value = value.substring(0, 200) + " ...";
        return value;
    }

    public FormatBean getFirstFormat() {
        if( getFirstMeasured() != null ) return MeasurementImpl.getFormat( getFirstMeasured() );
        return null;
    }

    public String getSecondValue() {
        Property p = this.getSecondMeasured();
        if( p == null ) return "";
        String value = p.getValue();
        if( value.length() > 200 ) value = value.substring(0, 200) + " ...";
        return value;
    }
    
    public FormatBean getSecondFormat() {
        if( getSecondMeasured() != null ) return MeasurementImpl.getFormat( getSecondMeasured() );
        return null;
    }


    public String getComparison() {
        if( compared != null ) return compared.getValue();
        if( isEqual() ) return "Equal";
        return "Different";
    }
    
    public boolean isEqual() {
        if( this.getUserEquivalence() == EquivalenceStatement.EQUAL ) {
            return true;
        }
        return false;
    }
    
    public boolean isFormatProperty() {
        if( getFirstMeasured() != null ) return MeasurementImpl.isFormatProperty( getFirstMeasured() );
        if( getSecondMeasured() != null ) return MeasurementImpl.isFormatProperty( getSecondMeasured() );
        return false;
    }
    
    public Property getProperty() {
        if( compared != null ) return compared.toProperty();
        if( getFirstMeasured() != null ) return getFirstMeasured();
        if( getSecondMeasured() != null ) return getSecondMeasured();
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(MeasuredComparisonBean o) {
        if( o.getProperty() != null && o.getProperty().getName() != null ) {
            if( this.getProperty() != null && this.getProperty().getName() != null ) {
                return this.getProperty().getName().compareTo( o.getProperty().getName() );
            }
        }
        return 0;
    }

    /**
     * @param measured1 the measured1 to set
     */
    public void setFirstMeasured(MeasurementImpl measured1) {
        this.measured1 = measured1;
    }

    /**
     * @return the measured1
     */
    public Property getFirstMeasured() {
        if( compared != null && compared.getTarget().getDigitalObjectProperties(0) != null && 
               compared.getTarget().getDigitalObjectProperties(0).size() > 0 ) 
            return compared.getTarget().getDigitalObjectProperties(0).get(0);
        if( measured1 != null  ) return measured1.getProperty();
        return null;
    }

    /**
     * @param measured2 the measured2 to set
     */
    public void setSecondMeasured(MeasurementImpl measured2) {
        this.measured2 = measured2;
    }

    /**
     * @return the measured2
     */
    public Property getSecondMeasured() {
        if( compared != null && compared.getTarget().getDigitalObjectProperties(1) != null && 
                compared.getTarget().getDigitalObjectProperties(1).size() > 0 )  
            return compared.getTarget().getDigitalObjectProperties(1).get(0);
        if( measured2 != null  ) return measured2.getProperty();
        return null;
    }

    /**
     * @return
     */
    public MeasurementImpl getCompared() {
        return compared;
    }
    
    /**
     * @return
     */
    public Equivalence getEquivalence() {
        if( compared == null ) return Equivalence.UNKNOWN;
        return compared.getEquivalence();
    }
    
    /**
     * @return
     */
    public EquivalenceStatement getUserEquivalence() {
        if( compared == null ) return EquivalenceStatement.NOT_APPLICABLE;
        return compared.getUserEquivalence();
    }
    
    /**
     * @return
     */
    public void setUserEquivalence( EquivalenceStatement es ) {
        if( compared == null ) return;
        compared.setUserEquivalence(es);
        DigitalObjectCompare.persistExperiment();
    }
    
    /**
     * @return
     */
    public String getUserEquivalenceComment() {
        if( compared == null ) return "";
        return compared.getUserEquivalenceComment();
    }
    
}
