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
package eu.planets_project.pp.plato.action.user;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.model.Organisation;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.tree.PolicyTree;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.TreeLoader;

/**
 * TODO HK add documentation
 * @author cbu
 *
 */
@Name("organisationalPolicies")
@Scope(ScopeType.SESSION)
public class OrganisationalPolicies implements Serializable {

    private static final long serialVersionUID = 1484526643930159342L;
    
    private static Log log = PlatoLogger.getLogger(OrganisationalPolicies.class);
    
    @In
    private EntityManager em;
    
    @Out(required=false)
    @In(required=false)
    private byte[] organisationalPoliciesMindMap;
    
    @In(required = false)
    private String fileName;
    
    @In
    @Out
    private User user;
    
    public void uploadFreemindTree() {
        if (organisationalPoliciesMindMap == null) {
            return;
        }
        PolicyTree newtree = null;
        try {
            InputStream istream = new ByteArrayInputStream(organisationalPoliciesMindMap);

            newtree = new TreeLoader().loadFreeMindPolicyTree(istream);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        if (newtree == null) {
            log.error("Cannot upload policy tree.");

            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "The uploaded file is not a valid Freemind mindmap. Maybe it is corrupted?");

            return;
        }
        
        Organisation org = user.getOrganisation();
        
        if (org == null) {
            org = new Organisation();
            user.setOrganisation(org);
        }
        
        org.setPolicyTree(newtree);
    }
    
    public void removePolicyTree() {
        Organisation org = user.getOrganisation();
        
        if (org != null) {
            org.setPolicyTree(null);
        }
    }
    
    public String save() {
        Organisation org = user.getOrganisation();
        
        // nothing to save
        if (org == null) {
            return "";
        }
        
        if (org.getId() == 0) {
            em.persist(org);
        }
        
        em.persist(em.merge(user));
        
        return "success";
    }

    public String discard() {
        
        user = em.find(User.class, user.getId());
        
        return "success";
    }


    public byte[] getOrganisationalPoliciesMindMap() {
        return organisationalPoliciesMindMap;
    }

    public void setOrganisationalPoliciesMindMap(
            byte[] organisationalPoliciesMindMap) {
        this.organisationalPoliciesMindMap = organisationalPoliciesMindMap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
