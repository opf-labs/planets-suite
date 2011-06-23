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
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.ISetImportanceFactorsAction;
import eu.planets_project.pp.plato.action.interfaces.ITransformMeasuredValues;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.transform.NumericTransformer;
import eu.planets_project.pp.plato.model.transform.Transformer;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;
import eu.planets_project.pp.plato.validators.TreeValidator;

/**
 * Implements actions for workflow step 'Transform Measured Values. The user can enter
 * transformations for each leaf selected from the displayed objective tree.
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("transform")
public class TransformMeasuredValuesAction extends AbstractWorkflowStep implements ITransformMeasuredValues, INodeValidator {

    /**
     * 
     */
    private static final long serialVersionUID = -4802766805202200359L;

    private static final Log log = PlatoLogger.getLogger(TransformMeasuredValuesAction.class);

    @In(create = true)
    private ISetImportanceFactorsAction importanceFactors;

    /**
     * sets (primitive) default values for all numeric and boolean transformers
     */
    public void calculateDefaultTransformers() {
       selectedPlan.calculateDefaultTransformers();
    }
   
    public TransformMeasuredValuesAction() {
        requiredPlanState = new Integer(PlanState.RESULTS_CAPTURED);
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    protected IWorkflowStep getSuccessor() {
        return importanceFactors;
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
       initLeafLists();
       
       // initialising the values for free text transformers
       for (Leaf l:selectedPlan.getTree().getRoot().getAllLeaves()) {
           l.initTransformer();
       }

    }

    /**
     * Leaves displayed to the user.
     */
    @Out(required = false)
    @In(required=false)
    List<Leaf> leaves;

    /**
     * Leaves displayed to the user.
     */
    @Out(required = false)
    @In(required=false)
    List<Leaf> errorleaves;

    @Override
    public String save() {
        logTransformers();
        return super.save();
    }
    
    /**
     * Node/Leaf selected by the user from the objective tree. All leaves under that node
     * will be subsequently displayed to the user for input request.
     */
    public String select(Object ob) {
        initErrorLeaves();
        log.debug("Select Called with: " + ob.toString());

        if (ob instanceof Node) {
            log.debug("Setting all Leaves");
            leaves = ((Node) ob).getAllLeaves();
        }else if (ob instanceof Leaf) {
            log.debug("Setting leaf: " + ob.toString());
            
            leaves.clear();
            leaves.add((Leaf) ob);

        }
        logTransformers();
        return null;
    }

    /**
     * @param t
     */
    private void logTransformer(String sessionID, String leafName, Transformer t) {
        if (t == null) {
            log.error("TRANSFORMER NULL at "+leafName+" IN SESSION "+sessionID);
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(sessionID).append(":").append(leafName);
        if (t instanceof NumericTransformer) {
            NumericTransformer nt = (NumericTransformer) t;
            sb.append("::NUMERICTRANSFORMER:: ");
            sb.append(nt.getThreshold1()).append(" ")
              .append(nt.getThreshold2()).append(" ")
              .append(nt.getThreshold3()).append(" ")
              .append(nt.getThreshold4()).append(" ")
              .append(nt.getThreshold5());
            log.debug(sb.toString());
        }
    }

   

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @Override
    @RaiseEvent("reload")
    public String discard() {
        logTransformers();
        String result = super.discard();
        logTransformers();
        init();
        return result;
    }

    private void logTransformers() {
        String id = "";
        try {
            id = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest()).getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }
        for (Leaf leaf : leaves) {
            logTransformer(id,leaf.getName(), leaf.getTransformer());
        }
        for (Leaf leaf : errorleaves) {
            logTransformer(id,leaf.getName(),leaf.getTransformer());
        }
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
        boolean validate = validator.validate(selectedPlan.getTree().getRoot(),
                this, nodes, showValidationErrors);
        if (!validate) {
            initLeafLists();
            //All non-validating leaves should be in the list nodes
            for (TreeNode node : nodes) {
                if (node.isLeaf()) {
                    this.errorleaves.add((Leaf) node);
                }
            }
        }
        return validate;
       
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
     * @see eu.planets_project.pp.plato.util.INodeValidator#validateNode(eu.planets_project.pp.plato.model.TreeNode, java.util.List, java.util.List)
     */
    public boolean validateNode(TreeNode node, List<String> errormessages, List<TreeNode> nodes) {
        return node.isCompletelyTransformed(errormessages, nodes);
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    protected String getWorkflowstepName() {
        return "transform";
    }

    /**
     * Touches all leaves.
     *
     * @see eu.planets_project.pp.plato.model.values.Value#touch()
     */
    public void approve() {
        for (Leaf leaf : leaves) {
            //logTransformer(leaf.getName(),leaf.getTransformer());
            leaf.getTransformer().touch();
        }
        for (Leaf leaf : errorleaves) {
            //logTransformer(leaf.getName(),leaf.getTransformer());
            leaf.getTransformer().touch();
        }
        logTransformers();
    }
}
