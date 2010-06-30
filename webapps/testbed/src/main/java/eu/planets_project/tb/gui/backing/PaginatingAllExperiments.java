/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.gui.backing;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;


public class PaginatingAllExperiments
    extends PaginatingDataModel<Experiment, Long>
{
    /** */
    private static final long serialVersionUID = 2672142810059859813L;

    /** */
    private static Log log = LogFactory.getLog(ListExp.class);

    /** */
    ExperimentPersistencyRemote ed = ExperimentPersistencyImpl.getInstance();

    /**
     * @see PaginatingDataModel#getId(java.lang.Object)
     */
    @Override
    public Long getId(Experiment object)
    {
        return object.getEntityID();
    }

    /**
     * @see PaginatingDataModel#findObjects(int, int, java.lang.String, boolean)
     */
    @Override
    public List<Experiment> findObjects(int firstRow, int numberOfRows, String sortField, boolean descending)
    {
//        return serviceForms.findFolders(firstRow, numberOfRows, sortField, descending);
        log.info("looking for experiments..."+firstRow+" +"+numberOfRows+" : "+sortField+" : "+descending);
        List<Experiment> exps = ed.getPagedExperiments(firstRow, numberOfRows, sortField, descending);
        log.info("Returning the experiments.");
        return exps;
    }

    /**
     * @see PaginatingDataModel#getObjectById(java.lang.Object)
     */
    @Override
    public Experiment getObjectById(Long id)
    {
//        return serviceForms.getFolder(id);
        return  ed.findExperiment(id);
    }

    /**
     * @see PaginatingDataModel#getDefaultSortField()
     */
    @Override
    public String getDefaultSortField()
    {
        return "name";
    }
    
    /**
     * @see eu.planets_project.tb.gui.backing.PaginatingDataModel#getDefaultSortDescending()
     */
    @Override
    public boolean getDefaultSortDescending() {
        return false;
    }

    /**
     * @see PaginatingDataModel#getNumRecords()
     */
    @Override
    public int getNumRecords()
    {
        return ed.getNumberOfExperiments();
    }

}
