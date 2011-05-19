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
package eu.planets_project.pp.plato.action.fte;

import java.util.ArrayList;
import java.util.List;

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
import eu.planets_project.pp.plato.action.interfaces.IDefineSampleRecords;
import eu.planets_project.pp.plato.action.interfaces.IEvaluateExperiments;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackAnalyseResults;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackEvaluateAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IRunExperiments;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.action.workflow.AbstractWorkflowStep;
import eu.planets_project.pp.plato.action.workflow.DefineBasisAction;
import eu.planets_project.pp.plato.action.workflow.EvaluateExperimentsAction;
import eu.planets_project.pp.plato.bean.ExperimentStatus;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.EvaluationStatus;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryDefinition;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryFactory;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;
import eu.planets_project.pp.plato.validators.TreeValidator;

/**
 * Action handler for the second step of Fast-track evaluation: 
 * Evaluate actions 
 * @author cbu
 */

@Stateful
@Scope(ScopeType.SESSION)
@Name("FTevaluate")
public class EvaluateAlternativesFastTrack extends AbstractWorkflowStep
        implements IFastTrackEvaluateAlternatives, INodeValidator {

    private static final long serialVersionUID = 7418877295595116067L;
    
    private static final Log log = PlatoLogger
            .getLogger(DefineBasisAction.class);

    @In(create = true)
    IFastTrackAnalyseResults FTanalyse;

    @SuppressWarnings("unused")
    @DataModel
    private List<Alternative> alternativesList;

    @In(create = true)
    IRunExperiments runexperiments;

    @DataModelSelection
    private Alternative selectedAlternative;

    @In(required=false)
    @Out(required=false)
    private ExperimentStatus experimentStatus = new ExperimentStatus();

    @In(create = true)
    IEvaluateExperiments evalexperiments;

    @In(create = true)
    IDefineAlternatives defineAlternatives;
    
    @In(create=true)
    IDefineSampleRecords defineSampleRecords;
    
    @Out(required = false)
    @In(required = false)
    private List<Leaf> leaves;
    
    public EvaluateAlternativesFastTrack() {
        requiredPlanState = new Integer(PlanState.FTE_REQUIREMENTS_DEFINED);
    }

    @Override
    protected void init() {
        alternativesList = selectedPlan.getAlternativesDefinition()
                .getAlternatives();
        if (leaves == null) {
            leaves = new ArrayList<Leaf>();
        }
        // we only use automated services in FTE.
        // If we don't have any services, we query the list.
        // Open issue - if you want to add one service, at the moment you would
        // have to remove all and then enter again.
        // Not so terribly bad for now.
        if (selectedPlan.getAlternativesDefinition().getAlternatives().size() == 0) {
            queryServices();
        }
        runexperiments.init();
        evalexperiments.init();

        //
        // We have to make sure that the value map of our leaves is properly initialized. If not, we have to 
        // call initValues to enable evaluation results to be stored for the requirements. In the 'normal' 
        // workflow initValues is called in RunExperimentsAction.save, so from there on everything is in order. 
        // In FTE we can't do that
        //
        boolean valueMapProperlyInitialized = 
            selectedPlan.getTree().getRoot().isValueMapProperlyInitialized(selectedPlan.getAlternativesDefinition().getConsideredAlternatives(), selectedPlan.getSampleRecordsDefinition().getRecords().size());
        
        if (!valueMapProperlyInitialized) {
            selectedPlan.getTree().initValues(
                    selectedPlan.getAlternativesDefinition().getConsideredAlternatives(),
                    selectedPlan.getSampleRecordsDefinition().getRecords()
                            .size());
        }
    }

    /**
     * constructs a list of automated services
     */
    private void queryServices() {

        List<PreservationActionRegistryDefinition> allRegistries = PreservationActionRegistryFactory
                .getAvailableRegistries();

        //List<PreservationActionRegistryDefinition> registries = new ArrayList<PreservationActionRegistryDefinition>();

        // get first sample with data
        SampleObject sample = selectedPlan.getSampleRecordsDefinition()
                .getFirstSampleWithFormat();
        if (sample == null) {
            return;
        }
        FormatInfo formatInfo = sample.getFormatInfo();

        for (PreservationActionRegistryDefinition reg : allRegistries) {
            if (reg.getShortname().contains("MiniMEE")
            ) {
                try {
                    List<PreservationActionDefinition> actions = defineAlternatives
                            .queryRegistry(formatInfo, reg);
                    /*
                     * populate the list of available services
                     * TODO what about adding planets and filtering
                     * services according to "sensible" target formats
                     * (e.g. images: png,tiff,jp2,jpg,dng) ?
                     */
                    for (PreservationActionDefinition definition : actions) {
                        Alternative a = Alternative.createAlternative(
                                selectedPlan.getAlternativesDefinition().createUniqueName(definition.getShortname()),
                                definition);
                        // and add it to the preservation planning project
                        selectedPlan.getAlternativesDefinition()
                                .addAlternative(a);
                    }
                } catch (PlatoServiceException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        alternativesList = selectedPlan.getAlternativesDefinition()
                .getAlternatives();
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    @Override
    protected String getWorkflowstepName() {
        return "FTevaluate";
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    @Override
    protected IWorkflowStep getSuccessor() {
        return FTanalyse;
    }

    @Override
    public String save() {

        runexperiments.prepareAlternatives();
        runexperiments.prepareTempFileSaving();

        super.save(selectedPlan);
        changed = "";
        
        super.save(selectedPlan.getTree());
        
        doClearEm();
        init();
        return "";
    }
    
    protected boolean needsClearEm() {
        return true;
    }
    
    /**
     * @see eu.planets_project.pp.plato.util.INodeValidator#validateNode(eu.planets_project.pp.plato.model.TreeNode, java.util.List, java.util.List)
     */
    public boolean validateNode(TreeNode node, List<String> nodelist,
            List<TreeNode> nodes) {
        return node.isCompletelyEvaluated(nodelist, nodes, selectedPlan.getAlternativesDefinition().getConsideredAlternatives());
    }
    
    /**
     * @see EvaluateExperimentsAction#validate
     * 
     *  First we have to check if the experiments have been conducted. This is inevitable, otherwise validator.validate would 
     *  cause a NullPointerException because leaf.valueMap is empty. Rest of the validation is the same as in {@link EvaluateExperimentsAction.validate}
     *  Since validate is protected we cannot call IEvaluateExperiments.validate.
     */
    public boolean validate(boolean showValidationErrors) {
        
        EvaluationStatus evaluationStatus = selectedPlan.getTree().getRoot().getEvaluationStatus();
        
        if (evaluationStatus != EvaluationStatus.COMPLETE) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Experiments have not been conducted.");
            return false;
        }
        
        ITreeValidator validator = new TreeValidator();
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        
        boolean valid = validator.validate(selectedPlan.getTree().getRoot(),
                this, nodes, showValidationErrors);
        if (!valid) {
            if (leaves == null) {
                leaves = new ArrayList<Leaf>();
            } else {
                leaves.clear();
            }
            //All invalid leaves should be in the list so that they are displayed
            for (TreeNode node : nodes) {
                if (node.isLeaf()) {
                    this.leaves.add((Leaf) node);
                }
            }
        }
        
        return valid;
    }

    public String removeAlternative() {
        selectedPlan.getAlternativesDefinition().removeAlternative(selectedAlternative);
        alternativesList = selectedPlan.getAlternativesDefinition().getAlternatives();
        selectedPlan.getAlternativesDefinition().touch();

        selectedPlan.getTree().removeValues(selectedAlternative);
        
        return null;
    }

    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * Runs experiments of all considered alternatives.
     * 
     */
    public void runAllExperiments() {
        
        leaves.clear();
        
        if (experimentStatus == null) {
            experimentStatus = new ExperimentStatus();
        }
        experimentStatus.experimentSetup(selectedPlan
                .getAlternativesDefinition().getAlternatives(), selectedPlan
                .getSampleRecordsDefinition().getRecords());
    }

    public void clearExperiments() {
        experimentStatus.clear();
        selectedPlan.getTree().initValues(
                selectedPlan.getAlternativesDefinition()
                        .getConsideredAlternatives(),
                selectedPlan.getSampleRecordsDefinition().getRecords().size());
        
    }

    /**
     * We have the rule that all evaluation settings have to be either changed or confirmed once
     * by the user.
     * This approve function makes it easier to confirm the settings for many leaves at once - 
     * It touches all currently displayed leaves so that they are marked as confirmed.
     * 
     * We have to duplicate this from @link {@link EvaluateExperimentsAction}
     * since the {@link #leaves} is dupliacted too.
     * @see eu.planets_project.pp.plato.model.values.Value#touch()
     */
    public void approve() {
        for (Leaf leaf : leaves) {
            for (Values values : leaf.getValueMap().values()) {
                for (Value value : values.getList()) {
                    value.touch();
                }
            }
        }
    }
    
    public void evaluateAll() {

        runexperiments.prepareTempFileSaving();
        
        //
        // before evaluation we need to describe the sample records and results in xcdl, else
        // our xcdl evluators wouldn't work. xcdl characterisation is not performed automatically
        // upon upload. in the 'normal' workfow the user can choose to do xcdl characterisation.
        for (Alternative a: selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
            
            for (SampleObject so: selectedPlan.getSampleRecordsDefinition().getRecords()) {
                
                DigitalObject result = a.getExperiment().getResults().get(so);

                defineSampleRecords.characteriseXcdl(so);
                defineSampleRecords.characteriseXcdl(result);
            }
        }

        evalexperiments.evaluateAll();
    }
    
    @Override
    @RaiseEvent("reload")
    public String discard() {
        String result = super.discard();
        init();
        return result;
    }
    
}
