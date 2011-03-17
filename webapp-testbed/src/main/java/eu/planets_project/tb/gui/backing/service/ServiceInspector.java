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
package eu.planets_project.tb.gui.backing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * 
 * A request-scope bean that handles inspection of a service.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceInspector {
    /** */
    private static final Log log = LogFactory.getLog(ServiceInspector.class);

    private String serviceName;
    
    private String serviceHash;
    
    private ServiceRecordBean srb = null;
    
    private List<ServiceRecordBean> srbs = new ArrayList<ServiceRecordBean>();

    /**
     * @param serviceName
     */
    public void setServiceName(String serviceName) { 
        this.serviceName = serviceName;
        lookForService();
    }

    /**
     * @return
     */
    public String getServiceName() { 
        return serviceName; 
    }
    
    
    /**
     * @param serviceHash the serviceHash to set
     */
    public void setServiceHash(String serviceHash) {
        this.serviceHash = serviceHash;
        lookForService();
    }

    /**
     * @return the serviceHash
     */
    public String getServiceHash() {
        return serviceHash;
    }


    /** */
    private void lookForService() {
        // By Hash takes precedence:
        if( serviceHash != null && ! "".equals(serviceHash)) {
            lookForServiceByHash(); 
            return;
        }
        if( serviceName != null && ! "".equals(serviceName)) {
            lookForServiceByName(); 
        }
    }
    
    /**
     * 
     */
    private void lookForServiceByName() {
        log.info("Looking up service: " + this.serviceName);
        this.srbs = new ArrayList<ServiceRecordBean>();
        if( this.serviceName == null ) return;
        
        // Get the service browser:
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        // Need a consistent way of getting the full record...
        for( ServiceRecordBean srb : sb.getAllServicesAndRecords() ) {
            if( this.serviceName.equals(srb.getName()) ) {
                log.info("Found matching Service Record Bean: "+srb.getName()+" : "+srb.getServiceRecord() );
                 this.srbs.add(srb);
            }
        }
    }

    /**
     * 
     */
    private void lookForServiceByHash() {
        log.info("Looking up service by hash: " + this.serviceHash);
        if( this.serviceHash == null ) return;
        
        // Get the service browser:
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        // Need a consistent way of getting the full record...
        for( ServiceRecordBean srb : sb.getAllServicesAndRecords() ) {
            if( this.serviceHash.equals(srb.getServiceHash()) ) {
                log.info("Found matching Service Record Bean: "+srb.getName()+" : "+srb.getServiceRecord() );
                 this.srb = srb;
            }
        }
    }


    /**
     * @return
     */
    public ServiceRecordBean getService() {
        if( this.srb == null ) {
            if( this.srbs.size() > 0 ) {
                return this.srbs.get(0);
            }
        }
        
        return this.srb;
    }
    
    public List<Experiment> getExperiments() {
        // Single Services:
        if( this.srb != null ) {
            if( srb.getServiceRecord() != null ) {
                return srb.getServiceRecord().getExperiments();
            } else {
                return new ArrayList<Experiment>();
            }
        }
        // Lists:
        HashMap<Long,Experiment> exps = new HashMap<Long,Experiment>();
        if( this.srbs != null ) {
            for( ServiceRecordBean srb : srbs ) {
                if( srb.getServiceRecord() != null ) {
                    for( Experiment exp : srb.getServiceRecord().getExperiments() ) {
                        exps.put(Long.valueOf(exp.getEntityID()), exp);
                    }
                }
            }
        }
        return new ArrayList<Experiment>(exps.values());
    }
    
    public int getNumberOfExperiments() {
        // Single Services:
        if( this.srb != null ) {
            if( srb.getServiceRecord() != null ) {
                return srb.getServiceRecord().getExperimentIds().size();
            } else {
                return 0;
            }
        }
        // Lists:
        HashSet<Long> uniques = new HashSet<Long>();
        if( this.srbs != null ) {
            for( ServiceRecordBean srb : srbs ) {
                if( srb.getServiceRecord() != null ) {
                    for( Long id : srb.getServiceRecord().getExperimentIds() ) {
                        uniques.add(id);
                    }
                }
            }
        }
        return uniques.size();
    }

    /**
     * @return
     */
    public List<ServiceRecordBean> getServiceVersions() {
       return this.srbs;
    }

}
