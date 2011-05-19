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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IEvaluateExperiments;
import eu.planets_project.pp.plato.action.interfaces.ITransformMeasuredValues;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.evaluation.IActionEvaluator;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.evaluation.MiniRED;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.EvaluationStatus;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.characterisation.jhove.JHoveAdaptor;
import eu.planets_project.pp.plato.services.characterisation.jhove.tree.JHoveTree;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;
import eu.planets_project.pp.plato.validators.TreeValidator;

/**
 * Implements actions for workflow step 'Evaluate Experiments', i.e. enable the user
 * to enter the evaluation result per alternative and sample record. The user has to enter
 * evaluation result for all leaves in the objective tree.
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("evalexperiments")
public class EvaluateExperimentsAction extends AbstractWorkflowStep implements
        IEvaluateExperiments, INodeValidator, IStatusListener{
    /**
     * 
     */
    private static final long serialVersionUID = -3892845379219070784L;


    protected boolean needsClearEm() {
        return true;
    }
    private static final Log log = PlatoLogger.getLogger(EvaluateExperimentsAction.class);

    private StringBuffer evaluationLogBuffer;
    
    @In(create = true)
    ITransformMeasuredValues transform;


    @SuppressWarnings("unused")
    @Out(required=false)
    private JHoveTree jhoveTree1;
    
    @SuppressWarnings("unused")
    @Out(required=false)
    private JHoveTree jhoveTree2;
    
    
    /**
     * Creates from the SampleObject object, retrieved by injection, a Tree with all the
     * characteristics extracted from Jhove
     * 
     */
    public JHoveTree characteriseJHove(DigitalObject object) {
        JHoveTree jhoveTree = new JHoveTree();
        
        //returns an empty tree for null
        if(object==null) {
            return jhoveTree;
        }

        if (object.getJhoveXMLString() == null || "".equals(object.getJhoveXMLString())) {
            object.setJhoveXMLString(new JHoveAdaptor().describe(object));
        } 
        if(object.getJhoveXMLString()!=null && !"".equals(object.getJhoveXMLString())) {
            jhoveTree= new JHoveAdaptor().digestString(object.getFullname(),object.getJhoveXMLString());
        }
        
        return jhoveTree;
    }  

    /**
     * the node currently selected in the objective tree.
     */
    @In(required = false)
    @Out(required = false)
    TreeNode node;
    
    
    public EvaluateExperimentsAction() {
        requiredPlanState = new Integer(PlanState.EXPERIMENT_PERFORMED);
    }

    /**
     * List of all leaves for which the evaluation settings shall be displayed to the user. 
     */
    @Out(required = false)
    @In(required = false)
    List<Leaf> leaves;
    
    @Out
    private List<MeasurableProperty> measurableProperties = new ArrayList<MeasurableProperty>();

    /**
     * Leaves displayed to the user.
     */
    @Out(required = false)
    @In(required=false)
    List<Leaf> errorleaves;

    @Out
    private BooleanCapsule hasAutomatedMeasurements = new BooleanCapsule();
    
    protected IWorkflowStep getSuccessor() {
        return transform;
    }

    /**
     * initialises the values in the tree and inits/clears the leaf list
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        clearLogBuffer();
        initLeafLists();
        boolean xcl =  false;
        hasAutomatedMeasurements.setBool(false);
        Iterator<Leaf> iter = selectedPlan.getTree().getRoot().getAllLeaves().iterator(); 
        while (iter.hasNext()) {
            Leaf l = iter.next();
            if (l.isMapped()) {
                hasAutomatedMeasurements.setBool(true);
                
                if (l.getMeasurementInfo().getProperty().getName().startsWith("xcl/")) {
                    xcl = true;
                }
            }
        }
        if (xcl)  {
            for (DigitalObject o : selectedPlan.getSampleRecordsDefinition().getRecords()) {
                if ((o.getXcdlDescription() == null)|| !o.getXcdlDescription().isDataExistent()) {
                        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                           "Some XCL descriptions for samples are missing, XCL comparison will not work for these samples. ");
                        break;
                }
            }
            for (Alternative a: selectedPlan.getAlternativesDefinition().getAlternatives()) {
                for (DigitalObject r: a.getExperiment().getResults().values()) {
                    if ((r.getXcdlDescription() == null)|| !r.getXcdlDescription().isDataExistent()) {
                        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                           "XCL descriptions for experiment results of "+ a.getName()  +" are missing, " +
                           "XCL comparison will not work for these objects. ");
                        break;
                    }
                }
            }
        }
        
        // TODO for now this is ALWAYS reloaded when entering,
        // but should be cached in the future.
        MiniRED.getInstance().reloadEvaluators();         
        refreshMeasurableProperties();
        
    }

    private void clearLogBuffer() {
        evaluationLogBuffer = new StringBuffer();
        Contexts.getEventContext().remove("evaluationMessage");
    }

    /**
     * @param record sample object the alternative has been carried out on
     * @param alternative alternative which has been carried out on the alternative (we determine the result object from the alternative)
     */
    public void setTreeFromRecordAltern(Object record, Object alternative){
        
        if (!(record instanceof SampleObject) || !(alternative instanceof Alternative)) {
            return;
        }
        
        Alternative a = (Alternative)alternative;
        SampleObject o = (SampleObject)record;
        
        o = em.merge(o);
        jhoveTree1=characteriseJHove(o);
        
        // get the result 
        // we have to merge the sample object back into the session to be able to access the byte stream
        DigitalObject result = em.merge(a.getExperiment().getResults().get(record));
        a.getExperiment().getResults().put(o, result);
        
        jhoveTree2=characteriseJHove(result);     
    }
    
    /**
     * Select a node or leaf from the tree.
     */
    public String select(Object ob) {
        initErrorLeaves();
        log.trace("Select Called with: " + ob.toString());
        if (ob instanceof Node) {
            log.debug("Setting all Leaves");
            leaves = ((Node) ob).getAllLeaves();
        } else if (ob instanceof Leaf) {
            log.debug("Setting leaf: " + ob.toString());
            leaves.clear();
            leaves.add((Leaf) ob);
        }
        return null;
    }

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @Override
    @RaiseEvent("reload")
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
     * @see eu.planets_project.pp.plato.action.workflow.AbstractWorkflowStep#validate()
     */
    public boolean validate(boolean showValidationErrors) {
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

    /**
     * @see eu.planets_project.pp.plato.util.INodeValidator#validateNode(eu.planets_project.pp.plato.model.TreeNode, java.util.List, java.util.List)
     */
    public boolean validateNode(TreeNode node, List<String> nodelist,
            List<TreeNode> nodes) {
        return node.isCompletelyEvaluated(nodelist, nodes, selectedPlan.getAlternativesDefinition().getConsideredAlternatives());
    }

    /**
     * We have the rule that all evaluation settings have to be either changed or confirmed once
     * by the user.
     * This approve function makes it easier to confirm the settings for many leaves at once - 
     * It touches all currently displayed leaves so that they are marked as confirmed.
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

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    protected String getWorkflowstepName() {
        return "evalexperiments";
    }



    /**
     * evaluates the given leaves automatically.
     * This is only possible for criteria, where information on the measurement has been defined.
     * The registered evaluators are applied one after an other, 
     * if an evaluator is able to measure a criterion, its value is applied and the criterion is excluded from further evaluation.
     * 
     * First per alternative all action related evaluators are called.
     * 
     * Then per alternative, for each sample object, all object/runtime related evaluators are called. 
     * 
     * @param leaves
     */
    private void evaluateLeaves(List<Leaf> leaves) {
        clearLogBuffer();

        // we evaluate measurements and have to assign each result to the corresponding leaf: build a map
        HashMap<MeasurementInfoUri, Leaf> measurementOfLeaf = new HashMap<MeasurementInfoUri, Leaf>();

        // list of measurements which shall be evaluated
        List<MeasurementInfoUri> allMeasurementsToEval = new LinkedList<MeasurementInfoUri>();

        for(Leaf l : leaves) {
            // measure this criterion automatically
            MeasurementInfoUri m = l.getMeasurementInfo().toMeasurementInfoUri();
            if ((m != null) && (m.getAsURI() != null)) {
                measurementOfLeaf.put(m , l);
               allMeasurementsToEval.add(m);
            }
        }            

        try {
            // start evaluation:
            List<MeasurementInfoUri> measurementsToEval = new ArrayList<MeasurementInfoUri>(); 
            // first action evaluators  
            List<IActionEvaluator> actionEvaluators = MiniRED.getInstance().getActionEvaluationSequence();
            for (Alternative alternative : selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
                // we want to evaluate each property only once, by the evaluator with the highest priority
                measurementsToEval.clear();
                measurementsToEval.addAll(allMeasurementsToEval);
                for (IActionEvaluator evaluator : actionEvaluators) {
                    Map<MeasurementInfoUri, Value> results = evaluator.evaluate(alternative, measurementsToEval, this);
                    // apply all results
                    for (MeasurementInfoUri m : results.keySet()) {
                        Value value = results.get(m);
                        if (value != null) {
                            Leaf l = measurementOfLeaf.get(m);
                            value.setScale(l.getScale());
                            l.getValues(alternative.getName()).setValue(0,value);
                        }
                    }
                    // exclude evaluated leaves from further evaluation
                    measurementsToEval.removeAll(results.keySet());
                }
            }
            // then object evaluators
            List<IObjectEvaluator> objEvaluators = MiniRED.getInstance().getObjectEvaluationSequence();
            for (Alternative alternative : selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
                // .. for all alternatives
                List<SampleObject> samples = selectedPlan.getSampleRecordsDefinition().getRecords();
                for (int i = 0; i < samples.size(); i++){
                    // we want to evaluate each property only once, by the evaluator with the highest priority
                    measurementsToEval.clear();
                    measurementsToEval.addAll(allMeasurementsToEval);
                    

                    
                    for (IObjectEvaluator evaluator : objEvaluators) {
                        DigitalObject r = alternative.getExperiment().getResults().get(samples.get(i));
                        DigitalObject r2 = (r == null ? null : em.merge(r));
                        try {
                            Map<MeasurementInfoUri, Value> results = evaluator.evaluate(
                                    alternative, 
                                    em.merge(samples.get(i)), 
                                    r2, 
                                    measurementsToEval,
                                    this);
                            // apply all results
                            for (MeasurementInfoUri m : results.keySet()) {
                                Value value = results.get(m);
                                if (value != null) {
                                    Leaf l = measurementOfLeaf.get(m);
                                    value.setScale(l.getScale());
                                    // add evaluation result for the current result-object!
                                    l.getValues(alternative.getName()).setValue(i, value);
                                }
                            }
                            // exclude evaluated leaves from further evaluation
                            measurementsToEval.removeAll(results.keySet());
                        } catch (Exception e) {
                            log.error("evaluator failed" + e.getMessage(),e);
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Automated evaluation threw exception "+e.getMessage(),e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Automated evaluation failed:"+ e.getMessage());
            updateStatus("Automated evaluation threw exception "+e.getMessage());
        }
        Contexts.getEventContext().set("evaluationMessage", evaluationLogBuffer.toString());
    }
    public void evaluateAll() {
        evaluateLeaves(selectedPlan.getTree().getRoot().getAllLeaves());
    }
    public void evaluate(Leaf leaf) {
        evaluateLeaves(Arrays.asList(leaf));
    }
    

    private void refreshMeasurableProperties() {
        measurableProperties.clear(); 
        measurableProperties.addAll(selectedPlan.getMeasurableProperties());
        for (MeasurableProperty p: measurableProperties) {
            log.debug("prop:: "+p.getName());
        }
    }

    public void updateStatus(String msg) {
        log.info(msg);
        evaluationLogBuffer.append(msg).append("\n");
    }
 
    @Override
    public String save() {
        // initialising the values for free text transformers
        for (Leaf l:selectedPlan.getTree().getRoot().getAllLeaves()) {
            l.initTransformer();
        }
        return super.save();
    }

    /**
     * 
     */
    private void initErrorLeaves() {
        if(errorleaves == null){
            errorleaves = new ArrayList<Leaf>();
        } else  {
            errorleaves.clear();
        }
    }

    /**
     * 
     */
    private void initLeafLists() {
        if(leaves == null){
            leaves = new ArrayList<Leaf>();
        } else {
            leaves.clear();
        }
        initErrorLeaves();
    }
    
}