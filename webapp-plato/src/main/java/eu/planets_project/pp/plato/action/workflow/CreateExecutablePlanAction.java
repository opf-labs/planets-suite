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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import eu.planets_project.pp.plato.action.interfaces.ICreateExecutablePlan;
import eu.planets_project.pp.plato.action.interfaces.IDefinePlan;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.services.action.IPreservationActionRegistry;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryDefinition;
import eu.planets_project.pp.plato.services.action.PreservationActionRegistryFactory;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Scope(ScopeType.SESSION)
@Name("createExecutablePlan")
public class CreateExecutablePlanAction extends AbstractWorkflowStep implements ICreateExecutablePlan {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5536874272738085658L;

    private static final Log log = PlatoLogger.getLogger(CreateExecutablePlanAction.class);

    @In(create=true)
    IDefinePlan definePlan;
    
    @Out
    private BooleanCapsule executablePlanPossible = new BooleanCapsule(false);
    
    @Out
    private BooleanCapsule eprintsExecutablePlanPossible = new BooleanCapsule(false);
    
    
    public CreateExecutablePlanAction() {
        requiredPlanState = new Integer(PlanState.ANALYSED);
    }
    
    protected String getWorkflowstepName() {
        return "createExecutablePlan";
    }

    protected IWorkflowStep getSuccessor() {
        return definePlan;
    }
    
    /**
     * Is Plato able to create an executable plan? At the moment this 
     * is only possible if the recommended action is a Planets service.
     * 
     * @return true/false if Plato can create an executable plan
     * 
     * @return
     */
    private boolean isExecutablePreservationPlanPossible() {
        
        Alternative recommendedAlternative = selectedPlan.getRecommendation().getAlternative();
        
        // does the recommended alternative exist and does it have an "action". if not we can't
        // create an executable preservation plan
        if (recommendedAlternative == null || recommendedAlternative.getAction() == null) {
            return false;
        }
        
        // if the recommended action is not a Planets action, we cannot create an executable
        // preservation plan
        if (!"Planets-local".equals(recommendedAlternative.getAction().getActionIdentifier())) {
            return false;
        }
     
        return true;
    }
    
    public boolean isEprintsPlanPossible() {
        
        return false;
    }
    
    private String getToolIdentifier(IPreservationActionRegistry registry) {
        
        Alternative recommendedAlternative = selectedPlan.getRecommendation().getAlternative();
                
        String toolIdentifier = registry.getToolIdentifier(recommendedAlternative.getAction().getUrl());
        
        return toolIdentifier;
    }

    
        
    

    public void downloadExecutablePlan() {
        
        if (selectedPlan.getRecommendation().getAlternative() == null
                || selectedPlan.getRecommendation().getAlternative().getAction() == null) {
            return;
        }
        
        byte[] plan;
        try {
            plan = selectedPlan.getExecutablePlanDefinition().getExecutablePlan().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Unsupported encoding. " + e.getMessage());
            return;
        }
        
        if (plan != null) {
            Downloader.instance().download(plan, "executable-preservation-plan.xml", "text/xml");
        }
    }
    
    public void downloadEprintsExecutablePlan() {

        if (selectedPlan.getRecommendation().getAlternative() == null
                || selectedPlan.getRecommendation().getAlternative().getAction() == null) {
            return;
        }
              
        byte[] plan;
        try {
            plan = selectedPlan.getExecutablePlanDefinition().getEprintsExecutablePlan().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Unsupported encoding. " + e.getMessage());
            return;
        }
        
        if (plan != null) {
            Downloader.instance().download(plan, "executable-preservation-plan.xml", "text/xml");
        }
    }

    private void generateExecutablePlan()  {
        
        // this is the recommended action
        String wsdlLocation = selectedPlan.getRecommendation().getAlternative().getAction().getUrl();
        
        // and this is the target format we want to migrate to
        String targetFormat = selectedPlan.getRecommendation().getAlternative().getAction().getTargetFormat();
        
        String workflowFile = "data/plans/executables/ExecutablePreservationPlan.xml";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(workflowFile);
        
        // we load the workflow template and fill out the migrate1 service
        
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        
        try {
            doc = builder.build(in);
        } catch (JDOMException e) {
          FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error in template.");
          log.error(e);
          return;
        } catch (IOException e) {
          FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error in template.");
          log.error(e);
          return;
        }
        
        Element root = doc.getRootElement();
        
        try {
            JDOMXPath endpointXPath = new JDOMXPath("services/service[@id='migrate1']/endpoint");
            
            Element endpoint = (Element)endpointXPath.selectSingleNode(root);
            
            endpoint.setText(wsdlLocation);
            
            String settings = selectedPlan.getRecommendation().getAlternative().getExperiment().getSettings();
            
            if (settings == null) {
                settings = "";
            }
            
            Scanner scanner = new Scanner(settings);
            
            JDOMXPath migrateServiceXPath = new JDOMXPath("services/service[@id='migrate1']");
            
            Element migrateService = (Element)migrateServiceXPath.selectSingleNode(root);
            
            migrateService.setContent(endpoint);
            
            Element parameters = new Element ("parameters");
            
            Element targetFormatParam = new Element("param");
            targetFormatParam.addContent(new Element("name").setText("planets:service/migration/input/migrate_to_fmt"));
            targetFormatParam.addContent(new Element("value").setText(targetFormat));
            parameters.addContent(targetFormatParam);

            int index;
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                if ((index = line.indexOf('=')) > 0) {
                    String name = line.substring(0, index);
                    String value = line.substring(index+1);

                    if (name.length()>0 && value.length()>0) {
                        Element param = new Element("param");
                        param.addContent(new Element("name").setText(name.trim()));
                        param.addContent(new Element("value").setText(value.trim()));
                        parameters.addContent(param);
                    }
                }
            }
            
            migrateService.addContent(parameters);
            
        } catch (JaxenException e) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error in template.");
            log.error(e);
            return;
        }
        
      XMLOutputter outputter = new XMLOutputter();
      
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      
      try {
          outputter.output(doc, byteArray);
      } catch (IOException e) {
          FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "Error writing the preservation plan workflow.");
          log.error(e);
          
          return;
      }
      
      selectedPlan.getExecutablePlanDefinition().setExecutablePlan(byteArray.toString());
    }
    

    public boolean validate(boolean showValidationErrors) {

        return true;
    }


    /**
     * Write both plan definition and project properties to database.
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {

        /** user is set in save() */
        save(selectedPlan.getPlanProperties());
        save(selectedPlan.getExecutablePlanDefinition());
        changed = "";

        return null;
    }    

    /**
     * 
     */
    public void init() {
        
        // If we don't have tool parameters, we copy them from the chosen alternative's config settings:
        if (selectedPlan.getExecutablePlanDefinition().getToolParameters() == null ||
                "".equals(selectedPlan.getExecutablePlanDefinition().getToolParameters())) {

            selectedPlan.getExecutablePlanDefinition().setToolParameters(
                    selectedPlan.getRecommendation().getAlternative().getExperiment().getSettings());
        }
        
        executablePlanPossible.setBool(isExecutablePreservationPlanPossible());
        eprintsExecutablePlanPossible.setBool(isEprintsPlanPossible());
        
        if (executablePlanPossible.isBool() == true) {
            generateExecutablePlan();
            return;
        }
        
    }

    /**
     * Obligatory EJB destroy method.
     */
    @Destroy
    @Remove
    public void destroy() {
    }
}
