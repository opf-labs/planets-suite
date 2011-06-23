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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IAnalyseResults;
import eu.planets_project.pp.plato.action.interfaces.ICreateExecutablePlan;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.bean.ReportLeaf;
import eu.planets_project.pp.plato.bean.ResultNode;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.Trigger;
import eu.planets_project.pp.plato.model.aggregators.IAggregator;
import eu.planets_project.pp.plato.model.aggregators.WeightedMultiplication;
import eu.planets_project.pp.plato.model.aggregators.WeightedSum;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.sensitivity.OrderChangeCountTest;
import eu.planets_project.pp.plato.sensitivity.SimpleIterativeWeightModifier;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Scope(ScopeType.SESSION)
@Name("analyseResults")
public class AnalyseResultsAction extends AbstractWorkflowStep implements
        IAnalyseResults {

    /**
     * 
     */
    private static final long serialVersionUID = -4779937576474488010L;

    private static final Log log = PlatoLogger
            .getLogger(AnalyseResultsAction.class);

    public AnalyseResultsAction() {
        requiredPlanState = new Integer(PlanState.WEIGHTS_SET);
    }

    protected String getWorkflowstepName() {
        return "analyseResults";
    }

    @In(create = true)
    ICreateExecutablePlan createExecutablePlan;

    @Out(required = false)
    private String[] recommendationAlternatives;

    @In(required = false)
    @Out(required = false)
    private String selectedRecommendation;
    
    @Out(required = false)
    private ResultNode multNode;
    
    @Out(required = false)
    private ResultNode sumNode;
    

    protected IWorkflowStep getSuccessor() {
        return createExecutablePlan;
    }

    /**
     * Downloads a {@link eu.planets_project.pp.plato.model.DigitalObject} with the help of
     * {@link eu.planets_project.pp.plato.util.Downloader}
     *
     * @param upload file that shall be downloaded
     */
    public void download(Object object) {
        if (object instanceof DigitalObject) {
            DigitalObject o = em.merge((DigitalObject)object); 
            Downloader.instance().download(o); 
        }
    }

    public void init() {
        log.debug("initialising analyseResult");
        this.acceptableAlternatives.clear();

        if (leafBeans == null) {
            leafBeans = new ArrayList<ReportLeaf>();
        } else {
            leafBeans.clear();
        }

        for (Leaf l : this.selectedPlan.getTree().getRoot().getAllLeaves()) {
            this.leafBeans.add(new ReportLeaf(l, this.selectedPlan.getAlternativesDefinition().getConsideredAlternatives()));
            
            l.initTransformer();
        }

        /*
         * Set roots and fill result-beans of the Multiplication- and Sum-Trees.
         */
        if (this.selectedPlan.getState().getValue() >= PlanState.TRANSFORMATION_DEFINED) {
            multNode = new ResultNode(selectedPlan.getTree().getRoot(),
                    new WeightedMultiplication(), selectedPlan.getAlternativesDefinition().getConsideredAlternatives());

            for (Alternative a: this.selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
                Double d= multNode.getResults().get(a.getName());
                if (d > 0.0) {
                    this.acceptableAlternatives.add(a);
                }
            }

            sumNode = new ResultNode(selectedPlan.getTree().getRoot(),
                    sumAggregator,
                    selectedPlan.getAlternativesDefinition().getConsideredAlternatives());
        }

        this.recommendationAlternatives = new String[acceptableAlternatives.size()];

        int i = 0;
        for (Alternative a : this.acceptableAlternatives) {
            this.recommendationAlternatives[i++] = a.getName();
        }

        // we select the stored recommendation
        if (this.selectedPlan.getRecommendation().getAlternative() != null) {
            this.selectedRecommendation = this.selectedPlan.getRecommendation()
                    .getAlternative().getName();
        }

        analyseSensitivity();

    }
    @Out
    private List<Alternative> acceptableAlternatives = new ArrayList<Alternative>();

    @Out
    private BooleanCapsule displayChangelogs = new BooleanCapsule(false);


    private IAggregator sumAggregator = new WeightedSum();

    @DataModel
    private Map<Trigger,String> selectedTriggers;

    @Out
    private List<ReportLeaf> leafBeans = new ArrayList<ReportLeaf>();

    public String switchDisplayChangelogs() {
        this.displayChangelogs.setBool(!this.displayChangelogs.isBool());
        return null;
    }


    public String save() {

        for (Alternative a : selectedPlan.getAlternativesDefinition()
                .getConsideredAlternatives()) {
            if (a.getName().equals(selectedRecommendation)) {
                selectedPlan.getRecommendation().setAlternative(a);
                break;
            }
        }

        save(selectedPlan.getRecommendation());

        changed = "";
        return null;
    }

    public boolean validate(boolean showValidationErrors) {

        if (selectedRecommendation == null) {
            if (showValidationErrors) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "You have to select a recommendation to proceed with the workflow.");
                return false;
            }
        }

        return true;
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

    @Destroy
    @Remove
    public void destroy() {
    }

    public String getSelectedRecommendation() {
        return selectedRecommendation;
    }

    public void setSelectedRecommendation(String selectedRecommendation) {
        this.selectedRecommendation = selectedRecommendation;
    }

    @Out
    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }

    public String analyseSensitivity() {
        long start = System.currentTimeMillis();
        // FIXME HK reintroduce SENSITIVITY analysis for large trees - Plato 3.1
        if (selectedPlan.getTree().getRoot().getAllLeaves().size() < 40) {
            log.debug("Starting sensitivity analysis ... " );
            sumNode.analyseSensitivity(
                    new SimpleIterativeWeightModifier(),
                    new OrderChangeCountTest(selectedPlan.getTree()
                            .getRoot(), sumAggregator, acceptableAlternatives));
            log.debug("Sensitivity analysis took: " + (System.currentTimeMillis() - start) + "ms.");
        } else {
            log.debug("Sensitivity analysis NOT CONDUCTED: Too many leaves.");
        }
        return "success";
    }    
}
