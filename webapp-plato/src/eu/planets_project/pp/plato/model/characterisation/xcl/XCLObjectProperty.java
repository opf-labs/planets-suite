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
package eu.planets_project.pp.plato.model.characterisation.xcl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.ITouchable;
import eu.planets_project.pp.plato.model.ObjectProperty;
import eu.planets_project.pp.plato.model.measurement.Metric;

@Entity
@DiscriminatorValue("X")
public class XCLObjectProperty extends ObjectProperty{

    private static final long serialVersionUID = -2491433982815846784L;

//    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
//    @IndexColumn(name="metric_index")
    // we don't store all possible metrics 
    @Transient
    private List<Metric> metrics = new ArrayList<Metric>();

    public void addMetric(Metric metric) {
        metrics.add(metric);
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }
    
    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    @Override
    public void handleChanges(IChangesHandler h) {
        super.handleChanges(h);
        // and pass all metrics to the changes handler
        for(Metric m : metrics){
            m.handleChanges(h);
        }
    }

    @Override
    public ObjectProperty clone() {
        // create shallow copy:
        XCLObjectProperty clone = (XCLObjectProperty)super.clone();
        clone.setMetrics(new ArrayList<Metric>());
        for (Metric m : metrics) {
            clone.metrics.add(m.clone());
        }

        return clone;
    }
    
}
