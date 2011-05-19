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

package eu.planets_project.pp.plato.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.action.interfaces.IProjectImport;
import eu.planets_project.pp.plato.action.project.LoadPlanAction;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.ProjectImporter;

/**
 * This class inserts test data into the persistence layer, including import of
 * objective trees from case studies.
 *
 * TODO why not application scope or request scope?
 * it's only used once and wouldn't need to stay in the session.
 *
 * @author Christoph Becker
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("testDataLoader")
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class TestDataLoaderImpl implements Serializable, TestDataLoader {

    private static final long serialVersionUID = 2155152208617526555L;

    private static final Log log = PlatoLogger.getLogger(TestDataLoaderImpl.class);

    /**
     * pointing to the server-side directory (within the plato.ear deployment)
     * containing exported XML planning projects that shall be imported automatically.
     * @see #importAutoloadPlans()
     */
    public static final String AUTOLOAD_DIRECTORY_NAME = "data/projects/autoload/";
    public static final String DEMOPLANS_DIRECTORY_NAME= "data/projects/demos/";
    
    public static final String PUBLIC_TEMPLATES_DIRECTORY_NAME = "data/templates/public_templates/";

    public static final String PUBLIC_FRAGMENTS_DIRECTORY_NAME = "data/templates/public_fragments/";

    @PersistenceContext(unitName="platoDatabase")
    EntityManager em;
    
    @In(create=true)
    private IProjectImport projectImport;

    /**
     * This method iterates through the directory defined in {@link #AUTOLOAD_DIRECTORY_NAME}
     * and imports all projects contained in the XML files that are in this directory.
     * @see ProjectImporter
     * @see ProjectImporter#importProjects(InputStream)
     * @see JarFileIterator
     */
    public String importAutoloadPlans() {

        log.info("TestDataLoaderImpl starts");
        try {
            List<String> files = listFiles(AUTOLOAD_DIRECTORY_NAME, ".xml");
            
            for (String xmlFileName : files) {

                if (xmlFileName.startsWith(AUTOLOAD_DIRECTORY_NAME)
                        && xmlFileName.endsWith(".xml")) {
                    log.info("Adding Case Study XML " + xmlFileName + ".");
                    InputStream in = Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(
                            xmlFileName);
                    ProjectImporter projectImporter = new ProjectImporter();
                    try {
                        for (Plan plan : projectImporter.importProjects(in)) {

                            em.persist(plan);
                            em.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e1) {
            log.error("Unable to open JAR file for case study import.", e1);
        }
        return "success";

    }

    /**
     *  Lists all files in a certain directory and with specific extension, e.g. list all files in directory data/project/autoload
     *  with extension .xml. The method can handle both, files in .jar archives and plain ones.
     *  
     *  This method has been introduced to be able to handle both, directories in .jar archives and plain directories. We need to be 
     *  able to handle both, zipped and exploded archives, i.e. plato.ear zipped or exploded.
     *  
     *  @param directory directory that shall be browsed
     *  @param fileExtension filter by file extension, e.g. ".xml", ".mm" 
     *  
     *  @return files in the directory
     */
    private List<String> listFiles(String directory, String fileExtension) throws MalformedURLException, IOException {
        
        URL url = Thread.currentThread().getContextClassLoader().getResource(directory);
        File dir = new File(url.getFile());
        String directoryPath = dir.getAbsolutePath();
        
        List<String> files = new ArrayList<String>();
        
        if (directoryPath.indexOf(".jar!") != -1) {
            URL urlJar = new URL(directoryPath.substring(
                    directoryPath.indexOf("file:"),
                    directoryPath.indexOf('!')));
            
            Enumeration<JarEntry> entries = new JarFile(urlJar.getFile()).entries();
            
            while ( entries.hasMoreElements() ) {
                String fileName = entries.nextElement().getName();
                
                fileName = fileName.replace('\\', '/');
                
                if (fileName.startsWith(directory)
                        && fileName.endsWith(fileExtension)) {
                    files.add(fileName);
                }
            }
            
        } else {
            File[] fileArray = dir.listFiles();
            
            for (int i = 0; fileArray != null && i < fileArray.length; i++) {
                String fileName = fileArray[i].getAbsolutePath();
                
                fileName = fileName.replace('\\', '/');
                
                int dirStart = fileName.indexOf(directory);
                
                if (dirStart != -1 && fileName.endsWith(fileExtension)) {
                    files.add(fileName.substring(dirStart));
                }
            }              
        }
        return files;
    }
    
    private void insertTemplateTree(String directory) {
        
        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist("admin");

        List<String> files = new ArrayList<String>();
               
        try {
            files = listFiles(directory, ".xml");
        } catch (MalformedURLException e1) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Template library couldn't be populated. ");
            return;
        } catch (IOException e1) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Template library couldn't be populated. ");
            return;
        }

        for (String filename : files) {

            try {
                log.info("Inserting templates from "+filename);
                InputStream istream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                byte[] bytes = new byte[512];

                int readBytes;
                while ((readBytes = istream.read(bytes)) > 0) {
                    out.write(bytes, 0, readBytes);
                }

                byte[] data = out.toByteArray();

                istream.close();
                out.close();

                projectImport.storeTemplatesInLibrary(data);
            } catch (IOException e) {
                log.error("Failed to insert templates: " + e.getMessage());
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Template library couldn't be populated. ");
                return;
            } catch (SAXException e) {
                log.error("Failed to insert templates: " + e.getMessage());
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Template library couldn't be populated. ");
                return;
            }
        }
    }
            
    public String insertTemplateTree() {

        insertTemplateTree(PUBLIC_FRAGMENTS_DIRECTORY_NAME);
        insertTemplateTree(PUBLIC_TEMPLATES_DIRECTORY_NAME);

        return "success";
    }

    @Remove
    @Destroy
    public void destroy() {

    }


    @In(create=true)
    private LoadPlanAction loadPlan;



    @In(required=false)
    private User user;

    /**
     * This convenience function is accessible at the end of the project list screen.
     * It creates a demo project, based on the XML file <code>Demo_Project__Preservation_Plan_for_Papers.xml</code>,
     * sets it to private and sets the current user as the owner.
     * Also slightly changes the name and description to
     * reflect the fact that it is meant to be a demo project.
     * The user can then do whatever she wants to that project.
     */
    @RaiseEvent("projectListChanged")
    public String createDemoProject(String type) {
        try {
            
            List<String> files = listFiles(DEMOPLANS_DIRECTORY_NAME, ".xml");

            for (String xmlFileName : files) {
                if (xmlFileName.startsWith(DEMOPLANS_DIRECTORY_NAME)
                        && xmlFileName.endsWith(type+".xml")) {
                    log.info("Adding DEMO PLAN " + xmlFileName + " for user "+user.getUsername()+".");
                    InputStream in = Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(
                            xmlFileName);
                    ProjectImporter projectImporter = new ProjectImporter();
                    try {
                        for (Plan plan : projectImporter.importProjects(in)) {
                            PlanProperties pp = plan.getPlanProperties();
                            // We set the current user as the owner of the project.
                            if (user == null) {
                                log.error("user is null! why?");
                            } else {
                                pp.setOwner(user.getUsername());
                            }
                            pp.setName("MY DEMO PLAN: "+pp.getName());
                            pp.setDescription("This is a DEMO plan for the user '"
                                    +user.getUsername()+"'. "+ pp.getDescription());
                            pp.setPrivateProject(true);
                            
                            /*
                            for (Trigger trigger : project
                                    .getProjectBasis().getTriggers()
                                    .keySet()) {
                                em.persist(em.merge(trigger));
                            }
                            */
                            em.persist(plan);
                            em.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }

                }
            }

        } catch (IOException e1) {
            log.error("Unable to open JAR file for case study import.", e1);
        }
        loadPlan.listMyProjects();
        FacesMessages.instance().add("Your demo plan has been created. Please load it from the list below.");
        return null;
    }
}
