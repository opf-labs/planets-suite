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
package eu.planets_project.tb.api.persistency;

import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * @author alindley
 *
 */

@Remote
public interface ExperimentPersistencyRemote {
	
	public long persistExperiment(Experiment experiment);
	public Experiment findExperiment(long id);
	
	public void updateExperiment(Experiment experiment);
	public void deleteExperiment(long id);
	public void deleteExperiment(Experiment experiment);
	public List<Experiment> queryAllExperiments();
	public boolean queryIsExperimentNameUnique(String sExpName);
	public List<Experiment> searchAllExperiments(String toFind);
	
	public List<Experiment> getPagedExperiments(int firstRow, int numberOfRows, String sortField, boolean descending);
	
	public int getNumberOfExperiments();
	
    public List<ServiceRecordImpl> getServiceRecords();
    public ServiceRecordImpl findServiceRecordByHashcode( String serviceHash );

    public MeasurementImpl findMeasurement( long id );
    public void removeMeasurement( MeasurementImpl m );

    public MeasurementEventImpl findMeasurementEvent( long id );
    public void removeMeasurementEvent( MeasurementEventImpl me );

}
