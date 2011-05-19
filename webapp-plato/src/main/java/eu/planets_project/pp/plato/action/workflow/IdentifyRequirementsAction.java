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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
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

import eu.planets_project.pp.plato.action.TestDataLoader;
import eu.planets_project.pp.plato.action.interfaces.IDefineAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IIdentifyRequirements;
import eu.planets_project.pp.plato.action.interfaces.IRequirementsExpert;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.bean.MeasurablePropertyMapper;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.bean.TreeHelperBean;
import eu.planets_project.pp.plato.evaluation.MeasurementsDescriptor;
import eu.planets_project.pp.plato.evaluation.MiniRED;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.model.tree.TemplateTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;
import eu.planets_project.pp.plato.validators.TreeValidator;
import eu.planets_project.pp.plato.xml.ProjectExporter;
import eu.planets_project.pp.plato.xml.TreeLoader;

/**
 * Implements actions for workflow step 'Identify Requirements'.
 * Includes tree manipulation and
 * @author Hannes Kulovits
 * @author Christoph Becker
 * @author Michael Kraxner
 * @author Riccardo Gottardi
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("identifyRequirements")
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class IdentifyRequirementsAction extends AbstractWorkflowStep implements
        IIdentifyRequirements {

    /**
     * 
     */
    private static final long serialVersionUID = 2989873312263543863L;

    private static final Log log = PlatoLogger.getLogger(IdentifyRequirementsAction.class);

    /**
     * Indicates whether an uploaded FreeMind file contains units
     */
    @Out
    private BooleanCapsule hasUnits = new BooleanCapsule(false);

    /**
     * Indicates whether the nodes comments are edited or scale,restriction, unit ...
     */
    @Out
    private BooleanCapsule doEditNodeComments = new BooleanCapsule(false);

    @In(create = true)
    IDefineAlternatives defineAlternatives;

    /**
     * filename of the uploaded file hopefully containing an objective tree in .mm format.
     * Is injected via the file upload form.
     */
    @In(required = false)
    private String fileName;

    private List<TreeNode> nodesToDelete = new ArrayList<TreeNode>();

    /**
     * User currently logged in
     */
    @In(required=false)
    private User user;
    
    @In(create=true)
    @Out
    private TreeHelperBean treeHelper;

    /**
     * kind of a 'touch' that forces ajax to send field values.
     */
    public void scaleChanged(Scale v) {
    }
    
    /**
     * FreeMind upload
     */
    private byte[] file;

    /**
     * Several files important to the defined requirements can be attached to the preservation plan.
     */
    @Out(required=false)
    private DigitalObject fileToAttach = new DigitalObject();

    /**
     * Uploaded file
     */
    @DataModel
    private List<DigitalObject> requirementsUploads;

    
    
    /**
     * File selected from list {@link #requirementsUploads}
     */
    @DataModelSelection
    private DigitalObject selectedUpload;

    /**
     * Validator for the main objective tree
     */
    private ITreeValidator validator = new TreeValidator();


    /**
     * Reference to the fragments tree
     */
    @In(required = false)
    @Out(required = false)
    private TemplateTree fragmentRoot;

    /**
     * Reference to the template tree
     */
    @In(required = false)
    @Out(required = false)
    private TemplateTree templateRoot;

    @In(create = true)
    private IRequirementsExpert requirementsExpert;

    
    /**
     * If a fragment-operation is taking place, this is a reference to the
     * {@link TreeNode} in the objective tree which will either be stored in the
     * template library (if {@link #saveFragment} is true) or the insertion target for
     * a fragment from the library (if {@link saveFragment} is false)
     *
     * @see #selectFragmentForSaving(Object)
     * @see #selectInsertionTarget(Object)
     */
// MK: it is not used in the view!    
//    @Out(required = false)
    private TreeNode selectedFragment;


    /**
     * The TestDataLoader is used to insert public fragments/templates if they are not
     * present yet
     *
     * @see #selectTemplateLibrary()
     */
    @In(create = true)
    private TestDataLoader testDataLoader;
    
    @In(create = true)
    private MeasurablePropertyMapper measurablePropertyMapper;
    
    private MeasurementsDescriptor descriptor = null;
    

    public IdentifyRequirementsAction() {
        requiredPlanState = new Integer(PlanState.RECORDS_CHOSEN);
    }

    protected IWorkflowStep getSuccessor() {
        return defineAlternatives;
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        
        log.info("Calling init of IdentifyRequirements");
        this.requirementsUploads = selectedPlan.getRequirementsDefinition().getUploads();
        
        // Initialize PP5-Characterisation
        measurablePropertyMapper.init(selectedPlan);

        nodesToDelete.clear();
        
        descriptor =  MiniRED.getInstance().getMeasurementsDescriptor();
    }
    
    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * Resets the transformer of all leaves to the default transformer.
     * @see eu.planets_project.pp.plato.action.workflow.IdentifyRequirementsAction#resetTransformers()
     */
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
     * Writes {@link eu.planets_project.pp.plato.model.Plan#getRequirementsDefinition()} to the database.
     */
    private void saveRequirementsDefinition() {
        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        for (DigitalObject u : selectedPlan.getRequirementsDefinition().getUploads()) {
            prep.prepare(u);
            if (u.getId() == 0) {
                em.persist(u);
            } else {
                em.persist(em.merge(u));
            }
        }

        save(selectedPlan.getRequirementsDefinition());
    }

    /**
     * Persists the currently visible fragment/template-tree
     */
    public String saveLibrary() {
        /* dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
        if (fragmentRoot != null) {
            prep.prepare(fragmentRoot);
            em.persist(em.merge(fragmentRoot));
        }
        if (templateRoot != null) {
            prep.prepare(templateRoot);
            em.persist(em.merge(templateRoot));
        }
        cancelFragmentOperation();
        return null;
    }

    /**
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {
        log.info("--- Trying to save requirements");

        saveRequirementsDefinition();

        this.resetTransformers();
        for (Leaf leaf : selectedPlan.getTree().getRoot().getAllLeaves()) {
            leaf.resetValues(selectedPlan.getAlternativesDefinition().getConsideredAlternatives());
        }
        /*
         * the properties of the whole tree have to be validated before persisting
         * (jsf cannot check the object-level constraints!)
         */
        if (validateProperties(selectedPlan.getTree(), ObjectiveTree.class, true)) {
            if (selectedPlan.getTree().getRoot().getId() == 0) {
                // this means the reference to the root has been changed, e.g. by useTemplate (I think thats the only case, actually) -
                // so we need to get this ID back (otherwise, each subsequent SAVE will result in a new entity persist)
                // simplest way: reload
                selectedPlan.getTree().setRoot(em.merge(selectedPlan.getTree().getRoot()));
            }
            save(selectedPlan.getTree());
            changed = "";
        }
        
        for (TreeNode n : nodesToDelete) {
        	if (n.getId() != 0) {
        		em.remove(em.merge(n));
        	}
        }
        em.flush();
        nodesToDelete.clear();

        log.info("--- saved requirements");
        
        if (selectedPlan.getTree().getRoot().getId() == 0) {
            // this means the reference to the root has been changed, e.g. by useTemplate (I think thats the only case, actually) -
            // so we need to get this ID back (otherwise, each subsequent SAVE will result in a new entity persist)
            // simplest way: reload
            selectedPlan.getTree().setRoot(em.merge(selectedPlan.getTree().getRoot()));
        }
        
        return null;
    }

    /**
     * Uploads a new objective tree from a FreeMind file.
     *
     * @return null Always returns null.
     */
    public String upload() {
        log.debug("FileName: " + fileName);
        log.debug("Length of File: " + file.length);
        log.debug("HasUnits is: " + hasUnits.isBool());
        if (fileName.endsWith("mm")) {
             InputStream istream = new ByteArrayInputStream(this.file);
             ObjectiveTree newtree = new TreeLoader().loadFreeMindStream(
                  istream, this.hasUnits.isBool(), true);

            if (newtree == null) {
                log.debug("File is corrupted and new Tree cannot be built");
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "This is not a valid Freemind file, maybe it is corrupted. Please make sure you added at least one level of nodes to the midmap.");
                return null;
            }
            // delete old tree
            nodesToDelete.add(selectedPlan.getTree().getRoot());

            
            selectedPlan.getTree().setRoot(newtree.getRoot());
            // make sure all scales are set according to measurement infos
            selectedPlan.getTree().adjustScalesToMeasurements(descriptor);
            selectedPlan.getTree().setWeightsInitialized(false);

        } else if ("".equals(fileName)) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Please select a Freemind file first.");
        } else {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "You have to upload Freemind files.");
        }
        return null;
    }

    /**
     * Adds {@link #fileToAttach} to {@link #requirementsUploads}.
     */
    public void attachFile() {
        if (!fileToAttach.isDataExistent()) {

            log.debug("No file for upload selected.");
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "You have to select a file before starting upload.");

            return;
        }
        requirementsUploads.add(fileToAttach.clone());
    }

    /**
     * Removes file selected by the user from list of uploads ({@link #requirementsUploads})
     */
    public void removeAttachedFile () {

        if (selectedUpload == null) {
            return;
        }

        requirementsUploads.remove(selectedUpload);
    }

    /**
     * Attaches a new Leaf to the given object (which is, hopefully, a Node)
     */
    public void addLeaf(Object object) {
        if (object instanceof Node) {
            Node node = (Node) object;
            node.addChild(new Leaf());
            log.debug("Leaf added: to NODE");
            // this node has been changed()
            node.touch();
            expandNode(node);
            
        }
    }

    /**
     * Attaches a new Node to the given object (which is, hopefully, a Node)
     */
    public void addNode(Object object) {
        if (object instanceof Node) {
            Node node = (Node) object;
            node.addChild(new Node());
            // this node has been changed()
            node.touch();
            expandNode(node);
            }
    }
    
    public void downloadTree() {
        ProjectExporter exporter = new ProjectExporter();
        Downloader.instance().downloadMM(
                exporter.exportTreeToFreemind(selectedPlan),
                selectedPlan.getPlanProperties().getName()+".mm");
    }
    
    /**
     * Downloads the result file of a specific sample record.
     *
     * @param object sample record the user wants to download the result file.
     */
    public void downloadAttachment() {
        if (selectedUpload == null) {
            return;
        }
        Downloader.instance().download(em.merge(selectedUpload));
    }
    
    
    private void expandNode(TreeNode node) {
        treeHelper.expandNode(node);
        Set<TreeNode> parents = node.getAllParents();
        for (TreeNode parent : parents) {
            treeHelper.expandNode(parent);
        }
        
    }

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @Override
    @RaiseEvent("reload")
    public String discard() {
        String ret = super.discard();
        if (! "success".equals(ret)) {
            log.warn("Discard called, but failed.");
            return ret;
        }
        // do NOT delete nodes which have been marked for deletion
        nodesToDelete.clear();

        requirementsUploads = selectedPlan.getRequirementsDefinition().getUploads();

        return "success";
    }

    /**
     * Removes a node from its objective tree.
     *
     * @param object {@link eu.planets_project.pp.plato.model.tree.TreeNode} to remove from objective tree.
     *
     * @return Always returns null.
     */
    public String remove(Object object) {

        removeNode(object);
        return null;
    }


    private void removeNode(Object nodeToRemove) {
        TreeNode node = (TreeNode) nodeToRemove;
        if (node.getParent() != null) {
            // parent has been changed
            ((Node) node.getParent()).touch();
            ((Node) node.getParent()).removeChild(node);
            if(treeHelper != null) {
                treeHelper.closeNode(node);
            }
        }
        nodesToDelete.add(node);
    }
    
    public String initTemplates() {
        selectTemplateLibrary("Public Templates");
        return null;
    }

    public void selectTemplateLibrary(String selectedTemplateLibrary) {
        if (templateRoot == null) {
            templateRoot = getLibrary(selectedTemplateLibrary);
        }
    }
    
    public String initFragments() {
        selectFragmentLibrary("Public Fragments");
        return null;
    }
    
    public void selectFragmentLibrary(String selectedTemplateLibrary) {
        if (fragmentRoot == null) {
            fragmentRoot = getLibrary(selectedTemplateLibrary);
        }
    }

    /**
     * Retrieves and displays the tree of the newly selected template library
     * which is determined by {@link #selectedTemplateLibrary}
     *
     * Invoked when the drowndown-box is manipulated.
     */
    private TemplateTree getLibrary(String selectedTemplateLibrary) {
        TemplateTree tt = null;
        //log.debug("Getting template tree " + selectedTemplateLibrary + " for user " + user.getUsername());
            try {
                tt = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();
                if (tt == null || tt.getRoot() == null || tt.getRoot().getChildren().size() == 0) {
                    testDataLoader.insertTemplateTree();
                    tt = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();                    
                }
            } catch (NoResultException e) {
                testDataLoader.insertTemplateTree();
                tt = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();
            }
            return tt;
    }

    /**
     * Performs cleanup after a completed fragment insertion- or saving-operation.
     * Disables auto-downscrolling to template library and forgets the previously
     * selected fragment.
     */
    private void fragmentOperationCompleted() {
        selectedFragment = null;
    }

    public String selectTreeForSaving() {
        // Store root
        selectedFragment = selectedPlan.getTree().getRoot();
        // Set "Save"-mode
        // MK: saveFragment is never read
        // saveFragment.setBool(true);
        
        // outject name and description
        tempNode= new Node();
        tempNode.setName( selectedFragment.getName());
        tempNode.setDescription(selectedFragment.getDescription());
        
        selectTemplateLibrary("Public Templates");
        return null;
    }
    
    @Out(required=false)
    private Node tempNode;
    
    /**
     * Sets the given {@link TreeNode} as the new {@link #selectedFragment} and the
     * template-library mode to "save" (which causes the "Save here"-buttons to appear
     * in the template library). Also makes the template-tree visible if it was
     * not displayed yet.
     *
     * Invoked when the user selects a node from the objective tree to be saved
     * into the fragments library.
     *
     * @param object the newly selected TreeNode that is supposed to be stored in the template library
     *
     * @see #saveFragmentHere(Object)
     */
    public String selectFragmentForSaving(Object object) {
        if (object instanceof TreeNode) {
            // Store active selection
            selectedFragment = (TreeNode) object;
            
            selectFragmentLibrary("Public Fragments");

            
            // outject name and description
            tempNode= new Node();
            tempNode.setName(selectedFragment.getName());
            tempNode.setDescription(selectedFragment.getDescription());
        }
        return null;
    }
    
    public String cancelFragmentOperation() {
     // Store active selection
        selectedFragment = null;
        return null;
    }

    /**
     * Completes a save-into-template-library-operation by adding a clone of the
     * previously selected {@link #selectedFragment} as a new child of the given
     * {@link Node} from the template library.
     *
     * @param object The {@link Node} in the template library where the previously
     * selected {@link #selectedFragment} should be saved to.
     *
     * @see #selectFragmentForSaving(Object)
     */
    public String saveFragmentHere(Object object) {
        log.info("Saving " + selectedFragment + " to " + object);
        Node target = (Node) object;
        TreeNode clone = selectedFragment.clone();
        
        clone.setName(tempNode.getName());
        clone.setDescription(tempNode.getDescription());
        
        target.addChild(clone);
        clone.touchAll(user.getUsername()); // the newly added TreeNode should be touched.
        //FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully stored node \"" + selectedFragment.getName() + "\" into fragment library as a child node of \"" + ((Node) object).getName() + "\"");
        
        em.persist(em.merge(target));
        fragmentOperationCompleted();
        return null;
    }

    public String saveTemp() {
        log.debug("saving temp "+tempNode.getName()+", "+tempNode.getDescription());
        return null;
    }
    
    /**
     * Sets the given {@link Node} as the new {@link #selectedFragment} and the
     * template-library mode to "insert" (which causes the "Insert this"-buttons to
     * appear in the template library). Also makes the template-tree visible if it was
     * not displayed yet.
     *
     * Invoked when the user selects a node from the objective tree as the target
     * of a fragments-insertion-operation.
     *
     * @param object the newly selected TreeNode where a fragment from the template library is supposed to be inserted
     *
     * @see #insertThisFragment(Object)
     */
    public String selectInsertionTarget(Object object) {
        if (object instanceof Node) {
            // Store active selection as new insertion target
            selectedFragment = (TreeNode) object;
            
            selectFragmentLibrary("Public Fragments");
        }
        return null;
    }

    /**
     * Inserts a clone of the given {@link TreeNode} from the template library as a
     * new child of the previously selected {@link #selectedFragment} from the objective
     * tree.
     * @param object the fragment from the template-library that will be cloned and inserted
     *  into the current objective tree
     *
     * @see {@link #selectInsertionTarget(Object)}
     */
    public String insertThisFragment(Object object) {

        TreeNode clone = ((TreeNode) object).clone();
        
        selectedPlan.getTree().getRoot().touch();

        // Insert into the tree
        ((Node) selectedFragment).addChild(clone);

        // Explicitly touch the node where the fragment was inserted
        selectedFragment.touch();

        // Not all browsers seem to correctly detect that the tree has been
        // changed after an insertion operation in all cases. Therefore we
        // explicitly set the session variable "changed" to "true" in order
        // to prevent the user from selecting an arbitrary workflow-step from
        // the menu which could potentially cause a crash.
        changed = "true";

        //FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully inserted fragment \"" + ((TreeNode) object).getName() + "\" into your objective tree as a child of \"" + selectedFragment.getName() + "\"");

        fragmentOperationCompleted();
        return null;
    }

    /**
     * Replaces the current objective tree with a clone of the selected template.
     *
     * @param object The selected {@link TreeNode} from the template-tree which is in fact the root node of a template that will be cloned to replace the current objective tree
     */
    @RaiseEvent("reload")
    public String useTemplate(Object object) {
        log.info("Using template " + ((TreeNode) object).getName());
        TreeNode newRoot = ((TreeNode) object).clone();
                     
        selectedPlan.getTree().setWeightsInitialized(false);
        nodesToDelete.add(selectedPlan.getTree().getRoot());
        selectedPlan.getTree().setRoot(newRoot);
        changed = "true";
        
        selectedPlan.getTree().getRoot().touch();
        
        // we have to return "something" else the reload-event is not raised
        return "success";
    }

    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * checks if the user is allowed to proceed ....
     */
    public boolean validate(boolean showValidationErrors) {

        /*
         * validation errors of tree-properties need not be shown again,
         * this already happened before persisting, in save() -
         * this is why we call the validation method with showValidationErrors=false
         */
        if (!validateProperties(selectedPlan.getTree(), ObjectiveTree.class, false)) {
            return false;
        }

        /*
         * Check if we have a validator.
         */
        if (validator == null) {
            log.warn("CHECK THIS: validator is null in identify");
            return true;
        }

        ArrayList<TreeNode> errorNodes = new ArrayList<TreeNode>();
        boolean isValid = validator.validate(selectedPlan.getTree().getRoot(), this,
                errorNodes, showValidationErrors);
        
        for (TreeNode treeNode : errorNodes) {
            expandNode(treeNode);
        }

        return isValid;
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

    protected String getWorkflowstepName() {
        return "identifyRequirements";
    }

    public ITreeValidator getValidator() {
        return validator;
    }

    public void setValidator(ITreeValidator validator) {
        this.validator = validator;
    }

    public List<DigitalObject> getRequirementsUploads() {
        return requirementsUploads;
    }

    public void setRequirementsUploads(List<DigitalObject> requirementsUploads) {
        this.requirementsUploads = requirementsUploads;
    }

    public DigitalObject getSelectedUpload() {
        return selectedUpload;
    }

    public void setSelectedUpload(DigitalObject selectedUpload) {
        this.selectedUpload = selectedUpload;
    }

    public DigitalObject getFileToAttach() {
        return fileToAttach;
    }

    public void setFileToAttach(DigitalObject fileToAttach) {
        this.fileToAttach = fileToAttach;
    }

    public void convertToNode(Object leaf) {
        Leaf l = (Leaf) leaf;
        l.getParent().convertToNode(l);
    }

    public void convertToLeaf(Object node) {
        Node n = (Node) node;
        n.getParent().convertToLeaf(n);
    }

    public String startExpert() {
        return requirementsExpert.enter();
    }
    
    

}
