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
package eu.planets_project.pp.plato.util;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlanProperties;

/**
 * Helper for the {@link eu.planets_project.pp.plato.util.DownloadServlet}. This
 * is just a bean that determines the report stored in {@link eu.planets_project.pp.plato.model.PlanProperties}.
 *
 * @author Hannes Kulovits
 */
@Name("downloadManagerHelperBean")
@Stateful
@Scope(ScopeType.SESSION)
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class DownloadManagerHelperBean implements Serializable, IDownloadManagerHelperBean {

    /**
     * 
     */
    private static final long serialVersionUID = -9100346433950623541L;
    @PersistenceContext
    protected EntityManager em;

    /**
     * Determines the report of a specific project. The report is stored in the
     * project properties.
     *
     * @param ppId project properties id
     * @return DigitalObject stored in {@link eu.planets_project.pp.plato.model.PlanProperties}
     */
    public DigitalObject getUploadedReportFile(String ppId) {

        Integer i = new Integer(ppId);
        PlanProperties pp = (PlanProperties)em.createQuery("select p from PlanProperties p where p.id = :ppId")
            .setParameter("ppId", i)
            .getSingleResult();

        if (pp== null) {
            return null;
        }
        
        // must be merged back into session because of lazy loading of data byte stream
        DigitalObject o = (DigitalObject)em.merge(pp.getReportUpload());
        
        byte[] data = o.getData().getData();

        return o;
    }

    @Remove
    @Destroy
    public void destroy() {
    }
}
