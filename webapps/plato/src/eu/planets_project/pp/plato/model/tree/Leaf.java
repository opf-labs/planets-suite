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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.MapKey;
import org.hibernate.validator.Valid;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.EvaluationStatus;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.ITouchable;
import eu.planets_project.pp.plato.model.SampleAggregationMode;
import eu.planets_project.pp.plato.model.TargetValueObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.measurement.MeasurementInfo;
import eu.planets_project.pp.plato.model.scales.FreeStringScale;
import eu.planets_project.pp.plato.model.scales.OrdinalScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.scales.ScaleType;
import eu.planets_project.pp.plato.model.scales.YanScale;
import eu.planets_project.pp.plato.model.transform.NumericTransformer;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.transform.Transformer;
import eu.planets_project.pp.plato.model.values.FreeStringValue;
import eu.planets_project.pp.plato.model.values.TargetValues;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * A leaf node in the objective tree does not contain any children,
 * but instead defines the actual measurement scale to be used and points
 * to conforming valueMap. Part of the implementation of the Composite
 * design pattern, cf. TreeNode, Node - Leaf corresponds to the
 * <code>Leaf</code>, surprise!
 * @author Christoph Becker
 */
@Entity
@DiscriminatorValue("L")
public class Leaf extends TreeNode {

    private static final long serialVersionUID = -6561945098296876384L;

    /**
     * The {@link Transformer} stores the user-set transformation rules.
     * There are two types:
     * <ul>
     * <li>numeric transformation (thresholds) </li>
     * <li>ordinal transformation: direct mapping from values to numeric
     * values. This also applies to boolean scales. </li>
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Transformer transformer;

    /**
     * touches everything: this, the scale and the transformer (if existing)
     */
    @Override
    public void touchAll(String username) {
        touch(username);
        if (scale != null) {
            scale.touch(username);
        }
        if (transformer != null) {
            transformer.touch(username);
        }
    }

    /**
     * determines the aggregation mode for the values of the sample records(!)
     * WITHIN one alternative. The overall aggregation method over the tree is a
     * different beer!
     * Is initialised with {@link SampleAggregationMode#WORST}, but later initialised
     * according to the {@link Scale} in {@link #setDefaultAggregation()}
     */
    @Enumerated
    private SampleAggregationMode aggregationMode = SampleAggregationMode.WORST;

    /**
     * specifies the {@link Scale} to be used for evaluating experiment
     * outcomes
     */
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private Scale scale;

    /**
     * We have values actually per
     * <ul>
     * <li> preservation strategy ({@link Alternative}),</li>
     * <li> leaf node (of course), AND </li>
     * <li> sample record.</li>
     * </ul>
     * So we have another encapsulation: {@link Values}
     *
     * The key member of Map must be renamed to key_name otherwise derby
     * complains. The default value 'key' seems to be a keyword.
     */
    @MapKey(columns = { @Column(name = "key_name") })
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN}) 
    private Map<String, Values> valueMap = new ConcurrentHashMap<String, Values>();

    @OneToOne(cascade = CascadeType.ALL)
    private MeasurementInfo measurementInfo = new MeasurementInfo();
    
    public Map<String, Values> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, Values> v) {
        this.valueMap = v;
    }

    /**
     * @return the <b>unweighted</b> result value for an Alternative. This is the aggregation of
     *         all transformed evaluation values
     * @see #aggregateValues(TargetValues)
     * @see #transformValues(Alternative)
     */
    public double getResult(Alternative a) {
        return aggregateValues(transformValues(a));
    }

    /**
     * Aggregates values of one Alternative, depending on the {@link #aggregationMode}
     * @param values the TargetValue element over which aggregation shall be
     * performed according to the {@link #aggregationMode}
     * @return a single number denoting the aggregated, transformed, unweighted
     * result value of this Leaf.
     */
    private double aggregateValues(TargetValues values) {
        if (aggregationMode == SampleAggregationMode.WORST) {
            return values.worst();
        } else {
            return values.average();
        }
    }

    /**
     * Returns the {@link TargetValues evaluation values} for each SampleObject for one {@link Alternative}
     * already transformed from the measurement scale to the final scale used for ranking.
     *
     * @see #getResult(Alternative)
     * @param a the {@link Alternative} for which evaluation values shall be returned
     * @return {@link TargetValues}
     */
    public TargetValues transformValues(Alternative a) {
        Values v = valueMap.get(a.getName());
        if (transformer == null) {
            PlatoLogger.getLogger(getClass()).fatal("transformer is null!");
        }
        return transformer.transformValues(v);
    }

    public Leaf() {
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public void setValues(String alternative, Values values) {
        valueMap.put(alternative, values);
    }

    public Values getValues(String alternative) {
        return valueMap.get(alternative);
    }


    public Scale getScale() {
        return scale;
    }

    /**
     * The standard setter sets the scale of the leaf to the given instance <code>scale</code>,
     * but leaves {@link #transformer} and {@link #aggregationMode} unchanged.
     *
     * <b>Important: If you want to change the type of the scale, e.g. from Boolean to Numeric,
     * you have to take transformation settings and aggregation mode into account.
     *  Thus you need to use {@link #changeScale(Scale)} instead, which also takes care
     *  of the transformer and aggregationMode.</b>
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     * When a scale is changed e.g. from Boolean to a number,
     * all evaluation values that have already been associated become
     * invalid and need to be removed.
     *
     * This function resets all evaluation {@link Values} associated with
     * this Leaf, which depend on the {@link Scale} that is set.
     * This means that if the scale is not set, all Values are removed.
     * If the scale is set, we iterate into all values for all alternatives
     * and samplerecords and check if the scale in there differs from the
     * scale that has been set. If yes, we remove the values.
     * Furthermore, if this Leaf has been changed from an Object criterion
     * to an Action criterion, all excess values are removed.
     */
    public void resetValues(List<Alternative> list) {
        if (scale == null) {
            /*
             * there is no scaletype set, so we remove existing values
             */
            valueMap.clear();
            return;
        }
        // Get the Values for each Alternative
        for (Alternative a : list) {
            Values values = valueMap.get(a.getName());
            if (values == null) {
                Logger.getLogger(this.getClass()).debug("values is null for alternative "+ a.getName()+ " in Leaf "+name);
                continue;
            }
            // Check value of each sample object for conformance with Scale -
            // if we find a changed scale, we reset everything.
            // It might be faster not to check ALL values, but this is safer.
            for (Value value : values.getList()) {
                // If the scale has changed, we reset all evaluation values of this Alternative:
                // this may look strange, but it is OK that the scale of a value is null.
                // If there have been values before, you change the scale and then save - the linkage is lost                
                // if (value.getScale() == null) {
                //      Logger.getLogger(Leaf.class).error("WHAT THE...?? no scale for value"+getName());
                // } else {
                    if ((value.getScale() == null) ||
                        (!value.getScale().getClass().equals(scale.getClass())) ) {
                        if (!a.isDiscarded()) { // for discarded alternatives, that's ok.
                            Logger.getLogger(Leaf.class).debug(
                                    "Leaf "+this.getName()+" Class: " + value.getClass() + " not like "
                                            + scale.getClass()+". RESETTING the valuemap now!");
                            valueMap.clear(); // reset all values
                            return;
                        }
                    }
                // }
                // PLEASE NOTE- WRT ORDINAL RESTRICTIONS:
                // we do NOT reset values when the restriction has changed, such as 
                // the ordinal values or the boundaries.
                // Instead, those values that are still valid remain, the others will be checked
                // and need to be corrected anyway in the evaluate step.
                // Should be nicer for the user. If we find out this leads to validation problems
                // (which shouldnt be the case because the data types are valid as long as the scale
                // doesnt change) then we will reset the values even if just the restriction changes.
            }
            /*
             * maybe this leaf was set to single, reset all values
             */
            if (isSingle() && values.size() > 1) {
                valueMap.clear();
                return;
            }
        }
    }

    /**
     * Sets a default transformer corresponding to the current scale of this
     * leaf. The transformer is initialized with default-values.
     *
     * If no scale is set, the current transformer will be set to null!
     */
    public void setDefaultTransformer() {
        if (scale == null) {
            PlatoLogger.getLogger(this.getClass()).warn(
                    "Can't set DefaultTransformer, no scale set!");
            this.setTransformer(null);
            return;
        }
        if (ScaleType.ordinal.equals(scale.getType())) {
            OrdinalTransformer t = new OrdinalTransformer();
            this.setTransformer(t);
            if (!(scale instanceof FreeStringScale)) {
                Map<String, TargetValueObject> map = t.getMapping();
                OrdinalScale o = (OrdinalScale) scale;
                for (String s : o.getList()) {
                    map.put(s, new TargetValueObject());
                }
            }
        } else {
            NumericTransformer t = new NumericTransformer();
            this.setTransformer(t);
        }
    }

    /**
     * Returns the fully qualified class-name ("canonical name") of the current scale
     * @return the canonical classname of the scale, or null if no scale is set
     */
    public String getScaleByClassName() {
        if (scale == null)
            return null;
        else
            return scale.getClass().getCanonicalName();
    }

    /**
     * Sets the Scale according to the provided name, IF the name differs from the
     * classname of the currently set {@link #scale}
     * 
     * resets property mappings, if present.
     * 
     * @param className canonical class name of the new scale
     */
    public void setScaleByClassName(String className) {
        Scale scaleType = null;
        try {
            if (className != null && !"".equals(className)) {
                scaleType = (Scale) Class.forName(className).newInstance();
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        }
        changeScale(scaleType);
    }

    /**
     * Changes the {@link Scale} to the provided one.
     * if the new scale differs from the type of the current scale,
     * it also:
     * <ul>
     *     <li>sets: default aggregators and transformers.</li>
     * </ul>
     * It does not set a reference to the provided scale, but clones it instead!
     * @param newScale the new Scale to be set
     */
    public void changeScale(Scale newScale) {
        if (newScale == null) {
            PlatoLogger.getLogger(Leaf.class).debug("CHECK THIS: setting scale to null.");
            scale = null;
        } else {
            // If
            if ((this.scale == null) //we don't have a scale yet
               || (!scale.getClass().getName().equals(newScale.getClass().getName())))
                // the new scale is not the same as ours
            {
                // a new scale was chosen, remove mapping
                setMeasurementInfo(new MeasurementInfo());

                setScale(newScale.clone());
                setDefaultAggregation();
                
                if (scale != null) {
                    setDefaultTransformer();
                }
            }
        }
    }

    /**
     * is used to adjust the scale of this leaf to its mapping
     * - the type of the new scale has already been checked, mapping information is not discarded.
     * - a new scale is created, even the types of the current and the new Scale match 
     *   (to get clean aggregation and transformer values)
     * 
     * @param newScale
     */
    public void adjustScale(Scale newScale) {
        if (newScale == null) {
            PlatoLogger.getLogger(Leaf.class).debug("CHECK THIS: try to setg scale to null due to measurement info: this should NOT happen at all.");
        } else {
            if ((this.scale == null) //we don't have a scale yet
               || (!scale.getClass().getName().equals(newScale.getClass().getName())))
                // the new scale is not the same as ours
            {
                setScale(newScale.clone());
                setDefaultAggregation();
                if (scale != null) {
                    setDefaultTransformer();
                }
            }
        }
    }

    /**
     * sets the {@link #aggregationMode} depending on {@link #scale}.
     * For all ordinal scales we set it to using the worst result,
     * and for numeric scales we use the average result
     * @see SampleAggregationMode
     */
    private void setDefaultAggregation() {
        if (scale instanceof OrdinalScale) {
            setAggregationMode(SampleAggregationMode.WORST);
        } else { // numeric
            setAggregationMode(SampleAggregationMode.AVERAGE);
        }
    }

    @Override
    /**
     * This is a leaf, so: YES, I am.
     * @return true
     */
    public boolean isLeaf() {
        return true;
    }

    public SampleAggregationMode getAggregationMode() {
        return aggregationMode;
    }

    public void setAggregationMode(SampleAggregationMode aggregationMode) {
        this.aggregationMode = aggregationMode;
    }

    /**
     * unused at the moment.
     * TODO checking the size of the valuemap is not enough.
     */
    public EvaluationStatus getEvaluationStatus() {
        return (valueMap.size() > 0) ? EvaluationStatus.COMPLETE
                : EvaluationStatus.NONE;
    }

    /**
     * Unused at the moment.
     * @return the transformation status.
     * TODO checking transformer for null state is NOT enough
     */
    public EvaluationStatus getTransformationStatus() {
        return (transformer != null) ? EvaluationStatus.COMPLETE
                : EvaluationStatus.NONE;
    }

    /**
     * removes associated evaluation {@link Values} for a given list of alternatives
     * and a give record index.
     * @param list list of Alternatives for which values shall be removed
     * @param record index of the record for which  values shall be removed
     */
    public void removeValues(List<Alternative> list, int record) {
        for (Alternative a : list) {
            Values v = getValues(a.getName());
            // maybe this alternative has no values at all - e.g. because it was just created
            if ((v != null)  // there is a Values object
                && (v.getList().size() > record) // there can be a value for this sample record
                && (v.getList().get(record) != null)) { // there is a value
                PlatoLogger.getLogger(this.getClass()).debug("removing values:: "+getName()+" ,"+record+", "+a.getName());
                v.getList().remove(record);
            }
        }
    }
    
    /**
     * The value map is properly initialized if its size equals the number of alternatives and the 
     * number of values equals the number of records. 
     * 
     * @return true if value map is properly initialized
     */
    @Override
    public boolean isValueMapProperlyInitialized(List<Alternative> alternatives, int numberRecords) {
        if (valueMap.size() != alternatives.size()) {
            return false;
        }
        
        for (Alternative a : alternatives) {
            if (!valueMap.keySet().contains(a.getName())) {
                return false;
            }
        }
        
        for (String a : valueMap.keySet()) {
            if (!isSingle() && valueMap.get(a).size() != numberRecords) {
                return false;
            } else if (isSingle() && valueMap.get(a).size() != 1) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Creates empty Values for all Alternatives and SampleRecords as provided
     * in the parameters, PLUS ensures that values are linked to scales if the
     * parameter addLinkage is true
     *
     * An assumption here is that other methods take care of removing values when
     * removing records ({@link #removeValues(List, int)}),
     * and of resetting values when changing scales and from object
     * to action criterion. ({@link #resetValues()})
     * These methods need to be called when manipulating the object model.
     *
     * @param list of Alternatives
     * @param records The number of records determines how many {@link Values} are
     * created and associated for every {@link Alternative}
     * @param addLinkage If true, ensure that values are linked to scales
     * by calling {@link #initScaleValueLinkage(List, int)}
     */
    public void initValues(List<Alternative> list, int records,
            boolean addLinkage) {
        /** maybe we have not completed the step identify requirements yet -
         * so there might be no scales! **/
        if (scale == null)
            return;
        for (Alternative a : list) {
            // for every Alternative we get the container of the values of each sample object
            // from the map
            Values v = valueMap.get(a.getName());

            // If it doesnt exist, we create it and link it in the map
            if (v == null) {
                v = new Values();
                valueMap.put(a.getName(), v);
                // it the valueMap has just been created and the leaf is single,
                // we need to add one value.
                if (isSingle()) {
                    v.add(scale.createValue());
                }
            }

            // 20090217, hotfix CB: if a Leaf is set to SINGLE *after* initValues has been called,
            // the Value object at position 0 of the ValueS object might not be properly initialised.
            // Check and initialise if needed:
            if (isSingle()) {
                if (v.size() == 0) {
                    PlatoLogger.getLogger(this.getClass()).warn("adding value to a SINGLE LEAF WITH A VALUES OBJECT WITHOUT A PROPER VALUE:" + getName());
                    v.getList().add(scale.createValue());
                } else {
                    if (v.getValue(0) == null) {
                        PlatoLogger.getLogger(this.getClass()).warn("adding value to a SINGLE LEAF WITH A VALUES OBJECT WITHOUT A PROPER VALUE:" + getName());
                        v.setValue(0,scale.createValue());
                    }
                }
            }
            // end hotfix 20090217
            
            // So we can be sure now that we have a value container and
            // that it is linked and that for Action criteria, i.e. single
            // values, we have the one value.
            // For Object criteria we have to be sure that the number of values
            // corresponds to the number of sample objects, so we fill the list up
            if (!isSingle()) {
                // this is to add MISSING values for records.
                // it doesnt make a difference for this condition
                // whether we just created a new valuemap or are
                // refilling an existing one

                // Note that the index here starts at the size of the values array
                // and runs to the total number of records.
                // so if we have enough - nothing happens; if some are missing, they are
                // added at the end
                for (int i = v.size(); i < records; i++) {
                    v.add(scale.createValue());
                }
            }
        }
        if (addLinkage) {
            initScaleValueLinkage(list, records);
        }
    }

    /**
     * ensures that values are linked to scales by setting all of them
     * explicitly. We need that especially for export/import
     *
     * @param list List of Alternatives over which to iterate
     * @param records denotes the number of records for the iteration
     */
    public void initScaleValueLinkage(List<Alternative> list, int records) {
        for (Alternative a : list) {
            Values v = valueMap.get(a.getName());
            if (v == null) {
                throw new IllegalStateException("initScaleLinkage called,"
                        + " but the valueMap is still empty - that's a bug."
                        + " Leaf:" + getName());
            }
            if (isSingle()) {
                v.getValue(0).setScale(scale);
            } else {
                for (int i = 0; i < records; i++) {
                    v.getValue(i).setScale(scale);
                }
            }
        }
    }

    /**
     * Checks if the Scale of this Leaf is existent and correctly specified.
     * To achieve this, it calls {@link Scale#isCorrectlySpecified(String, List)}
     * if there is a scale, or returns false otherwise.
     * @see TreeNode#isCompletelySpecified(List)
     * @see Scale#isCorrectlySpecified(String, List)
     */
    @Override
    public boolean isCompletelySpecified(List<String> errorMessages) {
        if (this.scale == null) {
            errorMessages.add("Leaf " + this.getName() + " has no scale");
            return false;
        }
        if (scale instanceof YanScale) {
            errorMessages.add("Criterion "+getName()+" is associated with a 'Yes/Acceptable/No' scale, which is discouraged. We recommend to refine the criterion to be as objective as possible.");
        }
        return this.scale.isCorrectlySpecified(this.getName(), errorMessages);
    }

    /**
     * Checks if this Leaf is completely evaluated, i.e. we have correct
     * values for all Alternatives and samples.
     * For this means we need to iterate over all alternatives and check
     * all values. This is done by calling {@link Scale#isEvaluated(Value)}
     * @see eu.planets_project.pp.plato.model.tree.TreeNode#isCompletelyEvaluated(List, List, List)
     * @see Scale#isEvaluated(Value)
     * @param errorMessages This is the <b>list of messages</b> where we add a message about this Leaf in case validation
     * fails, i.e. it is not completely evaluated.
     * @param nodes This is the <b>list of nodes </b>where we add this Leaf in case validation
     * fails, i.e. it is not completely evaluated.
     * @param alternatives the list of Alternatives over which to iterate when checking
     * for evaluation values
     */
    @Override
    public boolean isCompletelyEvaluated(List<String> errorMessages,
            List<TreeNode> nodes, List<Alternative> alternatives) {
        boolean validates = true;
        PlatoLogger.getLogger(this.getClass()).debug("checking complete evaluation for leaf " +getName());
        for (Alternative a : alternatives) {
            Values values = valueMap.get(a.getName());
            PlatoLogger.getLogger(this.getClass()).debug("checking values for "+a.getName());
            if (this.isSingle()) {
                if (values.size() < 1) {
                    Logger.getLogger(Leaf.class).warn(
                            "Not Enough Value Objects in Values");
                    validates = false;
                } else {
                    if (!scale.isEvaluated(values.getValue(0))) {
                        validates = false;
                    }
                }
            } else {
                int i = 0;
                for (Value value : values.getList()) {
                    PlatoLogger.getLogger(this.getClass()).debug("checking value for "+(i));
                    if (!scale.isEvaluated(value)) {
                        validates = false;
                        break;
                    }
                    i++;
                }
            }
        }
        if (!validates) {
            // I add an error message to the list, and myself to the list of error nodes
            errorMessages.add("Leaf " + this.getName()
                    + " is not properly evaluated");
            nodes.add(this);
        }
        return validates;
    }

    /**
     * Checks if the transformation settings for this Leaf are complete and correct.
     * @see Transformer#isTransformable(List)
     * @see TreeNode#isCompletelyTransformed(List, List)
     * @param errormessages This is the <b>list of messages</b> where we add a message about this Leaf in case validation
     * fails, i.e. it is not completely evaluated.
     * @param nodes This is the <b>list of nodes </b>where we add this Leaf in case validation
     * fails, i.e. it is not completely evaluated.
     */
    @Override
    public boolean isCompletelyTransformed(List<String> errormessages, List<TreeNode> nodes) {
        if (this.transformer == null) {
            errormessages.add("Leaf " + this.getName()+" is not properly transformed");
            nodes.add(this);
            PlatoLogger.getLogger(this.getClass()).error("Transformer is NULL in Leaf "+getParent().getName()+" > "+getName());
            return false;
        }
        if (!this.transformer.isTransformable(errormessages) || !this.transformer.isChanged()) {
            errormessages.add("Leaf " + this.getName()+" is not properly transformed");
            nodes.add(this);
            return false;
        }
        return true;
    }

    @Override
    /**
     * Checks if the weight is in [0,1].
     * @see Node#isCorrecltlyWeighted(List<String>)
     */
    public boolean isCorrectlyWeighted(List<String> errormessages) {
        // A leaf is always weighted correctly as long as its weight is in [0,1]
        if (this.weight >= 0 && this.weight <= 1) {
            return true;
        }
        errormessages.add("Leaf " + this.getName() + " has an illegal weight ("
                + this.weight + ")");
        return false;
    }


    @Override
    /**
     * Returns a clone of this Leaf. Includes: <ul>
     * <li>{@link Scale}</li>
     * <li>{@link AggregationMode}</li>
     * <li>{@link ValueMap} which is initialised, but not cloned</li>
     * </ul>
     * Excludes transformer! The transformer is set to <code>null</code>
     */
    public TreeNode clone() {
        Leaf clone = (Leaf) super.clone();
        if (this.getScale() != null) {
            clone.setScale(this.getScale().clone());
        }
        clone.setValueMap(new ConcurrentHashMap<String, Values>());
        
        Transformer newTransformer = null;
        if (transformer != null) {
            newTransformer = transformer.clone();
        }
        clone.setTransformer(newTransformer);
        clone.setAggregationMode(this.getAggregationMode());
        if (measurementInfo != null) {
            clone.setMeasurementInfo(measurementInfo.clone());
        }
        return clone;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
      public void handleChanges(IChangesHandler h) {
        super.handleChanges(h);

        // call handleChanges of all properties
        if (scale != null) {
            scale.handleChanges(h);
        }
        if (transformer != null) {
            transformer.handleChanges(h);
        }
        if (measurementInfo != null) {
            measurementInfo.handleChanges(h);
        }

    }

    @Transient
    public boolean isMapped() {
        return ((measurementInfo != null) &&(measurementInfo.getUri() != null));
    }

    /**
     * this method updates the value map, changing the name of the alternative to the new one.
     * @param oldName old name to be updated
     * @param newName new name to be used instead of oldName
     */
    public void updateAlternativeName(String oldName, String newName) {
        if (valueMap.containsKey(oldName))
            valueMap.put(newName, valueMap.remove(oldName));
        
        /*
         for (String name: valueMap.keySet()) {
            if (name.equals(oldName)) {
                valueMap.put(newName, valueMap.get(oldName));
                valueMap.remove(oldName);
            }
        }
        */       
        
    }

    /**
     * <ul>
     * <li>
     * removes all {@link Values} from the {@link #valueMap} which are not mapped by one of the 
     * names provided in the list
     * </li>
     * <li>
     * removes all {@link Value} objects in the {@link Values} which are out of the index of 
     * the sample records (which should not happen, but apparently we have some projects where this
     * is the case), or where a leaf is single and there is more than one {@link Value}
     * </li>
     * </ul>
     * @param alternatives list of names of alternatives
     * @return number of {@link Values} objects removed
     */
    public int removeLooseValues(List<String> alternatives, int records) {
        int number = 0;
        Iterator<String> it =  valueMap.keySet().iterator();
        List<String> namesToRemove = new ArrayList<String>();
        while (it.hasNext()) {
            String altName = it.next();
            if (!alternatives.contains(altName)) {
                PlatoLogger.getLogger(this.getClass()).warn("removing Values for "+altName+" at leaf "
                        +getName());
                namesToRemove.add(altName);
                number++;
            } else {
                Values v = valueMap.get(altName);
                int removed  = v.removeLooseValues(isSingle() ? 1 : records);
                PlatoLogger.getLogger(this.getClass()).warn("removed "+removed+" Value objects " +
                                "for "+altName+" at leaf "+getName());
                number += removed;
            }
        }
        for (String s: namesToRemove) { 
            valueMap.remove(s);
        }
        return number;
    }
    
    public void normalizeWeights(boolean recoursive) {
        // this is a leaf which means there are no children 
        // and therefore there is nothing to do
    }

    public MeasurementInfo getMeasurementInfo() {
        return measurementInfo;
    }

    public void setMeasurementInfo(MeasurementInfo measurementInfo) {
        this.measurementInfo = measurementInfo;
    }

    
    
    /**
     * initialises the ordinal transformer for free text scales
     * AND has a side effect: textual values in free text scales
     * with equalsIgnoreCase=true to an existing mapping are changed
     * to the case of the mapping string!

     */
    public void initTransformer() {
        initTransformer(null);
    }
    
    /**
     * initialises the ordinal transformer for free text scales, @see #initTransformer()
     * @param defaultTarget if this is used (must be 0.0<=defaultTarget<=5.0, unchecked)
     * then for each newly added mapping, the default target is set as provided.
     */
    public void initTransformer(Double defaultTarget) {

        if (scale instanceof FreeStringScale) {
            FreeStringScale freeScale = (FreeStringScale) scale;
            // We collect all distinct actually EXISTING values
            OrdinalTransformer t = (OrdinalTransformer) transformer;
            Map<String, TargetValueObject> map = t.getMapping();

            HashSet<String> allValues = new HashSet<String>();
            for (Values values: valueMap.values()) {
                for (Value v : values.getList()) {
                    FreeStringValue text = (FreeStringValue) v;
                    if (!text.toString().equals("")) {
                        for (String s: map.keySet()) {
                            // if the value is NOT the same, but IS the same with other case, 
                            // we replace the value with the cases predefined by the mapping
                            if (text.getValue().equalsIgnoreCase(s) && !text.getValue().equals(s)) {
                                text.setValue(s);
                            }
                        }
                        allValues.add(text.getValue());
                    }
                }
            }
            
            // We remove all values from the transformer that do not actually occur (anymore)
            // I am disabling this for now - why would we want to remove known mappings?
            // They don't do harm because for the lookup, we use the actually encountered values
            // (see below)
//            HashSet<String> keysToRemove = new HashSet<String>(); 
//           for (String s: map.keySet()) {
//               if (!allValues.contains(s)) {
//                   keysToRemove.add(s);
//               }
//           }
//           for (String s: keysToRemove) {
//               map.remove(s);
//           }
           
            // We add all values that occur, but dont are not in the map yet:
            for (String s: allValues) {
                if (!map.containsKey(s)) {
                    if (defaultTarget == null) {
                        map.put(s, new TargetValueObject());
                    } else {
                        map.put(s, new TargetValueObject(defaultTarget.doubleValue()));
                    }
                }
            }      
            
            // We also have to publish the known values
            // to the SCALE because it provides the reference lookup
            // for iterating and defining the transformation
            freeScale.setPossibleValues(allValues);
        }
    }
    
}