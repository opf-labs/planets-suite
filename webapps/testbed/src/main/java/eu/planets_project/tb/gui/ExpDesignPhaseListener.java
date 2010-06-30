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
package eu.planets_project.tb.gui;



import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * TODO This is rather awkward, and should really be handled by the faces-config navigation. But, I don't know how else to deal with the ExperimentBean that is placed in the session manually.
 * eu.planets_project.tb.gui.ExpDesignPhaseListener.afterPhase(ExpDesignPhaseListener.java:37)
 * @author AnJackson
 *
 */
public class ExpDesignPhaseListener implements PhaseListener {

    static final long serialVersionUID = 237213472384324l;
    private static Log log = LogFactory.getLog(ExpDesignPhaseListener.class);

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    public void afterPhase(PhaseEvent arg0) {
        FacesContext context = arg0.getFacesContext();
        /* Support for pushing the ExperimentBean in the request. */
        ExperimentInspector ei = (ExperimentInspector)JSFUtil.getManagedObject("ExperimentInspector");
        ei.getExperimentId();
        // Check if there is an ExperimentBean in the session.
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        
        if( context == null  || context.getViewRoot() == null ) return;
        String viewId = context.getViewRoot().getViewId();
        // log.debug("ViewID: "+viewId);
        if( viewId.startsWith("/exp/exp_stage") ) {
            // Force reset if this is not stage 1, and there is no DB-backing for the bean:
            if( ! viewId.startsWith("/exp/exp_stage1") ) {
                if( expBean != null && expBean.getExperiment() == null ) expBean = null;
                if( expBean != null && expBean.getExperiment() != null && expBean.getExperiment().getEntityID() == -1 ) expBean = null;
            }
            ExpDesignPhaseListener.redirectIfRequired(context, "my_experiments", expBean );
        }
        if( viewId.startsWith("/reader/exp_stage") ||
            viewId.startsWith("/admin/manage_exp")  ||
            viewId.startsWith("/admin/exp_delete") ) {
            ExpDesignPhaseListener.redirectIfRequired(context, "browse_experiments", expBean );
        }

    }
    
    private static void redirectIfRequired(FacesContext context, String newView, ExperimentBean expBean ) {
        // Redirect to experiment list if not.
        if( expBean == null ) {
            log.debug("ExperimentBean == null! Redirecting.");
            context.getApplication().getNavigationHandler().handleNavigation(context,"", newView);
            //context.responseComplete();
        }
        log.debug("ExperimentBean found.");
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    public void beforePhase(PhaseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    public PhaseId getPhaseId() {
        // Ensure this listener is called at the first step.
        return PhaseId.RESTORE_VIEW;
    }

}
