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

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.IDefinePlan;
import eu.planets_project.pp.plato.action.interfaces.IValidatePlan;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.util.PlatoLogger;
/**
 * Implements actions for workflow step 'Define Plan'.
 *
 *
 * @author Mark Guttenbrunner
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("definePlan")
public class DefinePlanAction extends AbstractWorkflowStep implements IDefinePlan {

    /**
     * 
     */
    private static final long serialVersionUID = -4986768892884848812L;

    private static final Log log = PlatoLogger.getLogger(DefinePlanAction.class);

    @In(create=true)
    IValidatePlan validatePlan;

    /**
     * For now we define a fixed set of available triggers
     */
    // Apparently this does not work, maybe give it a try with Seam 2.0: @Factory("reevaltriggers")
    public void initTriggers() {

    }

    public DefinePlanAction() {
        requiredPlanState = new Integer(PlanState.EXECUTEABLE_PLAN_CREATED);
    }

    protected String getWorkflowstepName() {
        return "definePlan";
    }
    
    protected IWorkflowStep getSuccessor() {
        return validatePlan;
    }

    public boolean validate(boolean showValidationErrors) {

//        if (whateveriswrong) {
//
//            // we only add a message if we are supposed to do so
//            if (showValidationErrors) {
//                FacesMessages
//                        .instance()
//                        .add(FacesMessage.SEVERITY_ERROR,
//                                "At least one alternative must be added to proceed with the workflow.");
//            }
//
//            return false;
//        }

        return true;
    }
    
    public void init() {
     }


    /**
     * Write both plan definition and project properties to database.
     * 
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {

       
        save(selectedPlan.getPlanProperties());
        save(selectedPlan.getPlanDefinition());
        changed = "";

        return null;
    }
    
    @Override
    protected void processChanges(IChangesHandler t){
        t.visit(selectedPlan.getPlanDefinition());
        t.visit(selectedPlan.getPlanProperties());
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
     * Obligatory EJB destroy method.
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    
}
