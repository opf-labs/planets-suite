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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IEmulationAction;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.view.CreateView;
import eu.planets_project.services.view.CreateViewResult;

public class PlanetsEmulationService implements IEmulationAction {
    
    private String sessionIdentifier;

    public String startSession(PreservationActionDefinition action,
            SampleObject sampleObject) throws PlatoServiceException {
        
        if (perform(action, sampleObject)) {
            return sessionIdentifier;
        }

        return "";
    }

    public boolean perform(PreservationActionDefinition action,
            SampleObject sampleObject) throws PlatoServiceException {
        
        URL wsdlLocation;
        try {
            wsdlLocation = new URL(action.getUrl());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }
        
        Service service = null;
        try {
            service = Service.create(wsdlLocation, CreateView.QNAME);
        } catch(WebServiceException e) {
            throw new PlatoServiceException("Error creating web service.", e);
        }

        //
        // we have to create a temp file to later on pass it to the the Planets GRATE viewer.
        // for some reasons, when i pass it on by value, it doesn't work. GRATE always complains
        // content is null. so i reckon that something doesn't work with call by value of a 
        // Planets DigitalObject. By reference works fine.
        //
        File tempFile = null;;
        try {
            tempFile = File.createTempFile("emu" + System.nanoTime(), "emu");
            tempFile.deleteOnExit();
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(tempFile));
            out.write(sampleObject.getData().getData());
            out.close();
            
        } catch (IOException e1) {
            PlatoLogger.getLogger(PlanetsEmulationService.class).fatal(e1.getMessage());
            return false;
        }
        
        DigitalObject dob = new DigitalObject.Builder(Content.byReference(tempFile))
            .title(sampleObject.getShortName()).build();
        
        List<DigitalObject> digitalObjects = new ArrayList<DigitalObject>();
        digitalObjects.add(dob);
        
        CreateView viewService = (CreateView) service.getPort(CreateView.class);
        
        CreateViewResult view = null;
        
        try {
            view = viewService.createView(digitalObjects, null);
            
            ServiceReport report = view.getReport();
            
            if (report.getStatus() != ServiceReport.Status.SUCCESS ) {
                sessionIdentifier = "";
                return false;
            } 
            
            sessionIdentifier = view.getViewURL().toExternalForm();
            
            return true;
            
        } catch( Exception e ) {
            PlatoLogger.getLogger(PlanetsEmulationService.class).error(e.getMessage());
        }
        
        return false;
    }

}
