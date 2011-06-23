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

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import eu.planets_project.pp.plato.action.interfaces.IValidatePlan;
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
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Scope(ScopeType.SESSION)
@Name("validatePlan")
public class ValidatePlanAction extends AbstractWorkflowStep implements IValidatePlan {

    /**
     * 
     */
    private static final long serialVersionUID = 2574623883162544803L;

    /**
     * for display on the page.
     */
    @Out
    private String planetsExecutablePlanPrettyFormat = "";

    /**
     * for display on the page.
     */
    @Out
    private String eprintsExecutablePlanPrettyFormat = "";
    
    private static final Log log = PlatoLogger.getLogger(ValidatePlanAction.class);

    public ValidatePlanAction() {
        requiredPlanState = new Integer(PlanState.PLAN_DEFINED);
    }

    protected String getWorkflowstepName() {
        return "validatePlan";
    }
    
    /**
     * Approves this plan and touches it.
     */
    public String approve() {
        selectedPlan.touch();
        proceed();
        return null;
    }
    
    /**
     * sets the plan status back to not-approved, i.e. {@link PlanState#PLAN_DEFINED}
     */
    public String revise() {
        selectedPlan.getState().setValue(PlanState.PLAN_DEFINED);
        selectedPlan.touch();
        super.save();
        return null;
    }
    
    protected IWorkflowStep getSuccessor() {
        return null;
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

    protected void init() {
        log.debug("initialising validatePlan");
        
        planetsExecutablePlanPrettyFormat = "";
        eprintsExecutablePlanPrettyFormat = "";
            
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
                    acceptableAlternatives);
        }
        
        
        planetsExecutablePlanPrettyFormat = formatExecutablePlan(selectedPlan.getExecutablePlanDefinition().getExecutablePlan());
        eprintsExecutablePlanPrettyFormat = formatExecutablePlan(selectedPlan.getExecutablePlanDefinition().getEprintsExecutablePlan());
    }

    /**
     * reads the executable preservation plan and formats it.
     * 
     */
    private String formatExecutablePlan(String executablePlan) {

        if (executablePlan == null || "".equals(executablePlan)) {
            return "";
        }
        
        try {
            Document doc = DocumentHelper.parseText(executablePlan);
            
            StringWriter sw = new StringWriter();
            
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewlines(true);
            format.setTrimText(true);
            format.setIndent("  ");
            format.setExpandEmptyElements(false);
            format.setNewLineAfterNTags(20);
            
            XMLWriter writer = new XMLWriter(sw, format);
            
            writer.write(doc);
            writer.close();
            
            return sw.toString();
            
        } catch (DocumentException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    @Out
    private List<Alternative> acceptableAlternatives = new ArrayList<Alternative>();

    @Out
    private BooleanCapsule displayChangelogs = new BooleanCapsule(false);

    @Out
    private BooleanCapsule displayEvalTransform = new BooleanCapsule(false);

    
    private IAggregator sumAggregator = new WeightedSum();

    @DataModel
    private Map<Trigger,String> selectedTriggers;

    @DataModel
    private Map<Trigger,String> reevalSelectedTriggers;

    @Out
    private List<ReportLeaf> leafBeans = new ArrayList<ReportLeaf>();

    @Out(required=false)
    private ResultNode sumNode;

    @Out(required=false)
    private ResultNode multNode;

    public String switchDisplayChangelogs() {
        this.displayChangelogs.setBool(!this.displayChangelogs.isBool());
        return null;
    }
    
    public String switchDisplayEvalTransform() {
        this.displayEvalTransform.setBool(!this.displayEvalTransform.isBool());
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

    @Out
    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }
}
