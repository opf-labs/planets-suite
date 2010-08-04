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
package eu.planets_project.pp.plato.action.project;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Allows the user to set configuration options for the preservation plan and perform
 * plan-related actions.
 *
 * <ul>
 *   <li>Set plan private.</li>
 *   <li>Upload a report.</li>
 *   <li>Publish report. This option only goes with a private plan. When the plan is
 *       set to private, the user can however publish the uploaded report. This results in
 *       a private plan (no-one except the creator can load and edit the plan but anyone
 *       can display the report.</li>
 *   <li>Delete plan permanently.</li>
 * </ul>
 *
 * @author Hannes Kulovits
 *
 */
@Name("projectSettings")
@Scope(ScopeType.SESSION)
public class ProjectSettings implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Log log = PlatoLogger.getLogger(ProjectSettings.class);

    @In
    EntityManager em;

    @In @Out(required = false)
    protected Plan selectedPlan;

    @Out(required=false)
    private DigitalObject reportUpload = new DigitalObject();

    @In (required=false)
    private User user;
    
    private final static int code = -1859658482;
    
    /**
     * Password enterd by the user.
     */
    @In(required=false)
    private String pw;
    
    public String auth()  {
        if (pw != null && pw.hashCode()==code) {
            int i = 0;
            for (Alternative a:selectedPlan.getAlternativesDefinition().getAlternatives()) {
                if (a.getAction() != null && a.getAction().getActionIdentifier().toLowerCase().contains("minimee")) {
                    a.getAction().setExecute(true);
                    i++;
                }
            }
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                    "Authenticated for "+i+" actions",
                    new Object[] {});            
        } else {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                    "Wrong code",
                    new Object[] {});
        }
        return null;
    }
    
    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    /**
     * Downloads currently selected sample record. Uses {@link eu.planets_project.pp.plato.util.Downloader}
     * to perform the download.
     */
    public void downloadReport() {
        // must be merged back so the data byte stream can be lazy loaded.
        DigitalObject report = (DigitalObject)em.merge(selectedPlan.getPlanProperties().getReportUpload());
        Downloader.instance().download(report);
    }

    /**
     * This method is called when the user wants to visit the workflow step 'Plan Settings'.
     * Initializes the member variables.
     *
     * @return always returns 'success'
     */
    public String enter() {
        //this.reportUpload = selectedPlan.getPlanProperties().getReportUpload();

        return "success";
    }

    /**
     * Deletes the plan.
     * @return "loadPlan" when the plan was removed successfully.
     *         null when the user does not have the permissions to delete the plan.
     */
    @RaiseEvent("projectListChanged")
    public String delete() {
        if (mayChange(selectedPlan)) {
            log.info("deleting plan "+selectedPlan.getPlanProperties().getName());
            em.remove(em.merge(selectedPlan));
            em.flush();
            FacesMessages.instance().add("Plan '" + selectedPlan.getPlanProperties().getName() + "' successfully deleted.");
            selectedPlan = null;
            return "loadPlan";
        } else {
            FacesMessages.instance().add("You are not the owner of this plan and thus not allowed to delete it.");
            return null;
        }
    }

    /**
     * Stores the project settings to the database.
     * @return null Always returns null.
     */
    public String save() {
        if (mayChange(selectedPlan)) {
            if (log.isDebugEnabled()) {
                log.debug("Save settings of plan" + selectedPlan.getPlanProperties().getName());
            }

            if (selectedPlan.getPlanProperties().isReportPublic()
                    && !selectedPlan.getPlanProperties().isPrivateProject()) {
                FacesMessages.instance().add(
                        FacesMessage.SEVERITY_ERROR,
                        "The option 'Publish report' requires the plan to be set to private");

                return null;
            }

            /** dont forget to prepare changed entities e.g. set current user */
            PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
            prep.prepare(selectedPlan.getPlanProperties());
            em.persist(em.merge(selectedPlan.getPlanProperties()));
        } else {
            FacesMessages.instance().add("You are not the owner of this plan and thus not allowed to change it.");
        }
        return null;
    }

    /**
     * Restore settings from last time the project settings were stored.
     */
    public String discard() {
        if ((selectedPlan == null) ||
            (selectedPlan.getId()==0))
            return null;
        selectedPlan.setPlanProperties(em.find(PlanProperties.class, selectedPlan.getPlanProperties().getId()));
        return null;
    }

    /**
     * Determines if the logged in user has the permission to change the project.
     *
     * Only the administrator (admin) and the project owner have the
     * permission to change the project.
     *
     * @return true if the logged in user has the permissions
     *         false if the logged in user doesn't have the permissions
     */
    public boolean mayChange(Plan plan) {
        return (user != null) && (user.isAdmin() || user.getUsername().equals(plan.getPlanProperties().getOwner()));
    }

    /**
     * Determines if the logged in user has the permission to view the project.
     *
     * The logged in user has the permission to view the project if he/she has the permission
     * to change it {@link #mayChange(Plan)} or the project is not set to private.
     *
     * @return true if the logged in user has the permissions
     *         false if the logged in user doesn't have the permissions
     */
    public boolean mayRead(Plan plan) {
        return !plan.getPlanProperties().isPrivateProject() || mayChange(plan);
    }

    public boolean getMayChange() {
        return mayChange(selectedPlan);
    }

    public boolean getMayRead() {
        return mayRead(selectedPlan);
    }

    public DigitalObject getReportUpload() {
        return reportUpload;
}
    public void setReportUpload(DigitalObject reportUpload) {
        this.reportUpload = reportUpload;
    }

    /**
     * Removes the uploaded report from the database.
     *
     * @return Always returns an empty string.
     */
    public String removeReportUpload() {
        DigitalObject newUpload = new DigitalObject();
        selectedPlan.getPlanProperties().setReportUpload(newUpload);
        reportUpload = newUpload;
        save();
        return "";
    }

    /**
     * Stores the uploaded report {@link #reportUpload} in the database.
     */
    public void uploadReport() {
        if (mayChange(selectedPlan)) {
            if (reportUpload.getData() != null && reportUpload.getData().getSize() > 0) {
                DigitalObject report = reportUpload.clone();

                selectedPlan.getPlanProperties().setReportUpload(report);

                save();

                FacesMessages.instance().add(
                        FacesMessage.SEVERITY_INFO, "The report has been uploaded successfully.");
            }
        } else {
            FacesMessages.instance().add("You are not the owner of this plan and thus not allowed to change it.");
        }

    }

    @Remove
    @Destroy
    public void destroy() {

    }
}
