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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class MassMigrationSetup implements Serializable {
    private static final long serialVersionUID = -6730089335709937774L;
    
    @Id @GeneratedValue
    private int id;
    
    
    @Column(length = 60)
    private String name;
    
    @Column(length = 2000)
    private String description;

    @Column(length = 255)
    private String sourcePath;
    
    @Column(length = 255)
    private String resultPath;

    @Column(length = 255)
    private String lastResultPath; 
    
    @OneToOne(cascade = CascadeType.ALL)
    private MassMigrationStatus status = new MassMigrationStatus();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MassMigrationExperiment> experiments = new ArrayList<MassMigrationExperiment>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public List<MassMigrationExperiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<MassMigrationExperiment> experiments) {
        this.experiments = experiments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MassMigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MassMigrationStatus status) {
        this.status = status;
    }
    
    public void prepareMassMigration() {
        getStatus().reset();
        for (MassMigrationExperiment exp : getExperiments()) {
            exp.clearPreviousResults();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastResultPath() {
        return lastResultPath;
    }

    public void setLastResultPath(String lastResultPath) {
        this.lastResultPath = lastResultPath;
    }
     
    public List<String> getAllAverageProperties() {
        ArrayList<String> allProps = new ArrayList<String>();
        for (MassMigrationExperiment exp : experiments) {
            if (exp.getAverages()!= null) {
                for(String prop: exp.getAverages().getMeasurements().keySet()) {
                    if (!allProps.contains(prop))
                        allProps.add(prop);
                }
            }
        }
        Collections.sort(allProps);
        return allProps;
    }
}
