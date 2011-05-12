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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.action.interfaces.IDefineSampleRecords;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackDefineRequirements;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackEvaluateAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IIdentifyRequirements;
import eu.planets_project.pp.plato.action.interfaces.IProjectImport;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.action.project.LoadPlanAction;
import eu.planets_project.pp.plato.action.workflow.AbstractWorkflowStep;
import eu.planets_project.pp.plato.bean.FastTrackTemplate;
import eu.planets_project.pp.plato.bean.FastTrackTemplates;
import eu.planets_project.pp.plato.bean.TreeHelperBean;
import eu.planets_project.pp.plato.evaluation.MiniRED;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.model.tree.TemplateTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.xml.TreeLoader;

/**
 * Action handler for the first step of Fast-track evaluation: Define requirements
 * @author cbu
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("FTrequirements")
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class DefineRequirementsFastTrack 
extends AbstractWorkflowStep 
implements IFastTrackDefineRequirements {
    
    private static final long serialVersionUID = -5840252761666399020L;
    
    private static final Log log = PlatoLogger.getLogger(DefineRequirementsFastTrack.class);
    
    @In
    EntityManager em;
    
    @In(create=true)
    private LoadPlanAction loadPlan;
    
    @In(create=true)
    IFastTrackEvaluateAlternatives FTevaluate;
    
    @In(create=true)
    IIdentifyRequirements identifyRequirements;
   
    @In(create=true)
    IDefineSampleRecords defineSampleRecords;
    
    @In (required=false)
    private User user;
    

    
    
    @Out(required = false)
    private List<FastTrackTemplate> fastTrackTemplateList = new ArrayList<FastTrackTemplate>();;
    
    @In(create = true)
    private FastTrackTemplates fastTrackTemplates;
    
    public DefineRequirementsFastTrack() {
        requiredPlanState = new Integer(PlanState.FTE_INITIALISED);
    }
    

    @Override
    protected void init() {
        defineSampleRecords.init();
        
        fastTrackTemplates.init();
    }
    
    public void startFastTrackEvaluation() {

    }
    
    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    @Override
    protected String getWorkflowstepName() {
        return "FTrequirements";
    }
    
    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    @Override
    protected IWorkflowStep getSuccessor() {
        return FTevaluate;
    }
    
    @Override
    protected void doClearEm() {
        super.doClearEm();
    }
    
   
        @Override
        public String save() {
        log.debug("Persisting plan " + selectedPlan.getPlanProperties().getName());
        selectedPlan.touch();

        selectedPlan.getPlanProperties().setOpenHandle(1);
        
        // we have to set the plan properties id so 'Close Plan' can do an unlock
        loadPlan.setPlanPropertiesID(selectedPlan.getPlanProperties().getId());

        for (Leaf leaf : selectedPlan.getTree().getRoot().getAllLeaves()) {
            leaf.resetValues(selectedPlan.getAlternativesDefinition().getConsideredAlternatives());
        }
        
        defineSampleRecords.save();
        super.save();
        changed="";
        return null;
    }
    
    @Destroy
    @Remove
    public void destroy() {
    }

    @In(create=true)
    @Out
    private TreeHelperBean treeHelper;
    
    private List<TreeNode> nodesToDelete = new ArrayList<TreeNode>();
    
       
    private void resetTransformers(){
        TreeNode root = this.selectedPlan.getTree().getRoot();
        for (Leaf leaf : root.getAllLeaves()) {
            /*
             * maybe the scaletype is not set yet
             * -> leaf.setDefaultTransformer has to handle null-values itself
             */
            if ((leaf.getScale() == null) ||
                (leaf.getScale().isDirty())) {
                leaf.setDefaultTransformer();
            }
        }
    }

    
    /**
     * @see INodeValidator#validateNode(TreeNode, List, List)
     */
    public boolean validateNode(TreeNode node, List<String> errorMessages,
            List<TreeNode> nodes) {
        boolean isValid = node.isCompletelySpecified(errorMessages);
        if (!isValid) {
            nodes.add(node);
        }
        return isValid; 
    }
    
    @In(create=true)
    private IProjectImport projectImport;
    
    public void useFastTrackTemplate() {
        
        if (fastTrackTemplates.getFastTrackTemplate() != null) {
            log.info(fastTrackTemplates.getFastTrackTemplate().getAbsolutePath());
        }

        File file = new File(fastTrackTemplates.getFastTrackTemplate().getAbsolutePath());
        
        List<TemplateTree> templates = null;
        
        try {
            FileInputStream fis;
            fis = new FileInputStream(file);

            byte[] data = new byte[(int)file.length()];

            fis.read(data);
            fis.close();
            
            templates = projectImport.importTemplates(data);
            
            if (templates.size() != 1) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Unable to load template.");
                return;
            }
            
            TreeNode newRoot = ((TreeNode)templates.get(0).getRoot()).clone();

            newRoot.touchAll(user.getUsername());

            //newtree.adjustScalesToMeasurements(MiniRED.getInstance().getMeasurementsDescriptor());
            newRoot.initWeights();
            
            nodesToDelete.add(selectedPlan.getTree().getRoot());
            
            selectedPlan.getTree().setRoot(newRoot);
            
	    // setWeightsInitialized must be called so that initWeights does its work
            selectedPlan.getTree().setWeightsInitialized(false);
            // initWeights *must* be called because it amongst other things
            // sets the weight of the root node to 1.0. if that doesn't happed
            // the whole evaluation doesn't work.
            selectedPlan.getTree().initWeights();
            changed = "true";

        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return;
        } catch (SAXException e) {
            log.error(e.getMessage(),e);
        }
        
    }


    public boolean validate(boolean showValidationErrors) {
        
        boolean valid = defineSampleRecords.validate(showValidationErrors);
        
        return valid && identifyRequirements.validate(showValidationErrors);
    }
}
