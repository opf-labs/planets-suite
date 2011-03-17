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

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Phase Listener for logging session id and view id.
 *
 * @author Hannes Kulovits
 */
public class SessionPageListener implements PhaseListener {

    private static final long serialVersionUID = -8996900015695374366L;

    private Log log = PlatoLogger.getLogger(SessionPageListener.class);

    private String id = "";

    /**
     * Logs the session id and view id
     */
    public void afterPhase(PhaseEvent event) {
        String id = "";
        try {
            id = ((HttpServletRequest) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequest()).getSession().getId();
        } catch (RuntimeException e) {
        }
        log.trace(id + " - "
                + event.getFacesContext().getViewRoot().getViewId());
    }

    public void beforePhase(PhaseEvent arg0) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
