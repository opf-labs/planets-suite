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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
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

import eu.planets_project.pp.plato.action.interfaces.IDefineAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IGoNoGo;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.AlternativesDefinition;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IPreservationActionRegistry;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryDefinition;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryFactory;
import eu.planets_project.pp.plato.util.PlatoLogger;
/**
 * Implements actions for workflow step 'Define Sample Records'
 * where sample records can be added and described, and their format can be identified.
 *
 * @author Michael Kraxner
 */

@Stateful
@Scope(ScopeType.SESSION)
@Name("defineAlternatives")
public class DefineAlternativesAction extends AbstractWorkflowStep implements
        IDefineAlternatives {


    /**
     * 
     */
    private static final long serialVersionUID = -277644335068218608L;

    private static final Log log = PlatoLogger
            .getLogger(DefineAlternativesAction.class);

    /**
     * The go/no-go step is the successor
     */
    @In(create = true)
    IGoNoGo gonogo;

    @DataModelSelection
    private Alternative selectedAlternative;

    @In (required=false)
    private User user;

    protected String getWorkflowstepName() {
        return "defineAlternatives";
    }

    /**
     * Contains all alternatives of the preservation planning project:
     * considered and discarded alternatives
     */
    @SuppressWarnings("unused")
    @DataModel
    private List<Alternative> alternativeList;

    /**
     * this private utility list is used for checking for changes of alternative names.
     * this is needed because the name is used for mapping the evaluation values, in the hashmap
     * that maps to {@link Values}
     */
    private HashMap<Alternative,String> alternativeNames = new HashMap<Alternative,String>();
    
    /**
     * The alternative the user has selected to edit - or a newly created one
     */
    @Out(required = false)
    private Alternative alt;

    /**
     * This determines the behaviour of the remove-buttons on the page (see
     * there) - to remove alternatives from the list
     */
    @Out
    private int allowRemove = -1;

    /**
     * A list of all currently defined preservation service registries
     */
    @Out(required = false)
    private List<PreservationActionRegistryDefinition> availableRegistries;

    /**
     * A list of all currently defined preservation services registries,
     * each of them accompanied with a flag if the user wants to search this registry
     */
    @Out(required = false)
    private Map<PreservationActionRegistryDefinition, Boolean> registrySelection;

   
    /**
     * The list of all preservation services found in the selected registries
     */
    @Out(required = false)
    private List<PreservationActionDefinition> availableActions = new ArrayList<PreservationActionDefinition>();

    /**
     *
     * @return Number of found preservation services.
     */
    @Out(required = false)
    private int GetNumOfAvailableActions() {
        return availableActions.size();
    }

    public DefineAlternativesAction() {
        requiredPlanState = new Integer(PlanState.TREE_DEFINED);
    }

    /**
     * Feeds the list allAlternativesList for being displayed. It contains the
     * considered as well as discarded alternatives.
     *
     * Loads the list of all defined preservation service registries.
     */
    public void init() {

        allowRemove = -1;

        alternativeList = selectedPlan.getAlternativesDefinition()
                .getAlternatives();

        alt = null;
        
        resetAlternativeNames();

        // populate the list of preservation service registries
        List<PreservationActionRegistryDefinition> registries =
            PreservationActionRegistryFactory.getAvailableRegistries();
        if (registrySelection == null)
            registrySelection = new HashMap<PreservationActionRegistryDefinition, Boolean>();
        else
            registrySelection.clear();

        for (PreservationActionRegistryDefinition definition : registries) {
            registrySelection.put(definition, false);
        }
        // Map.keySet() is not accessible via EL, therefore we have to outject the list of registries
        availableRegistries = registries;
        availableActions.clear();
     }

    /**
     * gets the names of all alternatives in the list and maps them to their current names -
     * stored in  {@link #alternativeNames} for checking later if some names changed.
     */
    private void resetAlternativeNames() {
        alternativeNames.clear();
        for (Alternative a: alternativeList) {
            alternativeNames.put(a,a.getName());
        }   
    }

    protected IWorkflowStep getSuccessor() {
        return gonogo;
    }

    /**
     * Creates a new alternative in the memory. The alternative is neither added
     * to the list of alternatives nor stored in database (user has to press the
     * save button to store it permanently in the database).
     */
    public String create() {
        log.debug("Creating New Alternative");

        alt = Alternative.createAlternative();

        return null;
    }

    public String select() {
        alt = selectedAlternative;
        allowRemove = -1;
        return null;
    }

    /**
     * Adds a newly created alternative to the list of alternatives Does not
     * persist the alternative in the database!
     */
    public void editAlternative() {

        if (alt == null) {
            log
                    .error("No alternative selected. This method should not have been called.");
            return;
        }
        AlternativesDefinition alternativesDefinition = selectedPlan.getAlternativesDefinition();
        // its a new alternative and the alternative is not yet in list of
        // alternatives
        // note: the user can also edit an alternative that has already been added to the list
        if ((alt.getId() == 0) && 
            (!alternativesDefinition.getAlternatives().contains(alt))) {
            String altName = alt.getName(); 
            if ((altName.length() > 20)||
                (null != alternativesDefinition.alternativeByName(altName))){
                FacesMessages.instance()
                    .add(FacesMessage.SEVERITY_ERROR,
                        "Please provide a unique name which has no more than 20 characters.");
                return;
            }

            // add it to the preservation planning project
            alternativesDefinition.addAlternative(alt);
            // refresh the datamodel list
            alternativeList = alternativesDefinition.getAlternatives();
            // the alternativesdefinition has been changed
            alternativesDefinition.touch();
        }
        // this alternative has been changed
        alt.touch();

        // exit editing mode
        alt = null;
    }

    @Override
    public String save() {
        /** dont forget to prepare changed entities e.g. set current user */
        /** we have to do this now, so new alternatives get also the current user set */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
        prep.prepare(selectedPlan.getAlternativesDefinition());
        
        // if there are new alternatives save them first!
        for (Alternative alt : alternativeList) {
            if (alt.getId() == 0)

                em.persist(alt);
        }
        // update name mappings in the tree
        boolean updatedAlternativeNames = updateAlternativeNames();

        
        save(selectedPlan.getRecommendation());
        save(selectedPlan.getAlternativesDefinition());
        
        // if name mappings were updated, we have to save the tree,
        // or if an alternative has been deleted - so for now we just do it:
        save(selectedPlan.getTree());
        
        if (updatedAlternativeNames) {
            for (Leaf l: selectedPlan.getTree().getRoot().getAllLeaves()) {
                em.persist(em.merge(l));
            }           
        }
        
        changed = "";
        
        resetAlternativeNames();
        return null;
    }

    private boolean updateAlternativeNames() {
        boolean result = false;
        for (Alternative a: alternativeList) {
            String name = alternativeNames.get(a);
            if (name != null && (!name.equals(a.getName()))) {
                selectedPlan.getTree().updateAlternativeName(name,a.getName());
                result = true;
            }
        }
        return result;
    }

    /**
     * The preservation planning project must have at least on alternative
     * defined (considered or discarded alternative)
     */
    public boolean validate(boolean showValidationErrors) {
        
        if (alt != null) {
            FacesMessages
            .instance()
            .add(FacesMessage.SEVERITY_ERROR,
                    "You are currently editing an Alternative. Please save the alternative first before you proceed to the next step.");           
            
            return false;
        }

        int nrOfAlternatives = selectedPlan.getAlternativesDefinition()
                .getAlternatives().size();
        // +
        // selectedPlan.getAlternativesDefinition().getDiscardedAlternatives().size();

        if (nrOfAlternatives <= 0) {

            // we only add a message if we are supposed to do so
            if (showValidationErrors) {
                FacesMessages
                        .instance()
                        .add(FacesMessage.SEVERITY_ERROR,
                                "At least one alternative must be added to proceed with the workflow.");
            }

            return false;
        }

        return true;
    }

    /**
     * Removes an alternative from the list AND also removes all associated
     * evaluation values contained in the tree, if there are any.
     */
    public String removeAlternative() {
        log.info("Removing Alternative from Plan: "+ selectedAlternative.getName());
        updateAlternativeNames();
        
        if (selectedPlan.getRecommendation().getAlternative() == selectedAlternative) {
            selectedPlan.getRecommendation().setAlternative(null);
            selectedPlan.getRecommendation().setReasoning("");
            selectedPlan.getRecommendation().setEffects("");
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
               "You have removed the action which was chosen as the recommended alternative.");
        }
        selectedPlan.getAlternativesDefinition().removeAlternative(selectedAlternative);

        alternativeList = selectedPlan.getAlternativesDefinition()
                .getAlternatives();
        selectedPlan.getAlternativesDefinition().touch();

        selectedPlan.getTree().removeValues(selectedAlternative);

        allowRemove = -1;

        // after the selected alternative was deleted, alt, which is needed to
        // display the currently selected alternative, must be set to null.
        alt = null;
        selectedAlternative = null;
        return null;
    }

    /**
     * Checks if the alternative contains evaluation values. If yes, the user
     * should be asked for confirmation before removing it. If not, the
     * alternative is immediately removed.
     *
     * @see ObjectiveTree#hasValues(int[], Alternative)
     */
    public String askRemoveAlternative() {
        if (selectedAlternative == null || alternativeList.size() == 0) {
            allowRemove = -1;
            return null;
        }

        // We need the indices of all records to be able to check whether values
        // exist.
        // The array needs to be created here because the ObjectiveTree doesn't
        // know how many records exist!
        int records[] = new int[selectedPlan.getSampleRecordsDefinition()
                .getRecords().size()];
        for (int i = 0; i < records.length; i++) {
            records[i] = i;
        }
        Set<String> altName = new HashSet<String>(1); 
        altName.add(selectedAlternative.getName());
        if (selectedPlan.getTree().hasValues(records, altName)) {
            allowRemove = selectedAlternative.getId();
        } else {
            removeAlternative();
        }
        return null;
    }
    
    public List<PreservationActionDefinition> queryRegistry(FormatInfo formatInfo,
            PreservationActionRegistryDefinition registry) 
            throws PlatoServiceException 
    {
        IPreservationActionRegistry serviceLocator = null;
        try {
            serviceLocator = PreservationActionRegistryFactory.getInstance(registry);
        } catch (IllegalArgumentException e1) {
            throw new PlatoServiceException( "Registry:  " + registry.getShortname() + " has changed and needs to be reconfigured.");
        }
        if (serviceLocator == null) {
            throw new PlatoServiceException ("Failed to access " + registry.getShortname());
        }
        // query the registry
        return serviceLocator.getAvailableActions(formatInfo);
    }

    /**
     * Looks up the <code>registry</code> for preservation services
     * that can handle objects which are of the same type as the first sample record.
     */
    public String showPreservationServices(Object registry) {
        // get first sample with data 
        SampleObject sample = selectedPlan.getSampleRecordsDefinition().getFirstSampleWithFormat();
        if (sample == null) {
            return null;
        }
        FormatInfo formatInfo = sample.getFormatInfo();

        PreservationActionRegistryDefinition reg = (PreservationActionRegistryDefinition) registry;
        try {
            availableActions.clear();
            List<PreservationActionDefinition> actions = queryRegistry(formatInfo, reg);
            availableActions.addAll(actions);
            // this registry is responsible for the result - mark it as selected
            for (PreservationActionRegistryDefinition r: registrySelection.keySet()) {
                registrySelection.put(r, (registry == r) );
            }
        } catch (PlatoServiceException e) {
            log.error(e.getMessage(),e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "Failed to look up services of: " + reg.getShortname()
                    + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Looks up the <code>registry</code> for preservation services
     * that can handle objects which are of the same type as the first sample record.
     */
//    public String showPreservationServices(Object registry) {
//        // get first sample with data 
//        SampleObject sample = selectedPlan.getSampleRecordsDefinition().getFirstSampleWithFormat();
//        if (sample == null) {
//            return null;
//        }
//        FormatInfo formatInfo = sample.getFormatInfo();
//
//        availableActions.clear();
//
//        // get the service locator for the selected registry
//        PreservationActionRegistryDefinition reg = (PreservationActionRegistryDefinition) registry;
//  
//        IPreservationActionRegistry serviceLocator = null;
//        try {
//            serviceLocator = PreservationActionRegistryFactory.getInstance(reg);
//        } catch (IllegalArgumentException e1) {
//            FacesMessages.instance().add(
//                    FacesMessage.SEVERITY_INFO,
//                    "Registry:  " + reg.getShortname()
//                            + " has changed and needs to be reconfigured.");
//            log.error( "Registry:  " + reg.getShortname() + " has changed and needs to be reconfigured.");
//            return null;
//        }
//        try {
//            if (serviceLocator == null) {
//                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
//                        "Failed to access " + reg.getShortname());
//                log.error("Failed to access " + reg.getShortname());
//                return null;
//            }
//            // query the registry
//            List<PreservationActionDefinition> actions = serviceLocator
//                    .getAvailableActions(formatInfo);
//            if ((actions == null) || (actions.size() == 0))
//                // could not find a matching service - something went wrong
//                registryInfoMsg = serviceLocator.getLastInfo();
//            else {
//                /*
//                 * populate the list of available services
//                 */
//                for (PreservationActionDefinition definition : actions) {
//                    availableActions.add(definition);
//
//                }
//            }
//        } catch (PlatoServiceException e) {
//            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
//                    "Failed to look up services of: " + reg.getShortname() + ": " + e.getMessage());
//            log.error("Failed to look up services of: " + reg.getShortname() + ": " + e.getMessage(), e.getCause());
//        }
//
//        // this registry is responsible for the result - mark it as selected
//        for (PreservationActionRegistryDefinition r: registrySelection.keySet()) {
//            registrySelection.put(r, (registry == r) );
//        }
//        return null;
//    }

    /**
     * Creates an alternative for each selected preservation service
     * and adds it to the list of alternatives.
     */
    public String createAlternativesForPreservationActions() {
        availableActions.size();
        /*
         * mark project as changed so the user has to save or discard the
         * project before exporting
         */
        changed = "T";

        for (PreservationActionDefinition action : availableActions) {
            if (action.isSelected()) {
                /*
                 * Create a new alternative for this service
                 */
                String uniqueName = selectedPlan.getAlternativesDefinition().createUniqueName(action.getShortname());
                Alternative a = Alternative.createAlternative(uniqueName, action);
                
                // and add it to the preservation planning project
                selectedPlan.getAlternativesDefinition().addAlternative(a);
            }
        }

        // refresh the datamodel list
        alternativeList = selectedPlan.getAlternativesDefinition()
                .getAlternatives();
        // the alternativesdefinition has been changed
        selectedPlan.getAlternativesDefinition().touch();

        return null;
    }

    @Override
    @RaiseEvent("reload")
    public String discard() {
        String result = super.discard();
        init();
        return result;
    }

    @Destroy
    @Remove
    public void destroy() {
    }

    public int getAllowRemove() {
        return allowRemove;
    }
}
