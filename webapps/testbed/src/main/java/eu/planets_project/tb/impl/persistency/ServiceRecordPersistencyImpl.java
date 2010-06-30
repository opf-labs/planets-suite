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
package eu.planets_project.tb.impl.persistency;

import java.util.List;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;

/**
 * @author AnJackson
 *
 */
@Stateless
public class ServiceRecordPersistencyImpl implements ServiceRecordPersistencyRemote {

    @PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION)
    private EntityManager manager;

    // A Log for this:
    private Log log = LogFactory.getLog(ServiceRecordPersistencyImpl.class);   
    
    /**
     * A Factory method to build a reference to this interface.
     * @return
     */
    public static ServiceRecordPersistencyRemote getInstance() {
        Log log = LogFactory.getLog(ServiceRecordPersistencyImpl.class);
        try {
            Context jndiContext = new javax.naming.InitialContext();
            ServiceRecordPersistencyRemote dao_r = (ServiceRecordPersistencyRemote) PortableRemoteObject
                    .narrow(jndiContext
                            .lookup("testbed/ServiceRecordPersistencyImpl/remote"),
                            ServiceRecordPersistencyRemote.class);
            return dao_r;
        } catch (NamingException e) {
            log.error("Failure in getting PortableRemoteObject: "
                    + e.toString());
            return null;
        }
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#findServiceRecord(long)
     */
    public ServiceRecordImpl findServiceRecord(long id) {
        return manager.find(ServiceRecordImpl.class, id);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#findServiceRecordByHashcode(long)
     */
    @SuppressWarnings("unchecked")
    public ServiceRecordImpl findServiceRecordByHashcode( String hashcode ) {
        log.info("Looking for service records matching hashcode: "+hashcode);
        Query query = manager.createQuery("from ServiceRecordImpl where serviceHash=:hashcode");
        query.setParameter("hashcode", hashcode);
        List<ServiceRecordImpl> results = (List<ServiceRecordImpl>) query.getResultList();
        if( results == null || results.size() == 0 ) return null;
        log.info("Found "+results.size()+" services with the given hashcode.");
        return results.get(0);
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#getAllServiceRecords()
     */
    @SuppressWarnings("unchecked")
    public List<ServiceRecordImpl> getAllServiceRecords() {
        Query query = manager.createQuery("from ServiceRecordImpl");
        return (List<ServiceRecordImpl>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#getAllServiceRecordsByServiceName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<ServiceRecordImpl> getAllServiceRecordsByServiceName(String name) {
        Query query = manager.createQuery("from ServiceRecordImpl where serviceName=:name");
        query.setParameter("name", name);
        return (List<ServiceRecordImpl>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#getAllServiceRecordsForEndpoint(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<ServiceRecordImpl> getAllServiceRecordsForEndpoint(
            String endpoint) {
        Query query = manager.createQuery("from ServiceRecordImpl where endpoint=:endpoint");
        query.setParameter("endpoint", endpoint);
        return (List<ServiceRecordImpl>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#getAllServiceRecordsOfType(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<ServiceRecordImpl> getAllServiceRecordsOfType(String type) {
        Query query = manager.createQuery("from ServiceRecordImpl where serviceType=:type");
        query.setParameter("type", type);
        return (List<ServiceRecordImpl>) query.getResultList();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#persistServiceRecord(eu.planets_project.tb.impl.model.exec.ServiceRecordImpl)
     */
    public long persistServiceRecord(ServiceRecordImpl serviceRecord) {
        manager.persist(serviceRecord);
        return serviceRecord.getId();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote#updateServiceRecord(eu.planets_project.tb.impl.model.exec.ServiceRecordImpl)
     */
    public void updateServiceRecord(ServiceRecordImpl serviceRecord) {
        manager.merge(serviceRecord);
    }

    
}
