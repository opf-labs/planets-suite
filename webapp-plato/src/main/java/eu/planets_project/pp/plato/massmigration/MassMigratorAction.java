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
package eu.planets_project.pp.plato.massmigration;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.minimee.MiniMeeServiceRegistry;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Name("massmigrator")
@Scope(ScopeType.SESSION)
public class MassMigratorAction implements IMassMigrator, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3762053818641173669L;

    private Log log = PlatoLogger.getLogger(MassMigratorAction.class);
    
    private MiniMeeServiceRegistry registry = new MiniMeeServiceRegistry();
    
    @Out(required=false)
    private MassMigrationSetup selectedSetup = new MassMigrationSetup();


    @Out
    private List<MassMigrationSetup> setupList = new ArrayList<MassMigrationSetup>();
    
    /**
     * Entity manager for persistence.
     */
    @PersistenceContext
    private EntityManager em;
    
    @Out 
    private List<PreservationActionDefinition> availableMMActions = new ArrayList<PreservationActionDefinition>();
    
    @Out
    private FormatInfo sourceFormat = new FormatInfo();
    
    public void createSetup(){
        selectedSetup = new MassMigrationSetup();
    }
    
    public void deleteSetup(Object setup) {
        if (setup == null)
            return;
        if (! (setup instanceof MassMigrationSetup))
            return;
        
        MassMigrationSetup mmSetup = (MassMigrationSetup)setup;
        if ((selectedSetup != null) && (mmSetup.getId() == selectedSetup.getId())) {
            em.remove(em.merge(mmSetup));
            selectedSetup = null;            
        } else {
            setupList.remove(mmSetup);
            em.remove(em.merge(mmSetup));
        }
    }
    
    public void saveSetup() {
        if (selectedSetup == null) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Please create a new setp first.");
            return;
        }
        if ("".equals(selectedSetup.getName())) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Please provide a name.");
            return;
        }
        /*
        for (MassMigrationExperiment e : selectedSetup.getExperiments()) {
            // persist all experiment-info first
            // ...
            if (e.getId()==0)
               em.persist(e);
            else
               em.persist(em.merge(e));
        }*/
/*        MassMigrationStatus s = selectedSetup.getStatus(); 
        if (s != null) {
            s = em.merge(s);
            em.persist(s);
            selectedSetup.setStatus(s);
        }
*/
        if (selectedSetup.getId()==0) {
            em.persist(selectedSetup);            
        } else {
            MassMigrationSetup setup = em.merge(selectedSetup); 
           em.persist(setup);
        }
    }
    

    /**
     * populates setupList
     */
    public void listSetups() {
        String loadSetupQuery = "select s from MassMigrationSetup s order by s.name";
        setupList = em.createQuery(loadSetupQuery).getResultList();
    }
    
    public void loadSetup(Object setup) {
        if (! (setup instanceof MassMigrationSetup))
            return;
        
        selectedSetup = (MassMigrationSetup)setup;
        setupList.clear();
        
    }
    
    public void listActions() {
        try {
            availableMMActions.clear();
            registry.connect("");
            List <PreservationActionDefinition> l = registry.getAvailableActions(sourceFormat);
            // we are only interested in migration actions
            // it should be possible to query for this! 
            for (PreservationActionDefinition a : l) {
                if (!a.isEmulated())
                    availableMMActions.add(a);
            }
        } catch (MalformedURLException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Could not connect to service registry.");
            log.error("Could not connect to service registry.", e);
        } catch (ServiceException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Could not connect to service registry.");
            log.error("Could not connect to service registry.", e);
        } catch (PlatoServiceException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Failed to retrieve available preservation actions.");
            log.error("Failed to retrieve available preservation actions.", e);
        }
        
    }
    public void removeExperiment(Object experiment) {
        if (!(experiment instanceof MassMigrationExperiment))
            return;
        MassMigrationExperiment exp = (MassMigrationExperiment)experiment;
        selectedSetup.getExperiments().remove(exp);
    }
    
    public void createExperimentsForActions() {
        for (PreservationActionDefinition a : availableMMActions) {
            if (a.isSelected()) {
                // create an experiment for this action
                MassMigrationExperiment e = new MassMigrationExperiment();
                e.setAction(a);
                selectedSetup.getExperiments().add(e);
            }
            
        }
        
    }
    
    public String runMassMigration() {
        if (selectedSetup == null)
            return "success";
        if (selectedSetup.getStatus().getStatus() == MassMigrationStatus.RUNNING) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "This setup is already executing.");
            return "success";
        }
        selectedSetup.prepareMassMigration();
        /* 
         * Save setup after status reset, this way mass-migrations can be started 
         * again if something goes wrong, or you forgot to "save setup" after execution. 
         */
        saveSetup();
        
        selectedSetup.getStatus().setStatus(MassMigrationStatus.RUNNING);
        
        (new MassMigrationRunner(selectedSetup)).start();
        
        return "success";
    }
    
    @Destroy
    @Remove
    public void destroy() {
    }

}
