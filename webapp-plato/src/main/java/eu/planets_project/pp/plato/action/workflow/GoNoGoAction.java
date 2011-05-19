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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IDevelopExperiments;
import eu.planets_project.pp.plato.action.interfaces.IGoNoGo;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Decision;
import eu.planets_project.pp.plato.model.PlanState;

/**
 * Implements actions for workflow step 'Go-/No Go'.
 *
 * The system lets the user choose a decision from a list of possible decision (Go, No Go,
 * Provisional Go, ...). The user must choose the 'Go' decision in order to proceed to the
 * next workflow step. Furthermore, the user can discard certain infeasible alternatives.
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("gonogo")
public class GoNoGoAction extends AbstractWorkflowStep implements IGoNoGo {

    /**
     * 
     */
    private static final long serialVersionUID = -1602677423352776273L;

    @In(create=true)
    IDevelopExperiments devexperiments;

    /**
     * Decision taken by the user. The user can select one decision from a list.
     */
    @Out(required=false)
    private Decision decision;

    @SuppressWarnings("unused")
    @DataModelSelection
    private Alternative selectedAlternative;

    @SuppressWarnings("unused")
    @DataModel
    private List<Alternative> alternativeList;

    /**
     * contains for each alternative if it shall be discarded or not, dependent of the user's
     * choice. If the alternative shall be discarded the value is true, otherwise it is true.
     * It is used in the alternatives data table.
     */
    @Out(required = false)
    Map<Alternative, Boolean> discardedAlternatives;

    public GoNoGoAction() {
        requiredPlanState = new Integer(PlanState.ALTERNATIVES_DEFINED);
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {

        // first we get our stored decision
        decision = selectedPlan.getDecision();

        // if no decision has yet been stored we create one
        if (decision == null) {
            decision = new Decision();
        }

        alternativeList = selectedPlan.getAlternativesDefinition().getAlternatives();
        // second, we determine a list of alternatives that comprises all alternatives
        // the one the user wants to consider for evaluation plus the one the user wants
        // to discard

        discardedAlternatives = new HashMap<Alternative, Boolean>();

        for (Alternative discardedAlternative : selectedPlan.getAlternativesDefinition().getAlternatives()) {
            if(discardedAlternative.isDiscarded()) {
                discardedAlternatives.put(discardedAlternative, new Boolean(true));
            }
        }
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    protected IWorkflowStep getSuccessor() {
        return devexperiments;
    }

    /**
     * Saves the taken decision and the user choice which alternative shall be considered and
     * discarded respectively.
     */
    public String save() {

        for (Alternative alt : selectedPlan.getAlternativesDefinition().getAlternatives()) {

            if (discardedAlternatives.containsKey(alt)) {
                Boolean discarded = discardedAlternatives.get(alt);
                // maybe this alternative will be changed
                if (alt.isDiscarded() != discarded)
                    alt.touch();

                alt.setDiscarded(discarded);
            }
        }

        selectedPlan.setDecision(this.decision);

        super.save(selectedPlan.getDecision());
        super.save(selectedPlan.getAlternativesDefinition());
        changed = "";
        return null;
    }

    /**
     * The user must leave at least one alternative for evaluation. Plus, the GO decision must
     * be chosen in order to proceede with the workflow.
     */
    public boolean validate(boolean showValidationErrors) {
        boolean go = decision.isGoDecision();
        if (!go) {
            if (showValidationErrors) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "You have to take the GO decision to proceed with the workflow.");
            }
        }

        if (!discardedAlternatives.containsValue(new Boolean(false))) {
            go = false;
            if (showValidationErrors) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "At least one alternative must be considered for evaluation.");
            }
        }

        return go;
    }

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @RaiseEvent("reload")
    @Override
    public String discard() {
        String result = super.discard();
        init();
        return result;
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
     */
    protected String getWorkflowstepName() {
        return "gonogo";
    }
}
