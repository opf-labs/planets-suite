/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.api.persistency;


import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;


/**
 * 
 * @author AnJackson
 */
@Remote
public interface ServiceRecordPersistencyRemote {

    public long persistServiceRecord(ServiceRecordImpl serviceRecord);
    public void updateServiceRecord(ServiceRecordImpl serviceRecord);
    public void deleteServiceRecord(long id);
    public void deleteServiceRecord(ServiceRecordImpl serviceRecord);
    
    public ServiceRecordImpl findServiceRecord(long id);
    public ServiceRecordImpl findServiceRecordByHashcode(String serviceHash);
    public List<ServiceRecordImpl> getAllServiceRecords();
    public List<ServiceRecordImpl> getAllServiceRecordsOfType( String type );
    public List<ServiceRecordImpl> getAllServiceRecordsByServiceName( String name );
    public List<ServiceRecordImpl> getAllServiceRecordsForEndpoint( String endpoint );
    
}
