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

package eu.planets_project.pp.plato.services.action.planets;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IPreservationActionRegistry;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.view.CreateView;

public class PlanetsServiceRegistry implements IPreservationActionRegistry {
    
    private static final Log log = PlatoLogger.getLogger(PlanetsServiceRegistry.class);
    
    private String infoMessage = "Not connected.";
    
    private ServiceRegistry registry;
    
    public String getToolIdentifier(String url) {
        return "";
    }
    
    public String getToolParameters(String url) {
        return "";
    }
    

    /**
     * At the moment we connect to the local service registry
     */
    public void connect(String URL) throws ServiceException,
            MalformedURLException {

        registry = ServiceRegistryFactory.getServiceRegistry();
        
        if (registry == null) {
            throw new ServiceException("Unable to connect to service registry");
        }
    }

    /**
     * @throws PlanetsServiceException if no PUID is available AND if something goes really wrong
     * 
     * @param sourceFormat source format from which we want to migrate
     */
    public List<PreservationActionDefinition> getAvailableActions(FormatInfo sourceFormat)
            throws PlatoServiceException {
        
        FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
        
        // we only want migration services
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, Migrate.class.getCanonicalName()).build();
        
        ServiceDescription sdQueryViewer = new ServiceDescription.Builder(null, CreateView.class.getCanonicalName()).build();
        
        // query all migration services 
        List<ServiceDescription> list = registry.query(sdQuery);
        
        List<ServiceDescription> listViewers = registry.query(sdQueryViewer);
        
        list.addAll(listViewers);
        
        // the list of available preservation action services we want to offer the user 
        List<PreservationActionDefinition> services = new ArrayList<PreservationActionDefinition>();
        
        // iterate through services list
        for (ServiceDescription sd : list) {
            
            
            // iterate through migration paths of found service
            for( MigrationPath path : sd.getPaths() ) {
                
                Format inputFormat = fr.getFormatForUri(path.getInputFormat());
                
                // check if this is the input format we are looking for
                if (false == inputFormat.getExtensions().contains(sourceFormat.getDefaultExtension())) {
                    continue;
                }
                
                // get the output format the service is able to migrate to
                Format outputFormat = fr.getFormatForUri(path.getOutputFormat());

                PreservationActionDefinition pad = new PreservationActionDefinition();
                
                pad.setActionIdentifier("Planets-local");
                pad.setTargetFormat(outputFormat.getUri().toString());
                pad.setShortname(sd.getName() + " @ " + sd.getEndpoint().getHost());
                pad.setUrl(sd.getEndpoint().toString());
                
                String input;
                
                // if an extension is defined we take the first one from the list
                if (inputFormat.getExtensions().size() > 0) {
                    input = inputFormat.getExtensions().iterator().next();
                } else {
                    input = inputFormat.getUri().toString(); 
                }
                
                String output;
                
                // if an extension is defined we take the first one from the list                
                if (outputFormat.getExtensions().size() > 0) {
                    output = outputFormat.getExtensions().iterator().next();
                } else {
                    output = outputFormat.getUri().toString();
                }
                
                pad.setInfo(input+" > "+output);

                
                
                if (sd.getParameters() != null) {
                    String paramInfo = "";
                    // from the Planets registry we can have parameters and their possible values.
                    for (Parameter p : sd.getParameters()) {
                        paramInfo += (p.getName() + " = " + p.getValue() + "\n");
                    }
                    
                    pad.setParameterInfo(paramInfo);
                }
                
                services.add(pad);
            }
            
        }
        
        for (ServiceDescription sd : listViewers) {

            if (CreateView.class.getCanonicalName().equals(sd.getType())) {
                PreservationActionDefinition pad = new PreservationActionDefinition();
                
                pad.setActionIdentifier("Planets-Viewer-local");
                pad.setTargetFormat("");
                pad.setShortname(sd.getName() + " @ " + sd.getEndpoint().getHost());
                pad.setUrl(sd.getEndpoint().toString());
                pad.setEmulated(true);
                
                pad.setInfo(sd.getDescription());
                
                services.add(pad);
            }
        }
        

        return services;
    }

    public String getLastInfo() {
        return infoMessage;//"... is currently being integrated and will be available soon";
    }

}
