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

package eu.planets_project.pp.plato.action.workflow;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.IDevelopExperiments;
import eu.planets_project.pp.plato.action.interfaces.IRunExperiments;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.User;

/**
 * Implements actions for workflow step 'Develop Experiments'.
 *
 * At present this is just a text box per alternative.
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("devexperiments")
public class DevelopExperimentsAction extends AbstractWorkflowStep implements IDevelopExperiments {

    /**
     * 
     */
    private static final long serialVersionUID = 815927044490356685L;

    @In(create=true)
    IRunExperiments runexperiments;

    @In (required=false)
    private User user;

    protected IWorkflowStep getSuccessor() {
        return runexperiments;
    }

    public DevelopExperimentsAction() {
        requiredPlanState = new Integer(PlanState.GO_CHOSEN);
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        
        // if alternative has config setting defined (e.g. parameters for web services) we adopt
        // them as experiment settings
//        for (Alternative alt : selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
//
//            if (alt.getExperiment().getSettings() == null || "".equals(alt.getExperiment().getSettings())) {
//                alt.getExperiment().setSettings(alt.getResourceDescription().getConfigSettings());
//            }
//        }
    }

    /**
     * @see AbstractWorkflowStep#save()
     */
    public String save() {
        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        for (Alternative alt : selectedPlan.getAlternativesDefinition().getAlternatives()) {
            prep.prepare(alt);
            em.persist(em.merge(alt));
        }

        super.save(selectedPlan.getAlternativesDefinition());
        changed = "";

        return null;
    }

    /**
     * @see AbstractWorkflowStep#validate(boolean)
     *
     * @return Always returns true
     */
    public boolean validate(boolean showValidationErrors) {
        return true;
    }

    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     *
     * @return "devexperiments"
     */
    protected String getWorkflowstepName() {
        return "devexperiments";
    }

}
