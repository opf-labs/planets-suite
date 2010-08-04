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
package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.validator.Length;

import eu.planets_project.pp.plato.model.ChangeLog;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.ITouchable;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.tree.CriterionCategory;
import eu.planets_project.pp.plato.model.values.INumericValue;

/**
 * denotes a property that can be automatically measured.
 * A property has a name and a {@link Scale}
 * @author Christoph Becker
 *
 */
@Entity
public class MeasurableProperty implements Comparable<MeasurableProperty>, ITouchable, Cloneable, Serializable {
    private static final long serialVersionUID = -6675251424999307492L;

    @Id
    @GeneratedValue
    private int id;
    
    private String propertyId;
    private String name;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CriterionCategory category;
    
    @OneToOne(cascade=CascadeType.ALL)
    private Scale scale;
    
    /**
     * a list of all metrics that can be applied to this property
     */
    @Transient
    List<Metric> possibleMetrics = new ArrayList<Metric>();
    
    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();
    
    
    public MeasurableProperty(){}

    public MeasurableProperty(Scale scale, String name) {
        this.scale = scale;
        this.name = name;
    }
    
    public void clear() {
        id = Integer.MAX_VALUE;
        propertyId = null;
        name = null;
        description = null;
        category = null;
        scale = null;
        possibleMetrics = null;
    }
    public boolean isNumeric() {
        if (scale == null) {
            return false;
        }
        return (scale.createValue() instanceof INumericValue);
    }
    
//    /**
//     * returns true if o is a MeasurableProperty and has the same
//     * name as <code>this</code>
//     */
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        } 
//        if (o == null) {
//            return false;
//        } 
//        if (o instanceof MeasurableProperty) {
//            MeasurableProperty p = (MeasurableProperty) o;
//            return (name != null && name.equals(p.getName()));
//        }
//        return false;
//    }
//    
//    @Override
//    public int hashCode() {
//        if (this.name == null){
//            return 0;
//        }
//        return this.name.hashCode();
//    }
    
    /**
     * returns a clone of self.
     * Implemented for storing and inserting fragments.
     * Subclasses obtain a shallow copy by invoking this method, then 
     * modifying the fields required to obtain a deep copy of this object.
     * the id is not copied
     */
    public MeasurableProperty clone() {
        try {
            MeasurableProperty clone = (MeasurableProperty)super.clone();
            clone.id = 0;
            if (scale != null) {
                clone.setScale(scale.clone());
            }
            
            if (possibleMetrics != null) {
                clone.possibleMetrics = new ArrayList<Metric>();
                for (Metric m : possibleMetrics) {
                    Metric cloneM = m.clone();
                    clone.possibleMetrics.add(cloneM);
                }
            } else {
                clone.possibleMetrics = null;
            }
            // created-timestamp is automatically set to now
            clone.setChangeLog(new ChangeLog(this.getChangeLog().getChangedBy()));
            return clone;
        } catch (CloneNotSupportedException e) {
            // never thrown
            return null;
        }
    }    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Scale getScale() {
        return scale;
    }
    public void setScale(Scale scale) {
        this.scale = scale;
    }
    
    public int compareTo(MeasurableProperty p) {
        return name.compareTo(p.getName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public List<Metric> getPossibleMetrics() {
        return possibleMetrics;
    }

    public void setPossibleMetrics(List<Metric> possibleMetrics) {
        this.possibleMetrics = possibleMetrics;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        
        
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }
    
    /**
     * currently used by digester
     * usage: setCategoryAsString("outcome:object")
     * 
     * @param category
     */
    public void setCategoryAsString(String category) {
        if ((category == null)||("".equals(category))) {
            setCategory(null);
        } else {
            String cat[] = category.split(":");
            if (cat.length != 2) {
                throw new IllegalArgumentException("invalid criterion category:" + category);
            }
            setCategory(CriterionCategory.getType(cat[0], cat[1]));
            
        }
    }
}
