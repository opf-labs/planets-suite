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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class MassMigrationStatus implements Serializable {
    private static final long serialVersionUID = -8513892036477323532L;
    
    public static final int FAILED = -1;
    public static final int CREATED = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;

    @Id @GeneratedValue
    private int id;
    
    private int numOfSamples = 0;
    
    private int currentSample = 0;
    
    @Transient
    private int numOfTools = 0;
    
    @Transient
    private int currentTool = 0;
    
    
    private int status = CREATED;
    
    
    public void reset() {
        numOfSamples = 0;
        currentSample = 0;
        numOfTools = 0;
        currentTool = 0;
        status = CREATED;
    }
    public int getCurrentSample() {
        return currentSample;
    }

    public void setCurrentSample(int currentSample) {
        this.currentSample = currentSample;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumOfSamples() {
        return numOfSamples;
    }

    public void setNumOfSamples(int numOfSamples) {
        this.numOfSamples = numOfSamples;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getStatusAsString(){
        switch(status) {
            case FAILED : return "FAILED";
            case RUNNING: return "RUNNING";
            case FINISHED: return "FINISHED";
            default: return "CREATED";
        }
    }
    public int getNumOfTools() {
        return numOfTools;
    }
    public void setNumOfTools(int numOfTools) {
        this.numOfTools = numOfTools;
    }
    public int getCurrentTool() {
        return currentTool;
    }
    public void setCurrentTool(int currentTool) {
        this.currentTool = currentTool;
    }

}
