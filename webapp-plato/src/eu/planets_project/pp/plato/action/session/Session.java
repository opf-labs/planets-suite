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
package eu.planets_project.pp.plato.action.session;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.util.PlanetsUserManager;

/**
 * Phase listener that redirects the user in case of inconsistencies such as
 * gone user object or a raised exception.
 *
 * Used in {@link SessionListenerRenderResponse} and {@link SessionListenerApplyRequest}.
 *
 * @author Hannes Kulovits
 */
public class Session implements PhaseListener {

    private static final long serialVersionUID = 1L;

    /**
     * Redirects the user to page 'Load Plan' and tells him to load a project
     * @param context to determine the external context
     */
    public void redirectToProject(FacesContext context) {
        try {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "No project selected - please load project first.");
            context.getExternalContext().redirect(
                    "/plato/project/loadPlan.seam");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by JSF framework before phase is processed.
     *
     * We intercept here and redirect the user in case of inconsistencies.
     */
    public void beforePhase(PhaseEvent arg0) {

        FacesContext context = arg0.getFacesContext();
        Context session = Contexts.getSessionContext();
        if (session == null) {
            return;
        }

        PlanetsUserManager userManager = PlanetsUserManager.createUserManager();

        // do we have a user
        boolean user = (userManager.getLoggedInUser() != null);

        // is a planning project loaded
        boolean project = Contexts.getSessionContext().isSet("selectedPlan");

        String page = context.getViewRoot().getViewId();

        // Some pages should be always accessible, but for most we need a loaded project.
        // So we check: Is the destination ... ?
        boolean loadPage = (page.compareTo("/project/loadPlan.xhtml") == 0);
        boolean adminUtilsPage = (page.compareTo("/project/admin-utils.xhtml") == 0);
        boolean exception = (page.compareTo("/exception.xhtml") == 0);
        boolean logout = (page.compareTo("/logout.xhtml") == 0);
        boolean bugreport = (page.compareTo("/project/bugreport.xhtml") == 0);
        boolean feedback = (page.compareTo("/project/feedback.xhtml") == 0);
        boolean testpp5 = (page.compareTo("/project/test-pp5.xhtml") == 0);
        boolean massmigration = (page.compareTo("/massmigration/mmsetup.xhtml") == 0);
        boolean mmAdminUtils = (page.compareTo("/project/minimee-admin.xhtml") == 0);

        // if an exception occurred or the user wants to logout we do nothing
        if(exception || logout || feedback){
            return;
        }

        // if the user is still existent
        // ... but there is no selectedPlan OR the user wanted to go to
        // ... loadPlan.xhtml OR admin-utils.xhtml
        if (user && !(project || loadPage || adminUtilsPage || bugreport || testpp5 || massmigration || mmAdminUtils)) {
            System.out.println("Redirecting to Load: " + loadPage);
            this.redirectToProject(context);
            return;
        }

    }

    public void afterPhase(PhaseEvent arg0) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
