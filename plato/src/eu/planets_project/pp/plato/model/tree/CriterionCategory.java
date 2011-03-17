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
package eu.planets_project.pp.plato.model.tree;

public enum CriterionCategory {
    ACTION_STATIC("action", "static"), 
    ACTION_RUNTIME("action", "runtime"), 
    ACTION_JUDGEMENT("action", "judgement"), 
    OUTCOME_OBJECT("outcome", "object"),
    OUTCOME_FORMAT("outcome", "format"), 
    OUTCOME_EFFECT("outcome", "effect");
    
    
    private String category;
    private String subCategory;
    
    private CriterionCategory(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }
    
    @Override
    public String toString() {
        return category + ":" + subCategory;
    }
    
    public String getCategory(){
        return category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    /**
     * factory method, returns the criterion category for the given category and subcategory
     * @param category
     * @param subCategory
     * @return category, or null if category and subCategory do not correspond to a criterion category 
     */
    public static CriterionCategory getType(String category, String subCategory) {
        if ("action".equals(category)) {
            if(ACTION_JUDGEMENT.subCategory.equals(subCategory)) {
                return ACTION_JUDGEMENT;
            } else if(ACTION_RUNTIME.subCategory.equals(subCategory)) {
                return ACTION_RUNTIME;
            } else if(ACTION_STATIC.subCategory.equals(subCategory)) {
                return ACTION_STATIC;
            } 
        } else if ("outcome".equals(category)) {
            if(OUTCOME_EFFECT.subCategory.equals(subCategory)) {
                return OUTCOME_EFFECT;
            } else if(OUTCOME_FORMAT.subCategory.equals(subCategory)) {
                return OUTCOME_FORMAT;
            } else if(OUTCOME_OBJECT.subCategory.equals(subCategory)) {
                return OUTCOME_OBJECT;
            } 
        }
        return null;
    }
}
