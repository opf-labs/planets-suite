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
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
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
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;

import eu.planets_project.pp.plato.action.TestDataLoader;
import eu.planets_project.pp.plato.action.interfaces.IIdentifyRequirements;
import eu.planets_project.pp.plato.action.interfaces.IRequirementsExpert;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.LibraryTreeHelperBean;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.bean.TreeHelperBean;
import eu.planets_project.pp.plato.evaluation.MeasurementsDescriptor;
import eu.planets_project.pp.plato.evaluation.MiniRED;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.LibraryRequirement;
import eu.planets_project.pp.plato.model.tree.LibraryTree;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TemplateTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.ProjectExporter;

/**
 * Currently unused 'expert' view for criteria specification according
 * to the six-categories taxonomy.
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("requirementsExpert")
public class RequirementsExpertAction extends AbstractWorkflowStep implements
        IRequirementsExpert {

    private static final long serialVersionUID = 4236263515164701375L;

    private static final Log log = PlatoLogger.getLogger(RequirementsExpertAction.class);

    @In(create=true)
    private IIdentifyRequirements identifyRequirements;
    
    /**        

     * User currently logged in
     */
    @In(required=false)
    private User user;

    @In(create=true)
    @Out
    private TreeHelperBean treeHelper;
    
    private List<TreeNode> nodesToDelete = new ArrayList<TreeNode>();

    @Out(required = false)
    private LibraryTree libraryTree;
    
    @In(create=true)
    @Out
    private LibraryTreeHelperBean libraryTreeHelper;


    @Out(required=false)
    private TreeNode selectedLibNode;

    @Out(required=false)
    private TreeNode selectedReqNode;
    
    @Out(required = false)
    private TemplateTree reqexpTree;
    
    @In(create = true)
    private TestDataLoader testDataLoader;

    
    public RequirementsExpertAction() {
        requiredPlanState = new Integer(PlanState.RECORDS_CHOSEN);
    }

    protected IWorkflowStep getSuccessor() {
        return identifyRequirements;
    }
    
    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        
        log.info("Calling init of RequirementsExpert");
        
        List<LibraryTree> trees = null; 
        trees = em.createQuery("select l from LibraryTree l where (l.name = 'mainlibrary') ").getResultList();
        if ((trees == null) || (trees.size() == 0)) {
            libraryTree = new LibraryTree();
            libraryTree.addMainRequirements();
            libraryTree.setName("mainlibrary");
        } else {
            libraryTree = trees.get(0);
        }
        
        // load template tree
        String selectedTemplateLibrary = "Public Templates"; 
        try {
            reqexpTree = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();
            if ((reqexpTree == null) || (reqexpTree.getRoot() == null) || (reqexpTree.getRoot().getChildren().size() == 0)) {
                testDataLoader.insertTemplateTree();
                reqexpTree = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();                    
            }
        } catch (NoResultException e) {
            testDataLoader.insertTemplateTree();
            reqexpTree = (TemplateTree) em.createQuery("select n from TemplateTree n where name = ?").setParameter(1, selectedTemplateLibrary).getSingleResult();
        }
        
        selectedReqNode = null;
        selectedLibNode = null;

        nodesToDelete.clear();
        
    }
    

    /**
     * Persists the currently visible library
     */
    public String saveLibrary() {
        /*TODO:  dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        selectedReqNode = null;
        selectedLibNode = null;
        libraryTree = em.merge(libraryTree);
        libraryTree.setName("mainlibrary");
        em.persist(libraryTree);
        
        // TODO: handle deleted librarytree nodes
        
        return null;
    }
    
    public String saveRequirements() {
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        // before persisting: adjust all scales of leaves
        List<Leaf> leaves = reqexpTree.getRoot().getAllLeaves();
        MeasurementsDescriptor  descriptor = MiniRED.getInstance().getMeasurementsDescriptor();
        for (Leaf l : leaves) {
            MeasurementInfoUri mInfo = l.getMeasurementInfo().toMeasurementInfoUri();
            if (mInfo.getAsURI() != null) {
                Scale s = descriptor.getMeasurementScale(mInfo);
                if (s != null) {
                    l.adjustScale(s);
                }
            }
        }
        
        prep.prepare(reqexpTree);
        reqexpTree = em.merge(reqexpTree); 
        em.persist(reqexpTree);
        changed = "";
        
        for (TreeNode n : nodesToDelete) {
                if (n.getId() != 0) {
                        em.remove(em.merge(n));
                }
        }
        em.flush();
        nodesToDelete.clear();
        return null;
    }

    /**
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {
        log.info("--- Trying to save requirements");
        saveLibrary();
        
        log.info("--- Save library and requirements");
        saveRequirements();
        return null;
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

        return "success";
    }


    /**
     * Removes a node from its objective tree.
     *
     * @param object {@link eu.planets_project.pp.plato.model.tree.TreeNode} to remove from objective tree.
     *
     * @return Always returns null.
     */
    public void removeNode(Object nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }
        TreeNode node = (TreeNode) nodeToRemove;
        if (node.getParent() != null) {
            // parent has been changed
            ((Node) node.getParent()).touch();
            ((Node) node.getParent()).removeChild(node);
            if(treeHelper != null) {
                treeHelper.closeNode(node);
            }
        }
        selectedReqNode = null;
        nodesToDelete.add(node);
    }

    public void downloadTree() {
        ProjectExporter exporter = new ProjectExporter();
        Downloader.instance().downloadMM(
                exporter.exportTreeToFreemind(reqexpTree.getRoot()),
                reqexpTree.getName()+".mm");
    }
    
    @Out(required=false)
    private Node tempNode;
    
    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }



    protected String getWorkflowstepName() {
        return "requirementsExpert";
    }

    public void convertToNode(Object leaf) {
        Leaf l = (Leaf) leaf;
        l.getParent().convertToNode(l);
    }

    public void convertToLeaf(Object node) {
        Node n = (Node) node;
        n.getParent().convertToLeaf(n);
    }

    public boolean validate(boolean showValidationErrors) {
        return true;
    }

    public boolean validateNode(TreeNode node, List<String> errorMessages,
            List<TreeNode> nodes) {
        return true;
    }

    public void addCriterion(Object object) {
        if (object instanceof LibraryRequirement) {
            LibraryRequirement n = (LibraryRequirement)object;
            Leaf l = n.addCriterion();
            l.setName("<criterion name not set>");
            libraryTreeHelper.expand(n);
        }
    }

    public void addLibraryRequirement(Object object) {
        if (object instanceof LibraryRequirement) {
            LibraryRequirement n = (LibraryRequirement)object;
            LibraryRequirement node = n.addRequirement();
            node.setName("<requirement name not set>");
            libraryTreeHelper.expand(n);
        }
    }

    public void removeLibraryNode(Object object) {
        if (object instanceof TreeNode) {
            TreeNode n = (TreeNode)object;
            if (n.getParent() != null) {
                ((Node)n.getParent()).removeChild(n);
                selectedLibNode = null;
                libraryTreeHelper.closeNode(object);
            }
        }
    }

    public void processReqSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        
        Object current = tree.getRowData();
        TreeNode t = current instanceof TreeNode ? (TreeNode)current : null;
        
        if (t != null) {
            selectedReqNode = t;
        }
    }
    public void processLibSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        
        Object current = tree.getRowData();
        TreeNode currentNode = current instanceof TreeNode ? (TreeNode)current : null;
        
        if (currentNode != null) {
            selectedLibNode = currentNode;
        } 
    }
    
    public void applyMeasuremntInfo() {        
        if ((selectedLibNode == null)||
            (selectedReqNode == null)) {
            log.debug("apply mapping is not possible, select first a template and a req node.");
            return;
        }
        if (!(selectedLibNode instanceof Leaf) ||
           (!selectedReqNode.isLeaf())) {
            log.debug("apply mapping is not possible, you need to select two leaves.");
            return;
        }
        ((Leaf)selectedReqNode).setMeasurementInfo(((Leaf)selectedLibNode).getMeasurementInfo());
    }

    public void useLibraryFragment() {
        if ((selectedLibNode == null) || (selectedReqNode == null)) {
            log.debug("use template is not possible, select first a criterion/requirement of the library, and a requirement in Your tree.");
            return;
        }
        TreeNode node = null;
        if (selectedReqNode.isLeaf()) {
            node = selectedReqNode.getParent();
            ((Node)node).removeChild(selectedReqNode);
            nodesToDelete.add(selectedReqNode);
            selectedReqNode = node;
        } else {
            node = selectedReqNode;
        }
        if (node != null) {
            // append tree fragment
            TreeNode clone = selectedLibNode.clone();
            ((Node)node).addChild(clone);
            treeHelper.expand(clone);
        }
    }
}
