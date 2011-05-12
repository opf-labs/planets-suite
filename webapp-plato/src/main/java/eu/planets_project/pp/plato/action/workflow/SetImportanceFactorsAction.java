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

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.IAnalyseResults;
import eu.planets_project.pp.plato.action.interfaces.ISetImportanceFactorsAction;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.bean.ResultMap;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;
import eu.planets_project.pp.plato.validators.TreeValidator;

/**
 * Implements actions for workflow step 'Set Importance Factors'.
 *
 * The objective tree where the user can enter a weight for each node and leaf. When the
 * user enters a weight factor all other siblings are automatically adujusted so that the
 * sum equals to 1.0
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("importanceFactors")
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class SetImportanceFactorsAction extends AbstractWorkflowStep implements ISetImportanceFactorsAction, INodeValidator {

    /**
     * 
     */
    private static final long serialVersionUID = 931504115533301628L;

    private static final Log log = PlatoLogger.getLogger(SetImportanceFactorsAction.class);

    @In(create=true)
    private IAnalyseResults analyseResults;

    @Out(required = false)
    private HashMap<Leaf,ResultMap> results;
    
    /**
     * indicates whether a Mindmap that is uploaded contains unit definitions in the scales or not.
     */
    @Out(required = false)
    BooleanCapsule balanceWeights = new BooleanCapsule();


    public SetImportanceFactorsAction() {
        requiredPlanState = new Integer(PlanState.TRANSFORMATION_DEFINED);

        // we have to take special care of this member: it must be created instantly because
        // otherwise there would be an exception if method init is not called for some reason.
        // (@Out attribute requires non-null value)
        results = new HashMap<Leaf,ResultMap>();
    }

    @In(required=false)
    @Out(required=false)
    private Node otree;
    
    @Override
    @RaiseEvent("reload")
    public String discard() {
        String result = super.discard();
        resetFocus();
        return result;
    }
    
    public String resetFocus() {
        otree = (Node) selectedPlan.getTree().getRoot();
        return null;
    }
    
    public String focus(Object o) {
        otree = (Node) o;
        return null;
    }
     
    
    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    protected IWorkflowStep getSuccessor() {
        return analyseResults;
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        // The ObjectiveTree makes sure that weights that have already been initialized
        // aren't overwritten by this call.
        selectedPlan.getTree().initWeights();
        
        // fill our temporary result map with all the result values
        results = new HashMap<Leaf,ResultMap>();
        for (Leaf l : selectedPlan.getTree().getRoot().getAllLeaves()) {
            HashMap<String,Double> map = new HashMap<String,Double>();
            for (Alternative a: selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
                map.put(a.getName(), l.getResult(a));
            }
            ResultMap m = new ResultMap();
            m.setResults(map);
            results.put(l,m);
        }

        resetFocus();
    }

    /**
     * This method is called when auto-rebelancing the weights is clicked.
     */
    public String nothing() {
        return "";
    }

    /**
     * @see AbstractWorkflowStep#validate(boolean)
     */
    public boolean validate(boolean showValidationErrors) {
        // make sure that the sum of all subnodes/leaves of each node is 1.0
        ITreeValidator validator = new TreeValidator();
        // We don't need the last parameter because all nodes are expanded anyway
        return validator.validate(selectedPlan.getTree().getRoot(), this, null, showValidationErrors);

    }

    /**
     * @see INodeValidator#validateNode(TreeNode, List, List)
     */
    public boolean validateNode(TreeNode node, List<String> errormessages,
            List<TreeNode> nodes) {
        return node.isCorrectlyWeighted(errormessages);
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
        return "importanceFactors";
    }
}
