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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.action.IProjectExport;
import eu.planets_project.pp.plato.action.interfaces.IProjectImport;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.tree.LibraryTree;
import eu.planets_project.pp.plato.model.tree.TemplateTree;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.LibraryExport;
import eu.planets_project.pp.plato.xml.ProjectExporter;
import eu.planets_project.pp.plato.xml.ProjectImporter;

/**
 * Performs uploads of project- and template-XML files, 
 * and provides a download stream of the XML representation of the selected project.
 * Creates a copy of the selected project.
 * It uses {@link ProjectExporter} and {@link ProjectImporter} for creating the XML representation 
 * of projects and templates and vice versa.
 *    
 * @author Michael Kraxner
 *
 */
@Name("xml")
@Scope(ScopeType.SESSION)
public class XmlAction implements Serializable {
    @In
    private User user;

    /**
     * 
     */
    private static final long serialVersionUID = -4420194450910349095L;

    private static final Log log = PlatoLogger.getLogger(XmlAction.class);

    @In(required=false)
    Plan selectedPlan;

    /**
     * XML-data which is read in {@link #doImport()} 
     */
    @In(required=false)
    @Out(required=false)
    private String input = "";

    @In(required=false)
    @Out(required=false)
    private String directory = "";

    @In(required=false)
    @Out(required=false)
    private String templateName;
    
    @In(required=false)
    @Out(required=false)
    private String templateLibrary;
    
    @In(required=false)
    @Out(required=false)
    private String templateDescription;
    
    /**
     * Reference to the fragments tree
     */
    @Out(required = false)
    private TemplateTree fragmentRoot;

    /**
     * Reference to the template tree
     */
    @Out(required = false)
    private TemplateTree templateRoot;
    
    
    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void importFromDir() {
        File f = new File(directory);
        if (f == null || !f.isDirectory()) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
            "You have to select a valid server side path for import.");
            return;
        } 
        int count = projectImport.importAllProjectsFromDir(f);
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
            "IMPORTED "+count+" PROJECTS!");
    }
    
    @In
    EntityManager em;

    @In(required = false)
    private String fileName;

    /**
     * XML-data for file-upload
     */
    @In(required = false)
    @Out(required=false)
    private byte[] file;

    @In(create=true)
    private IProjectImport projectImport;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     *  Imports the XML-data of <code>input</code> and stores the projects in the database.
     */
    public String doImport() {
        try {
            List<Plan> plans = projectImport.importProjects(new ByteArrayInputStream(input.getBytes("UTF-8")));
            /*
             * store all projects
             */
            storeProjects(plans);
        } catch (Exception e) {
            log.info(e);
        }
        return null;
    }
    
    public String uploadPlans() {
        return uploadPlans(false);
    }

    /**
     * Imports the uploaded XML-data and stores the projects in the database.
     */
    @RaiseEvent("projectListChanged")
    public String uploadPlans(boolean changeUser) {
        if (file  == null || file.length == 0 ) {
            log.debug("No file for import selected.");
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "You have to select a valid XML file for import, conforming to a Plato XML schema.");
            return null;
        }
        boolean impResult = false;

        log.debug("FileName: " + fileName);
        //log.debug("Length of File: " + file.length);
        try {
            List<Plan> plans = projectImport.importProjects(new ByteArrayInputStream(file));
            int numOfProjects = plans.size();

            
            // if the plans are imported by a NORMAL USER in the web interface, they will be
            // assigned to this user, i.e. the owner is set to the current user.
            // If they are imported by an ADMIN, they stay property of the original user,
            // unless the admin uses a different button

            if (!user.isAdmin() || changeUser)  {
                for (Plan p: plans) {
                    p.getPlanProperties().setOwner(user.getUsername());
                }
            }
            /*
             * store all projects and removes them from the list
             */
            storeProjects(plans);

            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully imported " + numOfProjects + " projects!");
            if (!projectImport.getAppliedTransformations().isEmpty()) {
                StringBuffer msg = new StringBuffer();
                msg.append("The following transformations have been applied:<br/><br/>");
                msg.append("<ul>");
                for (String xsl : projectImport.getAppliedTransformations()) {
                    msg.append("<li>").append("<a href='../xslt/"+xsl+"' target='_blank'>"+xsl+"</a>").append("</li>");
                }
                msg.append("</ul>");
                FacesMessages.instance().add(new FacesMessage(FacesMessage.SEVERITY_INFO, "Your XML file was outdated, therefore it had to be migrated to the current Plato XML format.", msg.toString()));
            }
            impResult = true;
        } catch (Exception e) {
            /*
             * Import failed, do not reload the project list
             */
            log.debug("Import failed:" + e.getMessage());
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "You have to select a valid XML file for import, conforming to a Plato XML schema.");
        }
        this.file = null;
        this.fileName = "";
        
        if (impResult)
           return "success";
        else
            return null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

    /**
     * Imports the uploaded XML-Files and stores the templates in the database.
     */
    @RaiseEvent("projectListChanged")
    public String uploadTemplates() {
        
        try {
            projectImport.storeTemplatesInLibrary(file);
            
            fragmentRoot = null;
            templateRoot = null;
            
        } catch (SAXException e) {
            e.printStackTrace();
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error importing templates. " + e.getMessage());
            return null;
        } catch (IOException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error importing templates. " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Templates successfully imported");
        
        return null;
    }
    
    /**
     * This method takes the objective tree from the selected plan, creates a template out of it and
     * stores it in the template library.
     * 
     * The admin can choose the 
     *  * templateLibrary, e.g. "Public Templates"
     *  * templateName, i.e. the name of the root node, which is the name of the template
     *  * templateDescription
     * 
     *  Also see: {@link XmlAction#storeTemplatesInLibrary(byte[])}
     * 
     * 
     * @return
     */
    public String addTreeToTemplateLibrary() {
        
        if (selectedPlan == null) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Please select a plan first.");
            return null;
        }

        ProjectExporter exporter = new ProjectExporter();
        
        String xml = exporter.getObjectiveTreeAsTemplate(selectedPlan, templateLibrary, templateName, templateDescription);        
        try {
            projectImport.storeTemplatesInLibrary(xml.getBytes("UTF-8"));
            
            fragmentRoot = null;
            templateRoot = null;

        } catch (Exception e) {
            log.error(e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error importing tree as templates. " + e.getMessage());
            return null;
        }
        
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully imported tree as template.");
        
        return null;
    }
    
    
    /**
     * Creates a copy of the selected project and stores it in the database.
     * To create the copy the project is first exported, then imported as a new project.
     */
    public String cloneProject() {
        if (selectedPlan == null) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "No project selected - please load project first.");
            log.error("No project selected - please load project first.");
            return null;            
        }
        File tempFile = new File(OS.getTmpPath() + "cloneplans_" + System.currentTimeMillis() + ".xml");
        tempFile.deleteOnExit();
        ProjectExporter exporter = new ProjectExporter();
        try {
            exporter.exportToFile(selectedPlan, tempFile);
            List<Plan> plans = projectImport.importProjects(new FileInputStream(tempFile));
            /*
             * store project
             */
            storeProjects(plans);
            
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Plan '" + selectedPlan.getPlanProperties().getName() +"' successfully cloned.");
            log.debug("Plan '" + selectedPlan.getPlanProperties().getName() +"' successfully cloned.");
        } catch (Exception e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Could not clone project: '" + selectedPlan.getPlanProperties().getName() + "'.");
            log.debug("Could not clone project: '" + selectedPlan.getPlanProperties().getName() + "'.", e);
        }
        tempFile.delete();
        return null;
    }
    
    
    @In(create=true)
    IProjectExport projectExport;
    
    public String export() {
        if (selectedPlan != null) {
            // convert project-name to a filename, add date:
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
            
            String planName = selectedPlan.getPlanProperties().getName();
            
            if ((planName == null) || "".equals(planName)) {
                planName = "export";
            } 
            String normalizedPlanName = FileUtils.makeFilename(planName);
            String filename =  normalizedPlanName + "-" + formatter.format(new Date());                
            
            String binarydataTempPath = OS.getTmpPath() + normalizedPlanName + System.currentTimeMillis() + "/";
            File binarydataTempDir = new File(binarydataTempPath);
            binarydataTempDir.mkdirs();
            try {
                HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/x-download");
                response.setHeader("Content-Disposition", "attachement; filename=\""+filename+".xml\"");
                // the length of the resulting XML file is unknown due to formatting: response.setContentLength(xml.length());
                try {
                    BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                    
                    projectExport.exportComplete(selectedPlan.getPlanProperties().getId(), out, binarydataTempPath);
                    
                    out.flush();
                    out.close();
    
                } catch (IOException e) {
                    FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
                    log.error("Could not open response-outputstream: ", e);
                }
                FacesContext.getCurrentInstance().responseComplete();
            } finally {
                OS.deleteDirectory(binarydataTempDir);
            }
    
        }
        System.gc();
        return null;
    }

    /**
     * Stores projects in the database.
     *  
     * @param plans
     */
    private void storeProjects(List<Plan> plans) {
        /*
         * store all projects
         */
        while(!plans.isEmpty()) {
            Plan plan = plans.get(0);
            em.persist(plan);
            em.flush();
            
            plans.remove(plan);
            plan = null;
            em.clear();
            System.gc();
        }
    }
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    
    public String uploadLibrary() {
        if (file  == null || file.length == 0 ) {
            log.debug("No file for import selected.");
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "Please select a Library-export file.");
            return null;
        }
        
        LibraryExport imp = new LibraryExport();
        
        try {
            LibraryTree newLib = imp.importFromStream(new ByteArrayInputStream(file));
            // at the moment we only support one Library definition
            newLib.setName("mainlibrary");
            
            // delete existing library
            LibraryTree oldLib = null;
            List<LibraryTree> trees = null; 
            trees = em.createQuery("select l from LibraryTree l where (l.name = 'mainlibrary') ").getResultList();
            if ((trees != null) && (trees.size() > 0)) {
                oldLib = trees.get(0);
            }
            em.remove(oldLib);
//            em.flush();
            
            em.persist(newLib);
        } catch (Exception e) {
            log.error("Failed to import Library: ", e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while importing the library.");
        } 
        return null;
    }
    
    public String exportLibrary() {
        LibraryTree lib = null;
        List<LibraryTree> trees = null; 
        trees = em.createQuery("select l from LibraryTree l where (l.name = 'mainlibrary') ").getResultList();
        if ((trees != null) && (trees.size() > 0)) {
            lib = trees.get(0);
        }
        
        if (lib != null) {
            // convert project-name to a filename, add date:
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");
            String filename =  "RequirementsLibrary-" + formatter.format(new Date());                
            
            String binarydataTempPath = OS.getTmpPath() + "RequirementsLibrary-" + System.currentTimeMillis() + "/";
            File binarydataTempDir = new File(binarydataTempPath);
            binarydataTempDir.mkdirs();
            
            LibraryExport exp = new LibraryExport();
            try {
                HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/x-download");
                response.setHeader("Content-Disposition", "attachement; filename=\""+filename+".xml\"");
                // the length of the resulting XML file is unknown due to formatting: response.setContentLength(xml.length());
                try {
                    BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                    
                    exp.exportToStream(lib, out);
                    
                    out.flush();
                    out.close();
    
                } catch (IOException e) {
                    FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
                    log.error("Could not open response-outputstream: ", e);
                }
                FacesContext.getCurrentInstance().responseComplete();
            } finally {
                OS.deleteDirectory(binarydataTempDir);
            }
    
        } else {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "No Library found, create one first.");
        }
        System.gc();
        return null;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    public String getTemplateLibrary() {
        return templateLibrary;
    }

    public void setTemplateLibrary(String templateLibrary) {
        this.templateLibrary = templateLibrary;
    }    
}
