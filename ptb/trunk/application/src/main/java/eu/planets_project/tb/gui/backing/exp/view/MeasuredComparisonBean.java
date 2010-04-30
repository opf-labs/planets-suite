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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.compare.PropertyComparison.Equivalence;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.backing.exp.MeasurementBean;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.impl.model.eval.PropertyEvaluation;
import eu.planets_project.tb.impl.model.eval.PropertyEvaluation.EquivalenceStatement;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget.TargetType;

/**
 * 
 * @author Andrew.Jackson@bl.uk
 *
 */
public class MeasuredComparisonBean 
                    implements Serializable, Comparable<MeasuredComparisonBean> {
    
    private static final long serialVersionUID = 8134383234470113727L;

    static private Log log = LogFactory.getLog(MeasuredComparisonBean.class);
    
    private Property property;
    private PropertyEvaluation propertyEvaluation;
    
    protected List<MeasurementBean> first = new ArrayList<MeasurementBean>();
    protected List<MeasurementBean> second = new ArrayList<MeasurementBean>();
    protected List<MeasurementBean> compared = new ArrayList<MeasurementBean>();

    /**
     * @param propertyUri
     */
    public MeasuredComparisonBean( Property property, PropertyEvaluation propertyEvaluation ) {
        this.property = property;
        this.propertyEvaluation = propertyEvaluation;
    }
        
    public boolean isEqual() {
        if( this.getUserEquivalence() == EquivalenceStatement.EQUAL ) {
            return true;
        }
        return false;
    }
    
    /**
     * @return the property
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @return the compared
     */
    public List<MeasurementBean> getCompared() {
        return compared;
    }

    /**
     * @return the firstMeasured
     */
    public List<MeasurementBean> getFirst() {
        return first;
    }

    /**
     * @return the secondMeasured
     */
    public List<MeasurementBean> getSecond() {
        return second;
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
     * @return 
     */
    public List<MeasurementBean> getAllFirstMeasured() {
        List<MeasurementBean> mb = new ArrayList<MeasurementBean>(this.first);
        for( MeasurementBean cm : compared ) {
            if( cm.getTarget().getDigitalObjectProperties(0) != null && 
                cm.getTarget().getDigitalObjectProperties(0).size() > 0 ) 
             mb.add( new MeasurementBean( cm.getEvent(), 
                     new MeasurementImpl( cm.getEvent(), cm.getTarget().getDigitalObjectProperties(0).get(0) ) ) );
        }
        return mb;
    }
    
    /**
     * @return
     */
    public List<MeasurementBean> getAllSecondMeasured() {
        List<MeasurementBean> mb = new ArrayList<MeasurementBean>(this.second);
        for( MeasurementBean cm : compared ) {
            if( cm.getTarget().getDigitalObjectProperties(1) != null && 
                cm.getTarget().getDigitalObjectProperties(1).size() > 0 ) 
             mb.add( new MeasurementBean( cm.getEvent(), 
                     new MeasurementImpl( cm.getEvent(), cm.getTarget().getDigitalObjectProperties(1).get(1) ) ) );
        }
        return mb;
    }

    /**
     * @return
     */
    public List<MeasurementBean> getAllCompared() {
        // FIXME Add dumb comparisons of properties here?
        return compared;
    }
    
    /**
     * @return
     */
    public boolean isAllComparedEmpty() {
        if( this.compared == null || this.compared.size() == 0 ) return true;
        return false;
    }
    
    /**
     * @return
     */
    public EquivalenceStatement getUserEquivalence() {
        return getPropertyEvaluation().getUserEquivalence();
    }
    
    /**
     * @return
     */
    public void setUserEquivalence( EquivalenceStatement es ) {
        // Update if needed.
        if( getPropertyEvaluation().getUserEquivalence() != es ) {
            getPropertyEvaluation().setUserEquivalence(es);
            ExperimentInspector.persistExperiment();
        }
    }
    
    /**
     * @return
     */
    public String getUserEquivalenceComment() {
        if( getPropertyEvaluation() == null ) return "";
        return getPropertyEvaluation().getUserEquivalenceComment();
    }

    /**
     * @param me
     * @param dobUri1
     * @param dobUri2
     * @param peval 
     * @return
     */
    public static List<MeasuredComparisonBean> createFromEvents(
            String dobUri1, String dobUri2, Vector<PropertyEvaluation> pevals, MeasurementEventImpl ... mes ) {
        Map<String,MeasuredComparisonBean> cmp = new HashMap<String,MeasuredComparisonBean>();
        for( MeasurementEventImpl me : mes ) {
            if( me.getMeasurements() != null ) {
                log.info("Looking for comparisons out of "+me.getMeasurements().size());
                for( MeasurementImpl m : me.getMeasurements() ) {
                    // Create the comparison object:
                    if(m.getTarget().getType() == TargetType.DIGITAL_OBJECT_PAIR ) {
                        MeasuredComparisonBean mb = new MeasuredComparisonBean( 
                                m.getProperty(),
                                findPropertyEvaluation(pevals,m) );
                        mb.getCompared().add(new MeasurementBean(me, m) );
                        cmp.put( m.getIdentifier(), mb );
                    } else if( m.getTarget().getType() == TargetType.DIGITAL_OBJECT ) {
                        MeasuredComparisonBean mcb = cmp.get(m.getIdentifier());
                        if( mcb == null ) {
                            mcb = new MeasuredComparisonBean( 
                                    m.getProperty(),
                                    findPropertyEvaluation(pevals,m) );
                            cmp.put(m.getIdentifier(), mcb);
                        }
                        if( dobUri1.equals(m.getTarget().getDigitalObjects().firstElement())) {
                            mcb.getFirst().add(new MeasurementBean(me,m));
                        } else if( dobUri2.equals(m.getTarget().getDigitalObjects().firstElement())) {
                            mcb.getSecond().add(new MeasurementBean(me,m));
                        }
                    }
                }
            }
        }
        // Extract:
        List<MeasuredComparisonBean> cms = new ArrayList<MeasuredComparisonBean>(cmp.values());
        // Sort:
        Collections.sort(cms);
        return cms;
    }
    
    private static PropertyEvaluation findPropertyEvaluation( Vector<PropertyEvaluation> pevals, MeasurementImpl m ) {
        for( PropertyEvaluation peval : pevals ) {
            if( peval.getPropertyUri().equals( m.getIdentifierUri() ) ) return peval;
        }
        // Or make a new one:
        PropertyEvaluation peval = new PropertyEvaluation(m.getIdentifierUri());
        pevals.add(peval);
        return peval;
    }
    
    /**
     * @return
     */
    private PropertyEvaluation getPropertyEvaluation() {
        // Look up the property evaluation associated with this.
        if( this.propertyEvaluation == null ) {
            propertyEvaluation = new PropertyEvaluation( this.getProperty().getUri() );
        }
        // If not set, default to the equivalence from the measurement.
        if( propertyEvaluation.getUserEquivalence() == null ) {
            Equivalence eqv = this.getEquivalence();
            if( eqv == Equivalence.EQUAL ) {
                propertyEvaluation.setUserEquivalence( EquivalenceStatement.EQUAL );
            } else if( eqv == Equivalence.DIFFERENT ) {
                propertyEvaluation.setUserEquivalence( EquivalenceStatement.DIFFERENT );
            } else if( eqv == Equivalence.MISSING ) {
                propertyEvaluation.setUserEquivalence( EquivalenceStatement.MISSING );
            }
        }
        return propertyEvaluation;
    }

    /**
     * @return
     */
    public Equivalence getEquivalence() {
        // Default to the first comparison property:
        if( this.compared != null && this.compared.size() > 0 ) {
            log.info("Compared: "+this.compared.size()+" "+this.compared.get(0).getName()+" "+this.compared.get(0).getEquivalence());
            return this.compared.get(0).getEquivalence();
        }
        // Otherwise, look at the property sets
        if( this.first != null && this.second !=null ) {
            if( this.first.size() == this.second.size() ) {
                // FIXME This should clone the second list and remove them as the matches come.
                int diffs = 0;
                int misses = 0;
                for( MeasurementBean m1 : first ) {
                    // Look for matching MeasurementBean
                    boolean matched = false;
                    for( MeasurementBean m2 : second ) {
                        if( m1.getProperty().getUri().equals( m2.getProperty().getUri()) ) {
                            matched = true;
                            if( ! m1.getValue().equals( m2.getValue()) ) {
                                diffs++;
                            }
                        }
                    }
                    if( !matched ) misses++;
                }
                if( misses > 0 ) return Equivalence.MISSING;
                if( diffs > 0 ) return Equivalence.DIFFERENT;
                return Equivalence.EQUAL;
            } else {
                return Equivalence.MISSING;
            }
        }
        log.info("Compared: "+this.compared);
        return Equivalence.UNKNOWN;
    }
    
}
