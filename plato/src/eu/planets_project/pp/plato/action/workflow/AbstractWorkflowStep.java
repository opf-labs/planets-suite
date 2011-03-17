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

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.action.project.LoadPlanAction;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.util.PlatoLogger;

public abstract class AbstractWorkflowStep implements IWorkflowStep, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6895691692435519109L;

    protected boolean needsClearEm() {
        return false;
    }
    
    private static final Log log = PlatoLogger.getLogger(AbstractWorkflowStep.class);

    /**
     * CAUTION: Whenever you think you need an EXTENDED persistence context here,
     * think twice, and after *extensive* testing you'll most probably notice
     * that you don't need it!
     */
    @PersistenceContext
    protected EntityManager em;

    @In @Out
    protected Plan selectedPlan;

    @In
    private LoadPlanAction loadPlan;

    @In (required=false)
    protected User user;

    /**
     * Has to be set by the workflowstep-implementations themselves!
     */
    protected Integer requiredPlanState = null;

    protected abstract IWorkflowStep getSuccessor();

    public void setPlan(Plan p) {
        selectedPlan = p;
    }
    
    protected abstract String getWorkflowstepName();

    /**
     * This flag is used in views to prevent navigation per menu when there are unsaved changes.
     * This property preserves the changed-state during requests like adding a node
     * and can be used to reset the changed-state i.e. after save and discard
     */
    @In @Out
    protected String changed = "";

    /**
     * Performs initialisations required for this workflowstep
     * (initialisations for the action itself, like fill some beans for the GUI etc...)
     * ALL initialisation has to be performed in this method!
     */
    protected abstract void init();

    /**
     * checks if the project has progressed far enough so that the user is allowed
     * to enter this step; then initialisation is performed and the step is entered.
     * if the user is now allowed to enter, it returns null so that the user is redirected
     * to the last active page
     */
    public final String enter() {
        if (!checkState()) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "The project has not yet progressed far enough to enter this state. Please follow the workflow.");
            return null;
        }
        init();
        return getWorkflowstepName();
    }

    /**
     * This method does nothing except returning "null", causing the web-application
     * to refresh the current page, thus updating changes the user has applied to the
     * model in his browser since the page was loaded.
     * unused as of Oct 21, 2009
     */
    public final String updateModel() {
        return null;
    }

    /**
     * Evaluates whether the selected Plan is evolved enough to proceed to
     * the current WorkflowStep.
     */
    protected final boolean checkState() {
        if (requiredPlanState != null) {
            return selectedPlan.getState().getValue() >= requiredPlanState;
        }
        log.error("Information about the required project state needed for " + getClass().getName() + " is missing!");
        return false;
    }

    /**
     * Saves a certain entity of the preservation planning project and updates the project state.
     *
     * @param entity Entity that shall be saved.
     */
    protected void save(Object entity) {

        if (log.isDebugEnabled()) {
            log.debug("Persisting entity " + entity.getClass().getName());
        }

        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        /** firstly, we set the project state to requiredPlanState */
        prep.prepare(selectedPlan.getState());
        selectedPlan.getState().setValue(requiredPlanState);
        em.persist(em.merge(selectedPlan.getState()));

        /** secondly, we save the intended entity */
        prep.prepare(entity);
        em.persist(em.merge(entity));
        em.flush();

        if (selectedPlan.getPlanProperties().getReportUpload().isDataExistent()) {
            selectedPlan.getPlanProperties().setReportUpload(new DigitalObject());
            em.persist(em.merge(selectedPlan.getPlanProperties()));

            String msg = "Please consider that because data underlying the preservation plan has been changed, the uploaded report was automatically removed. ";
            msg += "If you would like to make the updated report available, please generate it again and upload it in 'Plan Settings'.";
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, msg);
        }
    }

    /**
     * Performs hibernate validation on a bean instance. Considers our validators (derived from
     * org.hibernate.validator.Validator)
     *
     * As the used class ClassValidator can be quite expensive to create we should consider
     * creating it once for each type we need validated.
     * @param <T>
     * @param entity Entity that shall be validated
     * @param beanClass Bean type which shall be validated
     * @param showValidationErrors
     * @return false if validation fails
     */
    protected <T>boolean validateProperties(T entity, Class<T> beanClass, boolean showValidationErrors) {

        boolean valid = true;

        ClassValidator<T> validator = new ClassValidator<T>(beanClass);
        InvalidValue[] invalidValues = validator.getInvalidValues(entity);

        if (invalidValues.length > 0)
        {
            valid = false;
            for (int i = 0; i < invalidValues.length; i ++) {
                if (invalidValues[i].getBean() instanceof Scale) {
                    /**
                     *  mapping the errors to their fields in the view does not work for values in the tree
                     *  therefore add the error messages manually to FacesMessages
                     */
                    if (showValidationErrors)
                        FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, invalidValues[i].getMessage());
                }
            }
            if (showValidationErrors)
                FacesMessages.instance().add(invalidValues);
        }

        return valid;
    }

    /**
     * This method should be called by the save command button on the xhtml page.
     * By default, this method saves the whole project.
     * Most concrete WorkflowActions save only those parts of the project that they modify.
     * @see #save(Object)
     *
     * @return null means that we want to remain on the same page.
     */
    public String save() {
       
        for (Leaf l: selectedPlan.getTree().getRoot().getAllLeaves()) {
            log.debug(l.getName()+": "+l.getScale().getDisplayName());
            for (String s: l.getValueMap().keySet()) {
                log.debug("   value entry for "+s);
            }
            if (l.getTransformer() instanceof OrdinalTransformer) {
                OrdinalTransformer t =(OrdinalTransformer) l.getTransformer();
                for (String s: t.getMapping().keySet()) {
                    log.debug("   transformer entry for "+s);
                }
            }

        }

        save(selectedPlan);

        changed = "";

        return null;
    }

    /**
     * This method should be invoked by the commandbuttons placed at the bottom
     * right of each xhtml-page.
     */
    public final String proceed() {

        save(); // Save the project anyway, even if not all fields are valid

        if (!validate(true)) {
            return null;
        }

        // If validation passed, explicitly persist new project-state!
        selectedPlan.getState().setValue(requiredPlanState+1);

        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
        prep.prepare(selectedPlan.getState());
        em.persist(em.merge(selectedPlan.getState()));
        em.flush();
        clearEm();

       // em.close();

        if (getSuccessor() != null) {
            log.debug("Trying to proceed to next step");
            if (needsClearEm()) {
                // if we are one of those that clears the EM, we might need to set
                // the newest instance of our plan into the successor or otherwise
                // it might not be able to properly evaluate the ENTER function
                getSuccessor().setPlan(selectedPlan);
            }
            return getSuccessor().enter();
        } else {
            // there is no successor, so this is the last step
            log.debug("Last Workflowstep reached");
            return null;
        }
    }
    
    
    /**
     * 
     */
    private void clearEm() {
        if (needsClearEm()) {
            doClearEm();
        }
    }

    /**
     * 
     */
    protected void doClearEm() {
        int projectId = selectedPlan.getId();
        selectedPlan = null;
        em.clear();
        selectedPlan = em.find(Plan.class, projectId);
        System.gc();
    }

    /**
     * Discards all changes of the model which are not persisted so far by
     * reloading the project.
     * Does not perform a rollback of database or the seam-context.
     * Raises a "reload" event to call InitWorkflowAction.init()
     * If a step uses context-variables which are not initialized in InitWorkflowAction.init(),
     * it has to refresh them itself
     */
    @RaiseEvent("reload")
    public String discard()
    {
        if ((selectedPlan == null) ||
            (selectedPlan.getId()==0))
            return null;
        selectedPlan = em.find(Plan.class, selectedPlan.getId());
        loadPlan.initializeProject(selectedPlan);
        // there are no changes left
        changed = "";
        // if null is returned the "reload" event is not raised!
        return "success";
    }

    public abstract void destroy();

    protected void processChanges(IChangesHandler t) {

    }
}
