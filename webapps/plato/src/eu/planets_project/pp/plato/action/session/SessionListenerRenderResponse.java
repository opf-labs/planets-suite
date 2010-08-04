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

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * Phase Listener for JSF 'Render Response' phase.
 *
 * This doesn't do anything but is very often useful for debug purposes.
 *
 * Delegates afterPhase and beforePhase to respective methods in {@link Session}
 *
 * @author Hannes Kulovits
 */
public class SessionListenerRenderResponse implements PhaseListener {

    private static final long serialVersionUID = -1425318200497394534L;

    private Session session = new Session();

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    /**
     * Delegates to {@link Session#afterPhase(PhaseEvent)}
     */
    public void afterPhase(PhaseEvent arg0) {
        session.afterPhase(arg0);
    }

    /**
     * Delegates to {@link Session#beforePhase(PhaseEvent)}
     */
    public void beforePhase(PhaseEvent arg0) {
        session.beforePhase(arg0);
    }
}
