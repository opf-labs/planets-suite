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
package eu.planets_project.pp.plato.action.project;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.hibernate.Hibernate;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.TestDataLoader;
import eu.planets_project.pp.plato.action.interfaces.IAnalyseResults;
import eu.planets_project.pp.plato.action.interfaces.ICreateExecutablePlan;
import eu.planets_project.pp.plato.action.interfaces.IDefineAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IDefineBasis;
import eu.planets_project.pp.plato.action.interfaces.IDefinePlan;
import eu.planets_project.pp.plato.action.interfaces.IDefineSampleRecords;
import eu.planets_project.pp.plato.action.interfaces.IDevelopExperiments;
import eu.planets_project.pp.plato.action.interfaces.IEvaluateExperiments;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackAnalyseResults;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackDefineRequirements;
import eu.planets_project.pp.plato.action.interfaces.IFastTrackEvaluateAlternatives;
import eu.planets_project.pp.plato.action.interfaces.IGoNoGo;
import eu.planets_project.pp.plato.action.interfaces.IIdentifyRequirements;
import eu.planets_project.pp.plato.action.interfaces.IRunExperiments;
import eu.planets_project.pp.plato.action.interfaces.ISetImportanceFactorsAction;
import eu.planets_project.pp.plato.action.interfaces.ITransformMeasuredValues;
import eu.planets_project.pp.plato.action.interfaces.IUtilAction;
import eu.planets_project.pp.plato.action.interfaces.IValidatePlan;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.transform.Transformer;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.IDownloadManagerHelperBean;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Loads PPs
 *
 * Implements operations specified in {@link eu.planets_project.pp.plato.action.interfaces.LoadPlanAction}
 *
 * @author Hannes Kulovits
 */

@Name("loadPlan")
@Scope(ScopeType.SESSION)
public class LoadPlanAction implements Serializable {

    private static final long serialVersionUID = -5699231548828633148L;

    private enum WhichProjects {
        ALLPROJECTS,
        PUBLICPROJECTS,
        MYPROJECTS,
        FTEPROJECTS,
        PUBLICFTEPROJECTS;
    }
    
    private static final Log log = PlatoLogger.getLogger(LoadPlanAction.class);
    
    @In(required = false, create = true)
    @Out(scope = ScopeType.APPLICATION)
    private IUtilAction utilAction;

    
    @In
    private FacesContext facesContext;

    /**
     * This download manager <b>helper</b> bean is used in the DownloadServlet because
     * injecting or looking up the EntityManager in a http servlet is just pain.
     *
     * @see eu.planets_project.pp.plato.util.DownloadServlet
     */
    @In(create=true)
    @Out(required=true)
    private IDownloadManagerHelperBean downloadManagerHelperBean;

    /**
     * we also have to inject the selected project because of our newProject
     * observer. When a new project is created, selectedPlan must be injected
     * into LoadPlanAction. If we would not inject it, selectedPlan would
     * be outjected as null.
     */
    @In(required = false)
    @Out(required = false, scope = ScopeType.SESSION)
    private Plan selectedPlan;

    /**
     * Contains projects from database.
     */
    @DataModel
    private List<PlanProperties> projectList;

    

    /**
     * Entity manager for persistence.
     */
    @In
    EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    /**
     * Plan selected by user from list {@link #projectList}.
     */
    @DataModelSelection(value = "projectList")
    private PlanProperties selection;

    /**
     * if the session ends, open projects must be closed but it is not sure that
     * the selectedPlan is still in the session context therefore keep the id
     * of the planProperties separately
     */
    private int planPropertiesId = 0;

    @In(create = true)
    private TestDataLoader testDataLoader;

  
    private WhichProjects lastLoadMode = WhichProjects.MYPROJECTS;

    public WhichProjects getLastLoadMode() {
        return lastLoadMode;
    }


    public void setLastLoadMode(WhichProjects lastLoadMode) {
        this.lastLoadMode = lastLoadMode;
    }


    @Observer("projectListChanged")
    public String relist() {
        list(lastLoadMode);
        log.debug("reloading  in "+lastLoadMode+": number of projects loaded: " + projectList.size());
        return "success";
    }

    @Transactional
    @PreDestroy
    public void preDestroy() {
        // maybe there is an open project left - close it
        unlockProject();       
    }
    
    @Destroy
    public void destroy() {
    }

    /**
     * Initializes the user and loads the project from database when session
     * component is created.
     * This is called when this bean is created.
     */
    @Create
    public void onCreate() {
        listMyProjects();
    }
    
    public String listFTEProjects() {
        list(WhichProjects.FTEPROJECTS);
        return "success";
    }

    public String listAllProjects() {
        list(WhichProjects.ALLPROJECTS);
        return "success";
    }
    
    public String listMyProjects() {
        list(WhichProjects.MYPROJECTS);
        return "success";
    }
    
    public String listPublicProjects() {
        list(WhichProjects.PUBLICPROJECTS);
        return "success";
    }
    
    public String listPublicFTEResults() {
        list(WhichProjects.PUBLICFTEPROJECTS);
        return "success";
    }
    
   
    @Out
    private String planlist = "my plans";

    /**
     * Load projects from data:
     * <ul>
     *   <li>When administrator is logged in, all projects are loaded from database.</li>
     *   <li>For any other user, only projects are loaded that are <b>not</b> set to private by another user.</li>
     * </ul>
     *
     * Furthermore, checks if project is locked by the current user, who may thus be allowed to unlock the project.
     * In this case the {@link PlanProperties#isAllowReload()} is set. In the user interface this flag means
     * that an 'Unlock' button is displayed.
     */
    public void list(WhichProjects whichProjects) {

        Contexts.getSessionContext().remove("projectList");

        // changed flag needs to be initialized here because list is the first method that is called
        // when Plato is accessed.
        // Injecting it as a member variable with create=true didn't work for some reason.
        String changed = (String) Contexts.getSessionContext().get("changed");
        if (changed == null)
            Contexts.getSessionContext().set("changed", "");

        String projectListQuery;
        
        if (whichProjects == WhichProjects.MYPROJECTS) {
            // load user's projects
            //projectListQuery = "select p from PlanProperties p where (p.owner = '"+ user.getUsername() + "' and not (p.projectBasis.identificationCode LIKE 'FAST-TRACK-%'))" + " order by p.id" ;
            projectListQuery = "select p.planProperties from Plan p where" + 
            " (p.planProperties.owner = '"+ user.getUsername() + "')" +
            " and (p.projectBasis.identificationCode = null or p.projectBasis.identificationCode NOT LIKE 'FAST-TRACK-%')" +
            " order by p.planProperties.id";
            
            setPlanlist("my preservation plans");
        } else if (whichProjects == WhichProjects.ALLPROJECTS && (user.isAdmin())) {
            // load all projects, public and private, 
            // but ONLY if the user is an admin
            projectListQuery = "select p from PlanProperties p order by p.id";
            setPlanlist("all preservation plans");
        } else if (whichProjects == WhichProjects.FTEPROJECTS) {
            
            projectListQuery = "select p.planProperties from Plan p where" + 
            " (p.planProperties.owner = '"+ user.getUsername() + "')" +
            " and (p.projectBasis.identificationCode LIKE 'FAST-TRACK-%')" +
            " order by p.planProperties.id";
            
            setPlanlist("fast track plans");
        } else if (whichProjects == WhichProjects.PUBLICFTEPROJECTS) {
            
            projectListQuery = "select p.planProperties from Plan p where" + 
            " (p.planProperties.privateProject = false )" +
            " and (p.projectBasis.identificationCode LIKE 'FAST-TRACK-%')" +
            " order by p.planProperties.id";
            
            setPlanlist("public fast track plans");
            
        } else {
           // load all public projects, which includes those with published reports
            projectListQuery = "select p.planProperties from Plan p where ((p.planProperties.privateProject = false)" +
            " or (p.planProperties.privateProject = true and p.planProperties.reportPublic = true)) and p.projectBasis.identificationCode NOT LIKE 'FAST-TRACK-%' " +
            " order by p.planProperties.id" ;            
            setPlanlist("public preservation plans");
       }
        
               
        projectList = em.createQuery(projectListQuery).getResultList();
        try {
            // if there are no PUBLIC projects, we want to load them:
            if (projectList.size() == 0 && (whichProjects != WhichProjects.MYPROJECTS && whichProjects != WhichProjects.FTEPROJECTS)) {
                testDataLoader.importAutoloadPlans();
                projectList = em.createQuery(projectListQuery).getResultList();
            }
        } catch (Exception e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Failed to insert testdata.");
            log.fatal("Testdataloader failed.", e);
        }

        //
        // readOnly in PlanProperties is *transient*, it is used in loadPlan.xhtml
        // to determine if a user is allowed to load a project
        //
        for (PlanProperties pp : projectList) {
            
            //
            // a project may not be loaded when
            //   ... it is set to private
            //   ... AND the user currently logged in is not the administrator
            //   ... AND the user currently logged in is not the owner of that project
            boolean readOnly = pp.isPrivateProject()
                && !user.isAdmin()
                && !user.getUsername().equals(pp.getOwner());

            boolean allowReload = pp.getOpenedByUser().equals(user.getUsername())
                && selectedPlan == null;

            pp.setReadOnly(readOnly);
            pp.setAllowReload(allowReload);
        }
        setLastLoadMode(whichProjects);
    }

    public void setPlanlist(String planlist) {
        this.planlist = planlist;
    }

    @In(create = true)
    ProjectSettings projectSettings;
    
    @In(create = true)
    IFastTrackDefineRequirements FTrequirements;
    
    @In(create = true)
    IFastTrackEvaluateAlternatives FTevaluate;

    @In(create = true)
    IFastTrackAnalyseResults FTanalyse;
    
    @In(create = true)
    IDefineBasis defineBasis;

    @In(create = true)
    IDefineSampleRecords defineSampleRecords;

    @In(create = true)
    IIdentifyRequirements identifyRequirements;

    @In(create = true)
    IDefineAlternatives defineAlternatives;

    @In(create = true)
    IGoNoGo gonogo;

    @In(create = true)
    IDevelopExperiments devexperiments;

    @In(create = true)
    IRunExperiments runexperiments;

    @In(create = true)
    IEvaluateExperiments evalexperiments;

    @In(create = true)
    ITransformMeasuredValues transform;

    @In(create = true)
    ISetImportanceFactorsAction importanceFactors;

    @In(create = true)
    IAnalyseResults analyseResults;

    @In(create = true)
    ICreateExecutablePlan createExecutablePlan;

    @In(create = true)
    IDefinePlan definePlan;

    @In(create = true)
    IValidatePlan validatePlan;

    @In
    private User user;

    public void setPlanPropertiesID(int id){
        planPropertiesId = id;
    }

    /**
     * Loads a project from database that was selected by the user.
     *
     * Previously loaded project is unlocked.
     *
     * Forwards the user to respective workflowstep by calling the workflow step's
     * <code>enter</code> method.
     */
    @RaiseEvent("reload")
    public String load() {
        String id = "";
        try {
            id = ((HttpServletRequest) facesContext.getExternalContext()
                    .getRequest()).getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }
        log.info("Session="+id+" of user "+user.getUsername() 
                + " is loading project "+selection.getId()
                + " - "+selection.getName());

        // try to lock the project
        Query q = em
                .createQuery("update PlanProperties pp set pp.openHandle = 1, pp.openedByUser = '" + user.getUsername() + "' where (pp.openHandle is null or pp.openHandle = 0) and pp.id = "
                        + selection.getId());
        int num = q.executeUpdate();
        if (num < 1) {
            FacesMessages
                    .instance()
                    .add(
                            FacesMessage.SEVERITY_INFO,
                            "In the meantime the plan has been loaded by an other user. Please choose another plan.");
            relist();
            log.debug("Locking plan failed");
            return null;
        }
        List<Plan> list = em.createQuery(
                "select p from Plan p where p.planProperties.id = "
                        + selection.getId()).getResultList();

        // we locked the project before be we now cannot load it. not good.
        if (list.size() != 1) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "An unexpected error has occured while loading the plan.");

            return null;
        }
        // ok - the selected project is free - unlock the old project
        unlockProject();
        // load the selected project (and keep the id!)
        setPlanPropertiesID(selection.getId());
        selectedPlan = em.find(Plan.class, list.get(0).getId());

        // Strangely enough the outjection doesnt work here, so to be sure we set the member explicitly
        Contexts.getSessionContext().set("selectedPlan", selectedPlan);

        this.initializeProject(selectedPlan);
        log.info("Plan " + selectedPlan.getPlanProperties().getName()
                + " loaded!");

        String msg = "The plan you loaded has reached the state "
                + selectedPlan.getState().getStateName()
                + ". Therefore you have been directed to the subsequent workflow step.";
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, msg);
        
        if (selectedPlan.isFastTrackEvaluationPlan()) {
            
            switch (selectedPlan.getState().getValue()) {
            case PlanState.FTE_INITIALISED:
                return FTrequirements.enter();
            case PlanState.FTE_REQUIREMENTS_DEFINED:
                return FTevaluate.enter();
            case PlanState.FTE_ALTERNATIVES_EVALUATED:
                return FTanalyse.enter();
            case PlanState.FTE_RESULTS_ANALYSED:
                return FTanalyse.enter();
            }
        }

        // redirect to step corresponding to the project's state:
        switch (selectedPlan.getState().getValue()) {
        case PlanState.BASIS_DEFINED:
            return defineSampleRecords.enter();
        case PlanState.RECORDS_CHOSEN:
            return identifyRequirements.enter();
        case PlanState.TREE_DEFINED:
            return defineAlternatives.enter();
        case PlanState.ALTERNATIVES_DEFINED:
            return gonogo.enter();
        case PlanState.GO_CHOSEN:
            return devexperiments.enter();
        case PlanState.EXPERIMENT_DEFINED:
            return runexperiments.enter();
        case PlanState.EXPERIMENT_PERFORMED:
            return evalexperiments.enter();
        case PlanState.RESULTS_CAPTURED:
            return transform.enter();
        case PlanState.TRANSFORMATION_DEFINED:
            return importanceFactors.enter();
        case PlanState.WEIGHTS_SET:
            return analyseResults.enter();
        case PlanState.ANALYSED:
            return createExecutablePlan.enter();
        case PlanState.EXECUTEABLE_PLAN_CREATED:
            return definePlan.enter();
        case PlanState.PLAN_DEFINED:
            return validatePlan.enter();
        case PlanState.PLAN_VALIDATED:
            return validatePlan.enter();
        default:
            return defineBasis.enter();
        }
    }
    
    public String startFastTrackEvaluation() {
        unlockProject();

        selectedPlan = new Plan();
        selectedPlan.getPlanProperties().setAuthor(user.getFullName());
        selectedPlan.getPlanProperties().setPrivateProject(true);
        selectedPlan.getPlanProperties().setOwner(user.getUsername());

        // set Fast Track properties
        selectedPlan.getState().setValue(PlanState.FTE_INITIALISED);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-kkmmss");
        String timestamp = format.format(new Date(System.currentTimeMillis()));
        String identificationCode = Plan.fastTrackEvaluationPrefix + timestamp;
        selectedPlan.getProjectBasis().setIdentificationCode(identificationCode);
        
        // We have to prevent the user from navigating to the step 'Load plan'
        // because the user wouldn't be able to leave this step: Going to 'Define
        // Basis' is not possible as the project hasn't been saved so far.
        //
        // We 'activate' the changed flag so that the user is asked to either
        // save the project or discard changes.
        TreeNode root = new Node();
        root.setName("Root");
        selectedPlan.getTree().setRoot(root);
        
        // ok - the selected project is free - unlock the old project
        unlockProject();
        
        
        
        // load the selected project (and keep the id!)
        setPlanPropertiesID(selectedPlan.getPlanProperties().getId());
        
        // Strangely enough the outjection doesnt work here, so to be sure we set the member explicitly
        Contexts.getSessionContext().set("selectedPlan", selectedPlan);

        this.initializeProject(selectedPlan);
        
        FTrequirements.enter();
        
        return "success";
    }

    /**
     * Hibernate initializes project and its parts.
     */
    public void initializeProject(Plan p) {
        Hibernate.initialize(p);
        Hibernate.initialize(p.getAlternativesDefinition());
        Hibernate.initialize(p.getSampleRecordsDefinition());
        Hibernate.initialize(p.getTree());
        initializeNodeRec(p.getTree().getRoot());
        log.debug("plan initialised");
    }

    /**
     * Traverses down the nodes in the tree and calls <code>Hibernate.initialize</code>
     * for each leaf. This is necessary to provide the application with a convenient
     * way of working with lazily initialized collections or proxies.
     *
     * @param node node from where initialization shall start
     */
    private void initializeNodeRec(TreeNode node) {

        Hibernate.initialize(node);
        if (node.isLeaf()) {
            Leaf leaf = (Leaf) node;
            Transformer t = leaf.getTransformer();
            Hibernate.initialize(t);
            if (t instanceof OrdinalTransformer) {
                OrdinalTransformer nt = (OrdinalTransformer) t;
                Hibernate.initialize(nt.getMapping());
            }
            //log.debug("hibernate initialising Transformer: " + leaf.getTransformer());
            for (Values value : leaf.getValueMap().values()) {
                Hibernate.initialize(value);
            }
        } else if (node instanceof Node) {
            Node recnode = (Node) node;
            Hibernate.initialize(node.getChildren());
            for (TreeNode newNode : recnode.getChildren()) {
                initializeNodeRec(newNode);
            }
        }
    }

    /**
     * Unlocks all projects in database.
     */
    public void unlockAll() {
        this.unlockQuery(false);
    }

    /**
     * Unlocks certain projects in database (dependent on parameter)
     *
     * @param useId If this is true, only project with id {@link #planPropertiesId} will be unlocked;
     * otherwise, all projects in database will be unlocked
     */
    private void unlockQuery(boolean useId) {
        
        String where = "";
        if (useId) {
            where = "where pp.id = " + planPropertiesId;
        }

        Query q = em
                .createQuery("update PlanProperties pp set pp.openHandle = 0, pp.openedByUser = ''"
                        + where);
        try {
            if (q.executeUpdate() < 1) {
                log.debug("Unlocking plan failed.");
            } else {
                log.debug("Unlocked plan");
            }
        } catch (Throwable e) {
            log.error("Unlocking plan failed:", e);
        }

        planPropertiesId = 0;
    }

    /**
     * Unlocks project with id {@link #planPropertiesId}.
     */
    public void unlockProject() {
        if (planPropertiesId != 0) {
            log.info("unlocking plan "+planPropertiesId);
            utilAction.unlockPlan(planPropertiesId);
        }
    }

    /**
     * Unlocks project depending on data model selection {@link #selection}
     */
    public String unlockselectedPlan() {
        if (selection != null) {
            planPropertiesId = selection.getId();
            unlockQuery(true);
        }
        return relist();
    }

    /**
     * Closes currently selected project {@link #selectedPlan}
     */
    public String closeProject() {
        if (selectedPlan != null) {
            unlockProject();
            selectedPlan = null;
        } else {
            log.debug("not unlocking any plan because none is open.");
        }
        return "success";
    }
}
