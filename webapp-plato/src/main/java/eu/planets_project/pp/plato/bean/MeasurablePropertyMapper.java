/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.workflow.AbstractWorkflowStep;
import eu.planets_project.pp.plato.evaluation.MiniRED;
import eu.planets_project.pp.plato.model.ObjectProperty;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.MeasurementInfo;
import eu.planets_project.pp.plato.model.measurement.Metric;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.tree.CriterionCategory;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.plato.StringCapsule;

@Name("measurablePropertyMapper")
@Scope(ScopeType.SESSION)
public class MeasurablePropertyMapper implements Serializable{
    
    private static final long serialVersionUID = -6882910401194589765L;


    private static final Log log = PlatoLogger.getLogger(MeasurablePropertyMapper.class);

    @In
    protected Plan selectedPlan;
    
    @Out(required = false)
    private Leaf mpmSelectedLeaf = null;

    private Collection<MeasurableProperty> allAvailableProperties = null;
    

    @In(required=false)
    @Out(required=false)
    private CriterionCategory mappingCategory = CriterionCategory.OUTCOME_OBJECT;
    
    /**
     * The currently chosen object property during the ObjectProperty-mapping process.
     */
    @In(required=false)
    @Out(required=false)
    private MeasurableProperty mappingProperty = null;

    /**
     * The status of the ObjectProperty-mapping process.
     * Can be either of <code>null|category|property|metric</code>
     */
    @Out
    private StringCapsule mappingStatus = new StringCapsule();

    /**
     * The currently chosen metric during the ObjectProperty-mapping process.
     */
    @In(required=false)
    @Out(required=false)
    private Metric mappingMetric = null;

    /**
     * Model for selection of an {@link ObjectProperty}. 
     */
    @Out(required=false)
    private List<SelectItem> propertiesModel = new ArrayList<SelectItem>();
    private Map<String, MeasurableProperty> propertiesMap = new HashMap<String, MeasurableProperty>();

    /**
     *  Model for selection of a {@link Metric}.
     *  It maps the names of the available metrics to the corresponding objects.
     */
    @Out(required=false)
    private List<SelectItem> metricsModel = new ArrayList<SelectItem>();
    private Map<String, Metric> metricsMap = new HashMap<String, Metric>();
    
    @Out(required=false)
    private List<SelectItem> categoriesModel = new ArrayList<SelectItem>();
    
    private Map<String, CriterionCategory> categoriesMap = new HashMap<String, CriterionCategory>();
    
    
    public void reset() {
        propertiesModel.clear();
        mappingStatus.setValue(null);
        mpmSelectedLeaf = null;
        allAvailableProperties = null;
        categoriesModel.clear();
        categoriesMap.clear();
        mappingCategory = CriterionCategory.OUTCOME_OBJECT;
        
        // we are adding these elements in a specific order to make them appear always in this order in the UI:
        categoriesMap.put(CriterionCategory.OUTCOME_OBJECT.toString(), CriterionCategory.OUTCOME_OBJECT);
        categoriesModel.add(new SelectItem(CriterionCategory.OUTCOME_OBJECT.toString()));
        categoriesMap.put(CriterionCategory.OUTCOME_FORMAT.toString(), CriterionCategory.OUTCOME_FORMAT);        
        categoriesModel.add(new SelectItem(CriterionCategory.OUTCOME_FORMAT.toString()));
        categoriesMap.put(CriterionCategory.OUTCOME_EFFECT.toString(), CriterionCategory.OUTCOME_EFFECT);        
        categoriesModel.add(new SelectItem(CriterionCategory.OUTCOME_EFFECT.toString()));
        categoriesMap.put(CriterionCategory.ACTION_RUNTIME.toString(), CriterionCategory.ACTION_RUNTIME);        
        categoriesModel.add(new SelectItem(CriterionCategory.ACTION_RUNTIME.toString()));
        categoriesMap.put(CriterionCategory.ACTION_STATIC.toString(), CriterionCategory.ACTION_STATIC);        
        categoriesModel.add(new SelectItem(CriterionCategory.ACTION_STATIC.toString()));
        categoriesMap.put(CriterionCategory.ACTION_JUDGEMENT.toString(), CriterionCategory.ACTION_JUDGEMENT);
        categoriesModel.add(new SelectItem(CriterionCategory.ACTION_JUDGEMENT.toString()));

    }
    
    public void init(Plan selectedPlan) {
        this.selectedPlan = selectedPlan;
        reset();
        getAvailableObjectProperties();
    }

    
    /**
     */
    private void getAvailableObjectProperties() {
        allAvailableProperties = MiniRED.getInstance().getPossibleMeasurements();
    }
    public void editMapping(){
        if (mpmSelectedLeaf == null) {
            return;
        }
        
        MeasurableProperty leafProp = mpmSelectedLeaf.getMeasurementInfo().getProperty();
        if ((leafProp != null) && (leafProp.getCategory() != null)) {
        }
        mappingStatus.setValue("category");
    }
    
    public void editProperty() {
        mappingProperty = null;
        propertiesModel.clear();
        propertiesMap.clear();
        
        List<MeasurableProperty> props = new ArrayList<MeasurableProperty>(); 
        if (allAvailableProperties != null) {
            // here we can filter out properties according to format of samples
            for (MeasurableProperty o: allAvailableProperties) {
                // show only properties of the selected category 
                if (o.getCategory().equals(mappingCategory)){
                    props.add(o);
                }
            }
            Collections.sort(props);
        }
        for (MeasurableProperty p: props) {
            String name = p.getName();
            propertiesMap.put(name, p);
            propertiesModel.add(new SelectItem(name));
        }
        
        MeasurableProperty leafProp = mpmSelectedLeaf.getMeasurementInfo().getProperty();
        if (leafProp != null) {
            // there was a mapping, try to restore the selected property
            mappingProperty = propertiesMap.get(leafProp.getName());
        } else if (props.size()>0){
            // no mapping was defined, select the first entry
            mappingProperty = props.get(0);
        }
        mappingStatus.setValue("property");
    }

    /**
     * Advances to editing the metric in the ObjectProperty-mapping process.
     * Populates the {@link #metricsModel} with all available metrics of the currently selected <param>leaf</param>.
     */
    public void editMetric(){
        if (mpmSelectedLeaf == null)
            return;

        metricsModel.clear();
        metricsMap.clear();
        mappingMetric = null;
        
        if (mappingProperty == null) {
           return;
        }
        
        for (Metric m : mappingProperty.getPossibleMetrics()) {
            String name = m.getName();
            metricsMap.put(name, m);
            metricsModel.add(new SelectItem(name));
        }
        if (!metricsModel.isEmpty()) {
            // if there are metrics, select the first one
            mappingMetric = mappingProperty.getPossibleMetrics().get(0);
        }
        mappingStatus.setValue("metric");

    }

    /**
     * Saves the property mapping by adjusting the scale of the given leaf <param>leaf</param>,
     * if the chosen metric is compatible with one of the available {@link Scale}s.
     * This finishes the ObjectProperty-mapping process.
     */
    public void savePropertyMapping() {
        if (mpmSelectedLeaf == null) {
            return;
        }
        if (mappingProperty != null) {
    
            MeasurementInfo mappingInfo = new MeasurementInfo();
            mappingInfo.setProperty(mappingProperty.clone());
            
            if (mappingMetric != null){
                mappingInfo.setMetric(mappingMetric.clone());
            } else {
                mappingInfo.setMetric(null);
            }
            Scale mappingScale =  mappingInfo.getScale();
                
            if (mappingScale != null) {
                // We have to set the scale first, as the measurement info will be reseted, if the scale changes
                mpmSelectedLeaf.changeScale(mappingScale);
                mpmSelectedLeaf.getMeasurementInfo().setProperty(mappingInfo.getProperty());
                mpmSelectedLeaf.getMeasurementInfo().setMetric(mappingInfo.getMetric());
                
                // only outcome object and action runtime criteria are evaluated per object, all others per action
                CriterionCategory mappingCategory = mappingProperty.getCategory();
                mpmSelectedLeaf.setSingle(!
                        (CriterionCategory.ACTION_RUNTIME.equals(mappingCategory) || 
                        CriterionCategory.OUTCOME_OBJECT.equals(mappingCategory)) );
                
                if ("- tbd -".equals(mpmSelectedLeaf.getName())) {
                     mpmSelectedLeaf.setName(mappingProperty.getName());
                }
            }
        }
        mappingMetric = null;
        mappingProperty = null;
        mappingStatus.setValue(null);
    }

    public void removeMapping() {
        if (mpmSelectedLeaf == null)
            return;
        mappingMetric = null;
        mappingProperty = null;
        mappingStatus.setValue(null);
        mpmSelectedLeaf.getMeasurementInfo().setMetric(null);
        mpmSelectedLeaf.getMeasurementInfo().setProperty(null);
        mpmSelectedLeaf = null;
    }
    
    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * starts the mapping process
     * @param leaf
     */
    public void editLeaf(Object leaf) {
        if (!(leaf instanceof Leaf))
            return;
        mpmSelectedLeaf = (Leaf)leaf;
        mappingStatus.setValue(null);        
    }
    
    public void selectionChanged() {
        // dummy call
    }

    public Object getSelectedCategory() {
        if (mappingCategory == null) {
            return null;
        }
        return mappingCategory.toString();
    }

    public void setSelectedCategory(Object value) {
        if (value == null) {
            mappingCategory = null;
            return;
        }
        mappingCategory = categoriesMap.get(value);
    }

    public Object getSelectedProperty() {
        if (mappingProperty == null) {
            return null;
        }
        return mappingProperty.getName();
    }

    public void setSelectedProperty(Object value) {
        if (value == null) {
            mappingProperty = null;
            return;
        }
        mappingProperty = propertiesMap.get(value);
    }

    public Object getSelectedMetric() {
        if (mappingMetric == null) {
            return null;
        }
        return mappingMetric.getName();
    }

    public void setSelectedMetric(Object value) {
        if (value == null) {
            mappingMetric = null;
            return;
        } 
        mappingMetric = metricsMap.get(value);
    }
    

}
