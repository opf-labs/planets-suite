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
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import eu.planets_project.pp.plato.action.interfaces.IDefineBasis;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackDefineRequirements;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Action for creating a new preservation plan.
 *
 * @author Hannes Kulovits
 */
@Name("newProject")
@Scope(ScopeType.SESSION)
public class NewProjectAction implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Log log = PlatoLogger.getLogger(NewProjectAction.class);

    @Out
    private Plan selectedPlan;

    @In
    EntityManager em;


    @In(required=false)
    @Out
    protected String changed = "";

    /**
     * Workflow step 'Load Plan'
     */
    @In(create=true)
    private LoadPlanAction loadPlan;

    @In(create = true)
    IDefineBasis defineBasis;
    
    @In(create = true)
    IFastTrackDefineRequirements FTrequirements;
    
    
    @In (required=false)
    private User user;

    public String createFTE() {
        createProject();
        
        // set Fast Track properties
        selectedPlan.getState().setValue(PlanState.FTE_INITIALISED);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-kkmmss");
        String timestamp = format.format(new Date(System.currentTimeMillis()));
        String identificationCode = Plan.fastTrackEvaluationPrefix + timestamp;
        selectedPlan.getProjectBasis().setIdentificationCode(identificationCode);

        // load the selected project (and keep the id!)
        loadPlan.setPlanPropertiesID(selectedPlan.getPlanProperties().getId());
        
        // The outjection doesnt work here (since this is not an EJB),
        // so we set the member explicitly
        Contexts.getSessionContext().set("selectedPlan", selectedPlan);

        loadPlan.initializeProject(selectedPlan);
        FTrequirements.enter();
        
        return "success";
    }
    
    /**
     * Creates a new preservation plan and unlocks an already loaded project
     * if necessary. Default values for the created project are as follows:
     * <ul>
     * <li>The responsible planner ist set to the username of the logged in user.</li>
     * <li>The project is set to private and can thus only be opened by the creator.</li>
     * </ul>
     *
     * @return success if the project could be created successfully. success is the only
     *         return value.
     */
    public String createProject() {

        // unlock current project, if there is one
        loadPlan.unlockProject();

        selectedPlan = new Plan();
        selectedPlan.getPlanProperties().setAuthor(user.getFullName());
        selectedPlan.getPlanProperties().setPrivateProject(true);
        selectedPlan.getPlanProperties().setOwner(user.getUsername());

        // We have to prevent the user from navigating to the step 'Load plan'
        // because the user wouldn't be able to leave this step: Going to 'Define
        // Basis' is not possible as the project hasn't been saved so far.
        //
        // We 'activate' the changed flag so that the user is asked to either
        // save the project or discard changes.
        changed = "T";

        TreeNode root = new Node();
        root.setName("Root");
        selectedPlan.getTree().setRoot(root);

//        PolicyNode policyRoot = new PolicyNode();
//        policyRoot.setName("Policy");
//        selectedPlan.getProjectBasis().getPolicyTree().setRoot(policyRoot);

        return "success";
    }

    /**
     * Stores the user's input value in the database.
     * Also calls {@link eu.planets_project.pp.plato.model.ITouchable#touch()} on the
     * project.
     *
     * @return success if the project was saved successfully. success is the only return
     *         value.
     */
    @RaiseEvent("reload")
    public String saveProject() {
       String s = saveProject(PlanState.INITIALISED);
       // As 'New Plan' is not a workflow step as 'Define Basis' for instance, which
       // in code terms means it is not derived from AbstractWorkflowStep, we have to
       // call the enter method of the next workflow step (Define Basis) ourselves.
       // Otherwise method init of defineBasis would not be called!
       if (defineBasis != null) {
           defineBasis.enter();
       }
       return s;
    }
    
    public String saveFTE() {
        String s =  saveProject(PlanState.FTE_INITIALISED);
        FTrequirements.enter();
        changed="";
        return s;
    }
    
    public String saveProject(int state) {
        log.debug("Persisting plan " + selectedPlan.getPlanProperties().getName());
        selectedPlan.touch();

        selectedPlan.getState().setValue(state);
        selectedPlan.getPlanProperties().setOpenHandle(1);

        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
        prep.prepare(selectedPlan);

        em.persist(selectedPlan);
        loadPlan.setPlanPropertiesID(selectedPlan.getPlanProperties().getId());

        return "success";        
    }
}
