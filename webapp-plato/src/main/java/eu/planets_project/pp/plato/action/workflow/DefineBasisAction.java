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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IDefineBasis;
import eu.planets_project.pp.plato.action.interfaces.IDefineSampleRecords;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PolicyNode;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.tree.PolicyTree;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.TreeLoader;


/**
 * Session bean for workflow step 'Define Basis'.
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("defineBasis")
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class DefineBasisAction extends AbstractWorkflowStep implements IDefineBasis {

    /**
     * 
     */
    private static final long serialVersionUID = -846554402422116674L;

    private static final Log log = PlatoLogger.getLogger(DefineBasisAction.class);
    
    @In
    private User user;


    /**
     * 'Define Sample Records' is our successor. Need this for {@link #getSuccessor()}.
     */
    @In(create=true)
    private IDefineSampleRecords defineSampleRecords;

    @Out(required=false)
    private DigitalObject policyMindMap = new DigitalObject();

    /**
     * Initialize current project state.
     */
    public DefineBasisAction() {
        requiredPlanState = new Integer(PlanState.INITIALISED);
    }

    public void removePolicyTree() {
        selectedPlan.getProjectBasis().getPolicyTree().setRoot(null);
    }

    public String uploadPolicyMindMap() {

        if (policyMindMap == null) {
            return "";
        }
        PolicyTree newtree = null;
        try {
            InputStream istream = new ByteArrayInputStream(policyMindMap.getData().getData());

            newtree = new TreeLoader().loadFreeMindPolicyTree(istream);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        if (newtree == null) {
            log.error("Cannot upload policy tree.");

            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "The uploaded file is not a valid Freemind mindmap. Maybe it is corrupted?");

            return null;
        }

        selectedPlan.getProjectBasis().getPolicyTree().setRoot(newtree.getRoot());

        return "";
    }


    /**
     * Initializes 'Define Basis' workflow step, at the moment just the triggers.
     *
     * @see AbstractWorkflowStep#init()
     */
    @Override
    protected void init() {

        // adopt organisational policies (if existent) if it's a new project. if user
        // removes policy tree, proceeds and comes back, don't adopt the policies again. we
        // ensure that by checking the plan state.
        if (selectedPlan.getState().getValue() == PlanState.INITIALISED
                && selectedPlan.getProjectBasis().getPolicyTree().getRoot() == null) {
            
            if (user.getOrganisation() != null 
                    && user.getOrganisation().getPolicyTree() != null) {
                
                PolicyNode clone = user.getOrganisation().getPolicyTree().getRoot().clone();
                
                selectedPlan.getProjectBasis().getPolicyTree().setRoot(clone);
            }
            
        }
        
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    @Override
    protected IWorkflowStep getSuccessor() {
        return defineSampleRecords;
    }

    /**
     * Write both project basis and project properties to database.
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {
        /*
         // we clear the list of triggers ...
        selectedPlan.getProjectBasis().getTriggers().clear();


        // ... as the user can also deselect one. We then add the newly selected ones.
        for (TriggerChoice choice : triggers) {
            if (choice.isSelected()) {
                em.persist(em.merge(choice.getTrigger()));
                selectedPlan.getProjectBasis().getTriggers().put(choice.getTrigger(), choice.getComment());
            }
        }
*/

        // user is set in save()
        save(selectedPlan.getPlanProperties());
        save(selectedPlan.getProjectBasis());
        changed = "";

        return null;
    }

    /**
     * @see AbstractWorkflowStep#validate(boolean)
     */
    public boolean validate(boolean showValidationErrors) {
        return true;
    }

    /**
     * Obligatory EJB destroy method.
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    @Override
    protected String getWorkflowstepName() {
        return "defineBasis";
    }

    /**
     * @see AbstractWorkflowStep#processChanges(IChangesHandler)
     */
    @Override
    protected void processChanges(IChangesHandler t){
        t.visit(selectedPlan.getProjectBasis());
        t.visit(selectedPlan.getPlanProperties());
    }

    public DigitalObject getPolicyMindMap() {
        return policyMindMap;
    }

    public void setPolicyMindMap(DigitalObject policyMindMap) {
        this.policyMindMap = policyMindMap;
    }

}
