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

package eu.planets_project.pp.plato.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * This class inserts test data into the persistence layer, including import of
 * objective trees from case studies.
 *
 * @author Christoph Becker
 */
@Stateful
@Scope(ScopeType.METHOD)
@Name("projectCleaner")
public class ProjectCleaner implements Serializable, IProjectCleaner {

    private static final long serialVersionUID = 2155152208617526555L;

    private static final Log log = PlatoLogger.getLogger(ProjectCleaner.class);

    @PersistenceContext
    EntityManager em;


    @Remove
    @Destroy
    public void destroy() {

    }

    public int cleanupProject(int pid) {
        List<Plan> list = em.createQuery(
                "select p from Plan p where p.planProperties.id = "
                        + pid).getResultList();
        if (list.size() != 1) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "An unexpected error has occured while loading the plan with properties"+pid);
            return 0;
        }
        Plan p = list.get(0);
        List<String> alternativeNames = new ArrayList<String>();
        for (Alternative a: p.getAlternativesDefinition().getAlternatives()) {
            alternativeNames.add(a.getName());
        }
        int number = p.getTree().removeLooseValues(alternativeNames,p.getSampleRecordsDefinition().getRecords().size());
        log.info("cleaned up values for plan "+p.getPlanProperties().getName()+":");
        log.info("removed "+number+" Value(s) instances from this project");
         if (number > 0) {
            em.persist(p.getTree());
        }
        em.clear();
        p = null;
        list.clear();
        list = null;
        return number;
     }
}
