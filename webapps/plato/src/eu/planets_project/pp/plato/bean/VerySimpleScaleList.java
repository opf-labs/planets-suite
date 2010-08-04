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
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.FloatRangeScale;
import eu.planets_project.pp.plato.model.scales.FloatScale;
import eu.planets_project.pp.plato.model.scales.FreeStringScale;
import eu.planets_project.pp.plato.model.scales.IntRangeScale;
import eu.planets_project.pp.plato.model.scales.IntegerScale;
import eu.planets_project.pp.plato.model.scales.OrdinalScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.scales.YanScale;


/**
 * SimpleScaleList provides a list of all available scales
 * and utility functions for the ScaleConverter.
 *
 * @author Michael Kraxner
 *
 */
@Name("verysimpleScaleList")
@Scope(ScopeType.SESSION)
public class VerySimpleScaleList implements Serializable {

    private static final long serialVersionUID = 6789787948208212546L;

    @In(create=true)
    private ResourceBundle resourceBundle;

    /**
     * Contains {@link javax.faces.model.SelectItem} where the canonical name of the
     * scale is the <code>Scale</code> and the string determined from {@link #resourceBundle}
     * is the <code>label</code>. Example: Scale=eu.planets_project.pp.plato.model.scales.BooleanScale,
     * label=Boolean.
     */
    private ArrayList<SelectItem> selectScales;

    /**
     * List containing canonical names of scales
     */
    private ArrayList<String> allScales;

    /**
     * Populates the lists {@link #selectScales} and {@link #allScales}
     */
    public void populateScaleList(){

        if(selectScales != null)
            return;

        selectScales = new ArrayList<SelectItem>();

        allScales = new ArrayList<String>();
        // add here all available scales

        allScales.add(BooleanScale.class.getCanonicalName());
        allScales.add(YanScale.class.getCanonicalName());
        allScales.add(OrdinalScale.class.getCanonicalName());
        allScales.add(IntegerScale.class.getCanonicalName());
        allScales.add(IntRangeScale.class.getCanonicalName());
        allScales.add(FloatScale.class.getCanonicalName());
        allScales.add(FloatRangeScale.class.getCanonicalName());
        allScales.add(PositiveFloatScale.class.getCanonicalName());
        allScales.add(PositiveIntegerScale.class.getCanonicalName());
        allScales.add(FreeStringScale.class.getCanonicalName());

        for (String className: allScales) {
            // use "pretty" name from the resource Bundle as label
            String displayName = resourceBundle.getString(className);
            selectScales.add(new SelectItem(className, displayName));
        }
    }

    /**
     * Returns the collection of SelectItems
     */
    public Collection getScaleList() {
        if (selectScales == null) {
            populateScaleList();
        }
        return selectScales;
    }

    public Scale getScaleByIndex(int scaleId) {
        if (selectScales == null) {
            populateScaleList();
        }
        return (Scale)selectScales.get(scaleId).getValue();
    }

    /**
     * @return index of scale entry in list {@link #allScales}
     */
    public int getIndexOfScale(Scale scale) {
        if (selectScales == null) {
            populateScaleList();
        }
        return allScales.indexOf(scale);
    }
}
