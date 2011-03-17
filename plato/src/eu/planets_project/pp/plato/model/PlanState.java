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

package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * This class represents the state of a planning project.
 *
 * Also provides a list of all possible states that a planning project can be in
 *
 * @author Christoph Becker
 * @author Kevin Stadler
 *
 */

/**
 * TODO: it would be nice to use an enum instead (Plato 3.1)
 *
public enum PlanState {
    CREATED(0, "Created"),
    INITIALIZED(1, "Initialised");
    // ...
    
    private String stateName;
    private int value;
    
    private PlanState(int value, String stateName) {
        this.stateName = stateName;
        this.value = value;
    }
    public String getStateName() {
        return stateName;
    }
    
    public int getValue() {
        return value;
    }
    
}
*/
@Entity
public class PlanState implements Serializable {

    private static final long serialVersionUID = -1377509183902236291L;

    public static final int CREATED = 0;

    public static final int INITIALISED = 1;

    public static final int BASIS_DEFINED = 2;

    public static final int RECORDS_CHOSEN = 3;

    public static final int TREE_DEFINED = 4;

    public static final int ALTERNATIVES_DEFINED = 5;

    public static final int GO_CHOSEN = 6;

    public static final int EXPERIMENT_DEFINED = 7;

    public static final int EXPERIMENT_PERFORMED = 8;

    public static final int RESULTS_CAPTURED = 9;

    public static final int TRANSFORMATION_DEFINED = 10;

    public static final int WEIGHTS_SET = 11;

    public static final int ANALYSED = 12;

    public static final int EXECUTEABLE_PLAN_CREATED = 13;

    public static final int PLAN_DEFINED = 14;

    public static final int PLAN_VALIDATED = 15;
    
    public static final int FTE_INITIALISED = 16;
    
    public static final int FTE_REQUIREMENTS_DEFINED = 17;
    
    public static final int FTE_ALTERNATIVES_EVALUATED = 18;
    
    public static final int FTE_RESULTS_ANALYSED = 19;
    
    /**
     * for convenience, e.g. logging output. PLEASE update when changing states!
     */
    public static String[] stateNames = new String[] { "Created", "Initialised",
            "Basis Defined", "Records Chosen", "Tree Defined",
            "Alternatives Defined", "Go Decision Taken", "Experiments Defined",
            "Experiments Performed", "Results Captured",
            "Transformations Defined", "Weights Set", "Analyzed", "Executable Plan Created", "Plan Defined", "Plan Validated",
            "Define Requirements (Fast-track evaluation)", "Evaluate Alternatives (Fast-track evaluation)", "Analyse Results (Fast-track evaluation)",
            "Completed fast-track evaluation"};

    public static String[] getStateNames() {
        return stateNames;
    }

    @Id @GeneratedValue
    private int id;

    private int value = PlanState.CREATED;

    @Transient
    public String getStateName() {
        return PlanState.getStateNames()[this.value];
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
