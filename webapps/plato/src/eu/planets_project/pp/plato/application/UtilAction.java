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
package eu.planets_project.pp.plato.application;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.IUtilAction;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Scope(ScopeType.APPLICATION)
@Name("utilAction")
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class UtilAction implements Serializable, IUtilAction {

    private static final long serialVersionUID = -2323932267631251076L;
    
    private static final Log log = PlatoLogger.getLogger(UtilAction.class);
    
    @PersistenceContext 
    private EntityManager em;

    /**
     * We need that method here because LoadPlanAction is no longer an EJB which makes
     * it way simpler but unable to use the EntityManager when the session timed out.
     * So we create this application bean to enable unlocking the plan locked in 
     * a session that's timed out.
     */
    public void unlockPlan(int planPropertiesId) {
        String where = "where pp.id = " + planPropertiesId;
        
        Query q = em
                .createQuery("update PlanProperties pp set pp.openHandle = 0, pp.openedByUser = ''"
                        + where);
        try {
            if (q.executeUpdate() < 1) {
                log.debug("Unlocking plan failed.");
            } else {
                log.debug("Unlocked plan");
            }
        } catch (Throwable e) {
            log.error("Unlocking plan failed:", e);
        }
        
        //em.getTransaction().commit();
        planPropertiesId = 0;
    }

    @Remove
    @Destroy
    public void destroy() {
    }

}
