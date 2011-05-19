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
package eu.planets_project.tb.impl.system;

import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.tb.api.system.TestbedStatelessAdmin;

/**
 * Originally a Stateless bean, but as this caches authentication credentials
 * it is actually Stateful.
 * 
 * This Bean RunsAs the Admin role, which means it has full access to the data stores.
 * 
 * It exists so that the background thread that runs experiments can access those data sources
 * in the absence of any authenticated user session.
 * 
 * It is made to time-out reasonably frequently, as otherwise the JOSSO framework 
 * times out the session, I think.
 * 
 * @link http://www.jboss.org/community/wiki/Ejb3DisableSfsbPassivation
 * 
 * URGENT Rename to TestbedAdminSessionBean?
 * URGENT Cache timeouts were still not happening: beans were being persisted for more that JOSSO's 30 minute session length!
 * Attempting to tweak META-INF/jboss.xml instead...
 * 
 * @author AnJackson
 *
 */
@Stateful
@Local(TestbedStatelessAdmin.class)
@Remote(TestbedStatelessAdmin.class)
@LocalBinding(jndiBinding="planets-project.eu/tb/TestbedAdminBean")
@RemoteBinding(jndiBinding="planets-project.eu/tb/TestbedAdminBean")
@SecurityDomain("PlanetsRealm")
@RunAs("admin")
public class TestbedStatelessAdminBean implements TestbedStatelessAdmin {

    // A Log:
    private static Log log = LogFactory.getLog(TestbedStatelessAdminBean.class);

    /**
     * Hook up to a local instance of the Planets Data Manager.
     * 
     * NOTE Trying to get the remote DM and narrow it to the local one did not work.
     * 
     * TODO Switch to the DigitalObjectManager form.
     * 
     * @return A DataManagerLocal, as discovered via JNDI.
     */
    public DataManagerLocal getPlanetsDataManagerAsAdmin() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            DataManagerLocal um = (DataManagerLocal) 
                jndiContext.lookup("planets-project.eu/DataManager/local");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local DataManager: "+e.toString());
            return null;
        }
    }
    

    /**
     * @return A reference to this EJB.
     */
    public static TestbedStatelessAdmin getTestbedAdminBean() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            TestbedStatelessAdmin um = (TestbedStatelessAdmin) 
                jndiContext.lookup("planets-project.eu/tb/TestbedAdminBean");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local TestbedStatelessAdmin: "+e.toString());
            return null;
        }
    }
}
