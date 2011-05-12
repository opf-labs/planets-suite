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
package eu.planets_project.pp.plato.massmigration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.MapKey;

import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;

/**
 * Holds information about migration of a set of files with one specific migration action.
 * 
 * @author kraxner
 */
@Entity
public class MassMigrationExperiment implements Serializable {
    private static final long serialVersionUID = -1080008328304618362L;

    @Id @GeneratedValue
    private int id;

    /**
     * Preservation action used to migrate all objects 
     */
    @OneToOne(cascade=CascadeType.ALL)    
    private PreservationActionDefinition action;

    /**
     * Map of experiment info, one per object
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapKey(columns = { @Column(name = "key_name") })
    @JoinTable(name = "mm_samples_results")    
    private Map<String, DetailedExperimentInfo> result = new HashMap<String, DetailedExperimentInfo>();
    
    /**
     * Average performance of the action, based on all sample files 
     */
    @OneToOne(cascade=CascadeType.ALL)
    private DetailedExperimentInfo averages = new DetailedExperimentInfo();

/*    @OneToOne(cascade=CascadeType.ALL)
    private ToolExperience result = new ToolExperience();
    */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PreservationActionDefinition getAction() {
        return action;
    }

    public void setAction(PreservationActionDefinition action) {
        this.action = action;
    }

    public Map<String, DetailedExperimentInfo> getResult() {
        return result;
    }

    public void setResult(Map<String, DetailedExperimentInfo> result) {
        this.result = result;
    }

    public void clearPreviousResults() {
        getResult().clear();
    }

    public DetailedExperimentInfo getAverages() {
        return averages;
    }

    public void setAverages(DetailedExperimentInfo averages) {
        this.averages = averages;
    }

}
